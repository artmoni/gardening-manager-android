package org.gots.allotment.provider.nuxeo;

import java.util.List;

import org.gots.allotment.provider.AllotmentProvider;
import org.gots.allotment.provider.local.LocalAllotmentProvider;
import org.gots.bean.BaseAllotmentInterface;
import org.nuxeo.android.documentprovider.LazyUpdatableDocumentsList;

import android.content.Context;

public class NuxeoAllotmentProvider extends LocalAllotmentProvider {
    protected static final String TAG = "NuxeoSeedProvider";

    private static final long TIMEOUT = 10;

    String myToken;

    String myLogin;

    String myDeviceId;

    protected String myApp;

    protected LazyUpdatableDocumentsList documentsList;

    public NuxeoAllotmentProvider(Context context) {
        super(context);
        myToken = gotsPrefs.getToken();
        myLogin = gotsPrefs.getNuxeoLogin();
        myDeviceId = gotsPrefs.getDeviceId();
        myApp = gotsPrefs.getGardeningManagerAppname();
    }

    @Override
    public BaseAllotmentInterface getCurrentAllotment() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<BaseAllotmentInterface> getMyAllotments() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BaseAllotmentInterface createAllotment(BaseAllotmentInterface allotment) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int removeAllotment(BaseAllotmentInterface allotment) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public BaseAllotmentInterface updateAllotment(BaseAllotmentInterface allotment) {
        // TODO Auto-generated method stub
        return null;
    }

}
