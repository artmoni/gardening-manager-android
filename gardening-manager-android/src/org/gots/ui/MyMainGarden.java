/*******************************************************************************
 * Copyright (c) 2012 sfleury.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     sfleury - initial API and implementation
 ******************************************************************************/
package org.gots.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.gots.R;
import org.gots.action.GardeningActionInterface;
import org.gots.action.bean.DeleteAction;
import org.gots.ads.GotsAdvertisement;
import org.gots.allotment.adapter.ListAllotmentAdapter;
import org.gots.bean.Allotment;
import org.gots.bean.BaseAllotmentInterface;
import org.gots.broadcast.BroadCastMessages;
import org.gots.provider.AllotmentContentProvider;
import org.gots.seed.GotsGrowingSeedManager;
import org.gots.weather.view.WeatherWidget;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

public class MyMainGarden extends BaseGotsActivity {

    public static final String SELECT_ALLOTMENT = "allotment.select";

    public static final String VENDOR_SEED_ID = "allotment.vendorseedid";

    protected static final String TAG = "MyMainGarden";

    private ListAllotmentAdapter lsa;

    ListView listAllotments;

    WeatherWidget weatherWidget;

    Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle(R.string.dashboard_allotments_name);

        // GardenManager gm =GardenManager.getInstance();
        registerReceiver(seedBroadcastReceiver, new IntentFilter(BroadCastMessages.GROWINGSEED_DISPLAYLIST));

        setContentView(R.layout.garden);
        listAllotments = (ListView) findViewById(R.id.IdGardenAllotmentsList);
        lsa = new ListAllotmentAdapter(MyMainGarden.this, new ArrayList<BaseAllotmentInterface>(),
                getIntent().getExtras());
        listAllotments.setAdapter(lsa);
        listAllotments.setDivider(null);
        listAllotments.setDividerHeight(0);
        listAllotments.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listAllotments.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                
                view.setSelected(true);
                startSupportActionMode(new MyCallBack(position));
            }

        });
        // listAllotments.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        // listAllotments.setBackgroundDrawable(getResources().getDrawable(R.drawable.help_hut_2));
        if (!gotsPurchase.isPremium()) {
            GotsAdvertisement ads = new GotsAdvertisement(this);

            LinearLayout layout = (LinearLayout) findViewById(R.id.idAdsTop);
            layout.addView(ads.getAdsLayout());
        }

        // setProgressAction(new Intent(this, AllotmentNotificationService.class));
    }

    @Override
    protected void onRefresh(String AUTHORITY) {
        super.onRefresh(AllotmentContentProvider.AUTHORITY);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(seedBroadcastReceiver);
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_garden, menu);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    public BroadcastReceiver seedBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // onResume();
            if (BroadCastMessages.GARDEN_EVENT.equals(intent.getAction())) {
                setProgressRefresh(false);
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case android.R.id.home:
            finish();
            return true;
        case R.id.new_allotment:

            // AllotmentDBHelper helper = new AllotmentDBHelper(this);
            // helper.insertAllotment(newAllotment);
            new AsyncTask<Void, Integer, BaseAllotmentInterface>() {
                @Override
                protected BaseAllotmentInterface doInBackground(Void... params) {
                    BaseAllotmentInterface newAllotment = new Allotment();
                    newAllotment.setName("" + new Random().nextInt());
                    return allotmentManager.createAllotment(newAllotment);
                }

                @Override
                protected void onPostExecute(BaseAllotmentInterface result) {
                    sendBroadcast(new Intent(BroadCastMessages.ALLOTMENT_EVENT));
                    onResume();
                    super.onPostExecute(result);
                }
            }.execute();

            // listAllotments.setBackgroundDrawable(getResources().getDrawable(R.drawable.background_simple));
            return true;

        case R.id.help:
            Intent browserIntent = new Intent(this, WebHelpActivity.class);
            browserIntent.putExtra(WebHelpActivity.URL, getClass().getSimpleName());
            startActivity(browserIntent);

            return true;

        default:
            return super.onOptionsItemSelected(item);
        }
    }

    public void showDialogRenameAllotment(final BaseAllotmentInterface allotmentInterface) {

        final EditText userinput = new EditText(this);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(userinput).setTitle("Allotment's name");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(final DialogInterface dialog, int id) {
                new AsyncTask<Void, Integer, Void>() {
                    protected void onPreExecute() {
                        allotmentInterface.setName(userinput.getText().toString());
                    };

                    @Override
                    protected Void doInBackground(Void... params) {
                        allotmentManager.updateAllotment(allotmentInterface);
                        return null;
                    }

                    protected void onPostExecute(Void result) {
                        lsa.notifyDataSetChanged();
                        dialog.cancel();
                    };

                }.execute();
            }
        }).setNegativeButton(this.getResources().getString(R.string.button_cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                    }
                });
        // AlertDialog dialog = builder.create();
        builder.setCancelable(true);
        builder.show();

    }

    private void removeAllotment(final BaseAllotmentInterface selectedAllotment2) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete").setCancelable(false).setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        new AsyncTask<BaseAllotmentInterface, Integer, Void>() {
                            @Override
                            protected void onPreExecute() {
                                setProgressRefresh(true);

                                super.onPreExecute();
                            }

                            @Override
                            protected Void doInBackground(BaseAllotmentInterface... params) {
                                GardeningActionInterface actionItem = new DeleteAction(MyMainGarden.this);
                                actionItem.execute(selectedAllotment2, null);
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void result) {
                                onResume();
                                setProgressRefresh(false);
                                sendBroadcast(new Intent(BroadCastMessages.ALLOTMENT_EVENT));
                                super.onPostExecute(result);
                            }
                        }.execute();
                        dialog.dismiss();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    @Override
    protected boolean requireAsyncDataRetrieval() {
        return true;
    }

    @Override
    protected Object retrieveNuxeoData() throws Exception {

        List<BaseAllotmentInterface> allotments = allotmentManager.getMyAllotments(false);
        GotsGrowingSeedManager growingSeedManager = GotsGrowingSeedManager.getInstance().initIfNew(MyMainGarden.this);

        for (int i = 0; i < allotments.size(); i++) {
            allotments.get(i).setSeeds(growingSeedManager.getGrowingSeedsByAllotment(allotments.get(i), false));
        }
        return allotments;
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {
        List<BaseAllotmentInterface> result = (List<BaseAllotmentInterface>) data;
        if (result != null)
            lsa.setAllotments(result);
        super.onNuxeoDataRetrieved(data);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private final class MyCallBack implements ActionMode.Callback {

        private BaseAllotmentInterface allotment;

        private MyCallBack(int position) {
            allotment = lsa.getItem(position);
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            
        }

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_allotment_contextual, menu);
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {

            switch (item.getItemId()) {
            case R.id.update_allotment:
                showDialogRenameAllotment(allotment);
                return true;
            case R.id.delete_allotment:
                removeAllotment(allotment);
                return true;
            default:
                break;
            }
            listAllotments.setItemChecked(-1, true);//clear selection in listview
            mode.finish();
            return true;
        }
    }
}
