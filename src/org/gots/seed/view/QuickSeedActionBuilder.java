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
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import net.londatiga.android.QuickAction;

import org.gots.R;
import org.gots.action.BaseActionInterface;
import org.gots.action.SeedActionInterface;
import org.gots.action.bean.DeleteAction;
import org.gots.action.bean.DetailAction;
import org.gots.action.bean.PhotoAction;
import org.gots.action.bean.ScheduleAction;
import org.gots.action.bean.WateringAction;
import org.gots.action.provider.local.ActionDBHelper;
import org.gots.action.provider.local.LocalActionSeedProvider;
import org.gots.action.view.ActionWidget;
import org.gots.seed.GrowingSeedInterface;
import org.gots.ui.NewActionActivity;
import org.gots.ui.TabSeedActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.BaseAdapter;

public class QuickSeedActionBuilder {

    final QuickAction quickAction;

    private View parentView;


    private Context mContext;

    public QuickSeedActionBuilder(Context context, final SeedWidget v, final BaseAdapter parentAdapter) {
        parentView = v;
        mContext = context;
        final GrowingSeedInterface seed = (GrowingSeedInterface) v.getTag();

        LocalActionSeedProvider helperActions = new LocalActionSeedProvider(mContext);
        ArrayList<BaseActionInterface> actions = helperActions.getActionsToDoBySeed(seed);

        quickAction = new QuickAction(mContext, QuickAction.HORIZONTAL);

        for (Iterator<BaseActionInterface> iterator = actions.iterator(); iterator.hasNext();) {
            BaseActionInterface baseActionInterface = iterator.next();
            if (!SeedActionInterface.class.isInstance(baseActionInterface))
                continue;
            final SeedActionInterface currentAction = (SeedActionInterface) baseActionInterface;

            ActionWidget actionWidget = new ActionWidget(mContext, currentAction);

            if (currentAction == null)
                continue;

            quickAction.addActionItem(actionWidget);
            actionWidget.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    SeedActionInterface actionItem = currentAction;
                    actionItem.execute(seed);
                    parentAdapter.notifyDataSetChanged();
                    quickAction.dismiss();
                }
            });

        }

        ScheduleAction planAction = new ScheduleAction(mContext);
        ActionWidget actionWidget = new ActionWidget(mContext, planAction);
        actionWidget.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, NewActionActivity.class);
                i.putExtra("org.gots.seed.id", seed.getGrowingSeedId());
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                mContext.startActivity(i);
                quickAction.dismiss();
            }
        });

        quickAction.addActionItem(actionWidget);

        ActionDBHelper helper = new ActionDBHelper(mContext);

        /*
         * ACTION WATERING
         */
        final WateringAction wateringAction = (WateringAction) helper.getActionByName("water");
        ActionWidget watering = new ActionWidget(mContext, wateringAction);
        watering.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SeedActionInterface actionItem = wateringAction;
                actionItem.execute(seed);
                parentAdapter.notifyDataSetChanged();
                quickAction.dismiss();

            }
        });
        quickAction.addPermanentActionItem(watering);

        /*
         * ACTION DELETE
         */
        final DeleteAction deleteAction = new DeleteAction(mContext);
        ActionWidget delete = new ActionWidget(mContext, deleteAction);
        delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(mContext.getResources().getString(R.string.action_delete_seed)).setCancelable(false).setPositiveButton(
                        "OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                SeedActionInterface actionItem = deleteAction;
                                actionItem.execute(seed);
                                parentAdapter.notifyDataSetChanged();
                                quickAction.dismiss();
                                dialog.dismiss();
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                builder.show();

            }
        });
        quickAction.addPermanentActionItem(delete);

        /*
         * ACTION PHOTO
         */
        final PhotoAction photoAction = (PhotoAction) helper.getActionByName("photo");
        ActionWidget photoWidget = new ActionWidget(mContext, photoAction);
        photoWidget.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SeedActionInterface actionItem = photoAction;
                if (PhotoAction.class.isInstance(actionItem)) {
                    File f;
                    Date now = new Date();

                    f = photoAction.getImageFile(now);
                    actionItem.setData(f.getAbsoluteFile());

                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    takePictureIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(takePictureIntent);
                    // TODO The action must not be executed if activity for result has been canceled because no image
                    // has been taken but the database get the imagefile
                    actionItem.execute(seed);

                }
                parentAdapter.notifyDataSetChanged();
                quickAction.dismiss();
            }
        });
        quickAction.addPermanentActionItem(photoWidget);

        /*
         * ACTION DETAIL
         */
        final DetailAction detail = new DetailAction(mContext);
        ActionWidget detailWidget = new ActionWidget(mContext, detail);
        detailWidget.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SeedActionInterface actionItem = detail;
                if (DetailAction.class.isInstance(actionItem)) {
                    // alert.show();
                    final Intent i = new Intent(mContext, TabSeedActivity.class);
                    i.putExtra("org.gots.seed.id", ((GrowingSeedInterface) parentView.getTag()).getGrowingSeedId());
                    i.putExtra("org.gots.seed.url", ((GrowingSeedInterface) parentView.getTag()).getUrlDescription());
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(i);
                } else {
                    actionItem.execute(seed);
                }
                parentAdapter.notifyDataSetChanged();
                quickAction.dismiss();
            }
        });
        quickAction.addPermanentActionItem(detailWidget);

    }

    public void show() {
        quickAction.show(parentView);
    }

}
