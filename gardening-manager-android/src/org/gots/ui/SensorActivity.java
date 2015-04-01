package org.gots.ui;

import org.gots.sensor.SensorChartFragment;
import org.gots.sensor.SensorLoginDialogFragment;
import org.gots.sensor.fragment.AllSensorResumeFragment;
import org.gots.sensor.fragment.SensorResumeFragment.OnSensorClickListener;
import org.gots.sensor.parrot.ParrotLocation;
import org.gots.ui.fragment.TutorialFragment;
import org.gots.R;

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
        transaction.add(R.id.idFragmentSensorContent, new TutorialFragment(R.layout.tutorial_f));
        transaction.addToBackStack(null);
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
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            break;

        default:
            break;
        }
        return super.onOptionsItemSelected(item);
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
        ft.replace(R.id.idFragmentSensorContent, new SensorChartFragment(locationSensor.getLocation_identifier()));
        ft.commit();
    }

    // @Override
    // protected boolean requireAsyncDataRetrieval() {
    // return true;
    // }
    //
    // @Override
    // protected Object retrieveNuxeoData() throws Exception {
    // ParrotSensorProvider sensorProvider = new ParrotSensorProvider(getApplicationContext());
    // List<ParrotLocation> locations = sensorProvider.getLocations();
    // return locations;
    // }
    //
    // @Override
    // protected void onNuxeoDataRetrieved(Object data) {
    // List<ParrotLocation> result = (List<ParrotLocation>) data;
    // FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    //
    // if (result.size() > 0) {
    // // SensorListFragment sensors = new SensorListFragment(result);
    // // ft.replace(R.id.idListFragment, sensors);
    // Gallery gallery = (Gallery) findViewById(R.id.idGallerySensor);
    // final LocationListAdapter locationAdapter = new LocationListAdapter(getApplicationContext(), result);
    // gallery.setAdapter(locationAdapter);
    // gallery.setOnItemClickListener(new OnItemClickListener() {
    // @Override
    // public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    // FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    // ft.setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out);
    // if (getSupportFragmentManager().getFragments().size() > 0)
    // ft.addToBackStack(null);
    // ft.replace(R.id.idFragmentSensor, new SensorChartFragment(
    // locationAdapter.getItem(position).getLocation_identifier()));
    // ft.commit();
    // }
    // });
    // } else {
    // ft.replace(R.id.idFragmentSensor, new TutorialFragment(R.layout.tutorial_f));
    // ft.commit();
    // }
    // super.onNuxeoDataRetrieved(data);
    // }
}
