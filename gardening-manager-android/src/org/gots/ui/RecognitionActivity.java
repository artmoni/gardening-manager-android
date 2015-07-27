package org.gots.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.android.vending.billing.util.Purchase;

import org.gots.R;
import org.gots.inapp.GotsBillingDialog;
import org.gots.inapp.GotsPurchaseItem;
import org.gots.inapp.OnPurchaseFinished;
import org.gots.ui.fragment.RecognitionFragment;
import org.gots.ui.fragment.RecognitionMainFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sfleury on 20/07/15.
 */
public class RecognitionActivity extends BaseGotsActivity implements RecognitionFragment.OnRecognitionFinished {
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_LOAD_IMAGE = 2;
    private RecognitionFragment recognitionFragment;
    private GotsPurchaseItem gotsPurchaseItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitleBar(R.string.plant_recognition);
        recognitionFragment = new RecognitionFragment();
        gotsPurchaseItem = new GotsPurchaseItem(getApplicationContext());

        addMainLayout(new RecognitionMainFragment(), null);

//        SearchByPicture();
    }

    private void SearchByPicture() {
//        if (gotsPurchaseItem.getFeatureRecognitionCounter() > 0) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, getCacheDir() + "/_tmp");
        startActivityForResult(cameraIntent, REQUEST_TAKE_PHOTO);
//        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null)
            if (requestCode == REQUEST_TAKE_PHOTO || requestCode == REQUEST_LOAD_IMAGE) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);

                recognitionFragment.setSearchImage(picturePath);
                addContentLayout(recognitionFragment, null);
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
    protected List<FloatingItem> onCreateFloatingMenu() {
        List<FloatingItem> floatingItems = new ArrayList<>();
        if (gotsPurchaseItem.getFeatureRecognitionCounter() > 0) {
            FloatingItem floatingItem = new FloatingItem();
            floatingItem.setTitle(getResources().getString(R.string.action_photo));
            floatingItem.setRessourceId(R.drawable.action_photo);
            floatingItem.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    SearchByPicture();
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
                                runAsyncDataRetrieval();
                            }
                        }
                    });
                }
            });
            floatingItems.add(floatingItem);
        }
        return floatingItems;
    }

    @Override
    public void onRecognitionSucceed() {
        gotsPurchaseItem.decrementRecognitionDailyCounter();
    }
}
