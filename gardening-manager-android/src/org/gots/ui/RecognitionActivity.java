package org.gots.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.gots.R;
import org.gots.inapp.GotsPurchaseItem;
import org.gots.nuxeo.NuxeoUtils;
import org.gots.ui.fragment.RecognitionFragment;
import org.gots.ui.fragment.RecognitionMainFragment;
import org.gots.utils.FileUtilities;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sfleury on 20/07/15.
 */
public class RecognitionActivity extends BaseGotsActivity implements RecognitionFragment.OnRecognitionFinished {
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_LOAD_IMAGE = 2;
    private static final String UPLOAD_BEGIN = "org.gots.recognition.proceed";
    private static final String UPLOAD_SUCCEED = "org.gots.recognition.uploadsucceed";
    private static final String RECOGNITION_FAILED = "org.gots.recognition.failed";
    private static final String UPLOAD_FAILED = "org.gots.recognition.uploadfailed";
    private RecognitionFragment recognitionFragment;
    private RecognitionMainFragment mainFragment;
    String picturePath = null;
    private boolean uploaded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitleBar(R.string.plant_recognition);

        if (mainFragment == null) {
            mainFragment = new RecognitionMainFragment();
            addMainLayout(mainFragment, null);
        }
        registerReceiver(broadcastReceiver, new IntentFilter(UPLOAD_BEGIN));
        registerReceiver(broadcastReceiver, new IntentFilter(UPLOAD_SUCCEED));
        registerReceiver(broadcastReceiver, new IntentFilter(UPLOAD_FAILED));
        registerReceiver(broadcastReceiver, new IntentFilter(RECOGNITION_FAILED));
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

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(UPLOAD_BEGIN)) {
                mainFragment.setMessage(getResources().getString(R.string.plant_recognition_begin));
            } else if (intent.getAction().equals(UPLOAD_SUCCEED)) {
                recognitionFragment = new RecognitionFragment();
                addContentLayout(recognitionFragment, intent.getExtras());
                mainFragment.setMessage(getResources().getString(R.string.plant_recognition_progress));
            } else if (intent.getAction().equals(UPLOAD_FAILED)) {
                mainFragment.setMessage("Please check your internet access");
            }else if (intent.getAction().equals(RECOGNITION_FAILED)) {
                mainFragment.setMessage("Try with another picture");
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        uploaded = false;
        if (data != null) {
            if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
                try {
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    Uri selectedImage = getImageUri(getApplicationContext(), photo);
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
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void uploadImage(final File imageFile) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                sendBroadcast(new Intent(UPLOAD_BEGIN));
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Void... voids) {
                seedManager.createRecognitionSeed(imageFile, new NuxeoUtils.OnBlobUpload() {
                    @Override
                    public void onUploadSuccess(Document document, File file, Serializable data) {
                        Bundle args = new Bundle();
                        args.putString(RecognitionFragment.IMAGE_PATH, file.getAbsolutePath());
                        args.putString(RecognitionFragment.DOCUMENT_ID, document.getId());
                        Intent intent = new Intent(UPLOAD_SUCCEED);
                        intent.putExtras(args);
                        sendBroadcast(intent);
                        uploaded = true;
                    }

                    @Override
                    public void onUploadFailed(String message) {
                        Intent intent = new Intent(UPLOAD_FAILED);
                        sendBroadcast(intent);
                        onRecognitionFailed("Upload image on server has failed: " + message);
                    }
                });
                return null;
            }


        }.execute();
    }

    public File getReduceFile(File originalFile) {
        FileOutputStream out = null;
        File outFile = null;
        try {
            outFile = new File(getCacheDir(), originalFile.getName() + "-400x300");
            out = new FileOutputStream(outFile);

            Bitmap bitmap = FileUtilities.decodeScaledBitmapFromSdCard(originalFile.getAbsolutePath(), 400, 300);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored

        } catch (Exception e) {
            Log.e(TAG, "setSearchImage " + e.getMessage());
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "setSearchImage finally " + e.getMessage());
            }
        }
        return outFile;
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

        if (picturePath != null && !uploaded) {
            File imageFile = getReduceFile(new File(picturePath));
            if (!imageFile.exists())
                onRecognitionFailed("File does not exists: " + imageFile.getAbsolutePath());
            uploadImage(imageFile);
        }
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
        mainFragment.setMessage("Great some plants are matching.");
        mainFragment.update();
    }

    @Override
    public void onRecognitionFailed(String message) {
        if (message != null) {
//            mainFragment.setMessage(message);
            Log.w(TAG, "onRecognitionFailed: " + message);
            sendBroadcast(new Intent(RECOGNITION_FAILED));
        }
    }

    @Override
    public void onRecognitionConfirmed(Document plantDoc) {
        Toast.makeText(this, "The plant has been published", Toast.LENGTH_LONG).show();
        getSupportFragmentManager().popBackStack();
        mainFragment.update();
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
        displayPurchaseFragment(skus);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }
}
