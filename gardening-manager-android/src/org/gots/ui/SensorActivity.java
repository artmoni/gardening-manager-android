package org.gots.ui;

import org.gots.R;
import org.gots.sensor.SensorChartFragment;
import org.gots.sensor.SensorLoginDialogFragment;
import org.gots.sensor.fragment.AllSensorResumeFragment;
import org.gots.sensor.fragment.SensorResumeFragment.OnSensorClickListener;
import org.gots.sensor.parrot.ParrotLocation;
import org.gots.ui.fragment.TutorialFragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

public class SensorActivity extends BaseGotsActivity implements OnSensorClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Parrot Flower Power");
        setContentView(R.layout.sensor);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        registerReceiver(sensorBroadcast, new IntentFilter(SensorLoginDialogFragment.EVENT_AUTHENTICATE));

        FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
        AllSensorResumeFragment allSensorResumeFragment = new AllSensorResumeFragment();
        transaction2.setCustomAnimations(R.anim.push_left_in, R.anim.push_right_out);
        transaction2.replace(R.id.idFragmentSensorList, allSensorResumeFragment).commit();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.idFragmentSensorContent, new TutorialFragment(R.layout.tutorial_f));
//        transaction.addToBackStack(null);
        transaction.commit();
    }

    BroadcastReceiver sensorBroadcast = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (SensorLoginDialogFragment.EVENT_AUTHENTICATE.equals(intent.getAction())) {
                runAsyncDataRetrieval();
            }
        }
    };

    @Override
    protected void onResume() {
        if (gotsPrefs.getParrotToken() == null) {
            SensorLoginDialogFragment login = new SensorLoginDialogFragment();
            login.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.CustomDialog);
            login.show(getSupportFragmentManager(), "sensor_login");
        }

        super.onResume();
    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(sensorBroadcast);
        super.onDestroy();
    }

    @Override
    public void OnSensorClick(ParrotLocation locationSensor) {

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out);
        ft.addToBackStack(null);
        ft.replace(R.id.idFragmentSensorContent, new SensorChartFragment(locationSensor));
        ft.commit();
    }

    @Override
    protected boolean requireFloatingButton() {
        return false;
    }

}
