package org.gots.seed;

import java.util.List;

import org.gots.preferences.GotsPreferences;
import org.gots.seed.provider.GotsSeedProvider;
import org.gots.seed.provider.local.LocalSeedProvider;
import org.gots.seed.provider.nuxeo.NuxeoSeedProvider;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class GotsSeedManager implements GotsSeedProvider {

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

        return listSeeds;
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
        return mSeedProvider.createSeed(seed);
    }

    @Override
    public BaseSeedInterface updateSeed(BaseSeedInterface newSeed) {
        return mSeedProvider.updateSeed(newSeed);
    }

}
