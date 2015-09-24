package org.gots.seed.provider.nuxeo;

import android.content.Context;
import android.util.Log;

import org.gots.bean.BaseAllotmentInterface;
import org.gots.nuxeo.NuxeoManager;
import org.gots.seed.BaseSeed;
import org.gots.seed.GotsSeedManager;
import org.gots.seed.GrowingSeed;
import org.gots.seed.GrowingSeedImpl;
import org.gots.seed.provider.local.LocalGrowingSeedProvider;
import org.nuxeo.android.repository.DocumentManager;
import org.nuxeo.ecm.automation.client.android.AndroidAutomationClient;
import org.nuxeo.ecm.automation.client.cache.CacheBehavior;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;
import org.nuxeo.ecm.automation.client.jaxrs.model.IdRef;
import org.nuxeo.ecm.automation.client.jaxrs.model.PropertyMap;

import java.util.ArrayList;
import java.util.List;

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
    public List<GrowingSeed> getGrowingSeedsByAllotment(BaseAllotmentInterface allotment, boolean force) {
        List<GrowingSeed> remoteGrowingSeeds = new ArrayList<GrowingSeed>();

        try {
            Session session = getNuxeoClient().getSession();
            DocumentManager service = session.getAdapter(DocumentManager.class);

            byte cacheParam = CacheBehavior.STORE;
            if (force) {
                cacheParam = (byte) (cacheParam | CacheBehavior.FORCE_REFRESH);
            }

            Documents growingSeedDocuments = service.query(
                    "SELECT * FROM GrowingSeed WHERE ecm:currentLifeCycleState != \"deleted\" AND ecm:parentId=\'"
                            + allotment.getUUID() + "\'", null, new String[]{"dc:modified DESC"}, "*", 0, 50,
                    cacheParam);
            for (Document doc : growingSeedDocuments) {
                GrowingSeed growingSeed = convertToGrowingSeed(doc);
                growingSeed = super.updateGrowingSeed(growingSeed, allotment);
                remoteGrowingSeeds.add(growingSeed);
            }

        } catch (Exception e) {
            Log.e(TAG, "getGrowingSeedsByAllotment " + e.getMessage());
            return super.getGrowingSeedsByAllotment(allotment,force);
        }
        return remoteGrowingSeeds;
    }


    private List<GrowingSeed> synchronize(List<GrowingSeed> localGrowingSeeds,
                                          List<GrowingSeed> remoteGrowingSeeds, BaseAllotmentInterface allotment) {
        List<GrowingSeed> myGrowingSeeds = new ArrayList<GrowingSeed>();

        for (GrowingSeed remoteGrowingSeed : remoteGrowingSeeds) {
            boolean found = false;
            for (GrowingSeed localGrowingSeed : localGrowingSeeds) {
                if (remoteGrowingSeed.getPlant().getUUID().equals(localGrowingSeed.getPlant().getUUID())) {
                    found = true;
                    break;
                }
            }

            if (!found)
                myGrowingSeeds.add(super.plantingSeed(remoteGrowingSeed, allotment));
            else {
                GrowingSeed updatableSeed = super.getGrowingSeedsByUUID(remoteGrowingSeed.getUUID());
                remoteGrowingSeed.setId(updatableSeed.getId());
                myGrowingSeeds.add(super.updateGrowingSeed(updatableSeed, allotment));
            }
        }

        for (GrowingSeed localGrowingSeed : localGrowingSeeds) {
            if (localGrowingSeed.getPlant().getUUID() == null)
                myGrowingSeeds.add(insertNuxeoSeed(localGrowingSeed, allotment));
            else {
                boolean found = false;
                for (GrowingSeed remoteGrowingSeed : remoteGrowingSeeds) {
                    if (localGrowingSeed.getPlant().getUUID().equals(remoteGrowingSeed.getPlant().getUUID())) {
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
    public GrowingSeed plantingSeed(GrowingSeed growingSeed, BaseAllotmentInterface allotment) {
//        growingSeed.getPlant().setUUID(null);
        return insertNuxeoSeed(super.plantingSeed(growingSeed, allotment), allotment);
    }

    protected GrowingSeed insertNuxeoSeed(GrowingSeed growingSeed,
                                          final BaseAllotmentInterface allotment) {

        Document allotmentDoc = null;
        try {
            Session session = getNuxeoClient().getSession();
            final DocumentManager service = session.getAdapter(DocumentManager.class);
            allotmentDoc = service.getDocument(allotment.getUUID());

            if (allotmentDoc == null)
                return null;


            final GotsSeedManager VendorSeedManager = GotsSeedManager.getInstance().initIfNew(mContext);
            BaseSeed vendorSeed = VendorSeedManager.getSeedById(growingSeed.getPlant().getSeedId());

            if (vendorSeed == null || vendorSeed.getUUID() == null)
                vendorSeed = VendorSeedManager.createSeed(vendorSeed, null);

            PropertyMap properties = convertToProperties(growingSeed);
            Document newGrowingPlantDoc = service.createDocument(allotmentDoc, "GrowingSeed", growingSeed.getPlant().getSpecie(), properties);

            growingSeed.setUUID(newGrowingPlantDoc.getId());
            growingSeed = super.updateGrowingSeed(growingSeed, allotment);
//            service.createRelation(newSeed, "http://purl.org/dc/terms/isFormatOf", new IdRef(vendorSeed.getUUID()));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return growingSeed;
    }

    protected PropertyMap convertToProperties(GrowingSeed growingSeed) {
        PropertyMap properties = new PropertyMap();
        properties.set("dc:title", growingSeed.getPlant().getSpecie() + " " + growingSeed.getPlant().getVariety());
        if (growingSeed.getDateSowing() != null)
            properties.set("growingseed:datesowing", growingSeed.getDateSowing());
        if (growingSeed.getDateHarvest() != null)
            properties.set("growingseed:dateharvest", growingSeed.getDateHarvest());
        properties.set("growingseed:vendorseedid", growingSeed.getPlant().getUUID());
        return properties;
    }

    private GrowingSeed convertToGrowingSeed(Document doc) {
        try {
            GrowingSeed growingSeed = new GrowingSeedImpl();
            growingSeed.setDateSowing(doc.getDate("growingseed:datesowing"));
            growingSeed.setDateHarvest(doc.getDate("growingseed:dateharvest"));

            NuxeoSeedProvider provider = new NuxeoSeedProvider(mContext);
            growingSeed.setPlant(provider.getSeedByUUID("growingseed:vendorseedid"));
            growingSeed.setUUID(doc.getId());
            return growingSeed;
        } catch (Exception e) {
            Log.e(TAG, "Your document schema is not correct", e);
            return null;
        }
    }

    @Override
    public void deleteGrowingSeed(GrowingSeed growingSeed) {
        try {
            Session session = getNuxeoClient().getSession();
            DocumentManager service = session.getAdapter(DocumentManager.class);
            service.remove(new IdRef(growingSeed.getUUID()));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        } finally {
            super.deleteGrowingSeed(growingSeed);
        }
    }

    @Override
    public GrowingSeed updateGrowingSeed(GrowingSeed growingSeed, BaseAllotmentInterface allotment) {

        try {
            Session session = getNuxeoClient().getSession();
            DocumentManager service = session.getAdapter(DocumentManager.class);
            service.update(new IdRef(growingSeed.getUUID()), convertToProperties(growingSeed));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return super.updateGrowingSeed(growingSeed, allotment);
    }

}
