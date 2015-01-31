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

import org.gots.action.BaseAction;
import org.gots.action.GardeningActionInterface;
import org.gots.action.GotsActionManager;
import org.gots.action.ActionOnSeed;
import org.gots.action.bean.ReduceQuantityAction;
import org.gots.action.bean.SowingAction;
import org.gots.action.util.ActionState;
import org.gots.action.view.ActionWidget;
import org.gots.bean.BaseAllotmentInterface;
import org.gots.broadcast.BroadCastMessages;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.GotsGrowingSeedManager;
import org.gots.seed.GrowingSeed;
import org.gots.seed.SeedUtil;
import org.gots.ui.GardenActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class MySeedsListAdapter extends SeedListAdapter {

    private BaseAllotmentInterface allotment;

    public MySeedsListAdapter(Context context, BaseAllotmentInterface allotment, List<BaseSeedInterface> seeds) {
        super(context, seeds);
        this.allotment = allotment;
    }

    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = super.getView(position, convertView, parent);
        Holder holder = (Holder) vi.getTag();
        final BaseSeedInterface currentSeed = getItem(position);

        holder.seedWidgetTile.setSeed(currentSeed);
        BaseAction action = null;
        if (allotment != null) {
            // action = new SowingAction(mContext);
            GotsActionManager helper = GotsActionManager.getInstance().initIfNew(mContext);
            action = helper.getActionByName("sow");
            ActionWidget actionWidget =new ActionWidget(mContext, action);
            actionWidget.setAction(action);

            if (Calendar.getInstance().get(Calendar.MONTH) >= currentSeed.getDateSowingMin()
                    && Calendar.getInstance().get(Calendar.MONTH) <= currentSeed.getDateSowingMax())
                actionWidget.setState(ActionState.NORMAL);
            else if (Calendar.getInstance().get(Calendar.MONTH) + 1 >= currentSeed.getDateSowingMin())
                actionWidget.setState(ActionState.WARNING);
            else
                actionWidget.setState(ActionState.UNDEFINED);
            actionWidget.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    new AsyncTask<Void, Integer, GrowingSeed>() {
                        @Override
                        protected GrowingSeed doInBackground(Void... params) {
                            GotsGrowingSeedManager provider = GotsGrowingSeedManager.getInstance().initIfNew(mContext);
                            // NuxeoGrowingSeedProvider provider = new NuxeoGrowingSeedProvider(mContext);
                            GrowingSeed growingSeed = (GrowingSeed) currentSeed;
                            growingSeed.setDateSowing(Calendar.getInstance().getTime());

                            return provider.plantingSeed(growingSeed, allotment);
                        }

                        @Override
                        protected void onPostExecute(GrowingSeed seed) {
                            // notifyDataSetChanged();
                            Toast.makeText(mContext, "Sowing" + " " + SeedUtil.translateSpecie(mContext, seed),
                                    Toast.LENGTH_LONG).show();
                            mContext.sendBroadcast(new Intent(BroadCastMessages.SEED_DISPLAYLIST));
                            ((Activity) mContext).finish();
                        }
                    }.execute();

                }
            });
//            holder.actionBox.addView(actionWidget);

        } else {

            action = new ReduceQuantityAction(mContext);
            // action.setState(ActionState.NORMAL);
            ActionWidget reduceWidget = new ActionWidget(mContext, action);
            reduceWidget.setAction(action);
            final BaseAction baseActionInterface = action;
            reduceWidget.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    new AsyncTask<Void, Integer, Void>() {
                        @Override
                        protected Void doInBackground(Void... params) {
                            if (allotment != null) {
                                GardeningActionInterface action = (GardeningActionInterface) baseActionInterface;
                                action.execute(allotment, (GrowingSeed) currentSeed);
                                ((Activity) mContext).finish();
                            } else {
                                ActionOnSeed action = (ActionOnSeed) baseActionInterface;
                                action.execute((GrowingSeed) currentSeed);
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void result) {
                            // notifyDataSetChanged();
                            Toast.makeText(
                                    mContext,
                                    SeedUtil.translateAction(mContext, baseActionInterface) + " - "
                                            + SeedUtil.translateSpecie(mContext, currentSeed), Toast.LENGTH_LONG).show();
                            mContext.sendBroadcast(new Intent(BroadCastMessages.SEED_DISPLAYLIST));
                            super.onPostExecute(result);
                        }
                    }.execute();

                }
            });
            
            SowingAction sowing = new SowingAction(mContext);
            sowing.setState(ActionState.NORMAL);
            ActionWidget sowingWidget = new ActionWidget(mContext, sowing);
            sowingWidget.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, GardenActivity.class);
                    intent.putExtra(GardenActivity.SELECT_ALLOTMENT, true);
                    intent.putExtra(GardenActivity.VENDOR_SEED_ID, currentSeed.getSeedId());
                    mContext.startActivity(intent);

                }
            });
            
//            holder.actionBox.addView(reduceWidget);
//            holder.actionBox.addView(sowingWidget);
        }

        try {

            Calendar sowTime = Calendar.getInstance();
            if (sowTime.get(Calendar.MONTH) > currentSeed.getDateSowingMin())
                sowTime.set(Calendar.YEAR, sowTime.get(Calendar.YEAR) + 1);
            sowTime.set(Calendar.MONTH, currentSeed.getDateSowingMin());

            Calendar harvestTime = new GregorianCalendar();
            harvestTime.setTime(sowTime.getTime());
            harvestTime.add(Calendar.DAY_OF_MONTH, currentSeed.getDurationMin());

        } catch (Exception e) {
            // holder.seedSowingDate.setText("--");
        }

        return vi;

    }

}
