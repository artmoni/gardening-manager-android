package org.gots.seed.provider.nuxeo;

import android.content.Context;
import android.util.Log;

import org.gots.exception.GardenNotFoundException;
import org.gots.exception.NotImplementedException;
import org.gots.garden.GardenInterface;
import org.gots.garden.GotsGardenManager;
import org.gots.nuxeo.NuxeoManager;
import org.gots.nuxeo.NuxeoUtils;
import org.gots.seed.BaseSeed;
import org.gots.seed.LikeStatus;
import org.gots.seed.SpeciesDocument;
import org.gots.seed.provider.local.LocalSeedProvider;
import org.gots.utils.FileUtilities;
import org.nuxeo.android.repository.DocumentManager;
import org.nuxeo.ecm.automation.client.android.AndroidAutomationClient;
import org.nuxeo.ecm.automation.client.cache.CacheBehavior;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.model.Blob;
import org.nuxeo.ecm.automation.client.jaxrs.model.DocRef;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;
import org.nuxeo.ecm.automation.client.jaxrs.model.FileBlob;
import org.nuxeo.ecm.automation.client.jaxrs.model.IdRef;
import org.nuxeo.ecm.automation.client.jaxrs.model.PathRef;
import org.nuxeo.ecm.automation.client.jaxrs.model.PropertyMap;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class NuxeoSeedProvider extends LocalSeedProvider {

    protected static final String TAG = "NuxeoSeedProvider";

    private static final String QUERY_FILTER_LANGUAGE = " AND vendorseed:language=\""
            + Locale.getDefault().getCountry().toLowerCase() + "\"";

    private static final String QUERY_FILTER_SECTION = " AND ecm:path STARTSWITH '/default-domain/sections/'";

    String myToken;

    String myLogin;

    String myDeviceId;

    protected String myApp;

    // protected LazyUpdatableDocumentsList documentsList;

    public NuxeoSeedProvider(Context context) {
        super(context);
        myToken = gotsPrefs.getToken();
        myLogin = gotsPrefs.getNuxeoLogin();
        myDeviceId = gotsPrefs.getDeviceId();
        myApp = gotsPrefs.getGardeningManagerAppname();
        NuxeoManager.getInstance().initIfNew(context);
    }

    @Override
    public List<BaseSeed> getVendorSeeds(boolean force, int page, int pageSize) {
        List<BaseSeed> remoteVendorSeeds = new ArrayList<BaseSeed>();
//        List<BaseSeed> myVendorSeeds = new ArrayList<BaseSeed>();

        try {
            Session session = getNuxeoClient().getSession();
            final DocumentManager service = session.getAdapter(DocumentManager.class);

            byte cacheParam = CacheBehavior.STORE;
            boolean refresh = force;
            if (refresh) {
                cacheParam = (byte) (cacheParam | CacheBehavior.FORCE_REFRESH);
                refresh = false;
            }
            Documents docs = service.query("SELECT * FROM VendorSeed WHERE ecm:currentLifeCycleState = \"approved\""
                            + QUERY_FILTER_LANGUAGE + QUERY_FILTER_SECTION, null, new String[]{"dc:modified DESC"}, "*",
                    page, pageSize, cacheParam);
            if (docs.size() < pageSize) {
                docs.addAll(service.query("SELECT * FROM VendorSeed WHERE ecm:currentLifeCycleState = \"project\""
                                + QUERY_FILTER_LANGUAGE, null, new String[]{"dc:modified DESC"}, "*", page, pageSize,
                        cacheParam));

            }
            for (Document document : docs) {
                try {
                    BaseSeed seed = NuxeoSeedConverter.convert(document);
                    Blob likeStatus = service.getLikeStatus(document);
                    LikeStatus likes = NuxeoSeedConverter.getLikeStatus(likeStatus);
                    if (seed != null) {
                        seed.setLikeStatus(likes);
                        Log.i(TAG, seed.toString());

                        NuxeoUtils.downloadBlob(service, document, getImageFile(seed), null);
//                    downloadImageAsync(service, document, seed);
                        if (super.getSeedByUUID(seed.getUUID()) == null)
                            seed = super.createSeed(seed, getImageFile(seed));
                        else
                            seed = super.updateSeed(seed);
                        remoteVendorSeeds.add(seed);
                    } else {
                        Log.w(TAG, "Nuxeo Seed conversion problem " + document.getTitle() + "- " + document.getId());
                    }
                } catch (NumberFormatException formatException) {
                    Log.w(TAG,
                            formatException.getMessage() + " for Document " + document.getTitle() + " - "
                                    + document.getId());
                }

            }
//            myVendorSeeds = synchronize(super.getVendorSeeds(force, page, pageSize), remoteVendorSeeds);
        } catch (Exception e) {
            Log.e(TAG, "getAllSeeds " + e.getMessage(), e);
            remoteVendorSeeds = super.getVendorSeeds(force, 0, 25);
        }
        return remoteVendorSeeds;
    }

//    protected void downloadImageAsync(final DocumentManager service, final Document document, BaseSeed seed) {
//
//            new AsyncTask<File, Void, FileBlob>() {
//                @Override
//                protected FileBlob doInBackground(File... params) {
//                    File imageFile = params[0];
//                    return NuxeoUtils.downloadBlob(service, document, imageFile);
//                }
//
//            }.execute(imageFile);
//    }

    private File getImageFile(BaseSeed seed) {
        File imageFile;
        imageFile = new File(gotsPrefs.getFilesDir(), seed.getUUID());
        if (!imageFile.exists()) {
            String filename = seed.getVariety().toLowerCase().replaceAll("\\s", "").replaceAll(" ", "");
            if (!"".equals(filename))
                imageFile = new File(gotsPrefs.getFilesDir(), filename);
        }
        return imageFile;
    }

    @Override
    public ArrayList<BaseSeed> getSeedByBarCode(String barecode) {
        ArrayList<BaseSeed> remoteVendorSeeds = new ArrayList<BaseSeed>();
        try {
            Session session = getNuxeoClient().getSession();
            DocumentManager service = session.getAdapter(DocumentManager.class);

            byte cacheParam = CacheBehavior.STORE;
            boolean refresh = true;
            if (refresh) {
                cacheParam = (byte) (cacheParam | CacheBehavior.FORCE_REFRESH);
                refresh = false;
            }
            Documents docs = service.query(
                    "SELECT * FROM VendorSeed WHERE ecm:currentLifeCycleState != \"deleted\" AND vendorseed:barcode=\""
                            + Long.parseLong(barecode) + "\"", null, new String[]{"dc:modified DESC"}, "*", 0, 200,
                    cacheParam);

            for (Iterator<Document> iterator = docs.iterator(); iterator.hasNext(); ) {
                Document document = iterator.next();
                BaseSeed seed = NuxeoSeedConverter.convert(document);
                if (seed != null) {
                    remoteVendorSeeds.add(seed);
                    Log.i(TAG, "Nuxeo Seed: " + seed);
                } else {
                    Log.w(TAG, "Nuxeo Seed conversion problem " + document.getTitle() + "- " + document.getId());
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "getSeedByBarCode " + e.getMessage(), e);
            remoteVendorSeeds = super.getSeedByBarCode(barecode);
        }
        return remoteVendorSeeds;
    }

    protected List<BaseSeed> synchronize(List<BaseSeed> localVendorSeeds,
                                         List<BaseSeed> remoteVendorSeeds) {
        newSeeds.clear();
        List<BaseSeed> myVendorSeeds = new ArrayList<BaseSeed>();

        for (BaseSeed remoteSeed : remoteVendorSeeds) {
            boolean found = false;
            for (BaseSeed localSeed : localVendorSeeds) {
                if (remoteSeed.getUUID() != null && remoteSeed.getUUID().equals(localSeed.getUUID())) {
                    // local and remote
                    // 1: overwrite remote
                    // updateRemoteGarden(localSeed);
                    // 2: TODO sync with remote instead
                    // syncGardens(localGarden,remoteGarden);
                    found = true;
                    remoteSeed.setSeedId(localSeed.getSeedId());
                    // myVendorSeeds.add(localSeed);

                    break;
                }
            }
            if (found) {
                myVendorSeeds.add(super.updateSeed(remoteSeed));
            }
            // myVendorSeeds.add();}

            else {
                BaseSeed seed = super.createSeed(remoteSeed, null);
                myVendorSeeds.add(seed);
                newSeeds.add(seed);
            }
        }

        for (BaseSeed localSeed : localVendorSeeds) {

            if (localSeed.getUUID() == null) {
                myVendorSeeds.add(localSeed);
                localSeed = createNuxeoVendorSeed(localSeed, null);
            } else {
                boolean found = false;
                for (BaseSeed remoteSeed : remoteVendorSeeds) {
                    if (remoteSeed.getUUID() != null && remoteSeed.getUUID().equals(localSeed.getUUID())) {
                        found = true;
                        break;
                    }
                }
                if (!found) { // local only with UUID -> delete local
                    // TODO take a decision if local seed should be remove if the remote description is removed.
                    // super.remove(localSeed);
                }
            }

            // TODO update remote stock from local stock
            if (localSeed.getNbSachet() > 0) {
                try {
                    updateStock(localSeed, GotsGardenManager.getInstance().initIfNew(mContext).getCurrentGarden());
                } catch (GardenNotFoundException e) {
                    Log.e(myApp, e.getMessage());
                }
            }
        }

        // List<BaseSeed> myStock =
        // getMyStock(GardenManager.getInstance().initIfNew(mContext).getCurrentGarden());
        // for (Iterator<BaseSeed> myVendorSeed = myVendorSeeds.iterator(); myVendorSeed.hasNext();) {
        // BaseSeed baseSeedInterface = myVendorSeed.next();
        //
        // boolean foundStock = false;
        // for (Iterator<BaseSeed> seedStock = myStock.iterator(); seedStock.hasNext();) {
        // BaseSeed stockSeed = seedStock.next();
        // if (stockSeed.getUUID().equals(baseSeedInterface.getUUID())) {
        // baseSeedInterface.setNbSachet(stockSeed.getNbSachet());
        // foundStock = true;
        // break;
        // }
        //
        // }
        // if (!foundStock)
        // baseSeedInterface.setNbSachet(0);
        //
        // }
        return myVendorSeeds;
    }

    @Override
    public BaseSeed getSeedById(int id) {
        return super.getSeedById(id);
    }

    @Override
    public void createRecognitionSeed(File file, NuxeoUtils.OnBlobUpload callback) {
        try {
            Session session = getNuxeoClient().getSession();
            DocumentManager service = session.getAdapter(DocumentManager.class);

            Document rootFolder = service.getDocument("/default-domain/workspaces/justvisual");
            final Document imageDoc = service.createDocument(rootFolder, "VendorSeed", file.getName());
            if (imageDoc != null) {
                NuxeoUtils nuxeoUtils = new NuxeoUtils();
                nuxeoUtils.uploadBlob(session, imageDoc, file, callback);
                Log.d(TAG, "createRecognitionSeed " + imageDoc.getTitle() + " - " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            Log.w(TAG, "createRecognitionSeed: " + e.getMessage());
//            return e.getMessage();
        }
    }

    @Override
    public BaseSeed getSeedByUUID(String uuid) {
        BaseSeed remoteSeed = null;

        Session session = getNuxeoClient().getSession();
        DocumentManager service = session.getAdapter(DocumentManager.class);
        final BaseSeed localSeed = super.getSeedByUUID(uuid);
        try {
            Document doc = service.getDocument(new IdRef(uuid), true);
            remoteSeed = NuxeoSeedConverter.convert(doc);
            if (localSeed == null) {
                remoteSeed = super.createSeed(remoteSeed, null);
            } else {
                remoteSeed.setSeedId(localSeed.getSeedId());
                remoteSeed = super.updateSeed(remoteSeed);
            }
            Log.d(TAG, "getSeedByUUID found: " + remoteSeed);
//            downloadImageAsync(service, doc, remoteSeed);
            NuxeoUtils.downloadBlob(service, doc, getImageFile(remoteSeed), null);

        } catch (Exception e) {
            remoteSeed = localSeed;
            Log.w(TAG, "getSeedByUUID not found " + e.getMessage());
        }

        return remoteSeed;
    }

    @Override
    public List<BaseSeed> getRecognitionSeeds(boolean force) {
        Session session = getNuxeoClient().getSession();
        DocumentManager service = session.getAdapter(DocumentManager.class);
        List<BaseSeed> result = new ArrayList<>();
        try {
            byte store = CacheBehavior.STORE;
            if (force)
                store = CacheBehavior.FORCE_REFRESH;
            Documents docs = service.query("SELECT * FROM VendorSeed WHERE ecm:currentLifeCycleState = 'published' AND ecm:path STARTSWITH '/default-domain/workspaces/justvisual' AND dc:creator = '" + session.getLogin().getUsername() + "'"
                    , null, new String[]{"dc:modified DESC"}, null, 0, 200,
                    store);
            for (Iterator<Document> iterator = docs.iterator(); iterator.hasNext(); ) {
                Document document = iterator.next();
                BaseSeed seed = getSeedByUUID(document.getId());
                if (seed != null) {
                    result.add(seed);
                }
            }
        } catch (Exception e) {
            Log.w(TAG, e.getMessage());
        }
        return result;
    }

    @Override
    public BaseSeed createSeed(BaseSeed seed, File imageFile) {
        seed = super.createSeed(seed, imageFile);
        return createNuxeoVendorSeed(seed, imageFile);
    }

    /*
     * Return new remote seed or null if error
     */
    protected BaseSeed createNuxeoVendorSeed(BaseSeed currentSeed, File file) {

        Session session = getNuxeoClient().getSession();
        DocumentManager service = session.getAdapter(DocumentManager.class);
        Document catalog = null;
        try {
            DocRef wsRef = new PathRef(service.getUserHome().getPath() + "/Catalog");
            catalog = service.getDocument(wsRef);
        } catch (Exception e) {
            Log.e(TAG, "Fetching folder Catalog " + e.getMessage(), e);

            Document folder;
            try {
                Document root = service.getDocument(service.getUserHome());

                folder = service.createDocument(root, "Hut", "Catalog");
                PropertyMap map = folder.getProperties();
                map.set("dc:title", "Catalog");
                service.update(folder, map);
                catalog = folder;

                Log.d(TAG, "create folder Catalog UUID " + folder.getId());
            } catch (Exception e1) {
                Log.e(TAG, "Creating folder Catalog " + e.getMessage(), e);
            }

        }

        if (catalog == null)
            return null;

        try {
            final Document documentVendorSeed = service.createDocument(catalog, "VendorSeed", currentSeed.getVariety(),
                    NuxeoSeedConverter.convert(catalog.getPath(), currentSeed).getProperties());
            // ****************** FILE UPLOAD ***************
            if (file != null) {
                NuxeoUtils nuxeoUtils = new NuxeoUtils();
                nuxeoUtils.uploadBlob(session, documentVendorSeed, file, null);
            }
            currentSeed.setUUID(documentVendorSeed.getId());
            Log.d(TAG, "RemoteSeed UUID " + documentVendorSeed.getId());
            super.updateSeed(currentSeed);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage() + " : " + currentSeed);
        }

        return currentSeed;
    }

    protected AndroidAutomationClient getNuxeoClient() {
        return NuxeoManager.getInstance().getNuxeoClient();
    }

    @Override
    public BaseSeed addToStock(BaseSeed vendorSeed, GardenInterface garden) {

        try {
            Session session = getNuxeoClient().getSession();
            DocumentManager service = session.getAdapter(DocumentManager.class);
            // TODO Change this when garden UUID manage uuid and not path
            Document gardenFolder = service.getDocument(new IdRef(garden.getUUID()));
            Document stockFolder = service.getDocument(new PathRef(gardenFolder.getPath() + "/My Stock"));

            Document stockitem;
            int quantity = 0;
            try {
                Document seedDoc = service.getDocument(new IdRef(vendorSeed.getUUID()));
            } catch (Exception e) {
                createNuxeoVendorSeed(vendorSeed, null);
            }
            try {
                stockitem = service.getDocument(new PathRef(stockFolder.getPath() + "/" + vendorSeed.getSpecie() + " "
                        + vendorSeed.getVariety()), true);

                quantity = Integer.valueOf(stockitem.getString("stockitem:quantity"));
            } catch (Exception e) {
                Log.i(TAG, e.getMessage() + " The seed is not in f");
                stockitem = service.createDocument(stockFolder, "StockItem",
                        vendorSeed.getSpecie() + " " + vendorSeed.getVariety());
                //TODO change relation to attribute
                service.createRelation(stockitem, "http://purl.org/dc/terms/isFormatOf",
                        new PathRef(vendorSeed.getUUID()));

            }

            PropertyMap map = new PropertyMap();
            map.set("stockitem:quantity", "" + ++quantity);
            map.set("dc:title", vendorSeed.getSpecie() + " " + vendorSeed.getVariety());
            map.set("stockitem:vendorseedid", vendorSeed.getUUID());

            service.update(stockitem, map);
            vendorSeed = super.addToStock(vendorSeed, garden);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return vendorSeed;
    }

    public void updateStock(BaseSeed vendorSeed, GardenInterface garden) {

        try {
            Session session = getNuxeoClient().getSession();
            DocumentManager service = session.getAdapter(DocumentManager.class);
            // TODO Change this when garden UUID manage uuid and not path
            Document gardenFolder = service.getDocument(new IdRef(garden.getUUID()));
            Document stockFolder = service.getDocument(new PathRef(gardenFolder.getPath() + "/My Stock"));

            Document stockitem;
            int quantity = vendorSeed.getNbSachet();
            try {
                stockitem = service.getDocument(new PathRef(stockFolder.getPath() + "/" + vendorSeed.getSpecie() + " "
                        + vendorSeed.getVariety()), true);
            } catch (Exception e) {
                Log.i(TAG, e.getMessage(), e);
                stockitem = service.createDocument(stockFolder, "StockItem",
                        vendorSeed.getSpecie() + " " + vendorSeed.getVariety());
                service.createRelation(stockitem, "http://purl.org/dc/terms/isFormatOf",
                        new PathRef(vendorSeed.getUUID()));

            }

            PropertyMap map = new PropertyMap();
            map.set("stockitem:quantity", "" + quantity);
            map.set("dc:title", vendorSeed.getSpecie() + " " + vendorSeed.getVariety());
            map.set("stockitem:vendorseedid", vendorSeed.getUUID());

            service.update(stockitem, map);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }

    }

    @Override
    public BaseSeed removeToStock(BaseSeed vendorSeed, GardenInterface garden) {

        try {
            Session session = getNuxeoClient().getSession();
            DocumentManager service = session.getAdapter(DocumentManager.class);
            // TODO Change this when garden UUID manage uuid and not path
            Document gardenFolder = service.getDocument(new IdRef(garden.getUUID()));
            Document stockFolder = service.getDocument(new PathRef(gardenFolder.getPath() + "/My Stock"));

            Document stockitem;
            int quantity = 0;
            stockitem = service.getDocument(new PathRef(stockFolder.getPath() + "/" + vendorSeed.getSpecie() + " "
                    + vendorSeed.getVariety()), true);
            // stockitem = service.getDocument(new PathRef(stockFolder.getPath() + "/" + vendorSeed.getSpecie() + " "
            // + vendorSeed.getVariety()), "*");
            quantity = Integer.valueOf(stockitem.getString("stockitem:quantity"));

            if (quantity > 0) {
                PropertyMap map = new PropertyMap();
                map.set("stockitem:quantity", "" + --quantity);
                service.update(stockitem, map);
            }
            vendorSeed = super.removeToStock(vendorSeed, garden);
            // service.remove(vendorSeed.getUUID());

        } catch (Exception e) {
            Log.e(TAG, vendorSeed.toString() + "\n" + e.getMessage(), e);
        }
        return vendorSeed;
    }

    @Override
    public List<BaseSeed> getMyStock(GardenInterface garden, boolean force) {
        List<BaseSeed> mySeeds = new ArrayList<BaseSeed>();
        try {
            Session session = getNuxeoClient().getSession();
            DocumentManager service = session.getAdapter(DocumentManager.class);
            Document gardenFolder = service.getDocument(new IdRef(garden.getUUID()));
            Document stockFolder = service.getDocument(new PathRef(gardenFolder.getPath() + "/My Stock"));
            byte cacheParam = CacheBehavior.STORE;
            if (force) {
                cacheParam = (byte) (cacheParam | CacheBehavior.FORCE_REFRESH);
            }
            Documents stockItems = service.query(
                    "SELECT * FROM StockItem WHERE ecm:currentLifeCycleState != \"deleted\" AND ecm:parentId=\""
                            + stockFolder.getId() + "\"", null, new String[]{"dc:modified DESC"}, "*", 0, 50,
                    cacheParam);

            for (Iterator<Document> iterator = stockItems.iterator(); iterator.hasNext(); ) {
                // Document stockItem = service.getDocument(iterator.next(), "*");
                Document stockItem = iterator.next();
                Documents relations = service.getRelations(stockItem, "http://purl.org/dc/terms/isFormatOf", true);
                if (relations.size() >= 1) {
                    Document originalSeed = service.getDocument(relations.get(0), "*");
                    // BaseSeed seed = NuxeoSeedConverter.convert(originalSeed);
                    BaseSeed seed = getSeedByUUID(originalSeed.getId());
                    if (seed == null)
                        continue;
                    seed.setNbSachet(Integer.valueOf(stockItem.getString("stockitem:quantity")));

                    // seed = super.getSeedByUUID(seed.getUUID());
                    if (seed.getNbSachet() > 0) {
                        super.updateSeed(seed);
                        mySeeds.add(seed);
                        Log.i(TAG, "getMyStock: " + seed.toString());
                    }
                }

            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);

        }

        // service.getRelations(doc, predicate);
        return mySeeds;
    }

    @Override
    public BaseSeed updateSeed(BaseSeed seed) {
        try {
            Session session = getNuxeoClient().getSession();
            DocumentManager service = session.getAdapter(DocumentManager.class);
            Document seedDoc = service.getDocument(new IdRef(seed.getUUID()));
            service.update(new IdRef(seed.getUUID()),
                    NuxeoSeedConverter.convert(seedDoc.getParentPath(), seed).getProperties());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return super.updateSeed(seed);
    }

    @Override
    public void deleteSeed(BaseSeed vendorSeed) {
        try {
            Session session = getNuxeoClient().getSession();
            DocumentManager service = session.getAdapter(DocumentManager.class);
            service.remove(new IdRef(vendorSeed.getUUID()));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        super.deleteSeed(vendorSeed);

    }

    public LikeStatus like(BaseSeed vendorSeed, boolean cancel) {
        Blob likeStatus;
        LikeStatus likes = new LikeStatus();
        try {
            Session session = getNuxeoClient().getSession();
            DocumentManager service = session.getAdapter(DocumentManager.class);
            Document doc = service.getDocument(new IdRef(vendorSeed.getUUID()));
            if (!cancel)
                likeStatus = service.like(doc);
            else
                likeStatus = service.cancelLike(doc);

            likes = NuxeoSeedConverter.getLikeStatus(likeStatus);
            vendorSeed.setLikeStatus(likes);
            super.updateSeed(vendorSeed);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return likes;
    }

    @Override
    public synchronized String[] getArraySpecies(boolean force) {
        List<String> latinNameSpecies = new ArrayList<String>();
        try {
            Session session = getNuxeoClient().getSession();
            DocumentManager service = session.getAdapter(DocumentManager.class);
            byte cacheParam = CacheBehavior.STORE;
            if (force) {
                cacheParam = (byte) (cacheParam | CacheBehavior.FORCE_REFRESH);
            }
            Documents docSpecies = service.query(
                    "SELECT * FROM Species WHERE ecm:currentLifeCycleState != \"deleted\"", null,
                    new String[]{"species:family_uuid DESC"}, "*", 0, 50, cacheParam);
            for (Document document : docSpecies) {
                latinNameSpecies.add(document.getTitle());

            }

            // Blob blob = (Blob) session.newRequest("Directory.Entries").set("directoryName", "topic").setHeader(
            // "Content-Type", "application/json+nxrequest").setInput(null).execute();
            //
            // // Blob blobSpecie = service.getDirectoryEntries("specie", "", true);
            // Log.i(TAG, blob.toString());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        String[] arraySpecies = new String[latinNameSpecies.size()];
        return latinNameSpecies.toArray(arraySpecies);
    }

    @Override
    public SpeciesDocument getSpecies(boolean force) throws NotImplementedException {
        SpeciesDocument doc = null;
        try {
            Session session = getNuxeoClient().getSession();
            DocumentManager service = session.getAdapter(DocumentManager.class);
            byte cacheParam = CacheBehavior.STORE;
            if (force) {
                cacheParam = (byte) (cacheParam | CacheBehavior.FORCE_REFRESH);
            }
            Documents docSpecies = service.query(
                    "SELECT * FROM Species WHERE ecm:currentLifeCycleState != \"deleted\"", null,
                    new String[]{"species:family_uuid DESC"}, "*", 0, 50, cacheParam);
            if (docSpecies.size() > 0)
                doc = (SpeciesDocument) docSpecies.get(0);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return doc;
    }

    @Override
    public List<BaseSeed> getSeedBySowingMonth(int month) {
        List<BaseSeed> seedsBySowingMonth = new ArrayList<>();

        try {
            Session session = getNuxeoClient().getSession();
            DocumentManager service = session.getAdapter(DocumentManager.class);
            byte cacheParam = CacheBehavior.STORE;
            Documents docSpecies = service.query(
                    "SELECT * FROM VendorSeed WHERE ecm:currentLifeCycleState != \"deleted\" AND vendorseed:datesowingmax >= "
                            + month + " AND vendorseed:datesowingmin<=" + month + "" + QUERY_FILTER_LANGUAGE, null,
                    null, "*", 0, 50, cacheParam);
            for (Document seedDoc : docSpecies) {
                BaseSeed seed = NuxeoSeedConverter.convert(seedDoc);
                if (super.getSeedByUUID(seedDoc.getId()) == null)
                    seed = super.createSeed(seed, null);
                else
                    seed = super.getSeedByUUID(seedDoc.getId());

                seedsBySowingMonth.add(seed);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return seedsBySowingMonth;
    }

    @Override
    public List<BaseSeed> getVendorSeedsByName(String currentFilter, boolean force) {
        List<BaseSeed> remoteVendorSeeds = new ArrayList<BaseSeed>();
        List<BaseSeed> myVendorSeeds = new ArrayList<BaseSeed>();

        try {
            Session session = getNuxeoClient().getSession();
            DocumentManager service = session.getAdapter(DocumentManager.class);

            byte cacheParam = CacheBehavior.STORE;
            boolean refresh = force;
            if (refresh) {
                cacheParam = (byte) (cacheParam | CacheBehavior.FORCE_REFRESH);
                refresh = false;
            }
            Documents docs = service.query("SELECT * FROM VendorSeed WHERE ecm:currentLifeCycleState != \"deleted\""
                            + QUERY_FILTER_LANGUAGE + "AND vendorseed:variety STARTSWITH " + currentFilter, null,
                    new String[]{"dc:modified DESC"}, "*", 0, 25, cacheParam);
            for (Document document : docs) {
                BaseSeed seed = NuxeoSeedConverter.convert(document);
                Blob likeStatus = service.getLikeStatus(document);
                LikeStatus likes = NuxeoSeedConverter.getLikeStatus(likeStatus);
                if (seed != null) {
                    seed.setLikeStatus(likes);
                    remoteVendorSeeds.add(seed);
                    Log.i(TAG, seed.toString());
                } else {
                    Log.w(TAG, "Nuxeo Seed conversion problem " + document.getTitle() + "- " + document.getId());
                }

                // download custom image
                File imageFile = new File(gotsPrefs.getFilesDir(),
                        seed.getVariety().toLowerCase().replaceAll("\\s", ""));
                if (imageFile != null && !imageFile.exists()) {
                    FileBlob image = service.getBlob(document);
                    if (image != null && image.getLength() > 0)
                        FileUtilities.copy(image.getFile(), imageFile);
                }
            }
            // getNuxeoClient().shutdown();
            myVendorSeeds = synchronize(super.getVendorSeedsByName(currentFilter, force), remoteVendorSeeds);
            // myVendorSeeds = remoteVendorSeeds;
        } catch (Exception e) {
            Log.e(TAG, "getVendorSeedsByName " + e.getMessage(), e);
            myVendorSeeds = super.getVendorSeedsByName(currentFilter, force);
        }
        return myVendorSeeds;
    }
}
