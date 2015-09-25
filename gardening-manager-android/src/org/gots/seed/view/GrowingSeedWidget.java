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

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.gots.R;
import org.gots.context.GotsContext;
import org.gots.seed.GrowingSeed;
import org.gots.seed.SeedUtil;
import org.gots.utils.GotsProgressBar;

import java.util.Calendar;

public class GrowingSeedWidget extends RelativeLayout {
    Context mContext;

    private GrowingSeed mSeed;
    private ImageView seedView;
    private GotsProgressBar progressBar;
    private TextView textProgress;

    protected GotsContext getGotsContext() {
        return GotsContext.get(mContext);
    }

    public GrowingSeedWidget(Context context) {
        super(context);
        this.mContext = context;
        initView();
    }

    public GrowingSeedWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.seed_widget, this);
    }

    protected void setupView() {
        if (mSeed == null || mSeed.getPlant() == null)
            return;

        setTag(mSeed);

        progressBar = (GotsProgressBar) findViewById(R.id.idSeedGotsProgressBar);
        textProgress = (TextView) findViewById(R.id.textProgress);

        long progressTime = Calendar.getInstance().getTimeInMillis() - mSeed.getDateSowing().getTime();
        progressBar.setMax(mSeed.getPlant().getDurationMin());
        final int progressValue = new Long(progressTime / 86400000).intValue();
        progressBar.setProgress(progressValue);
        textProgress.setText(String.valueOf(progressValue) + " %");

        seedView = (ImageView) findViewById(R.id.idSeedImage2);
        Bitmap image = SeedUtil.getSeedBitmap(getGotsContext().getServerConfig().getFilesDir(), mSeed.getPlant());
        if (image != null)
            seedView.setImageBitmap(image);
        else {
            seedView.setImageResource(SeedUtil.getSeedDrawable(getContext(), mSeed.getPlant()));
        }

        invalidate();
    }

    public void setSeed(GrowingSeed seed) {
        this.mSeed = seed;
        setupView();
    }

}
