package org.gots.ui;

import java.util.Calendar;
import java.util.List;

import org.gots.R;
import org.gots.action.BaseActionInterface;
import org.gots.action.adapter.SimpleListActionAdapter;
import org.gots.action.sql.ActionDBHelper;
import org.gots.action.sql.ActionSeedDBHelper;
import org.gots.seed.GrowingSeedInterface;
import org.gots.seed.provider.local.sql.GrowingSeedDBHelper;
import org.gots.seed.view.SeedWidgetLong;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RadioGroup;
import android.widget.Spinner;

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
        setContentView(R.layout.inputaction);


        ActionDBHelper helper = new ActionDBHelper(this);
        List<BaseActionInterface> actions = helper.getActions();

        listActions = (GridView) findViewById(R.id.idListAction);
        listActions.setAdapter(new SimpleListActionAdapter(actions));

        // listActions.setNumColumns(listActions.getCount());

        listActions.setOnItemClickListener(this);
        // listActions.invalidate();

        spinner = (Spinner) findViewById(R.id.spinnerDuration);
        spinner.setAdapter(new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, list));
        if (getIntent().getExtras() != null) {
            Integer seedId = getIntent().getExtras().getInt("org.gots.seed.id");
            GrowingSeedDBHelper seedHelper = new GrowingSeedDBHelper(this);
            mySeed = seedHelper.getSeedById(seedId);

            SeedWidgetLong seed = (SeedWidgetLong) findViewById(R.id.seedWidgetLong);
            seed.setSeed(mySeed);
        }

        radioGroup = (RadioGroup) findViewById(R.id.radioGroupSelectDuration);

        Button validate = (Button) findViewById(R.id.buttonPlanAction);
        validate.setOnClickListener(this);

        super.onCreate(savedInstanceState);
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

            ActionSeedDBHelper actionHelper = new ActionSeedDBHelper(this);
            actionHelper.insertAction(selectedAction, mySeed);

            GoogleAnalyticsTracker.getInstance().trackEvent(getClass().getSimpleName(), "NewAction",
                    selectedAction.getName(), 0);
            finish();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
        selectedAction = (BaseActionInterface) listActions.getItemAtPosition(arg2);
        // listActions.setSelection(arg2);
        arg1.setSelected(!arg0.isSelected());
    }
}
