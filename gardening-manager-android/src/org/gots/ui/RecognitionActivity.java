package org.gots.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.android.vending.billing.util.Purchase;

import org.gots.R;
import org.gots.inapp.GotsBillingDialog;
import org.gots.inapp.GotsPurchaseItem;
import org.gots.inapp.OnPurchaseFinished;
import org.gots.justvisual.ImageRecognition;
import org.gots.justvisual.JustVisualResult;
import org.gots.justvisual.OnRecognitionFinished;
import org.gots.nuxeo.NuxeoUtils;
import org.gots.ui.fragment.RecognitionFragment;
import org.gots.ui.fragment.RecognitionMainFragment;
import org.nuxeo.android.repository.DocumentManager;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by sfleury on 20/07/15.
 */
public class RecognitionActivity extends BaseGotsActivity implements OnRecognitionFinished, NuxeoUtils.OnBlobUpload {
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_LOAD_IMAGE = 2;
    private RecognitionFragment recognitionFragment;
    private GotsPurchaseItem gotsPurchaseItem;
    private RecognitionMainFragment mainFragment;
    private File reduceFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitleBar(R.string.plant_recognition);

        gotsPurchaseItem = new GotsPurchaseItem(getApplicationContext());

        if (mainFragment == null) {
            mainFragment = new RecognitionMainFragment();
            addMainLayout(mainFragment, null);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null)
            if ((requestCode == REQUEST_TAKE_PHOTO || requestCode == REQUEST_LOAD_IMAGE) && resultCode == RESULT_OK) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                reduceFile = NuxeoUtils.getReduceFile(getApplicationContext(), new File(picturePath));

                new NuxeoAsyncTask() {
                    @Override
                    protected Object doInBackground(Void... arg0) {
                        if (reduceFile != null) {
                            Session session = getNuxeoContext().getNuxeoClient().getSession();
                            DocumentManager service = session.getAdapter(DocumentManager.class);
                            try {

                                Document rootFolder = service.getDocument("/default-domain/workspaces/justvisual");
                                final Document imageDoc = service.createDocument(rootFolder, "VendorSeed", reduceFile.getName());
                                if (imageDoc != null) {
                                    NuxeoUtils.uploadBlob(session, imageDoc, reduceFile, RecognitionActivity.this);
                                    Log.d(TAG, "Uploading " + imageDoc.getTitle() + " - " + reduceFile.getAbsolutePath());
                                }
                            } catch (Exception e) {
                                Log.w(TAG, e.getMessage());
                                return e.getMessage();
                            }
                        }

                        return super.doInBackground(arg0);
                    }
                }.execute();
//                displayRecognitionResult(picturePath);
//                runAsyncDataRetrieval();
            }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void displayRecognitionResult(Map<String, List<JustVisualResult>> results) {
        if (recognitionFragment == null) {
            recognitionFragment = new RecognitionFragment();
            addContentLayout(recognitionFragment, null);
        }
        recognitionFragment.setData(reduceFile, results);
    }

    @Override
    protected boolean requireFloatingButton() {
        return true;
    }

    @Override
    protected boolean requireAsyncDataRetrieval() {
        return true;
    }

    @Override
    protected Object retrieveNuxeoData() throws Exception {

        return "";
    }

    @Override
    public void onUploadSuccess(Document document, Serializable data) {
        final String serverImageUrl = "http://my.gardening-manager.com/nuxeo/nxfile/default/" + document.getId() + "/blobholder:0/";
        ImageRecognition imageRecognition = new ImageRecognition();

        Map<String, List<JustVisualResult>> listMap = imageRecognition.queryRecognition(serverImageUrl, document);
        displayRecognitionResult(listMap);

    }


    @Override
    public void onUploadError(Document document, String message) {
        Log.w(TAG, "onUploadError: " + message);
    }

    @Override
    protected List<FloatingItem> onCreateFloatingMenu() {
        List<FloatingItem> floatingItems = new ArrayList<>();
        if (gotsPurchaseItem.getFeatureRecognitionCounter() > 0) {
            FloatingItem floatingItem = new FloatingItem();
            floatingItem.setTitle(getResources().getString(R.string.action_photo));
            floatingItem.setRessourceId(R.drawable.action_photo);
            floatingItem.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                    }
                }
            });
            floatingItems.add(floatingItem);

            FloatingItem libraryItem = new FloatingItem();
            libraryItem.setTitle(getResources().getString(R.string.action_photo_pick));
            libraryItem.setRessourceId(R.drawable.ic_flower_library);
            libraryItem.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, REQUEST_LOAD_IMAGE);
                }
            });
            floatingItems.add(libraryItem);
        } else {
            FloatingItem floatingItem = new FloatingItem();
            floatingItem.setTitle(getResources().getString(R.string.inapp_purchase_buy));
            floatingItem.setRessourceId(R.drawable.action_buy_online);
            floatingItem.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    displayPremiumFragment();
                }
            });
            floatingItems.add(floatingItem);
        }
        return floatingItems;
    }

    public void displayPremiumFragment() {
        FragmentManager fm = getSupportFragmentManager();
        final GotsBillingDialog editNameDialog = new GotsBillingDialog(GotsPurchaseItem.SKU_FEATURE_RECOGNITION_50);
        editNameDialog.addSKUFeature(GotsPurchaseItem.SKU_TEST_PURCHASE, true);
        editNameDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
        editNameDialog.show(fm, "fragment_edit_name");
        editNameDialog.setOnPurchasedFinishedListener(new OnPurchaseFinished() {
            @Override
            public void onPurchaseFailed(Purchase sku) {
//                            if (sku != null)
//                                editNameDialog.consumePurchase(sku);

            }

            @Override
            public void onPurchaseSucceed(Purchase sku) {
                if (GotsPurchaseItem.SKU_TEST_PURCHASE.equals(sku)) {
                    gotsPurchaseItem.setFeatureRecognitionCounter(gotsPurchaseItem.getFeatureRecognitionCounter() + 50);
                    editNameDialog.consumePurchase(sku);
                    mainFragment.update();
                }
            }
        });
    }

    @Override
    public void onRecognitionSucceed() {
        gotsPurchaseItem.decrementRecognitionDailyCounter();
//        mainFragment.update();
//        recognitionFragment.update();
//        runAsyncDataRetrieval();
    }

    @Override
    public void onRecognitionFailed(String message) {
        if (message != null)
            Log.w(TAG, "onRecognitionFailed: " + message);
    }

    @Override
    public void onRecognitionConfirmed(Document plantDoc) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_recognition, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.premium:
                displayPremiumFragment();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
