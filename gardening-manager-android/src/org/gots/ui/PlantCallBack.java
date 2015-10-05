package org.gots.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import org.gots.R;
import org.gots.seed.BaseSeed;
import org.gots.seed.SeedUtil;

/**
 * Created by sfleury on 04/10/15.
 */
class PlantCallBack implements ActionMode.Callback {

    protected final OnPlantCallBackClicked mPlantCallBackListener;
    protected BaseGotsActivity gotsActivity;
    private BaseSeed currentSeed;

    public interface OnPlantCallBackClicked {
        public void onPlantCallBackClicked();
    }

    PlantCallBack(BaseGotsActivity gotsActivity, BaseSeed seedInterface, OnPlantCallBackClicked plantCallBackClicked) {
        this.gotsActivity = gotsActivity;
        currentSeed = seedInterface;
        mPlantCallBackListener = plantCallBackClicked;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        if (currentSeed.getNbSachet() == 0)
            menu.findItem(R.id.action_stock_reduce).setVisible(false);

        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.menu_hut_contextual, menu);
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_stock_add:
                new AsyncTask<Void, Integer, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        gotsActivity.seedManager.addToStock(currentSeed, gotsActivity.getCurrentGarden());
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        gotsActivity.showNotification(SeedUtil.translateSpecie(gotsActivity.getApplicationContext(), currentSeed) + " +1 " + gotsActivity.getResources().getString(R.string.seed_action_stock_description), false);
                        if (mPlantCallBackListener != null)
                            mPlantCallBackListener.onPlantCallBackClicked();
                        super.onPostExecute(result);
                    }
                }.execute();
                break;

            case R.id.action_stock_reduce:
                new AsyncTask<Void, Integer, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        gotsActivity.seedManager.removeToStock(currentSeed, gotsActivity.getCurrentGarden());
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        gotsActivity.showNotification(SeedUtil.translateSpecie(gotsActivity.getApplicationContext(), currentSeed) + " -1 " + gotsActivity.getResources().getString(R.string.seed_action_stock_description), false);
                        if (mPlantCallBackListener != null)
                            mPlantCallBackListener.onPlantCallBackClicked();
                        super.onPostExecute(result);
                    }
                }.execute();
                break;
            case R.id.delete:

                AlertDialog.Builder builder = new AlertDialog.Builder(gotsActivity);
                builder.setMessage(gotsActivity.getResources().getString(R.string.action_delete_seed)).setCancelable(false).setPositiveButton(
                        "OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                new AsyncTask<Void, Integer, Void>() {
                                    @Override
                                    protected Void doInBackground(Void... params) {
                                        gotsActivity.seedManager.deleteSeed(currentSeed);
                                        return null;
                                    }

                                    @Override
                                    protected void onPostExecute(Void result) {
                                        gotsActivity.showNotification(currentSeed.getName() + " has been deleted", false);
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
                break;
            default:
                break;
        }


        mode.finish();
        return true;
    }
}
