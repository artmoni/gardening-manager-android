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
package org.gots.seed.view;

import java.util.Calendar;

import org.gots.R;
import org.gots.context.GotsContext;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.GrowingSeed;
import org.gots.seed.SeedUtil;
import org.gots.utils.GotsProgressBar;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SeedWidget extends RelativeLayout {
    Context mContext;

    private GrowingSeed mSeed;
    private ImageView seedView;
    private GotsProgressBar progressBar;
    private TextView textProgress;

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

        progressBar = (GotsProgressBar) findViewById(R.id.idSeedGotsProgressBar);
        textProgress = (TextView) findViewById(R.id.textProgress);
        if (mSeed.getDateSowing() != null) {
            long progressTime = Calendar.getInstance().getTimeInMillis() - mSeed.getDateSowing().getTime();
            progressBar.setMax(mSeed.getDurationMin());
            final int progressValue = new Long(progressTime / 86400000).intValue();
            progressBar.setProgress(progressValue);
            textProgress.setText(String.valueOf(progressValue) + " %");
        } else {
            progressBar.setVisibility(View.INVISIBLE);
            textProgress.setVisibility(View.INVISIBLE);
        }

        seedView = (ImageView) findViewById(R.id.idSeedImage2);
        Bitmap image = SeedUtil.getSeedBitmap(getGotsContext().getServerConfig().getFilesDir(), mSeed);
        if (image != null)
            seedView.setImageBitmap(image);
        else {
//            vegetableImageRessource = SeedUtil.getSeedDrawable(getContext(), mSeed);
            seedView.setImageResource(SeedUtil.getSeedDrawable(getContext(), mSeed));
        }
//        AsyncTask<Void, Void, Bitmap> downloadImg = new AsyncTask<Void, Void, Bitmap>() {
//            ImageView seedView;
//
//            private int vegetableImageRessource;
//
//            private GotsPreferences gotsPref;
//
//            @Override
//            protected void onPreExecute() {
//                seedView = (ImageView) findViewById(R.id.idSeedImage2);
//                gotsPref = getGotsContext().getServerConfig();
//                super.onPreExecute();
//            }
//
//            @Override
//            protected Bitmap doInBackground(Void... params) {
//                Bitmap image = SeedUtil.getSeedBitmap(gotsPref.getFilesDir(), mSeed);
//
//                return image;
//            }
//
//
//
//            protected void onPostExecute(Bitmap image) {
//                Bitmap image = SeedUtil.getSeedBitmap(gotsPref.getFilesDir(), mSeed);
//                if (image != null)
//                    seedView.setImageBitmap(image);
//                else {
//                    vegetableImageRessource = SeedUtil.getSeedDrawable(getContext(), mSeed);
//                    seedView.setImageResource(vegetableImageRessource);
//                }
//            }
//        };
//        downloadImg.execute();

        invalidate();
    }

    public void setSeed(BaseSeedInterface seed) {
        this.mSeed = (GrowingSeed) seed;
        setupView();
    }

}
