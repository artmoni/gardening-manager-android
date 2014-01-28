package org.gots.seed.provider.nuxeo;

import java.util.ArrayList;
import java.util.Iterator;
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
    }

    protected AndroidAutomationClient getNuxeoClient() {
        NuxeoManager nuxeoManager = NuxeoManager.getInstance();
        nuxeoManager.initIfNew(mContext);
        return nuxeoManager.getNuxeoClient();
    }

    @Override
    public List<GrowingSeedInterface> getGrowingSeedsByAllotment(BaseAllotmentInterface allotment) {
        List<GrowingSeedInterface> remoteGrowingSeeds = new ArrayList<GrowingSeedInterface>();
        List<GrowingSeedInterface> myGrowingSeeds = new ArrayList<GrowingSeedInterface>();
        List<GrowingSeedInterface> localGrowingSeeds = super.getGrowingSeedsByAllotment(allotment);

        boolean refresh = true;
        try {
            Session session = getNuxeoClient().getSession();
            DocumentManager service = session.getAdapter(DocumentManager.class);

            byte cacheParam = CacheBehavior.STORE;
            if (refresh) {
                cacheParam = (byte) (cacheParam | CacheBehavior.FORCE_REFRESH);
                refresh = false;
            }

            Documents docs = service.query(
                    "SELECT * FROM GrowingSeed WHERE ecm:currentLifeCycleState != \"deleted\" AND ecm:parentId=\'"
                            + allotment.getUUID() + "\'", null, new String[] { "dc:modified DESC" }, "*", 0, 50,
                    cacheParam);
            // Documents docs = service.getChildren(new IdRef(allotment.getUUID()));
            for (Iterator<Document> iterator = docs.iterator(); iterator.hasNext();) {
                Document growingSeedDocument = iterator.next();

                Documents relations = service.getRelations(growingSeedDocument, "http://purl.org/dc/terms/isFormatOf");
                if (relations.size() >= 1) {
                    GrowingSeedInterface growingSeed;
                    Document originalSeed = service.getDocument(relations.get(0), "*");

                    NuxeoSeedProvider provider = new NuxeoSeedProvider(mContext);
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
                myGrowingSeeds.add(super.insertSeed(remoteGrowingSeed, allotment));
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
    public GrowingSeedInterface insertSeed(GrowingSeedInterface growingSeed, BaseAllotmentInterface allotment) {
        growingSeed.setUUID(null);
        return insertNuxeoSeed(super.insertSeed(growingSeed, allotment), allotment);
    }

    protected GrowingSeedInterface insertNuxeoSeed(GrowingSeedInterface growingSeed,
            final BaseAllotmentInterface allotment) {
        Session session = getNuxeoClient().getSession();
        final DocumentManager service = session.getAdapter(DocumentManager.class);

        Document allotmentDoc = null;
        try {
            allotmentDoc = service.getDocument(allotment.getUUID());
        } catch (Exception e) {
            Log.e(TAG, "Fetching folder allotment " + e.getMessage(), e);
        }

        if (allotmentDoc == null)
            return null;

        try {

            PropertyMap properties = getProperties(growingSeed);
            BaseSeedInterface seed = GotsSeedManager.getInstance().initIfNew(mContext).getSeedById(
                    growingSeed.getSeedId());
            properties.set("growingseed:vendorseedid", seed.getUUID());

            Document newSeed = service.createDocument(allotmentDoc, "GrowingSeed", growingSeed.getSpecie(), properties);

            growingSeed.setUUID(newSeed.getId());
            growingSeed = super.updateGrowingSeed(growingSeed, allotment);
            service.createRelation(newSeed, "http://purl.org/dc/terms/isFormatOf", new IdRef(seed.getUUID()));
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

        return properties;
    }

    @Override
    public void deleteGrowingSeed(GrowingSeedInterface seed) {
        Session session = getNuxeoClient().getSession();
        DocumentManager service = session.getAdapter(DocumentManager.class);
        try {
            service.remove(new IdRef(seed.getUUID()));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            super.deleteGrowingSeed(seed);
        }
    }

}
