package org.gots.action;

import java.util.ArrayList;

import org.gots.action.provider.GotsActionSeedProvider;
import org.gots.action.provider.local.LocalActionSeedProvider;
import org.gots.seed.GrowingSeedInterface;
import org.gots.utils.NotConfiguredException;

import android.content.Context;

public class GotsActionSeedManager implements GotsActionSeedProvider {

    private static GotsActionSeedManager instance;

    private static Exception firstCall;

    GotsActionSeedProvider provider;

    private boolean initDone;

    private Context mContext;

    

    public static synchronized GotsActionSeedManager getInstance() {
        if (instance == null) {
            instance = new GotsActionSeedManager();
            firstCall = new Exception();

        } else if (!instance.initDone) {
            throw new NotConfiguredException(firstCall);
        }
        return instance;
    }

    public synchronized GotsActionSeedManager initIfNew(Context context) {
        if (initDone) {
            return this;
        }
        this.mContext = context;
        // mContext.registerReceiver(this, new IntentFilter(BroadCastMessages.CONNECTION_SETTINGS_CHANGED));
        setProvider();
        initDone = true;
        return this;
    }

    public void setProvider() {
        provider = new LocalActionSeedProvider(mContext);
    }

    @Override
    public long doAction(BaseActionInterface action, GrowingSeedInterface seed) {
        return provider.doAction(action, seed);
    }

    @Override
    public ArrayList<BaseActionInterface> getActionsToDo() {
        return provider.getActionsToDo();
    }

    @Override
    public ArrayList<BaseActionInterface> getActionsToDoBySeed(GrowingSeedInterface seed) {
        return provider.getActionsToDoBySeed(seed);
    }

    @Override
    public ArrayList<BaseActionInterface> getActionsDoneBySeed(GrowingSeedInterface seed) {
        return provider.getActionsDoneBySeed(seed);
    }

    @Override
    public long insertAction(BaseActionInterface action, GrowingSeedInterface seed) {
        // TODO Auto-generated method stub
        return 0;
    }

}
