package org.gots.authentication;

import java.util.List;

import org.gots.action.GotsActionSeedManager;
import org.gots.action.provider.GotsActionSeedProvider;
import org.gots.allotment.AllotmentManager;
import org.gots.broadcast.BroadCastMessages;
import org.gots.garden.GardenManager;
import org.gots.preferences.GotsPreferences;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.GotsGrowingSeedManager;
import org.gots.seed.GotsSeedManager;
import org.gots.seed.service.SeedNotification;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class SeedSyncAdapter extends AbstractThreadedSyncAdapter {
    private final AccountManager mAccountManager;

    private GotsPreferences gotsPrefs;

    private GotsSeedManager seedManager;

    private GardenManager gardenManager;

    private GotsActionSeedProvider actionseedManager;

    private AllotmentManager allotmentManager;

    private GotsGrowingSeedManager growingSeedManager;

    public SeedSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mAccountManager = AccountManager.get(context);

        gotsPrefs = GotsPreferences.getInstance();
        gotsPrefs.initIfNew(getContext());

        seedManager = GotsSeedManager.getInstance().initIfNew(getContext());
        gardenManager = GardenManager.getInstance().initIfNew(getContext());
        growingSeedManager = GotsGrowingSeedManager.getInstance().initIfNew(getContext());
        actionseedManager = GotsActionSeedManager.getInstance().initIfNew(getContext());
        allotmentManager = AllotmentManager.getInstance().initIfNew(getContext());

    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider,
            SyncResult syncResult) {
        Log.d("SeedSyncAdapter", "onPerformSync for account[" + account.name + "]");

        new AsyncTask<Void, Integer, List<BaseSeedInterface>>() {

            private Thread thread;

            boolean shouldcontinue = true;

            protected void onPreExecute() {
                thread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            while (shouldcontinue) {
                                getContext().sendBroadcast(new Intent(BroadCastMessages.PROGRESS_UPDATE));
                                sleep(1000);
                            }

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };

                thread.start();
                super.onPreExecute();
            };

            @Override
            protected List<BaseSeedInterface> doInBackground(Void... params) {
                seedManager.force_refresh(true);
                seedManager.getMyStock(gardenManager.getCurrentGarden());
                return seedManager.getVendorSeeds(true);

            }

            protected void onPostExecute(List<BaseSeedInterface> vendorSeeds) {

                List<BaseSeedInterface> newSeeds = seedManager.getNewSeeds();
                if (newSeeds != null && newSeeds.size() > 0) {
                    SeedNotification notification = new SeedNotification(getContext());
                    notification.createNotification(newSeeds);
                }
                // handler.removeCallbacks(sendUpdatesToUI);
                // handler.postDelayed(sendUpdatesToUI, 0); // 1 second
                shouldcontinue = false;
                getContext().sendBroadcast(new Intent(BroadCastMessages.PROGRESS_FINISHED));

                super.onPostExecute(vendorSeeds);
            };
        }.execute();

        // try {
        // // Get the auth token for the current account
        // String authToken = mAccountManager.blockingGetAuthToken(account, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS,
        // true);
        // ParseComServerAccessor parseComService = new ParseComServerAccessor();
        //
        // // Get shows from the remote server
        // List remoteTvShows = parseComService.getShows(authToken);
        //
        // // Get shows from the local storage
        // ArrayList localTvShows = new ArrayList();
        // Cursor curTvShows = provider.query(TvShowsContract.CONTENT_URI, null, null, null, null);
        // if (curTvShows != null) {
        // while (curTvShows.moveToNext()) {
        // localTvShows.add(TvShow.fromCursor(curTvShows));
        // }
        // curTvShows.close();
        // }
        // // TODO See what Local shows are missing on Remote
        //
        // // TODO See what Remote shows are missing on Local
        //
        // // TODO Updating remote tv shows
        //
        // // TODO Updating local tv shows
        //
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
    }
}
