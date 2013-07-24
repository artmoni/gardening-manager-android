package org.gots.seed;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.gots.preferences.GotsPreferences;
import org.gots.seed.provider.GotsSeedProvider;
import org.gots.seed.provider.local.LocalSeedProvider;
import org.gots.seed.provider.nuxeo.NuxeoSeedProvider;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

public class GotsSeedManager implements GotsSeedProvider {

    private static final String TAG = "GotsSeedManager";

    private Context mContext;

    private GotsSeedProvider mSeedProvider;

    public GotsSeedManager(Context mContext) {
        this.mContext = mContext;
        // mLocalProvider = new LocalSeedProvider(mContext);
        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (GotsPreferences.getInstance().isConnectedToServer() && ni != null && ni.isConnected()) {
            mSeedProvider = new NuxeoSeedProvider(mContext);
        } else
            mSeedProvider = new LocalSeedProvider(mContext);
    }

    public GotsSeedManager(Context context, GotsSeedProvider gotsSeedProvider) {
        mSeedProvider = gotsSeedProvider;

    }

    @Override
    public List<BaseSeedInterface> getVendorSeeds() {

        // VendorSeedDBHelper helper = new VendorSeedDBHelper(mContext);
        // for (Iterator<BaseSeedInterface> iterator =
        // mRemoteProvider.getAllSeeds().iterator(); iterator.hasNext();) {
        // BaseSeedInterface baseSeedInterface = iterator.next();
        // if (helper.getSeedByReference(baseSeedInterface.getReference()) !=
        // null) {
        // helper.updateSeed(baseSeedInterface);
        // } else {
        // helper.insertSeed(baseSeedInterface);
        // }
        // }
        List<BaseSeedInterface> listSeeds = mSeedProvider.getVendorSeeds();
        AsyncTask<BaseSeedInterface, Integer, List<BaseSeedInterface>> task = new AsyncTask<BaseSeedInterface, Integer, List<BaseSeedInterface>>() {
            @Override
            protected List<BaseSeedInterface> doInBackground(BaseSeedInterface... params) {

                return mSeedProvider.getVendorSeeds();
            }
        }.execute();

        try {
            return task.get();
        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (ExecutionException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void getAllFamilies() {
        // TODO Auto-generated method stub

    }

    @Override
    public void getFamilyById(int id) {
        // TODO Auto-generated method stub

    }

    @Override
    public BaseSeedInterface getSeedById() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BaseSeedInterface createSeed(BaseSeedInterface seed) {
        AsyncTask<BaseSeedInterface, Integer, BaseSeedInterface> task = new AsyncTask<BaseSeedInterface, Integer, BaseSeedInterface>() {
            @Override
            protected BaseSeedInterface doInBackground(BaseSeedInterface... params) {

                return mSeedProvider.createSeed(params[0]);
            }
        }.execute(seed);
        try {
            return task.get();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            Log.e(TAG, e.getMessage(), e);
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }

    @Override
    public BaseSeedInterface updateSeed(BaseSeedInterface newSeed) {
        return mSeedProvider.updateSeed(newSeed);
    }

}
