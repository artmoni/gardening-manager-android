package org.gots.action.provider.nuxeo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import org.gots.action.ActionFactory;
import org.gots.action.BaseActionInterface;
import org.gots.action.GotsActionManager;
import org.gots.action.SeedActionInterface;
import org.gots.action.provider.local.LocalActionSeedProvider;
import org.gots.exception.GotsServerRestrictedException;
import org.gots.nuxeo.NuxeoManager;
import org.gots.seed.GrowingSeedInterface;
import org.nuxeo.android.cache.blob.BlobWithProperties;
import org.nuxeo.android.repository.DocumentManager;
import org.nuxeo.android.upload.FileUploader;
import org.nuxeo.ecm.automation.client.android.AndroidAutomationClient;
import org.nuxeo.ecm.automation.client.cache.CacheBehavior;
import org.nuxeo.ecm.automation.client.jaxrs.AsyncCallback;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.model.Blob;
import org.nuxeo.ecm.automation.client.jaxrs.model.DocRef;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;
import org.nuxeo.ecm.automation.client.jaxrs.model.FileBlob;
import org.nuxeo.ecm.automation.client.jaxrs.model.IdRef;
import org.nuxeo.ecm.automation.client.jaxrs.model.PathRef;
import org.nuxeo.ecm.automation.client.jaxrs.model.PropertyMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Environment;
import android.util.Log;

public class NuxeoActionSeedProvider extends LocalActionSeedProvider {

    protected static final String TAG = "NuxeoActionSeedProvider";

    public NuxeoActionSeedProvider(Context mContext) {
        super(mContext);
        NuxeoManager.getInstance().initIfNew(mContext);
    }

    protected AndroidAutomationClient getNuxeoClient() {
        return NuxeoManager.getInstance().initIfNew(mContext).getNuxeoClient();
    }

    @Override
    public ArrayList<SeedActionInterface> getActionsToDo() {
        return super.getActionsToDo();
    }

    @Override
    public SeedActionInterface insertAction(GrowingSeedInterface seed, BaseActionInterface action) {
        action = insertNuxeoAction(seed, action);
        return super.insertAction(seed, action);
    }

    protected SeedActionInterface insertNuxeoAction(GrowingSeedInterface seed, BaseActionInterface action) {
        Session session = getNuxeoClient().getSession();
        DocumentManager documentMgr = session.getAdapter(DocumentManager.class);
        try {
            PropertyMap properties = new PropertyMap();
            properties.set("dc:title", action.getName());
            properties.set("action:duration", String.valueOf(action.getDuration()));

            Document docAction = documentMgr.getDocument(action.getUUID());
            if (docAction != null) {

                Document doc = documentMgr.copy(docAction, getActionsFolder(seed, documentMgr));
                documentMgr.update(doc, properties);
                action.setUUID(doc.getId());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return (SeedActionInterface) action;
    }

    protected Document getActionsFolder(GrowingSeedInterface seed, DocumentManager documentMgr) throws Exception {
        boolean subFolderExists = false;
        Document actionFolder = null;
        for (Document subFolder : documentMgr.getChildren(new IdRef(seed.getUUID()))) {

            if ("Actions".equals(subFolder.getTitle())) {
                Document currentDoc = documentMgr.getDocument(subFolder);
                if ("deleted".equals(currentDoc.getState()))
                    continue;
                subFolderExists = true;
                actionFolder = subFolder;
                break;
            }
        }
        if (!subFolderExists) {
            PropertyMap properties = new PropertyMap();
            properties.set("dc:title", "Actions");
            actionFolder = documentMgr.createDocument(new IdRef(seed.getUUID()), "Folder", "Actions", properties);
        }
        return actionFolder;
    }

    @Override
    public long doAction(SeedActionInterface action, GrowingSeedInterface seed) {
        Session session = getNuxeoClient().getSession();
        DocumentManager documentMgr = session.getAdapter(DocumentManager.class);
        try {

            PropertyMap properties = new PropertyMap();
            properties.set("action:dateactiondone", Calendar.getInstance().getTime());
            properties.set("action:description", action.getDescription());
            if (action.getData() != null)
                properties.set("action:data", action.getData().toString());

            // Document newDoc = documentMgr.copy(new DocRef(action.getUUID()), getActionsFolder(seed, documentMgr));
            documentMgr.update(new DocRef(action.getUUID()), properties);

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return super.doAction(action, seed);
    }

    @Override
    public List<SeedActionInterface> getActionsToDoBySeed(GrowingSeedInterface seed) {
        Session session = getNuxeoClient().getSession();
        DocumentManager documentMgr = session.getAdapter(DocumentManager.class);
        List<SeedActionInterface> actionsToDo = new ArrayList<SeedActionInterface>();
        try {
            // for (Document actionDoc : documentMgr.getChildren(getActionsFolder(seed, documentMgr))) {
            Documents actionDocs = documentMgr.query(
                    "SELECT * FROM Action WHERE ecm:currentLifeCycleState != \"deleted\" AND ecm:parentId=\""
                            + getActionsFolder(seed, documentMgr).getId() + "\" AND action:dateactiondone is null",
                    null, new String[] { "dc:modified DESC" }, "*", 0, 50, CacheBehavior.FORCE_REFRESH);

            for (Document actionDoc : actionDocs) {
                SeedActionInterface action = convert(actionDoc);
                action = super.populateState(action, seed);
                action.setGrowingSeedId(seed.getGrowingSeedId());
                actionsToDo.add(action);
            }

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return synchronize(seed, super.getActionsToDoBySeed(seed), actionsToDo);
    }

    protected List<SeedActionInterface> synchronize(GrowingSeedInterface seed,
            List<SeedActionInterface> myLocalActions, List<SeedActionInterface> remoteActions) {
        List<SeedActionInterface> myActions = new ArrayList<SeedActionInterface>();
        // Synchronize remote actions with local gardens
        for (SeedActionInterface remoteAction : remoteActions) {
            boolean found = false;
            for (SeedActionInterface localAction : myLocalActions) {
                if (remoteAction.getUUID() != null && remoteAction.getUUID().equals(localAction.getUUID())) {
                    found = true;
                    break;
                }
            }
            if (found) { // local and remote => update local
                // TODO check if remote can be out of date
                // syncGardens(localGarden,remoteGarden);
                myActions.add(super.update(seed, remoteAction));
            } else { // remote only => create local
                // SeedActionInterface newAction = super.insertAction(seed,
                // GotsActionManager.getInstance().initIfNew(mContext).getActionByName(remoteAction.getName()));
                // remoteAction.setActionSeedId(newAction.getActionSeedId());
                myActions.add(super.insertAction(seed, remoteAction));
            }
        }

        // Create remote garden when not exist remotely and remove local
        // garden if no more referenced online
        for (SeedActionInterface localAction : myLocalActions) {
            if (localAction.getUUID() == null) { // local only without
                                                 // UUID => create
                                                 // remote
                BaseActionInterface newAction = GotsActionManager.getInstance().initIfNew(mContext).getActionByName(
                        localAction.getName());
                newAction.setDuration(localAction.getDuration());
                myActions.add(insertNuxeoAction(seed, newAction));
            } else {
                boolean found = false;
                for (SeedActionInterface remoteAction : remoteActions) {
                    if (remoteAction.getUUID() != null && remoteAction.getUUID().equals(localAction.getUUID())) {
                        found = true;
                        break;
                    }
                }
                if (!found) { // local only with UUID -> delete local
                }
            }
        }
        return myActions;
    }

    @Override
    public List<SeedActionInterface> getActionsDoneBySeed(GrowingSeedInterface seed) {
        Session session = getNuxeoClient().getSession();
        DocumentManager documentMgr = session.getAdapter(DocumentManager.class);
        ArrayList<SeedActionInterface> actionsDone = new ArrayList<SeedActionInterface>();
        try {
            Documents actionDocs = documentMgr.query(
                    "SELECT * FROM Action WHERE ecm:currentLifeCycleState != \"deleted\" AND ecm:parentId=\""
                            + getActionsFolder(seed, documentMgr).getId() + "\" AND action:dateactiondone is not null",
                    null, new String[] { "dc:modified DESC" }, "*", 0, 50, CacheBehavior.FORCE_REFRESH);

            for (Document actionDoc : actionDocs) {
                SeedActionInterface action = convert(actionDoc);
                // super.populateState(action, seed);
                action.setGrowingSeedId(seed.getGrowingSeedId());
                actionsDone.add(action);
            }

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return synchronize(seed, super.getActionsDoneBySeed(seed), actionsDone);
    }

    private SeedActionInterface convert(Document actionDoc) {
        SeedActionInterface action = (SeedActionInterface) ActionFactory.buildAction(mContext, actionDoc.getTitle());
        action.setDateActionDone(actionDoc.getDate("action:dateactiondone"));
        action.setDescription(actionDoc.getString("action:description"));
        action.setData(actionDoc.getString("action:data"));
        action.setUUID(actionDoc.getId());

        if (actionDoc.getString("action:duration") != null)
            action.setDuration(Integer.parseInt(actionDoc.getString("action:duration")));

        return action;
    }

    PropertyMap blobProp = new PropertyMap();

    protected void attachBlobToDocument(GrowingSeedInterface seed, Blob blob) {
        Session session = getNuxeoClient().getSession();
        DocumentManager documentMgr = session.getAdapter(DocumentManager.class);
        Document seedDoc;
        Document pictureBook = null;
        try {
            seedDoc = documentMgr.getDocument(new IdRef(seed.getUUID()), true);
            pictureBook = documentMgr.getDocument(new PathRef(seedDoc.getPath()) + "/Picture");
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }
        if (pictureBook == null) {
            try {
                seedDoc = documentMgr.getDocument(new IdRef(seed.getUUID()));
                PropertyMap properties = new PropertyMap();
                properties.set("dc:title", "Picture");

                pictureBook = documentMgr.createDocument(new PathRef(seedDoc.getPath()), "PictureBook", "Picture",
                        properties);
            } catch (Exception e) {
                Log.i(TAG, e.getMessage());
            }

        }

        try {
            StringBuilder toJSON = new StringBuilder("{ ");
            toJSON.append(" \"type\" : \"blob\"");
            toJSON.append(", \"length\" : " + blobProp.get("length"));
            toJSON.append(", \"mime-type\" : \"" + blobProp.get("mime-type") + "\"");
            toJSON.append(", \"name\" : \"" + blobProp.get("name") + "\"");
            toJSON.append(", \"upload-batch\" : \"" + blobProp.get("upload-batch") + "\"");
            toJSON.append(", \"upload-fileId\" : \"" + blobProp.get("upload-fileId") + "\" ");
            toJSON.append("}");
            PropertyMap properties = new PropertyMap();
            properties.set("dc:title", blobProp.getString("name"));
            properties.set("file:content", toJSON.toString());
            documentMgr.createDocument(pictureBook, "Picture", blobProp.getString("name"), properties);
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
        }

    }
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
    // Raw height and width of image
    final int height = options.outHeight;
    final int width = options.outWidth;
    int inSampleSize = 1;

    if (height > reqHeight || width > reqWidth) {

        final int halfHeight = height / 2;
        final int halfWidth = width / 2;

        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
        // height and width larger than the requested height and width.
        while ((halfHeight / inSampleSize) > reqHeight
                && (halfWidth / inSampleSize) > reqWidth) {
            inSampleSize *= 2;
        }
    }

    return inSampleSize;
}
    @Override
    public void uploadPicture(final GrowingSeedInterface seed, File imageFile) {
        Session session = getNuxeoClient().getSession();
        FileUploader uploader = session.getAdapter(FileUploader.class);

        try {

            // SCALE IMAGE TO LOWER RESOLUTION
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            Bitmap bm = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);
            options.inSampleSize = calculateInSampleSize(options, 200   , 200);
            options.inJustDecodeBounds = false;
            bm = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);

//            float scale;
//            if (bm.getWidth() > bm.getHeight()) {
//                scale = 800 / (float) bm.getWidth();
//            } else {
//                scale = 800 / (float) bm.getHeight();
//            }
//            Bitmap scaledBm = Bitmap.createScaledBitmap(bm, (int) (bm.getWidth() * scale),
//                    (int) (bm.getHeight() * scale), true);
            Bitmap scaledBm = Bitmap.createScaledBitmap(bm, (int) (bm.getWidth() ),
                    (int) (bm.getHeight() ), true);

            // ROTATE IMAGE IF NOT LANDSCAPE
            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            Matrix matrix = new Matrix();
            switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90);
                scaledBm = Bitmap.createBitmap(scaledBm, 0, 0, scaledBm.getWidth(), scaledBm.getHeight(), matrix, true);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180);
                scaledBm = Bitmap.createBitmap(scaledBm, 0, 0, scaledBm.getWidth(), scaledBm.getHeight(), matrix, true);
                break;
            }

            try {
                FileOutputStream fos = new FileOutputStream(imageFile);
//                scaledBm.compress(Bitmap.CompressFormat.PNG, 90, fos);
                bm.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }

            String mString = "Your message here";
            exif.setAttribute("UserComment", mString);
            exif.saveAttributes();

            final FileBlob blobToUpload = new FileBlob(imageFile);
            blobToUpload.setMimeType("image/jpeg");

            String batchId = String.valueOf(new Random().nextInt(1000));
            final String fileId = blobToUpload.getFileName();

            BlobWithProperties blobUploading = uploader.storeAndUpload(batchId, fileId, blobToUpload,
                    new AsyncCallback<Serializable>() {

                        @Override
                        public void onSuccess(String executionId, Serializable data) {
                            attachBlobToDocument(seed, blobToUpload);
                            Log.i(TAG, "success");
                        }

                        @Override
                        public void onError(String executionId, Throwable e) {
                            Log.i(TAG, "errdroior");

                        }
                    });

            String uploadUUID = blobUploading.getProperty(FileUploader.UPLOAD_UUID);

            Log.i(TAG, "Started blob upload UUID " + uploadUUID);
            blobProp.set("type", "blob");
            blobProp.set("length", Long.valueOf(blobUploading.getLength()));
            blobProp.set("mime-type", blobUploading.getMimeType());
            blobProp.set("name", blobToUpload.getFileName());
            // set information for server side Blob mapping
            blobProp.set("upload-batch", batchId);
            blobProp.set("upload-fileId", fileId);
            // set information for the update query to know it's
            // dependencies
            blobProp.set("android-require-type", "upload");
            blobProp.set("android-require-uuid", uploadUUID);

        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        // super.uploadPicture(seed);
    }

    @Override
    public List<File> getPicture(GrowingSeedInterface mSeed) {
        Session session = getNuxeoClient().getSession();
        DocumentManager documentMgr = session.getAdapter(DocumentManager.class);
        Document seedDoc;
        List<File> imageFiles = new ArrayList<File>();
        try {
            seedDoc = documentMgr.getDocument(new IdRef(mSeed.getUUID()));
            Document pictureBook = documentMgr.getDocument(new PathRef(seedDoc.getPath() + "/Picture"), "*");
            Documents pictureList = documentMgr.query(
                    "SELECT * FROM Picture WHERE ecm:currentLifeCycleState != \"deleted\" AND ecm:parentId=\""
                            + pictureBook.getId() + "\"", null, new String[] { "dc:modified DESC" }, "*", 0, 50,
                    CacheBehavior.FORCE_REFRESH);
            for (Document doc : pictureList) {
                Log.i(TAG, doc.getName());

                doc = documentMgr.getDocument(doc, "*");
                // get the file content property
                PropertyMap map = doc.getProperties().getMap("file:content");
                // get the data URL
                String path = map.getString("data");
                FileBlob blob = (FileBlob) session.getFile(path);
                imageFiles.add(blob.getFile());
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return imageFiles;
    }

    @Override
    public File downloadHistory(GrowingSeedInterface mSeed) throws GotsServerRestrictedException {
        Session session = getNuxeoClient().getSession();
        DocumentManager service = session.getAdapter(DocumentManager.class);
        try {
            Document doc = service.getDocument(new DocRef(mSeed.getUUID()));
            FileBlob blob = (FileBlob) session.newRequest("seedGrowingActionHistory").setInput(doc).execute();
            File dir = new File(Environment.getExternalStorageDirectory(), "Gardening-Manager");
            File pdfFile = new File(dir, blob.getFileName().replaceAll(" ", "-"));
            copy(blob.getFile(), pdfFile);
            blob.getFile().delete();
            if (pdfFile.exists())
                return pdfFile;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }

    public void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

}
