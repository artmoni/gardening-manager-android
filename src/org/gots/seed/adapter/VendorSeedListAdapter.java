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
package org.gots.seed.adapter;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.gots.R;
import org.gots.action.SeedActionInterface;
import org.gots.action.bean.BuyingAction;
import org.gots.action.util.ActionState;
import org.gots.action.view.ActionWidget;
import org.gots.broadcast.BroadCastMessages;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.GrowingSeedInterface;
import org.gots.seed.view.SeedWidgetLong;
import org.gots.ui.NewSeedActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class VendorSeedListAdapter extends SeedListAdapter {

    private BuyingAction buying;

  

    public VendorSeedListAdapter(Context context, List<BaseSeedInterface> vendorSeeds) {
        super(context, vendorSeeds);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = super.getView(position, convertView, parent);
        Holder holder = (Holder)vi.getTag();
        final BaseSeedInterface currentSeed = getItem(position);
        

        
        buying = new BuyingAction(mContext);
        buying.setState(ActionState.NORMAL);
        holder.actionWidget.setAction(buying);
        holder.actionWidget.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Integer, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        SeedActionInterface action = buying;
                        action.execute((GrowingSeedInterface) currentSeed);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        // notifyDataSetChanged();
                        mContext.sendBroadcast(new Intent(BroadCastMessages.SEED_DISPLAYLIST));

                        super.onPostExecute(result);
                    }
                }.execute();

                // Toast.makeText(getContext(),
                // action.getName() + " " + currentSeed.getSpecie() + " " +
                // currentSeed.getVariety(), 30).show();
            }
        });
        // actionWidget.setOnActionItemClickListener(new
        // ActionWidget.OnActionItemClickListener() {
        //
        // @Override
        // public void onItemClick(ActionWidget source, BaseActionInterface
        // baseActionInterface) {
        // SeedActionInterface action = (SeedActionInterface)
        // baseActionInterface;
        // action.execute((GrowingSeedInterface) currentSeed);
        // Toast.makeText(getContext(),
        // action.getName() + " " + currentSeed.getSpecie() + " " +
        // currentSeed.getVariety(), 30).show();
        // notifyDataSetChanged();
        // }
        // });

        Calendar sowTime = Calendar.getInstance();
        if (sowTime.get(Calendar.MONTH) > currentSeed.getDateSowingMin())
            sowTime.set(Calendar.YEAR, sowTime.get(Calendar.YEAR) + 1);
        sowTime.set(Calendar.MONTH, currentSeed.getDateSowingMin());

        Calendar harvestTime = new GregorianCalendar();
        harvestTime.setTime(sowTime.getTime());
        harvestTime.add(Calendar.DAY_OF_MONTH, currentSeed.getDurationMin());

        holder.seedWidgetLong.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                Intent i = new Intent(mContext, NewSeedActivity.class);
                i.putExtra("org.gots.seedid", currentSeed.getSeedId());
                mContext.startActivity(i);
                return false;
            }
        });

        return vi;

    }
}
