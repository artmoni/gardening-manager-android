package org.gots.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.gots.R;
import org.gots.inapp.GotsPurchaseItem;
import org.gots.seed.BaseSeedInterface;
import org.gots.ui.fragment.RecognitionFragment;
import org.gots.ui.fragment.RecognitionMainFragment;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sfleury on 20/07/15.
 */
public class RecognitionActivity extends BaseGotsActivity implements RecognitionFragment.OnRecognitionFinished {
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_LOAD_IMAGE = 2;
    private RecognitionFragment recognitionFragment;
    private RecognitionMainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitleBar(R.string.plant_recognition);

        if (mainFragment == null) {
            mainFragment = new RecognitionMainFragment();
            addMainLayout(mainFragment, null);
        }

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String picturePath = null;

        if (data != null) {
            if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
                try {
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    Uri selectedImage = getImageUri(getApplicationContext(), photo);
//                String[] filePathColumn = {MediaStore.Images.Media.DATA};
//
//                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
//                Log.d(TAG, DatabaseUtils.dumpCursorToString(cursor));
//                cursor.moveToFirst();
//
//                int columnIndex = cursor.getColumnIndexOrThrow(filePathColumn[0]);
//                picturePath = cursor.getString(columnIndex);
//                Toast.makeText(getApplicationContext(), picturePath != null ? picturePath : "null", Toast.LENGTH_LONG).show();
                    File finalFile = new File(getRealPathFromURI(selectedImage));
                    picturePath = finalFile.getAbsolutePath();
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            } else if (requestCode == REQUEST_LOAD_IMAGE && resultCode == RESULT_OK) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                Log.d(TAG, DatabaseUtils.dumpCursorToString(cursor));
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndexOrThrow(filePathColumn[0]);
                picturePath = cursor.getString(columnIndex);
            }
        }
        if (picturePath != null) {
            recognitionFragment = new RecognitionFragment();
            Bundle args = new Bundle();
            args.putString(RecognitionFragment.IMAGE_PATH, picturePath);
            addContentLayout(recognitionFragment, args);
        }

        super.onActivityResult(requestCode, resultCode, data);
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
    protected void onNuxeoDataRetrieved(Object data) {
        if (mainFragment != null)
            mainFragment.update();
        super.onNuxeoDataRetrieved(data);
    }

    @Override
    protected List<FloatingItem> onCreateFloatingMenu() {
        List<FloatingItem> floatingItems = new ArrayList<>();
        if (gotsPurchase.getFeatureRecognitionCounter() + gotsPurchase.getFeatureRecognitionFreeCounter() > 0) {
            FloatingItem floatingItem = new FloatingItem();
            floatingItem.setTitle(getResources().getString(R.string.action_photo));
            floatingItem.setRessourceId(R.drawable.action_photo);
            floatingItem.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent takePictureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
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
                    openPurchaseFragment();
                }
            });
            floatingItems.add(floatingItem);
        }
        return floatingItems;
    }

    @Override
    public void onRecognitionSucceed() {
        gotsPurchase.decrementRecognitionDailyCounter();
        mainFragment.update();
    }

    @Override
    public void onRecognitionFailed(String message) {
        if (message != null)
            Log.w(TAG, "onRecognitionFailed: " + message);
    }

    @Override
    public void onRecognitionConfirmed(Document plantDoc) {
        getSupportFragmentManager().popBackStack();
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
                openPurchaseFragment();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void openPurchaseFragment() {
        List<String> skus = new ArrayList<>();
        skus.add(GotsPurchaseItem.SKU_FEATURE_RECOGNITION_50);
        skus.add(GotsPurchaseItem.SKU_TEST_PURCHASE);
        displayPremiumFragment(skus);
    }
}
