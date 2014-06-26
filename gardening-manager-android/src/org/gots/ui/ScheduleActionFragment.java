package org.gots.ui;

import java.util.Calendar;
import java.util.List;

import org.gots.R;
import org.gots.action.BaseActionInterface;
import org.gots.action.GotsActionManager;
import org.gots.action.GotsActionSeedManager;
import org.gots.action.adapter.SimpleListActionAdapter;
import org.gots.action.provider.GotsActionSeedProvider;
import org.gots.seed.GotsGrowingSeedManager;
import org.gots.seed.GrowingSeedInterface;
import org.gots.seed.view.SeedWidgetLong;

import android.content.Context;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class ScheduleActionFragment extends DialogFragment implements OnItemClickListener, OnClickListener {

    Integer[] list = new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };

    private GridView listActions;

    private GrowingSeedInterface mySeed;

    BaseActionInterface selectedAction;

    private Spinner spinner;

    private RadioGroup radioGroup;

    private String TAG = "NewActionActivity";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.inputaction, container);
        // ActionBar bar = getSupportActionBar();
        getDialog().setTitle(getResources().getString(R.string.action_planning));
        // bar.setDisplayHomeAsUpEnabled(true);
        listActions = (GridView) v.findViewById(R.id.idListAction);

        new AsyncTask<String, Void, List<BaseActionInterface>>() {
            private GotsActionManager helper;

            protected void onPreExecute() {

                helper = GotsActionManager.getInstance().initIfNew(getActivity());
            };

            @Override
            protected List<BaseActionInterface> doInBackground(String... params) {
                List<BaseActionInterface> actions = helper.getActions(false);

                return actions;
            }

            @SuppressWarnings("deprecation")
            protected void onPostExecute(List<BaseActionInterface> actions) {

                WindowManager wm = (WindowManager) getActivity().getApplicationContext().getSystemService(
                        Context.WINDOW_SERVICE);
                Display display = wm.getDefaultDisplay();
                int width;
                int sdk = android.os.Build.VERSION.SDK_INT;
                if (sdk < android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    width=display.getWidth();
                }
                else{
                    Point size = new Point();
                    display.getSize(size);
                    width= size.x;
                }

                int layoutsize = 200;
                int nbcolumn = (width - 200) / layoutsize;
                listActions.setNumColumns(nbcolumn);
                listActions.setAdapter(new SimpleListActionAdapter(actions));
                listActions.setOnItemClickListener(ScheduleActionFragment.this);
            };
        }.execute();

        // listActions.setNumColumns(listActions.getCount());

        // listActions.invalidate();

        spinner = (Spinner) v.findViewById(R.id.spinnerDuration);
        spinner.setAdapter(new ArrayAdapter<Integer>(getActivity(), android.R.layout.simple_spinner_item, list));
        if (getArguments() != null) {
            Bundle args = getArguments();
            Integer seedId = args.getInt("org.gots.seed.id");
            GotsGrowingSeedManager growingSeedManager = GotsGrowingSeedManager.getInstance().initIfNew(getActivity());
            mySeed = growingSeedManager.getGrowingSeedById(seedId);

            SeedWidgetLong seed = (SeedWidgetLong) v.findViewById(R.id.seedWidgetLong);
            seed.setSeed(mySeed);
        }

        radioGroup = (RadioGroup) v.findViewById(R.id.radioGroupSelectDuration);

        Button validate = (Button) v.findViewById(R.id.buttonPlanAction);
        validate.setOnClickListener(this);
        return v;

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.buttonPlanAction:
            scheduleAction();
            break;
        case R.id.idListAction:
            Log.i("listAction", "" + ((GridView) v).getCheckedItemPosition());
            break;
        default:
            break;
        }
    }

    private void scheduleAction() {
        if (mySeed == null) {
            Log.e(TAG, "seed is null");
            return;
        }
        int duration = (Integer) spinner.getSelectedItem();

        int radioButtonID = radioGroup.getCheckedRadioButtonId();
        View radioButton = radioGroup.findViewById(radioButtonID);
        int idx = radioGroup.indexOfChild(radioButton);

        switch (radioButtonID) {
        case R.id.radioWeek:
            duration = duration * 7;
            break;
        case R.id.radioMonth:
            duration = duration * 30;
            break;

        default:
            break;
        }

        Calendar sowingdate = Calendar.getInstance();
        sowingdate.setTime(mySeed.getDateSowing());

        Calendar today = Calendar.getInstance();
        int durationorig = today.get(Calendar.DAY_OF_YEAR) - sowingdate.get(Calendar.DAY_OF_YEAR);
        duration += durationorig;

        if (selectedAction == null) {
            Toast.makeText(getActivity(), "Please select an action", Toast.LENGTH_LONG).show();
            // AlertDialog alert = new AlertDialog(this);
            // AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            // builder.setMessage("Please select an action").setCancelable(false).setPositiveButton("OK",
            // new DialogInterface.OnClickListener() {
            // public void onClick(DialogInterface dialog, int id) {
            // }
            // }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            // public void onClick(DialogInterface dialog, int id) {
            // dialog.cancel();
            // // finish();
            // }
            // });

        } else {
            selectedAction.setDuration(duration);

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    GotsActionSeedProvider actionHelper = GotsActionSeedManager.getInstance().initIfNew(getActivity());
                    actionHelper.insertAction(mySeed, selectedAction);
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    // NewActionActivity.this.finish();
                    GoogleAnalyticsTracker.getInstance().trackEvent(getClass().getSimpleName(), "NewAction",
                            selectedAction.getName(), 0);
                    dismiss();
                    super.onPostExecute(result);
                }
            }.execute();

        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        selectedAction = (BaseActionInterface) listActions.getItemAtPosition(arg2);
        // listActions.setSelection(arg2);
        arg1.setSelected(!arg0.isSelected());
    }
}
