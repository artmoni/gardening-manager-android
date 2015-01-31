/*******************************************************************************
e * Copyright (c) 2012 sfleury.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     sfleury - initial API and implementation
 ******************************************************************************/
package org.gots.seed.view;

import java.util.List;

import net.londatiga.android.QuickAction;

import org.gots.R;
import org.gots.action.BaseAction;
import org.gots.action.GotsActionManager;
import org.gots.action.GotsActionSeedManager;
import org.gots.action.ActionOnSeed;
import org.gots.action.bean.DeleteAction;
import org.gots.action.bean.DetailAction;
import org.gots.action.bean.ScheduleAction;
import org.gots.action.bean.WateringAction;
import org.gots.action.provider.GotsActionSeedProvider;
import org.gots.action.util.ActionState;
import org.gots.action.view.ActionWidget;
import org.gots.broadcast.BroadCastMessages;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.GrowingSeed;
import org.gots.ui.TabSeedActivity;
import org.gots.ui.fragment.ScheduleActionFragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.Toast;

public class QuickSeedActionBuilder {

    final QuickAction quickAction;

    private View parentView;

    private Context mContext;

    private GrowingSeed seed;

    GotsActionSeedProvider actionSeedManager;

    private GotsActionManager actionManager;

    private class ActionTask extends AsyncTask<ActionOnSeed, Integer, Void> {
        @Override
        protected Void doInBackground(ActionOnSeed... params) {
            ActionOnSeed actionItem = params[0];
            actionItem.execute(seed);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            Toast.makeText(mContext, "action done", Toast.LENGTH_SHORT).show();
            quickAction.dismiss();
            super.onPostExecute(result);
        }
    }

    public QuickSeedActionBuilder(Context context, final SeedWidget parent, GrowingSeed growingSeed) {
        parentView = parent;
        mContext = context;
        this.seed = growingSeed;
        quickAction = new QuickAction(mContext, QuickAction.HORIZONTAL);
        actionManager = GotsActionManager.getInstance().initIfNew(mContext);
        actionSeedManager = GotsActionSeedManager.getInstance().initIfNew(mContext);

        new AsyncTask<Void, Void, List<ActionOnSeed>>() {

            @Override
            protected List<ActionOnSeed> doInBackground(Void... params) {
                GotsActionSeedProvider helperActions = GotsActionSeedManager.getInstance().initIfNew(mContext);

                return helperActions.getActionsToDoBySeed(seed, false);
            }

            protected void onPostExecute(List<ActionOnSeed> actions) {
                for (BaseAction baseActionInterface : actions) {
                    if (!ActionOnSeed.class.isInstance(baseActionInterface))
                        continue;
                    final ActionOnSeed currentAction = (ActionOnSeed) baseActionInterface;

                    ActionWidget actionWidget = new ActionWidget(mContext, currentAction);
                    actionWidget.setState(currentAction.getState());

                    quickAction.addActionItem(actionWidget);
                    actionWidget.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            new ActionTask().execute(currentAction);

                        }
                    });

                }
            };
        }.execute();

        ScheduleAction planAction = new ScheduleAction(mContext);
        ActionWidget actionWidget = new ActionWidget(mContext, planAction);
        actionWidget.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Intent i = new Intent(mContext, NewActionActivity.class);
                // i.putExtra("org.gots.seed.id", seed.getGrowingSeedId());
                // i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //
                // mContext.startActivity(i);
                FragmentManager fm = ((FragmentActivity) mContext).getSupportFragmentManager();
                DialogFragment editNameDialog = new ScheduleActionFragment();
                Bundle data = new Bundle();
                data.putInt("org.gots.seed.id", seed.getGrowingSeedId());
                editNameDialog.setArguments(data);
                editNameDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
                editNameDialog.show(fm, "fragment_edit_name");
                quickAction.dismiss();
            }
        });

        quickAction.addActionItem(actionWidget);

        /*
         * ACTION DETAIL
         */
        final DetailAction detail = new DetailAction(mContext);
        ActionWidget detailWidget = new ActionWidget(mContext, detail);
        detailWidget.setState(ActionState.UNDEFINED);
        detailWidget.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ActionOnSeed actionItem = detail;
                if (DetailAction.class.isInstance(actionItem)) {
                    // alert.show();
                    final Intent i = new Intent(mContext, TabSeedActivity.class);
                    i.putExtra("org.gots.seed.id", ((GrowingSeed) parentView.getTag()).getGrowingSeedId());
                    i.putExtra("org.gots.seed.url", ((GrowingSeed) parentView.getTag()).getUrlDescription());
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mContext.startActivity(i);
                } else {
                    actionItem.execute(seed);
                }
                // parentAdapter.notifyDataSetChanged();
                quickAction.dismiss();
            }
        });
        quickAction.addPermanentActionItem(detailWidget);

        /*
         * ACTION WATERING
         */
        new AsyncTask<Void, Integer, ActionOnSeed>() {

            @Override
            protected ActionOnSeed doInBackground(Void... params) {
                WateringAction wateringAction = (WateringAction) actionManager.getActionByName("water");

                return wateringAction;
            }

            protected void onPostExecute(final ActionOnSeed action) {
                ActionWidget watering = new ActionWidget(mContext, action);
                watering.setState(ActionState.UNDEFINED);
                watering.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        new AsyncTask<ActionOnSeed, Integer, Void>() {
                            @Override
                            protected Void doInBackground(ActionOnSeed... params) {
                                ActionOnSeed actionItem = params[0];
                                actionItem.setDuration(0);
                                actionItem = actionSeedManager.insertAction(seed, (ActionOnSeed) actionItem);
                                actionSeedManager.doAction(actionItem, seed);
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void result) {
                                Toast.makeText(mContext, "action done", Toast.LENGTH_SHORT).show();
                                quickAction.dismiss();
                                super.onPostExecute(result);
                            }
                        }.execute(action);
                    }
                });
                quickAction.addPermanentActionItem(watering);

            };
        }.execute();

        /*
         * ACTION DELETE
         */
        final DeleteAction deleteAction = new DeleteAction(mContext);
        ActionWidget delete = new ActionWidget(mContext, deleteAction);
        delete.setState(ActionState.UNDEFINED);

        delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setMessage(mContext.getResources().getString(R.string.action_delete_seed)).setCancelable(false).setPositiveButton(
                        "OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                new ActionTask().execute(deleteAction);
                                mContext.sendBroadcast(new Intent(BroadCastMessages.GROWINGSEED_DISPLAYLIST));
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

        // /*
        // * ACTION PHOTO
        // */
        // new AsyncTask<Void, Integer, PhotoAction>() {
        //
        // @Override
        // protected PhotoAction doInBackground(Void... params) {
        // PhotoAction photoAction = (PhotoAction) actionManager.getActionByName("photo");
        //
        // return photoAction;
        // }
        //
        // protected void onPostExecute(final PhotoAction photoAction) {
        // ActionWidget photoWidget = new ActionWidget(mContext, photoAction);
        // photoWidget.setState(ActionState.UNDEFINED);
        //
        // photoWidget.setOnClickListener(new View.OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // SeedActionInterface actionItem = photoAction;
        // if (PhotoAction.class.isInstance(actionItem)) {
        // // alert.show();
        // final Intent i = new Intent(mContext, TabSeedActivity.class);
        // i.putExtra("org.gots.seed.id", ((GrowingSeedInterface) parentView.getTag()).getGrowingSeedId());
        // i.putExtra("org.gots.seed.actionphoto","org.gots.seed.actionphoto");
        // i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // mContext.startActivity(i);
        //
        //
        //
        // }
        // // parentAdapter.notifyDataSetChanged();
        // quickAction.dismiss();
        // }
        // });
        // quickAction.addPermanentActionItem(photoWidget);
        //
        // };
        // }.execute();

    }

    public void show() {
        quickAction.show(parentView);
    }

}
