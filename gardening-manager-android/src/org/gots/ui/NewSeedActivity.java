/**
 * ****************************************************************************
 * Copyright (c) 2012 sfleury.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * <p>
 * Contributors:
 * sfleury - initial API and implementation
 * ****************************************************************************
 */
package org.gots.ui;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.gots.R;
import org.gots.seed.BaseSeed;
import org.gots.seed.BaseSeedImpl;
import org.gots.ui.fragment.PlanningFragment;
import org.gots.ui.fragment.PlantCreationFragment;
import org.gots.ui.fragment.SeedContentFragment;
import org.gots.ui.fragment.SeedDescriptionEditFragment;
import org.gots.ui.fragment.SpeciesFragment;
import org.gots.ui.fragment.VarietyFragment;
import org.gots.utils.FileUtilities;
import org.gots.utils.SelectOrTakePictureDialogFragment.PictureSelectorListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NewSeedActivity extends BaseGotsActivity implements OnClickListener, PictureSelectorListener,
        SeedContentFragment.OnSeedUpdated {
    public static final String ORG_GOTS_SEED_BARCODE = "org.gots.seed.barcode";

    public static final String ORG_GOTS_SEEDID = "org.gots.seedid";

    private static final String SELECTED_SPECIE = "selectedSpecie";


    private AutoCompleteTextView autoCompleteVariety;

    private Gallery gallerySpecies;

    private BaseSeed newSeed;

    private TextView textViewBarCode;

    private boolean isNewSeed = true;


    private ImageView pictureSelectorView;

    public static final int REQUEST_SCAN = 0;

    public static final int REQUEST_LOAD_IMAGE = 5000;

    public static final int REQUEST_TAKE_PHOTO = 6000;

    private String picturePath;


    private VarietyFragment varietyFragment;
    private SpeciesFragment speciesFragment;
    private PlanningFragment planninfFragment;
    private SeedDescriptionEditFragment descriptionFragment;
    private FloatingActionButton buttonNext;
    private List<SeedContentFragment> breadcrum;
    private int step = 0;
    private FloatingActionButton buttonPrevious;
    private PlantCreationFragment finalFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitleBar(R.string.seed_register_title);

        setContentView(R.layout.seed_new);
        if (getIntent().getIntExtra(ORG_GOTS_SEEDID, -1) != -1) {
            newSeed = seedManager.getSeedById(getIntent().getIntExtra(ORG_GOTS_SEEDID, -1));
            isNewSeed = false;

        } else {
            newSeed = new BaseSeedImpl();
        }

        if (getIntent().getStringExtra(ORG_GOTS_SEED_BARCODE) != null)
            newSeed.setBareCode(getIntent().getStringExtra(ORG_GOTS_SEED_BARCODE));

        finalFragment = new PlantCreationFragment();
        finalFragment.setSeed(newSeed);

        speciesFragment = new SpeciesFragment();
        speciesFragment.setSeed(newSeed);
        varietyFragment = new VarietyFragment();
        varietyFragment.setSeed(newSeed);
        planninfFragment = new PlanningFragment();
        planninfFragment.setSeed(newSeed);
        descriptionFragment = new SeedDescriptionEditFragment();
        descriptionFragment.setSeed(newSeed);

        breadcrum = new ArrayList<>();
        breadcrum.add(speciesFragment);
        breadcrum.add(varietyFragment);
        breadcrum.add(planninfFragment);
        breadcrum.add(descriptionFragment);

        buttonNext = new FloatingActionButton(getApplicationContext());
        buttonNext.setSize(FloatingActionButton.SIZE_NORMAL);
        buttonNext.setColorNormalResId(R.color.action_error_color);
        buttonNext.setColorPressedResId(R.color.action_warning_color);
        buttonNext.setIcon(R.drawable.ic_next);
        buttonNext.setTitle("Next");

        buttonNext.setStrokeVisible(false);
        buttonNext.setOnClickListener(this);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        buttonNext.setLayoutParams(params);
        ViewGroup root = (ViewGroup) getWindow().getDecorView().findViewById(android.R.id.content);
        ((ViewGroup) root.getChildAt(0)).addView(buttonNext);


        buttonPrevious = new FloatingActionButton(getApplicationContext());
        buttonPrevious.setSize(FloatingActionButton.SIZE_NORMAL);
        buttonPrevious.setColorNormalResId(R.color.action_error_color);
        buttonPrevious.setColorPressedResId(R.color.action_warning_color);
        buttonPrevious.setIcon(R.drawable.ic_previous);
        buttonPrevious.setTitle("Next");

        buttonPrevious.setStrokeVisible(false);
        buttonPrevious.setOnClickListener(this);
        buttonPrevious.setVisibility(View.GONE);

        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params2.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params2.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        buttonPrevious.setLayoutParams(params2);
        ((ViewGroup) root.getChildAt(0)).addView(buttonPrevious);


        addMainLayout(finalFragment, null);
        addContentLayout(breadcrum.get(step), null);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (gallerySpecies != null)
            outState.putInt(SELECTED_SPECIE, gallerySpecies.getSelectedItemPosition());
    }

    protected BaseSeed createOrUpdateSeed() {
        if (isNewSeed) {
            if (picturePath != null) {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                Bitmap bitmap = FileUtilities.decodeScaledBitmapFromSdCard(picturePath, 100, 100);
                bitmap.compress(CompressFormat.PNG, 0 /* ignored for PNG */, bos);
                byte[] bitmapdata = bos.toByteArray();

                // write the bytes in file
                FileOutputStream fos;
                try {
                    fos = new FileOutputStream(new File(gotsPrefs.getFilesDir(),
                            newSeed.getVariety().toLowerCase().replaceAll("\\s", "")));
                    fos.write(bitmapdata);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                newSeed = seedManager.createSeed(newSeed, new File(picturePath));
            } else
                newSeed = seedManager.createSeed(newSeed, null);
        } else
            newSeed = seedManager.updateSeed(newSeed);
        // seedManager.attach
//        return seedManager.addToStock(newSeed, getCurrentGarden());
        return newSeed;
    }



    @Override
    public void onClick(View v) {
        finalFragment.setSeed(newSeed);
        finalFragment.update();
        if (v == buttonNext) {
            step++;
            if (step > 0)
                buttonPrevious.setVisibility(View.VISIBLE);
            if (step == breadcrum.size()-1) {
                buttonNext.setIcon(R.drawable.ic_validate);
            }
            if (step == breadcrum.size()) {
                if (validateSeed()) {
                    new AsyncTask<Void, Void, BaseSeed>() {
                        @Override
                        protected BaseSeed doInBackground(Void... voids) {
                            return createOrUpdateSeed();
                        }

                        @Override
                        protected void onPostExecute(BaseSeed baseSeed) {
                            if (baseSeed != null) {
                                showNotification("Excellent your plant has been created",false);
                                NewSeedActivity.this.finish();
                            } else{
                                showNotification("There was a problem creating your plant", false);
                            }

                            super.onPostExecute(baseSeed);
                        }
                    }.execute();
                }

            }
            if (step < breadcrum.size())
                addContentLayout(breadcrum.get(step), null);

        } else if (v == buttonPrevious) {
            step--;
            if (step < breadcrum.size()-1) {
                buttonNext.setIcon(R.drawable.ic_next);
            }
            if (step == 0) {
                buttonPrevious.setVisibility(View.GONE);
            }
            if (step >= 0) {
                getSupportFragmentManager().popBackStack();
            }

        }
    }


    @SuppressWarnings("deprecation")
    private boolean validateSeed() {
//        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
//            findViewById(R.id.layoutSpecieGallery).setBackground(null);
//            findViewById(R.id.autoCompleteTextViewVariety).setBackground(null);
//        } else {
//            findViewById(R.id.layoutSpecieGallery).setBackgroundDrawable(null);
//            findViewById(R.id.autoCompleteTextViewVariety).setBackgroundDrawable(null);
//        }
        // findViewById(R.id.layoutInputSowing).setBackground(null);

        if (newSeed.getSpecie() == null || "".equals(newSeed.getSpecie())) {
            showNotification(getResources().getString(R.string.fillfields_specie),false);
            return false;
        }
        if (newSeed.getVariety() == null || "".equals(newSeed.getVariety())) {
            showNotification(getResources().getString(R.string.fillfields_variety), false);

            return false;
        }
        if (newSeed.getDateSowingMin() == -1 || newSeed.getDateSowingMax() == -1) {
            showNotification(getResources().getString(R.string.fillfields_dates), false);

            return false;
        }

        return true;
    }


    private void scanBarCode() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;
        if ((requestCode == REQUEST_LOAD_IMAGE || requestCode == REQUEST_TAKE_PHOTO) && resultCode == RESULT_OK
                && null != data) {

            if (!validateSeed()) {
                showNotification( "Seed must be validable to execute this action",false);
                return;
            }
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);

            Bitmap bitmap = FileUtilities.decodeScaledBitmapFromSdCard(picturePath, 100, 100);

            ImageView image = (ImageView) findViewById(R.id.imageNewVariety);
            image.setImageBitmap(bitmap);

            cursor.close();

        } else {
            IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (scanResult != null && scanResult.getContents() != "") {
                Log.i("Scan result", scanResult.toString());
                textViewBarCode.setText(scanResult.getContents());
                newSeed.setBareCode(textViewBarCode.getText().toString());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_newseed, menu);
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.inputseed);
    }


    @Override
    public void onSelectInGallery(DialogFragment fragment) {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, REQUEST_LOAD_IMAGE);
    }

    @Override
    public void onTakePicture(DialogFragment fragment) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, getCacheDir() + "_tmp");

        startActivityForResult(cameraIntent, REQUEST_TAKE_PHOTO);
    }


    @Override
    protected boolean requireFloatingButton() {
        return false;
    }

    @Override
    public void onSeedUpdated(BaseSeed seed) {
        finalFragment.setSeed(seed);
        finalFragment.update();
    }
}
