package org.gots.ui.fragment;

import java.util.Calendar;
import java.util.List;

import org.gots.R;
import org.gots.action.GotsActionSeedManager;
import org.gots.action.SeedActionInterface;
import org.gots.action.adapter.ListAllActionAdapter;
import org.gots.action.provider.GotsActionSeedProvider;
import org.gots.ads.GotsAdvertisement;
import org.gots.garden.GotsGardenManager;
import org.gots.inapp.GotsPurchaseItem;
import org.gots.nuxeo.NuxeoWorkflowProvider;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.GotsSeedManager;
import org.gots.seed.adapter.SeedListAdapter;
import org.gots.seed.adapter.VendorSeedListAdapter;
import org.gots.ui.ActionActivity;
import org.gots.ui.HutActivity;
import org.gots.ui.TabSeedActivity;
import org.gots.weather.view.WeatherView;
import org.gots.weather.view.WeatherWidget;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class DashboardResumeFragment extends BaseGotsFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.dashboard_resume, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        if (GotsGardenManager.getInstance().initIfNew(getActivity()).getCurrentGarden() == null) {
            // Intent intent = new Intent(getActivity(), ProfileCreationActivity.class);
            // startActivity(intent);
        } else {
//            displaySeeds();
            displayActions();
//            displayWeather();
            displayIncredible();

            GotsPurchaseItem gotsPurchase = new GotsPurchaseItem(getActivity());
            if (!gotsPurchase.isPremium()) {
                GotsAdvertisement ads = new GotsAdvertisement(getActivity());

                LinearLayout layout = (LinearLayout) getView().findViewById(R.id.idAdsTop);
                layout.addView(ads.getAdsLayout());
            }
        }
        super.onViewCreated(view, savedInstanceState);
    }

    protected void displayIncredible() {
        GotsGardenManager gardenManager = GotsGardenManager.getInstance().initIfNew(getActivity());
        if (gardenManager.getCurrentGarden().isIncredibleEdible()) {
            getView().findViewById(R.id.layoutIncredibleDescription).setVisibility(View.VISIBLE);
        } else
            getView().findViewById(R.id.layoutIncredibleDescription).setVisibility(View.GONE);

    }

    @Override
    protected void onCurrentGardenChanged() {
//        displaySeeds();
        displayActions();
//        displayWeather();
        displayIncredible();
    }

    @Override
    protected void onWeatherChanged() {
//        displayWeather();
    }

    @Override
    protected void onActionChanged() {
        displayActions();

    }

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
                if (isAdded()) {
                    ListAllActionAdapter actionAdapter = new ListAllActionAdapter(getActivity(), listActions.subList(0,
                            listActions.size() >= 5 ? 5 : listActions.size()), ListAllActionAdapter.STATUS_TODO);
                    listViewActions.setAdapter(actionAdapter);

                    getView().findViewById(R.id.buttonActions).setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(getActivity(), ActionActivity.class));
                        }
                    });
                    if (actionAdapter.getCount() > 0) {
                        getView().findViewById(R.id.layoutDashboardActions).setVisibility(View.VISIBLE);
                        // getView().findViewById(R.id.layoutDashboardActions).setLayoutParams(
                        // new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        // LinearLayout.LayoutParams.WRAP_CONTENT));
                    } else
                        getView().findViewById(R.id.layoutDashboardActions).setVisibility(View.GONE);

                }
                super.onPostExecute(listActions);
            }
        }.execute();

    }

//    protected void displaySeeds() {
//
//        new AsyncTask<Void, Void, List<BaseSeedInterface>>() {
//            Gallery gallery;
//
//            GotsSeedManager gotsSeedManager = GotsSeedManager.getInstance().initIfNew(getActivity());
//
//            @Override
//            protected void onPreExecute() {
//                gallery = (Gallery) getView().findViewById(R.id.gallery1);
//                super.onPreExecute();
//            }
//
//            @Override
//            protected List<BaseSeedInterface> doInBackground(Void... params) {
//                return gotsSeedManager.getSeedBySowingMonth(Calendar.getInstance().get(Calendar.MONTH) + 1);
//            }
//
//            @Override
//            protected void onPostExecute(List<BaseSeedInterface> list) {
//                if (isAdded()) {
//                    SeedListAdapter adapter = new VendorSeedListAdapter(getActivity(), list.subList(0,
//                            list.size() >= 5 ? 5 : list.size()));
//                    gallery.setAdapter(adapter);
//                    gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                        @Override
//                        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
//                            BaseSeedInterface baseSeedInterface = (BaseSeedInterface) arg0.getItemAtPosition(arg2);
//                            Intent i = new Intent(getActivity(), TabSeedActivity.class);
//                            i.putExtra(TabSeedActivity.GOTS_VENDORSEED_ID, baseSeedInterface.getSeedId());
//                            startActivity(i);
//                        }
//                    });
//
//                    gallery.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//                        @Override
//                        public boolean onItemLongClick(final AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
//                            new AsyncTask<Void, Void, Void>() {
//                                @Override
//                                protected Void doInBackground(Void... params) {
//                                    NuxeoWorkflowProvider nuxeoWorkflowProvider = new NuxeoWorkflowProvider(
//                                            getActivity());
//                                    BaseSeedInterface baseSeedInterface = (BaseSeedInterface) arg0.getItemAtPosition(arg2);
//                                    // nuxeoWorkflowProvider.startWorkflowValidation(baseSeedInterface);
//                                    nuxeoWorkflowProvider.getDocumentsRoute(baseSeedInterface);
//                                    return null;
//                                }
//                            }.execute();
//                            return false;
//                        }
//                    });
//                    getView().findViewById(R.id.buttonHut).setOnClickListener(new View.OnClickListener() {
//
//                        @Override
//                        public void onClick(View v) {
//                            startActivity(new Intent(getActivity(), HutActivity.class));
//                        }
//                    });
//                }
//                super.onPostExecute(list);
//            }
//        }.execute();
//
//    }

//    protected void displayWeather() {
//        // boolean isError = intent.getBooleanExtra("error", false);
//
//        LinearLayout weatherWidgetLayout = (LinearLayout) getView().findViewById(R.id.WeatherWidget);
//        weatherWidgetLayout.removeAllViews();
//
//        GotsGardenManager gardenManager = GotsGardenManager.getInstance().initIfNew(getActivity());
//        TextView descriptionWeather = (TextView) getView().findViewById(R.id.textViewWeatherDescription);
//        descriptionWeather.setText(gardenManager.getCurrentGarden().getLocality());
//        // if (isError) {
//        // TextView txtError = new TextView(this);
//        // txtError.setText(getResources().getText(R.string.weather_citynotfound));
//        // txtError.setTextColor(getResources().getColor(R.color.text_color_light));
//        // handle.addView(txtError);
//        // Log.d(TAG, "WeatherWidget display error");
//        //
//        // } else {
//        // weatherWidget2 = new WeatherWidget(getActivity(), WeatherView.IMAGE);
//        // handle.addView(weatherWidget2);
//        WeatherWidget weatherWidget = new WeatherWidget(getActivity(), WeatherView.FULL);
//        weatherWidgetLayout.addView(weatherWidget);
//        // }
//
//    }

    @Override
    protected boolean requireAsyncDataRetrieval() {
        return false;
    }
}
