package org.gots.ui.fragment;

import java.util.Calendar;
import java.util.List;

import org.gots.R;
import org.gots.action.GotsActionManager;
import org.gots.action.GotsActionSeedManager;
import org.gots.action.SeedActionInterface;
import org.gots.action.adapter.ListAllActionAdapter;
import org.gots.action.provider.GotsActionSeedProvider;
import org.gots.broadcast.BroadCastMessages;
import org.gots.garden.GardenManager;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.GotsSeedManager;
import org.gots.seed.adapter.SeedListAdapter;
import org.gots.seed.adapter.VendorSeedListAdapter;
import org.gots.ui.ActionActivity;
import org.gots.ui.HutActivity;
import org.gots.weather.view.WeatherView;
import org.gots.weather.view.WeatherWidget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class DashboardResumeFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(BroadCastMessages.GARDEN_CURRENT_CHANGED));
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(BroadCastMessages.ACTION_EVENT));

        return inflater.inflate(R.layout.dashboard_resume, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        displaySeeds(view);
        displayActions();
        displayWeather();
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BroadCastMessages.GARDEN_CURRENT_CHANGED.equals(intent.getAction())) {
                displaySeeds(getView());
                displayActions();
            } else if (BroadCastMessages.ACTION_EVENT.equals(intent.getAction())) {
                displayActions();
            }
        }
    };

    protected void displayActions() {

        new AsyncTask<Void, Void, List<SeedActionInterface>>() {
            ListView listViewActions;

            GotsActionSeedProvider actionSeedManager = GotsActionSeedManager.getInstance().initIfNew(getActivity());

            @Override
            protected void onPreExecute() {
                listViewActions = (ListView) getView().findViewById(R.id.listActions);
                super.onPreExecute();
            }

            @Override
            protected List<SeedActionInterface> doInBackground(Void... params) {
                return actionSeedManager.getActionsToDo();
            }

            @Override
            protected void onPostExecute(List<SeedActionInterface> listActions) {
                ListAllActionAdapter actionAdapter = new ListAllActionAdapter(getActivity(), listActions.subList(0,
                        listActions.size() >= 5 ? 5 : listActions.size()), ListAllActionAdapter.STATUS_TODO);
                listViewActions.setAdapter(actionAdapter);

                getView().findViewById(R.id.buttonActions).setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getActivity(), ActionActivity.class));
                    }
                });
                if (actionAdapter.getCount() > 0)
                    getView().findViewById(R.id.layoutDashboardActions).setVisibility(View.VISIBLE);
                super.onPostExecute(listActions);
            }
        }.execute();

    }

    protected void displaySeeds(View view) {

        new AsyncTask<Void, Void, List<BaseSeedInterface>>() {
            Gallery gallery;

            GotsSeedManager gotsSeedManager = GotsSeedManager.getInstance().initIfNew(getActivity());

            @Override
            protected void onPreExecute() {
                gallery = (Gallery) getView().findViewById(R.id.gallery1);
                super.onPreExecute();
            }

            @Override
            protected List<BaseSeedInterface> doInBackground(Void... params) {
                return gotsSeedManager.getSeedBySowingMonth(Calendar.getInstance().get(Calendar.MONTH));
            }

            @Override
            protected void onPostExecute(List<BaseSeedInterface> list) {
                if (isAdded()) {
                    SeedListAdapter adapter = new VendorSeedListAdapter(getActivity(), list.subList(0,
                            list.size() >= 5 ? 5 : list.size()));
                    gallery.setAdapter(adapter);
                    getView().findViewById(R.id.buttonHut).setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(getActivity(), HutActivity.class));
                        }
                    });
                }
                super.onPostExecute(list);
            }
        }.execute();

    }

    protected void displayWeather() {
        // boolean isError = intent.getBooleanExtra("error", false);

        LinearLayout weatherWidgetLayout = (LinearLayout) getView().findViewById(R.id.WeatherWidget);
        weatherWidgetLayout.removeAllViews();

        GardenManager gardenManager = GardenManager.getInstance().initIfNew(getActivity());
        TextView descriptionWeather = (TextView) getView().findViewById(R.id.textViewWeatherDescription);
        descriptionWeather.setText(gardenManager.getCurrentGarden().getLocality());
        // if (isError) {
        // TextView txtError = new TextView(this);
        // txtError.setText(getResources().getText(R.string.weather_citynotfound));
        // txtError.setTextColor(getResources().getColor(R.color.text_color_light));
        // handle.addView(txtError);
        // Log.d(TAG, "WeatherWidget display error");
        //
        // } else {
        // weatherWidget2 = new WeatherWidget(getActivity(), WeatherView.IMAGE);
        // handle.addView(weatherWidget2);
        WeatherWidget weatherWidget = new WeatherWidget(getActivity(), WeatherView.FULL);
        weatherWidgetLayout.addView(weatherWidget);
        // }

    }
}
