package org.gots.seed.provider.nuxeo;

import java.util.ArrayList;
import java.util.List;

import org.gots.bean.BaseAllotmentInterface;
import org.gots.nuxeo.NuxeoManager;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.GotsSeedManager;
import org.gots.seed.GrowingSeedInterface;
import org.gots.seed.provider.local.LocalGrowingSeedProvider;
import org.nuxeo.android.repository.DocumentManager;
import org.nuxeo.ecm.automation.client.android.AndroidAutomationClient;
import org.nuxeo.ecm.automation.client.cache.CacheBehavior;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;
import org.nuxeo.ecm.automation.client.jaxrs.model.IdRef;
import org.nuxeo.ecm.automation.client.jaxrs.model.PropertyMap;

import android.content.Context;
import android.util.Log;

public class NuxeoGrowingSeedProvider extends LocalGrowingSeedProvider {

    private static final String TAG = "NuxeoGrowingSeedProvider";

    public NuxeoGrowingSeedProvider(Context mContext) {
        super(mContext);
        NuxeoManager.getInstance().initIfNew(mContext);
    }

    protected AndroidAutomationClient getNuxeoClient() {
        return NuxeoManager.getInstance().getNuxeoClient();
    }

    @Override
    public List<GrowingSeedInterface> getGrowingSeedsByAllotment(BaseAllotmentInterface allotment, boolean force) {
        List<GrowingSeedInterface> remoteGrowingSeeds = new ArrayList<GrowingSeedInterface>();
        List<GrowingSeedInterface> myGrowingSeeds = new ArrayList<GrowingSeedInterface>();
        List<GrowingSeedInterface> localGrowingSeeds = super.getGrowingSeedsByAllotment(allotment, force);

        try {
            Session session = getNuxeoClient().getSession();
            DocumentManager service = session.getAdapter(DocumentManager.class);

            byte cacheParam = CacheBehavior.STORE;
            if (force) {
                cacheParam = (byte) (cacheParam | CacheBehavior.FORCE_REFRESH);
            }

            Documents growingSeedDocuments = service.query(
                    "SELECT * FROM GrowingSeed WHERE ecm:currentLifeCycleState != \"deleted\" AND ecm:parentId=\'"
                            + allotment.getUUID() + "\'", null, new String[] { "dc:modified DESC" }, "*", 0, 50,
                    cacheParam);
            // Documents docs = service.getChildren(new IdRef(allotment.getUUID()));
            NuxeoSeedProvider provider = new NuxeoSeedProvider(mContext);
            for (Document growingSeedDocument : growingSeedDocuments) {

                Documents relations = service.getRelations(growingSeedDocument, "http://purl.org/dc/terms/isFormatOf");
                if (relations.size() >= 1) {
                    GrowingSeedInterface growingSeed;
                    Document originalSeed = service.getDocument(relations.get(0), "*");

                    // growingSeed = (GrowingSeedInterface) NuxeoSeedConverter.convert(originalSeed);
                    growingSeed = (GrowingSeedInterface) provider.getSeedByUUID(originalSeed.getId());
                    growingSeed = NuxeoGrowingSeedConverter.populate(growingSeed, growingSeedDocument);
                    // growingSeed = super.updateGrowingSeed(growingSeed, allotment);
                    remoteGrowingSeeds.add(growingSeed);
                }
            }
            myGrowingSeeds = synchronize(localGrowingSeeds, remoteGrowingSeeds, allotment);
        } catch (Exception e) {
            Log.e(TAG, "getGrowingSeedsByAllotment " + e.getMessage(), e);
            myGrowingSeeds = localGrowingSeeds;
        }
        return myGrowingSeeds;
    }

    private List<GrowingSeedInterface> synchronize(List<GrowingSeedInterface> localGrowingSeeds,
            List<GrowingSeedInterface> remoteGrowingSeeds, BaseAllotmentInterface allotment) {
        List<GrowingSeedInterface> myGrowingSeeds = new ArrayList<GrowingSeedInterface>();

        for (GrowingSeedInterface remoteGrowingSeed : remoteGrowingSeeds) {
            boolean found = false;
            for (GrowingSeedInterface localGrowingSeed : localGrowingSeeds) {
                if (remoteGrowingSeed.getUUID().equals(localGrowingSeed.getUUID())) {
                    found = true;
                    break;
                }
            }

            if (!found)
                myGrowingSeeds.add(super.plantingSeed(remoteGrowingSeed, allotment));
            else {
                GrowingSeedInterface updatableSeed = super.getGrowingSeedsByUUID(remoteGrowingSeed.getUUID());
                remoteGrowingSeed.setGrowingSeedId(updatableSeed.getGrowingSeedId());
                myGrowingSeeds.add(super.updateGrowingSeed(updatableSeed, allotment));
            }
        }

        for (GrowingSeedInterface localGrowingSeed : localGrowingSeeds) {
            if (localGrowingSeed.getUUID() == null)
                myGrowingSeeds.add(insertNuxeoSeed(localGrowingSeed, allotment));
            else {
                boolean found = false;
                for (GrowingSeedInterface remoteGrowingSeed : remoteGrowingSeeds) {
                    if (localGrowingSeed.getUUID().equals(remoteGrowingSeed.getUUID())) {
                        found = true;
                        break;
                    }
                }
                if (!found)
                    super.deleteGrowingSeed(localGrowingSeed);
            }
        }
        return myGrowingSeeds;
    }

    @Override
    public GrowingSeedInterface plantingSeed(GrowingSeedInterface growingSeed, BaseAllotmentInterface allotment) {
        growingSeed.setUUID(null);
        return insertNuxeoSeed(super.plantingSeed(growingSeed, allotment), allotment);
    }

    protected GrowingSeedInterface insertNuxeoSeed(GrowingSeedInterface growingSeed,
            final BaseAllotmentInterface allotment) {

        Document allotmentDoc = null;
        try {
            Session session = getNuxeoClient().getSession();
            final DocumentManager service = session.getAdapter(DocumentManager.class);
            allotmentDoc = service.getDocument(allotment.getUUID());

            if (allotmentDoc == null)
                return null;

            PropertyMap properties = getProperties(growingSeed);

            final GotsSeedManager VendorSeedManager = GotsSeedManager.getInstance().initIfNew(mContext);
            BaseSeedInterface vendorSeed = VendorSeedManager.getSeedById(growingSeed.getSeedId());
            if (vendorSeed == null || vendorSeed.getUUID() == null)
                vendorSeed = VendorSeedManager.createSeed(vendorSeed);

            properties.set("growingseed:vendorseedid", vendorSeed.getUUID());

            Document newSeed = service.createDocument(allotmentDoc, "GrowingSeed", growingSeed.getSpecie(), properties);

            growingSeed.setUUID(newSeed.getId());
            growingSeed = super.updateGrowingSeed(growingSeed, allotment);
            service.createRelation(newSeed, "http://purl.org/dc/terms/isFormatOf", new IdRef(vendorSeed.getUUID()));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return growingSeed;
    }

    protected PropertyMap getProperties(GrowingSeedInterface growingSeed) {
        PropertyMap properties = new PropertyMap();
        properties.set("dc:title", growingSeed.getSpecie() + " " + growingSeed.getVariety());
        if (growingSeed.getDateSowing() != null)
            properties.set("growingseed:datesowing", growingSeed.getDateSowing());
        if (growingSeed.getDateHarvest() != null)
            properties.set("growingseed:dateharvest", growingSeed.getDateHarvest());

        return properties;
    }

    @Override
    public void deleteGrowingSeed(GrowingSeedInterface seed) {
        try {
            Session session = getNuxeoClient().getSession();
            DocumentManager service = session.getAdapter(DocumentManager.class);
            service.remove(new IdRef(seed.getUUID()));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            super.deleteGrowingSeed(seed);
        }
    }

    @Override
    public GrowingSeedInterface updateGrowingSeed(GrowingSeedInterface seed, BaseAllotmentInterface allotment) {

        try {
            Session session = getNuxeoClient().getSession();
            DocumentManager service = session.getAdapter(DocumentManager.class);
            service.update(new IdRef(seed.getUUID()), getProperties(seed));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return super.updateGrowingSeed(seed, allotment);
    }

}
