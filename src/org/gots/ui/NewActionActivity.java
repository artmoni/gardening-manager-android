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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class NewActionActivity extends AbstractActivity implements OnItemClickListener, OnClickListener {

    Integer[] list = new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };

    private GridView listActions;

    private GrowingSeedInterface mySeed;

    BaseActionInterface selectedAction;

    private Spinner spinner;

    private RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.inputaction);
        ActionBar bar = getSupportActionBar();
        bar.setTitle(getResources().getString(R.string.action_planning));
        bar.setDisplayHomeAsUpEnabled(true);

        new AsyncTask<String, Void, List<BaseActionInterface>>() {
            private GotsActionManager helper;

            protected void onPreExecute() {

                helper = GotsActionManager.getInstance().initIfNew(NewActionActivity.this);
            };

            @Override
            protected List<BaseActionInterface> doInBackground(String... params) {
                List<BaseActionInterface> actions = helper.getActions();

                return actions;
            }

            protected void onPostExecute(List<BaseActionInterface> actions) {

                listActions = (GridView) findViewById(R.id.idListAction);
                WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
                Display display = wm.getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);
                int width = size.x;
                int height = size.y;

                int layoutsize = 200;
                int nbcolumn = (width - 200) / layoutsize;
                listActions.setNumColumns(nbcolumn);
                listActions.setAdapter(new SimpleListActionAdapter(actions));
                listActions.setOnItemClickListener(NewActionActivity.this);
            };
        }.execute();

        // listActions.setNumColumns(listActions.getCount());

        // listActions.invalidate();

        spinner = (Spinner) findViewById(R.id.spinnerDuration);
        spinner.setAdapter(new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, list));
        if (getIntent().getExtras() != null) {
            Integer seedId = getIntent().getExtras().getInt("org.gots.seed.id");
            GotsGrowingSeedManager growingSeedManager = GotsGrowingSeedManager.getInstance().initIfNew(this);
            mySeed = growingSeedManager.getGrowingSeedById(seedId);

            SeedWidgetLong seed = (SeedWidgetLong) findViewById(R.id.seedWidgetLong);
            seed.setSeed(mySeed);
        }

        radioGroup = (RadioGroup) findViewById(R.id.radioGroupSelectDuration);

        Button validate = (Button) findViewById(R.id.buttonPlanAction);
        validate.setOnClickListener(this);

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

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch (item.getItemId()) {

        case android.R.id.home:
            finish();
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    private void scheduleAction() {

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
            // AlertDialog alert = new AlertDialog(this);
            AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
            builder.setMessage("Please select an action").setCancelable(false).setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    finish();
                }
            });

        } else {
            selectedAction.setDuration(duration);

            new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    GotsActionSeedProvider actionHelper = GotsActionSeedManager.getInstance().initIfNew(
                            getApplicationContext());
                    actionHelper.insertAction(mySeed, selectedAction);
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    NewActionActivity.this.finish();
                    GoogleAnalyticsTracker.getInstance().trackEvent(getClass().getSimpleName(), "NewAction",
                            selectedAction.getName(), 0);
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
