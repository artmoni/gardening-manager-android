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

import org.gots.R;
import org.gots.preferences.GotsPreferences;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.GrowingSeedInterface;
import org.gots.seed.SeedUtil;
import org.gots.seed.adapter.PlanningHarvestAdapter;
import org.gots.seed.adapter.PlanningSowAdapter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SeedWidgetLong extends LinearLayout  {
    Context mContext;

    private GrowingSeedInterface mSeed;

    public SeedWidgetLong(Context context) {
        super(context);
        this.mContext = context;
        initView();

    }

    public SeedWidgetLong(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.seed_widget_long, this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setupView();
    }

    private void setupView() {

        if (mSeed == null)
            return;

        if (mSeed.getFamily() != null) {
            int familyImageRessource = getResources().getIdentifier(
                    "org.gots:drawable/family_" + mSeed.getFamily().toLowerCase(), null, null);
           
            if (familyImageRessource != 0)
                setBackgroundResource(familyImageRessource);
            else 
                setBackground(mContext.getResources().getDrawable(R.drawable.family_unknown));
        }

        SeedWidget seedWidget = (SeedWidget) findViewById(R.id.idSeedWidget2);
        seedWidget.setSeed(mSeed);

        TextView seedSpecie = (TextView) findViewById(R.id.IdSeedSpecie);
        seedSpecie.setText(SeedUtil.translateSpecie(mContext, mSeed));
        if (GotsPreferences.DEBUG)
            seedSpecie.setText("("+mSeed.getSeedId()+")"+SeedUtil.translateSpecie(mContext, mSeed));

        TextView seedVariety = (TextView) findViewById(R.id.IdSeedVariety);
        seedVariety.setText(mSeed.getVariety());
        if (GotsPreferences.DEBUG &&  mSeed.getUUID()!=null)
            seedVariety.setText(mSeed.getVariety() + "\n" + mSeed.getUUID().substring(0, 10));

        PlanningWidget planningSow = (PlanningWidget) findViewById(R.id.IdSeedSowingPlanning);
        planningSow.setAdapter(new PlanningSowAdapter(mSeed));
        //
        PlanningWidget planningHarvest = (PlanningWidget) findViewById(R.id.IdSeedHarvestPlanning);
        planningHarvest.setAdapter(new PlanningHarvestAdapter(mSeed));

        LinearLayout stock = (LinearLayout) findViewById(R.id.idSeedStock);
        stock.removeAllViews();
        for (int i = 0; i < mSeed.getNbSachet(); i++) {
            ImageView seedbag = new ImageView(mContext);
            seedbag.setImageDrawable(mContext.getResources().getDrawable(R.drawable.seed_bag));
            // seedbag.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_planning_sow));

            LayoutParams params = new LinearLayout.LayoutParams(30, 30);
            seedbag.setLayoutParams(params);
            stock.addView(seedbag, i);
        }
        
        
    }

    // public static String unAccent(String s) {
    // //
    // // JDK1.5
    // // use sun.text.Normalizer.normalize(s, Normalizer.DECOMP, 0);
    // //
    // String temp = Normalizer.normalize(s, Normalizer.Form.NFD);
    // Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
    // return pattern.matcher(temp).replaceAll("");
    // }

   

    //
    // @Override
    // public void onClick(View v) {
    //
    // setTag(mSeed);
    // QuickSeedActionBuilder actionBuilder = new
    // QuickSeedActionBuilder((SeedWidget)v);
    // actionBuilder.show();
    // }

    public void setSeed(BaseSeedInterface seed) {
        this.mSeed = (GrowingSeedInterface) seed;
        setupView();
//        invalidate();
//        requestLayout();
//        refreshDrawableState();
    }

}
