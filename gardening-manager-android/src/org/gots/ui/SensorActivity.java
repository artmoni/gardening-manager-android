package org.gots.ui;

import java.util.List;

import org.gots.R;
import org.gots.allotment.adapter.ListAllotmentAdapter;
import org.gots.sensor.LocationListAdapter;
import org.gots.sensor.SensorFragment;
import org.gots.sensor.SensorListFragment;
import org.gots.sensor.SensorLoginFragment;
import org.gots.sensor.parrot.ParrotLocation;
import org.gots.sensor.parrot.ParrotSampleFertilizer;
import org.gots.sensor.parrot.ParrotSampleTemperature;
import org.gots.sensor.parrot.ParrotSensorProvider;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

        updateLocations();
    }

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
                if (gotsPrefs.getParrotToken() == null) {
                    SensorLoginFragment login = new SensorLoginFragment();
                    ft.replace(R.id.idFragmentSensor, login);
                } else {

                    SensorListFragment sensors = new SensorListFragment(result);
                    ft.replace(R.id.idFragmentSensor, sensors);
                }
                ft.commit();

            };
        }.execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
        case android.R.id.home:
//            getSupportFragmentManager().findFragmentById(R.id.idFragmentSensor);
            updateLocations();
            break;

        default:
            break;
        }
        return super.onOptionsItemSelected(item);
    }
}
