package org.gots.ui.fragment;

import org.gots.broadcast.BroadCastMessages;
import org.nuxeo.android.fragments.BaseNuxeoFragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

public abstract class BaseGotsFragment extends BaseNuxeoFragment {

    public BaseGotsFragment() {
        super();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(BroadCastMessages.GARDEN_CURRENT_CHANGED));
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(BroadCastMessages.ACTION_EVENT));
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(BroadCastMessages.WEATHER_DISPLAY_EVENT));

    }

    protected abstract void onCurrentGardenChanged();

    protected abstract void onWeatherChanged();

    protected abstract void onActionChanged();

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BroadCastMessages.GARDEN_CURRENT_CHANGED.equals(intent.getAction())) {
                onCurrentGardenChanged();
            } else if (BroadCastMessages.WEATHER_DISPLAY_EVENT.equals(intent.getAction())) {
                onWeatherChanged();
            } else if (BroadCastMessages.ACTION_EVENT.equals(intent.getAction())) {
                onActionChanged();
            }
        }
    };

}