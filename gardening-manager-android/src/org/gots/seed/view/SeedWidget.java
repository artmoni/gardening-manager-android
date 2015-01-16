/*******************************************************************************
 * Copyright (c) 2012 sfleury.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     sfleury - initial API and implementation
 ******************************************************************************/
package org.gots.seed.view;

import java.io.File;
import java.util.Calendar;

import org.gots.R;
import org.gots.context.GotsContext;
import org.gots.preferences.GotsPreferences;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.GrowingSeedInterface;
import org.gots.utils.FileUtilities;
import org.gots.utils.GotsProgressBar;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class SeedWidget extends RelativeLayout {
    Context mContext;

    private GrowingSeedInterface mSeed;

    protected GotsContext getGotsContext() {
        return GotsContext.get(mContext);
    }

    public SeedWidget(Context context) {
        super(context);
        this.mContext = context;
        initView();
    }

    public SeedWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.seed_widget, this);
    }

    protected void setupView() {
        if (mSeed == null /* || !changed */)
            return;

        setTag(mSeed);

        GotsProgressBar v1 = (GotsProgressBar) findViewById(R.id.idSeedGotsProgressBar);
        if (mSeed.getDateSowing() != null) {
            long progressTime = Calendar.getInstance().getTimeInMillis() - mSeed.getDateSowing().getTime();
            v1.setMax(mSeed.getDurationMin());
            v1.setProgress(new Long(progressTime / 86400000).intValue());
        } else {
            v1.setVisibility(View.INVISIBLE);
        }

        new AsyncTask<Void, Void, Bitmap>() {
            ImageView seedView;

            private int vegetableImageRessource;

            private GotsPreferences gotsPref;

            @Override
            protected void onPreExecute() {
                seedView = (ImageView) findViewById(R.id.idSeedImage2);
                gotsPref = getGotsContext().getServerConfig();
                super.onPreExecute();
            }

            @Override
            protected Bitmap doInBackground(Void... params) {
                File imageFile = null;
                Bitmap image = null;
                /* Check custom image for this variety */
                if (mSeed.getVariety() != null && !"".equals(mSeed.getVariety()))
                    imageFile = new File(gotsPref.getGotsExternalFileDir(),
                            mSeed.getVariety().toLowerCase().replaceAll("\\s", ""));
                if (imageFile != null && imageFile.exists()) {
                    image = FileUtilities.decodeScaledBitmapFromSdCard(imageFile.getAbsolutePath(), 100, 100);
                }
                /* Check custom image for this species */
                else if (mSeed.getSpecie() != null) {
                    imageFile = new File(gotsPref.getGotsExternalFileDir(), mSeed.getSpecie().toLowerCase().replaceAll(
                            "\\s", ""));
                    if (imageFile != null && imageFile.exists()) {
                        image = FileUtilities.decodeScaledBitmapFromSdCard(imageFile.getAbsolutePath(), 100, 100);
                    }
                }

                return image;
            }

            protected void onPostExecute(Bitmap image) {
                if (image != null)
                    seedView.setImageBitmap(image);
                else {
                    vegetableImageRessource = getSeedDrawable(getContext(), mSeed);
                    seedView.setImageResource(vegetableImageRessource);
                }
            }
        }.execute();

        // setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_line_selector));

        invalidate();
    }

    public static int getSeedDrawable(Context context, BaseSeedInterface seed) {
        int vegetableImageRessource = 0;

        if (seed.getUUID() != null)
            vegetableImageRessource = context.getResources().getIdentifier(
                    "org.gots:drawable/veget_" + seed.getUUID().toLowerCase(), null, null);

        if (vegetableImageRessource == 0 && seed.getSpecie() != null)
            vegetableImageRessource = context.getResources().getIdentifier(
                    "org.gots:drawable/specie_" + seed.getSpecie().toLowerCase().replaceAll("\\s", ""), null, null);
        else
            vegetableImageRessource = R.drawable.no_picture;
        return vegetableImageRessource;
    }

    // @Override
    // public void onClick(View v) {
    // if (!TabSeedActivity.class.isInstance(mContext) && v.getTag() != null) {
    // Intent i = new Intent(mContext, TabSeedActivity.class);
    // i.putExtra("org.gots.seed.vendorid", ((BaseSeedInterface) v.getTag()).getSeedId());
    // i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    // mContext.startActivity(i);
    // }
    // }

    public void setSeed(BaseSeedInterface seed) {
        this.mSeed = (GrowingSeedInterface) seed;
        setupView();
    }

}
