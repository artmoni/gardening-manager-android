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
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.GrowingSeedInterface;
import org.gots.ui.TabSeedActivity;
import org.gots.utils.GotsProgressBar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class SeedWidget extends RelativeLayout implements OnClickListener {
    Context mContext;

    private GrowingSeedInterface mSeed;

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

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setupView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.seed_widget, this);
        setOnClickListener(this);
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

            @Override
            protected void onPreExecute() {
                seedView = (ImageView) findViewById(R.id.idSeedImage2);
                super.onPreExecute();
            }

            @Override
            protected Bitmap doInBackground(Void... params) {
                Bitmap image = null;
                vegetableImageRessource = getSeedDrawable(getContext(), mSeed);
                if (vegetableImageRessource == 0 && mSeed.getSpecie() != null) {
                    File file = new File(mContext.getCacheDir() + "/"
                            + mSeed.getSpecie().toLowerCase().replaceAll("\\s", ""));
                    
//                    BitmapFactory.Options options = new BitmapFactory.Options();
//                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//                    image = BitmapFactory.decodeFile(file.getPath(), options);
                    image = decodeScaledBitmapFromSdCard(file.getAbsolutePath(), seedView.getWidth(), seedView.getHeight());
                }
                return image;
            }

            protected void onPostExecute(Bitmap image) {
                if (vegetableImageRessource != 0 && seedView != null)
                    seedView.setImageResource(vegetableImageRessource);
                else
                    seedView.setImageBitmap(image);
            };
        }.execute();

        setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.action_selector));

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
        return vegetableImageRessource;
    }

    @Override
    public void onClick(View v) {
        if (!TabSeedActivity.class.isInstance(mContext) && v.getTag() != null) {
            Intent i = new Intent(mContext, TabSeedActivity.class);
            i.putExtra("org.gots.seed.vendorid", ((BaseSeedInterface) v.getTag()).getSeedId());
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(i);
        }
    }

    public void setSeed(BaseSeedInterface seed) {
        this.mSeed = (GrowingSeedInterface) seed;
        setupView();

    }

    
    public static Bitmap decodeScaledBitmapFromSdCard(String filePath,
            int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }
}
