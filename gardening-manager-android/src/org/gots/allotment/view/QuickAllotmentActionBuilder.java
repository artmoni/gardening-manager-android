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
package org.gots.allotment.view;

import net.londatiga.android.QuickAction;

import org.gots.action.GotsActionManager;
import org.gots.action.bean.SowingAction;
import org.gots.action.view.ActionWidget;
import org.gots.ui.HutActivity;

import android.content.Intent;
import android.view.View;

public class QuickAllotmentActionBuilder {
    final QuickAction quickAction;

    private View parentView;

    private Integer currentAllotmentId;

    public QuickAllotmentActionBuilder(final View v, Integer allotmentId) {
        parentView = v;
        currentAllotmentId = allotmentId;
        quickAction = new QuickAction(v.getContext(), QuickAction.HORIZONTAL);

        GotsActionManager helper = GotsActionManager.getInstance().initIfNew(v.getContext());

        SowingAction sowing = (SowingAction) helper.getActionByName("sow");
        ActionWidget sowingWidget = new ActionWidget(v.getContext(), sowing);
        quickAction.addPermanentActionItem(sowingWidget);
        sowingWidget.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Intent i = new Intent(v.getContext(), MySeedsListActivity.class);
                // i.putExtra("org.gots.allotment.reference", ((Allotment)
                // v.getTag()).getName());
                // v.getContext().startActivity(i);
                // quickAction.dismiss();
                Intent i = new Intent(v.getContext(), HutActivity.class);
                i.putExtra("org.gots.allotment.reference", currentAllotmentId);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                v.getContext().startActivity(i);
            }
        });
        // sowingWidget.setOnActionItemClickListener(new
        // ActionWidget.OnActionItemClickListener() {
        // @Override
        // public void onItemClick(ActionWidget source, BaseActionInterface
        // action) {
        // Intent i = new Intent(v.getContext(), MySeedsListActivity.class);
        // i.putExtra("org.gots.allotment.reference", ((Allotment)
        // v.getTag()).getName());
        // v.getContext().startActivity(i);
        // quickAction.dismiss();
        // }
        //
        // });
//        final WateringAction watering = (WateringAction) helper.getActionByName("water");
//        ActionWidget wateringWidget = new ActionWidget(v.getContext(), watering);
//        quickAction.addPermanentActionItem(wateringWidget);
//        wateringWidget.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                GardeningActionInterface actionItem = watering;
//
//                actionItem.execute((Allotment) v.getTag(), null);
//
//                quickAction.dismiss();
//            }
//        });
        // wateringWidget.setOnActionItemClickListener(new
        // ActionWidget.OnActionItemClickListener() {

        // @Override
        // public void onItemClick(ActionWidget source, BaseActionInterface
        // action) {
        // GardeningActionInterface actionItem = (GardeningActionInterface)
        // action;
        //
        // actionItem.execute((Allotment) v.getTag(), null);
        //
        // quickAction.dismiss();
        // }
        //
        // });
    }

    public void show() {
        quickAction.show(parentView);
    }

}
