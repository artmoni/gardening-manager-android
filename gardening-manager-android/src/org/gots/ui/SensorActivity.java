package org.gots.ui;

import java.util.List;

import org.gots.R;
import org.gots.allotment.adapter.ListAllotmentAdapter;
import org.gots.inapp.GotsBillingDialog;
import org.gots.inapp.GotsPurchaseItem;
import org.gots.sensor.LocationListAdapter;
import org.gots.sensor.SensorChartFragment;
import org.gots.sensor.SensorListFragment;
import org.gots.sensor.SensorLoginDialogFragment;
import org.gots.sensor.parrot.ParrotLocation;
import org.gots.sensor.parrot.ParrotSampleFertilizer;
import org.gots.sensor.parrot.ParrotSampleTemperature;
import org.gots.sensor.parrot.ParrotSensorProvider;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class SensorActivity extends AbstractActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Parrot Flower Power");
        setContentView(R.layout.sensor);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        registerReceiver(sensorBroadcast, new IntentFilter(SensorLoginDialogFragment.EVENT_AUTHENTICATE));
    }

    BroadcastReceiver sensorBroadcast = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            updateLocations();
        }
    };

    private void updateLocations() {
        new AsyncTask<Void, Void, List<ParrotLocation>>() {

            @Override
            protected List<ParrotLocation> doInBackground(Void... params) {
                ParrotSensorProvider sensorProvider = new ParrotSensorProvider(getApplicationContext());
                List<ParrotLocation> locations = sensorProvider.getLocations();
                return locations;
            }

            protected void onPostExecute(List<ParrotLocation> result) {

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                SensorListFragment sensors = new SensorListFragment(result);
                ft.replace(R.id.idFragmentSensor, sensors);
                ft.commit();

            };
        }.execute();
    }

    @Override
    protected void onResume() {
       if (gotsPrefs.getParrotToken() == null) {
            SensorLoginDialogFragment login = new SensorLoginDialogFragment();
            login.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.CustomDialog);
            login.show(getSupportFragmentManager(), "sensor_login");
        } else
            updateLocations();

        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
        case android.R.id.home:
            // updateLocations();
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
}
