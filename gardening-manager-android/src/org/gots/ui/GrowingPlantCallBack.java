package org.gots.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.view.ActionMode;
import android.view.MenuItem;

import org.gots.R;
import org.gots.seed.GotsGrowingSeedManager;
import org.gots.seed.GrowingSeed;

/**
 * Created by sfleury on 05/10/15.
 */
public class GrowingPlantCallBack extends PlantCallBack {

    private final GrowingSeed mGrowingPlant;

    public GrowingPlantCallBack(BaseGotsActivity activity, GrowingSeed growingSeedInterface, OnPlantCallBackClicked onPlantCallBackClicked) {
        super(activity, growingSeedInterface.getPlant(), onPlantCallBackClicked);
        mGrowingPlant = growingSeedInterface;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete:
                AlertDialog.Builder builder = new AlertDialog.Builder(gotsActivity);
                builder.setMessage(gotsActivity.getResources().getString(R.string.action_delete_seed)).setCancelable(false).setPositiveButton(
                        "OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                new AsyncTask<Void, Integer, Void>() {
                                    GotsGrowingSeedManager growingSeedManager;

                                    @Override
                                    protected void onPreExecute() {
                                        super.onPreExecute();
                                        growingSeedManager = GotsGrowingSeedManager.getInstance().initIfNew(gotsActivity);
                                    }

                                    @Override
                                    protected Void doInBackground(Void... params) {
                                        growingSeedManager.deleteGrowingSeed(mGrowingPlant);
                                        return null;
                                    }

                                    @Override
                                    protected void onPostExecute(Void result) {
                                        gotsActivity.showNotification(mGrowingPlant.getPlant().getName() + " has been deleted", false);
                                        if (mPlantCallBackListener != null)
                                            mPlantCallBackListener.onPlantCallBackClicked();
                                        super.onPostExecute(result);
                                    }
                                }.execute();
                                dialog.dismiss();
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                builder.show();
                mode.finish();
                return true;
            default:
                return super.onActionItemClicked(mode, item);
        }


    }
}
