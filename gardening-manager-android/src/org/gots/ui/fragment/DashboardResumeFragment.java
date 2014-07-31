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
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.GotsSeedManager;
import org.gots.seed.adapter.SeedListAdapter;
import org.gots.seed.adapter.VendorSeedListAdapter;
import org.gots.ui.ActionActivity;
import org.gots.ui.HutActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Gallery;
import android.widget.ListView;
import android.widget.TextView;

public class DashboardResumeFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter(BroadCastMessages.GARDEN_CURRENT_CHANGED));

        return inflater.inflate(R.layout.dashboard_resume, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        displaySeeds(view);

        displayActions(view);
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
                displayActions(getView());
            }
        }
    };

    protected void displayActions(final View view) {

        new AsyncTask<Void, Void, List<SeedActionInterface>>() {
            ListView galleryActions;

            GotsActionSeedProvider actionSeedManager = GotsActionSeedManager.getInstance().initIfNew(getActivity());

            @Override
            protected void onPreExecute() {
                galleryActions = (ListView) view.findViewById(R.id.listActions);
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
                galleryActions.setAdapter(actionAdapter);
                view.findViewById(R.id.buttonActions).setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getActivity(), ActionActivity.class));
                    }
                });
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
                SeedListAdapter adapter = new VendorSeedListAdapter(getActivity(), list.subList(0,
                        list.size() >= 5 ? 5 : list.size()));
                gallery.setAdapter(adapter);

                getView().findViewById(R.id.buttonHut).setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(getActivity(), HutActivity.class));
                    }
                });
                super.onPostExecute(list);
            }
        }.execute();

    }
}
