package org.gots.ui;

import android.accounts.Account;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.UserRecoverableAuthException;

import org.gots.R;
import org.gots.action.BaseAction;
import org.gots.ads.GotsAdvertisement;
import org.gots.authentication.GotsSocialAuthentication;
import org.gots.authentication.provider.google.GoogleAuthentication;
import org.gots.authentication.provider.google.User;
import org.gots.broadcast.BroadCastMessages;
import org.gots.garden.GardenInterface;
import org.gots.inapp.AppRater;
import org.gots.inapp.GotsBillingDialog;
import org.gots.inapp.GotsPurchaseItem;
import org.gots.nuxeo.NuxeoWorkflowProvider;
import org.gots.preferences.GotsPreferences;
import org.gots.provider.ActionsContentProvider;
import org.gots.provider.AllotmentContentProvider;
import org.gots.provider.GardenContentProvider;
import org.gots.provider.SeedsContentProvider;
import org.gots.provider.SensorContentProvider;
import org.gots.provider.WeatherContentProvider;
import org.gots.sensor.fragment.AlertSensorResumeFragment;
import org.gots.sensor.fragment.SensorResumeFragment.OnSensorClickListener;
import org.gots.sensor.parrot.ParrotLocation;
import org.gots.sensor.parrot.ParrotLocationsStatus;
import org.gots.sensor.parrot.ParrotSensorProvider;
import org.gots.ui.BaseGotsActivity.GardenListener;
import org.gots.ui.fragment.ActionsResumeFragment;
import org.gots.ui.fragment.ActionsResumeFragment.OnActionsClickListener;
import org.gots.ui.fragment.CatalogResumeFragment;
import org.gots.ui.fragment.IncredibleResumeFragment;
import org.gots.ui.fragment.TutorialResumeFragment;
import org.gots.ui.fragment.TutorialResumeFragment.OnTutorialFinishedListener;
import org.gots.ui.fragment.WeatherResumeFragment;
import org.gots.ui.fragment.WorkflowResumeFragment;
import org.gots.ui.slidingmenu.NavDrawerItem;
import org.gots.ui.slidingmenu.adapter.NavDrawerListAdapter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.nuxeo.ecm.automation.client.jaxrs.model.Blob;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class CoreActivity extends BaseGotsActivity implements GardenListener, OnTutorialFinishedListener,
        OnActionsClickListener, OnSensorClickListener {
    public static final int INDEX_CATALOGUE = 0;
    public static final int INDEX_GARDEN = 1;
    public static final int INDEX_ACTIONS = 2;
    //    public static final int INDEX_PROFILE = 3;
    public static final int INDEX_SENSOR = 3;
    public static final int INDEX_RECOGNITION = 4;
    private DrawerLayout mDrawerLayout;

    private ListView mDrawerList;

    private ActionBarDrawerToggle mDrawerToggle;

    // nav drawer title
    private CharSequence mDrawerTitle;

    // used to store app title
    private CharSequence mTitle;

    // slide menu items
    private String[] navMenuTitles;

    private TypedArray navMenuIcons;

    private ArrayList<NavDrawerItem> navDrawerItems;

    private NavDrawerListAdapter adapter;

    private RelativeLayout mDrawerLinear;

    private Spinner spinnerGarden;

    private String TAG = "CoreActivity";

    private List<GardenInterface> myGardens;

    public static final String LAUNCHER_ACTION = "org.gots.dashboard.action";

    public static final String LAUNCHER_CATALOGUE = "org.gots.dashboard.catalogue";

    private boolean doubleBackToExitPressedOnce;

    // private GardenInterface currentGarden;

    private WorkflowResumeFragment workflowResumeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_drawer);
        // AppRater.app_launched(getApplicationContext());
        // currentGarden = getCurrentGarden();
        mTitle = mDrawerTitle = getTitle();

        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLinear = (RelativeLayout) findViewById(R.id.frame_menu);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
        spinnerGarden = (Spinner) findViewById(R.id.spinnerGarden);
        if (gotsPrefs.get("firstlaunch", true)) {
            mDrawerLayout.openDrawer(mDrawerLinear);
            gotsPrefs.set("firstlaunch", false);
        }
        displayDrawerMenu();

        // enabling action bar app icon and behaving it as toggle button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, // nav menu toggle icon
                R.string.app_name, // nav drawer open - description for
                // accessibility
                R.string.app_name // nav drawer close - description for
                // accessibility
        ) {
            public void onDrawerClosed(View view) {
                displayTitle();
                // calling onPrepareOptionsMenu() to show action bar icons
                supportInvalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                if (getCurrentGarden() != null)
                    getSupportActionBar().setTitle(getCurrentGarden().getName());
                // calling onPrepareOptionsMenu() to hide action bar icons
                supportInvalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

        if (savedInstanceState == null) {
            // on first time display view for first nav item
            displayView(10);
        }

        spinnerGarden.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int itemPosition, long arg3) {
                if (myGardens == null || myGardens.size() < itemPosition)
                    return;
                if (getCurrentGarden() != null && !getCurrentGarden().equals(myGardens.get(itemPosition)))
                    gardenManager.setCurrentGarden(myGardens.get(itemPosition));

                // startService(weatherIntent);
                Bundle bundle = new Bundle();
                bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
                bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
                ContentResolver.setSyncAutomatically(gotsPrefs.getUserAccount(), WeatherContentProvider.AUTHORITY, true);
                ContentResolver.requestSync(gotsPrefs.getUserAccount(), WeatherContentProvider.AUTHORITY, bundle);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        // registerReceiver(weatherBroadcastReceiver, new IntentFilter(BroadCastMessages.WEATHER_DISPLAY_EVENT));
        registerReceiver(broadcastReceiver, new IntentFilter("NuxeoServerConnectivityChanged"));
        registerReceiver(broadcastReceiver, new IntentFilter(BroadCastMessages.SEED_DISPLAYLIST));
        registerReceiver(broadcastReceiver, new IntentFilter(BroadCastMessages.GARDEN_EVENT));
        registerReceiver(broadcastReceiver, new IntentFilter(BroadCastMessages.ACTION_EVENT));
        registerReceiver(broadcastReceiver, new IntentFilter(BroadCastMessages.ALLOTMENT_EVENT));
        registerReceiver(broadcastReceiver, new IntentFilter(BroadCastMessages.GARDEN_CURRENT_CHANGED));
        registerReceiver(broadcastReceiver, new IntentFilter(BroadCastMessages.AUTHENTIFICATION_BEGIN));
        registerReceiver(broadcastReceiver, new IntentFilter(BroadCastMessages.AUTHENTIFICATION_END));

        Account userAccount = gotsPrefs.getUserAccount();
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);

        ContentResolver.setSyncAutomatically(userAccount, SeedsContentProvider.AUTHORITY, true);
        ContentResolver.requestSync(userAccount, SeedsContentProvider.AUTHORITY, bundle);
        ContentResolver.setSyncAutomatically(userAccount, GardenContentProvider.AUTHORITY, true);
        ContentResolver.requestSync(userAccount, GardenContentProvider.AUTHORITY, bundle);
        ContentResolver.setSyncAutomatically(userAccount, ActionsContentProvider.AUTHORITY, true);
        ContentResolver.requestSync(userAccount, ActionsContentProvider.AUTHORITY, bundle);
        ContentResolver.setSyncAutomatically(userAccount, AllotmentContentProvider.AUTHORITY, true);
        ContentResolver.requestSync(userAccount, AllotmentContentProvider.AUTHORITY, bundle);
        if (gotsPrefs.getParrotToken() != null) {
            ContentResolver.setSyncAutomatically(userAccount, SensorContentProvider.AUTHORITY, true);
            ContentResolver.requestSync(userAccount, SensorContentProvider.AUTHORITY, bundle);
        }
        GotsPurchaseItem gotsPurchase = new GotsPurchaseItem(this);
        if (!gotsPurchase.isPremium()) {
            GotsAdvertisement ads = new GotsAdvertisement(this);

            LinearLayout layout = (LinearLayout) findViewById(R.id.idAdsTop);
            layout.addView(ads.getAdsLayout());
        }

    }

    protected void displayDrawerMenu() {
        // nav drawer icons from resources
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);
        navDrawerItems = new ArrayList<NavDrawerItem>();

        // *************************
        // Catalogue
        // *************************
        NavDrawerItem navDrawerItem = new NavDrawerItem(navMenuTitles[INDEX_CATALOGUE], navMenuIcons.getResourceId(INDEX_CATALOGUE, -1));
        navDrawerItems.add(navDrawerItem);
        // displayDrawerMenuCatalogCounter();

        // *************************
        // Allotments
        // *************************
        navDrawerItem = new NavDrawerItem(navMenuTitles[INDEX_GARDEN], navMenuIcons.getResourceId(INDEX_GARDEN, -1));
        navDrawerItems.add(navDrawerItem);
        // displayDrawerMenuAllotmentCounter();
        // *************************
        // Actions
        // *************************
        navDrawerItem = new NavDrawerItem(navMenuTitles[INDEX_ACTIONS], navMenuIcons.getResourceId(INDEX_ACTIONS, -1));
        navDrawerItems.add(navDrawerItem);
        // displayDrawerMenuActionsCounter();

        // *************************
        // Profiles
        // *************************
//        navDrawerItem = new NavDrawerItem(navMenuTitles[IN], navMenuIcons.getResourceId(3, -1));
//        navDrawerItems.add(navDrawerItem);
        // displayDrawerMenuProfileCounter();

        // *************************
        // Sensors
        // *************************
        navDrawerItem = new NavDrawerItem(navMenuTitles[INDEX_SENSOR], navMenuIcons.getResourceId(INDEX_SENSOR, -1));
        navDrawerItems.add(navDrawerItem);
        // displayDrawerMenuSensorCounter();

        // *************************
        // Recognition
        // *************************
        navDrawerItem = new NavDrawerItem(navMenuTitles[INDEX_RECOGNITION], navMenuIcons.getResourceId(INDEX_RECOGNITION, -1));
//        navDrawerItem.setCounterVisibility(false);
        navDrawerItems.add(navDrawerItem);

        // What's hot, We will add a counter here
        // navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1)));

        // Recycle the typed array
        navMenuIcons.recycle();

        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
        mDrawerList.removeAllViewsInLayout();
        mDrawerList.setAdapter(adapter);

        findViewById(R.id.bt_share).setVisibility(View.GONE);
        // findViewById(R.id.bt_share).setOnClickListener(new View.OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // new AsyncTask<NavDrawerItem, Void, Integer>() {
        // private ImageView imageShare;
        //
        // protected void onPreExecute() {
        // imageShare = (ImageView) findViewById(R.id.bt_share);
        // };
        //
        // @Override
        // protected Integer doInBackground(NavDrawerItem... params) {
        // return gardenManager.share(gardenManager.getCurrentGarden(), "sebastien.fleury@gmail.com",
        // "Read");
        // }
        //
        // @Override
        // protected void onPostExecute(Integer result) {
        // if (result.intValue() == -1)
        // imageShare.setImageDrawable(getResources().getDrawable(R.drawable.garden_unshared));
        // else
        // imageShare.setImageDrawable(getResources().getDrawable(R.drawable.garden_shared));
        // super.onPostExecute(result);
        // }
        // }.execute();
        // }
        // });

    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        private ImageView connectionView;

        @Override
        public void onReceive(Context context, Intent intent) {
            if (BroadCastMessages.AUTHENTIFICATION_BEGIN.equals(intent.getAction())) {
                if (menu != null && menu.findItem(R.id.connection) != null) {
                    MenuItem itemConnection = menu.findItem(R.id.connection);
                    if (connectionView == null)
                        connectionView = new ImageView(getApplicationContext());
                    connectionView.setImageDrawable(getResources().getDrawable(R.drawable.garden_disconnected));
                    Animation rotation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.disappear);
                    rotation.setRepeatCount(Animation.INFINITE);
                    connectionView.startAnimation(rotation);
                    itemConnection = MenuItemCompat.setActionView(itemConnection, connectionView);
                }

            } else if (BroadCastMessages.AUTHENTIFICATION_END.equals(intent.getAction())) {
                if (menu != null && menu.findItem(R.id.connection) != null) {
                    if (connectionView != null)
                        connectionView.clearAnimation();
                    MenuItem itemConnection = menu.findItem(R.id.connection);
                    itemConnection = MenuItemCompat.setActionView(itemConnection, null);
                    displaySpinnerGarden();
                }
            }
            if ("NuxeoServerConnectivityChanged".equals(intent.getAction())) {
                Log.d(TAG, "Connection settings changed");
                invalidateOptionsMenu();
            } else if (BroadCastMessages.SEED_DISPLAYLIST.equals(intent.getAction())) {
                displayDrawerMenuCatalogCounter();
            } else if (BroadCastMessages.GARDEN_EVENT.equals(intent.getAction())) {
//                displayDrawerMenuProfileCounter();
                displaySpinnerGarden();
            } else if (BroadCastMessages.ALLOTMENT_EVENT.equals(intent.getAction())) {
                displayDrawerMenuAllotmentCounter();
            }
        }

    };

    private Menu menu;

    protected void displayDrawerMenuRecognitionCounter() {
        new AsyncTask<NavDrawerItem, Void, Integer>() {
            NavDrawerItem item;

            @Override
            protected Integer doInBackground(NavDrawerItem... params) {
                item = params[0];
                return gotsPurchase.getFeatureRecognitionCounter() + gotsPurchase.getFeatureRecognitionFreeCounter();
            }

            @Override
            protected void onPostExecute(Integer result) {
                item.setCounterVisibility(result > 0);
                item.setCount(result.toString());
                adapter.notifyDataSetChanged();

                super.onPostExecute(result);
            }
        }.execute(navDrawerItems.get(INDEX_RECOGNITION));
    }

//    protected void displayDrawerMenuProfileCounter() {
//        new AsyncTask<NavDrawerItem, Void, Integer>() {
//            NavDrawerItem item;
//
//            @Override
//            protected Integer doInBackground(NavDrawerItem... params) {
//                item = params[0];
//                return gardenManager.getMyGardens(false).size();
//            }
//
//            @Override
//            protected void onPostExecute(Integer result) {
//                item.setCounterVisibility(result > 0);
//                item.setCount(result.toString());
//                adapter.notifyDataSetChanged();
//
//                super.onPostExecute(result);
//            }
//        }.execute(navDrawerItems.get(INDEX_PROFILE));
//    }


    protected void displayDrawerMenuActionsCounter() {
        new AsyncTask<NavDrawerItem, Void, Integer>() {
            NavDrawerItem item;

            @Override
            protected Integer doInBackground(NavDrawerItem... params) {
                item = params[0];
                return actionseedProvider.getActionsToDo(false).size();
            }

            @Override
            protected void onPostExecute(Integer result) {
                item.setCounterVisibility(result > 0);
                item.setCount(result.toString());
                adapter.notifyDataSetChanged();
                super.onPostExecute(result);
            }
        }.execute(navDrawerItems.get(INDEX_ACTIONS));
    }

    protected void displayDrawerMenuAllotmentCounter() {
        new AsyncTask<NavDrawerItem, Void, Integer>() {
            NavDrawerItem item;

            @Override
            protected Integer doInBackground(NavDrawerItem... params) {
                item = params[0];
                return allotmentManager.getMyAllotments(false).size();
            }

            @Override
            protected void onPostExecute(Integer result) {
                item.setCounterVisibility(result > 0);
                item.setCount(result.toString());
                adapter.notifyDataSetChanged();
                super.onPostExecute(result);
            }
        }.execute(navDrawerItems.get(INDEX_GARDEN));
    }

    protected void displayDrawerMenuCatalogCounter() {
        new AsyncTask<NavDrawerItem, Void, Integer>() {
            NavDrawerItem item;

            @Override
            protected Integer doInBackground(NavDrawerItem... params) {
                item = params[0];
                return seedManager.getVendorSeeds(false, 0, 25).size();
            }

            @Override
            protected void onPostExecute(Integer result) {
                item.setCounterVisibility(result > 0);
                item.setCount(result.toString());
                adapter.notifyDataSetChanged();
                super.onPostExecute(result);
            }
        }.execute(navDrawerItems.get(INDEX_CATALOGUE));
    }

    protected void displayDrawerMenuSensorCounter() {
        new AsyncTask<NavDrawerItem, Void, Integer>() {
            NavDrawerItem item;

            @Override
            protected Integer doInBackground(NavDrawerItem... params) {
                item = params[0];
                if (gotsPrefs.getParrotToken() != null) {
                    ParrotSensorProvider parrotSensorProvider = new ParrotSensorProvider(getApplicationContext());
                    return parrotSensorProvider.getSensors().size();
                } else
                    return 0;
            }

            @Override
            protected void onPostExecute(Integer result) {
                item.setCounterVisibility(result > 0);
                item.setCount(result.toString());
                adapter.notifyDataSetChanged();
                super.onPostExecute(result);
            }
        }.execute(navDrawerItems.get(INDEX_SENSOR));
    }

    /**
     * Slide menu item click listener
     */
    private class SlideMenuClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // display view for selected nav drawer item
            displayView(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dashboard, menu);
        this.menu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // toggle nav drawer on selecting action bar app icon/title
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.help:
                Intent browserIntent = new Intent(this, WebHelpActivity.class);
                browserIntent.putExtra(WebHelpActivity.URL_CLASSNAME, getClass().getSimpleName());
                startActivity(browserIntent);

                return true;
            case R.id.about:
                Intent aboutIntent = new Intent(this, AboutActivity.class);
                startActivity(aboutIntent);

                return true;
            case R.id.premium:
                displayPremiumDialog();
                return true;

            case R.id.settings:
                Intent settingsIntent = new Intent(this, PreferenceActivity.class);
                startActivity(settingsIntent);
                // FragmentTransaction ft = getFragmentManager().beginTransaction();
                // ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
                // ft.replace(R.id.idContent, new PreferenceActivity()).addToBackStack("back").commit();
                return true;

            case R.id.connection:
//                LoginFragment login = new LoginFragment();
//                login.show(getSupportFragmentManager(), TAG);
                Intent profile = new Intent(this, ProfileActivity.class);
                startActivity(profile);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        final MenuItem itemConnected = (MenuItem) menu.findItem(R.id.connection);
//        if (!gotsPrefs.isConnectedToServer())
//            itemConnected.setIcon(getResources().getDrawable(R.drawable.ic_login));
//        else {
//            new AsyncTask<Void, Void, Boolean>() {
//                @Override
//                protected Boolean doInBackground(Void... voids) {
//                    return nuxeoManager.getNuxeoClient().getNetworkStatus().testNuxeoServerReachable();
//                }
//
//                @Override
//                protected void onPostExecute(Boolean b) {
//                    if (b)
//                        itemConnected.setIcon(getResources().getDrawable(R.drawable.garden_connected));
//                    else
//                        itemConnected.setIcon(getResources().getDrawable(R.drawable.garden_disconnected));
//                    super.onPostExecute(b);
//                }
//            }.execute();
//        }

        if (gotsPurchase.isPremium())
            menu.findItem(R.id.premium).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
        displaySpinnerGarden();
        displayUserAvatar();

        if (LAUNCHER_ACTION.equals(getIntent().getAction())) {
            startActivity(new Intent(this, ActionActivity.class));
        } else if (LAUNCHER_CATALOGUE.equals(getIntent().getAction()))
            startActivity(new Intent(this, CatalogueActivity.class));

        // if (getCurrentGarden() == null || (myGardens != null && myGardens.size() == 0)) {
        // Intent intent = new Intent(getApplicationContext(), ProfileCreationActivity.class);
        // startActivity(intent);
        // }
        AppRater.app_launched(CoreActivity.this);
    }

    protected void displayUserAvatar() {
        if (getCurrentGarden() == null) {
            return;
        }

        View avatarView = findViewById(R.id.imageAvatar);
        if (gotsPrefs.isConnectedToServer()) {
            if (getCurrentGarden().isIncredibleEdible())
                ((ImageView) avatarView).setImageDrawable(getResources().getDrawable(
                        R.drawable.ic_garden_incredible));
            else {
                UserInfo userInfoTask = new UserInfo();
                userInfoTask.execute((ImageView) avatarView);
            }
        }

    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Diplaying fragment view for selected nav drawer list item
     */
    private void displayView(int position) {
        // update the main content by replacing fragments
        Intent i = null;
        switch (position) {
            case INDEX_CATALOGUE:
                i = new Intent(getApplicationContext(), CatalogueActivity.class);
                break;
            case INDEX_GARDEN:
                i = new Intent(getApplicationContext(), GardenActivity.class);
                break;
            case INDEX_ACTIONS:
                // fragment = new ActionActivity();
                i = new Intent(getApplicationContext(), ActionActivity.class);

                break;
//            case 3:
//                // fragment = new ProfileActivity();
//                i = new Intent(getApplicationContext(), org.gots.ui.ProfileActivity.class);
//
//                break;
            case INDEX_SENSOR:
                i = new Intent(getApplicationContext(), SensorActivity.class);
                // fragment = new PagesFragment();
//                GotsPurchaseItem purchaseItem = new GotsPurchaseItem(this);
//
//                if (purchaseItem.getFeatureParrot() ? true : purchaseItem.isPremium()) {
//                    i = new Intent(this, SensorActivity.class);
//                } else {
//                    // if (!purchaseItem.getFeatureParrot() && !purchaseItem.isPremium()) {
//                    FragmentManager fm = getSupportFragmentManager();
//                    GotsBillingDialog editNameDialog = new GotsBillingDialog(GotsPurchaseItem.SKU_FEATURE_PARROT);
//                    editNameDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
//                    editNameDialog.show(fm, "fragment_edit_name");
//                }
                break;
            case INDEX_RECOGNITION:
//                displayPremiumDialog();
                displayRecognitionDialog();
                break;

            default:
                break;
        }
        if (i != null) {
            startActivity(i);
        }

        // update selected item and title, then close the drawer
        if (position <= navMenuTitles.length) {
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(navMenuTitles[position]);

        }
    }

    private void displayRecognitionDialog() {
        Intent i = new Intent(getApplicationContext(), RecognitionActivity.class);
        startActivity(i);
    }

    protected void displayCatalogueFragment() {

        CatalogResumeFragment catalogueResumeFragment = (CatalogResumeFragment) getSupportFragmentManager().findFragmentById(
                R.id.idFragmentCatalog);
        if (catalogueResumeFragment == null) {
            catalogueResumeFragment = new CatalogResumeFragment();
            FragmentTransaction transactionCatalogue = getSupportFragmentManager().beginTransaction();
            transactionCatalogue.setCustomAnimations(R.anim.push_left_in, R.anim.push_right_out);
            transactionCatalogue.replace(R.id.idFragmentCatalog, catalogueResumeFragment).commitAllowingStateLoss();
        } else
            catalogueResumeFragment.update();
    }

    protected void displayPremiumDialog() {
        FragmentManager fm = getSupportFragmentManager();
        GotsBillingDialog editNameDialog = new GotsBillingDialog();
        editNameDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
        editNameDialog.show(fm, "fragment_edit_name");
    }

    protected void displaySpinnerGarden() {

        new AsyncTask<Void, Void, GardenInterface>() {

            @Override
            protected GardenInterface doInBackground(Void... params) {
                myGardens = gardenManager.getMyGardens(false);

                if (getCurrentGarden() == null)
                    myGardens = gardenManager.getMyGardens(true);

                return getCurrentGarden();
            }

            protected void onPostExecute(GardenInterface currentGarden) {
                if (currentGarden == null && myGardens.size() > 0) {
                    gardenManager.setCurrentGarden(myGardens.get(0));
                }

                int selectedGardenIndex = 0;
                String[] dropdownValues = new String[myGardens.size()];
                for (int i = 0; i < myGardens.size(); i++) {
                    GardenInterface garden = myGardens.get(i);
                    dropdownValues[i] = garden.getName() != null ? garden.getName() : garden.getAddress().toString();
                    if (garden != null && currentGarden != null && garden.getId() == currentGarden.getId()) {
                        selectedGardenIndex = i;
                    }
                }
                if (dropdownValues.length > 0) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(CoreActivity.this,
                            android.R.layout.simple_spinner_item, android.R.id.text1, dropdownValues);

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    // actionBar.setListNavigationCallbacks(adapter, CoreActivity.this);

                    spinnerGarden.setAdapter(adapter);
                    spinnerGarden.setSelection(selectedGardenIndex);
                }
            }

            ;
        }.execute();

    }

    private void downloadImage(String userid, String url) {
        if (userid == null)
            return;
        File file = new File(getApplicationContext().getCacheDir() + "/" + userid.toLowerCase().replaceAll("\\s", ""));
        if (!file.exists()) {
            try {
                URLConnection conn = new URL(url).openConnection();
                conn.connect();
                Bitmap image = BitmapFactory.decodeStream(conn.getInputStream());
                FileOutputStream out = new FileOutputStream(file);
                image.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return;
    }

    public class UserInfo extends AsyncTask<ImageView, Void, Void> {
        ImageView imageProfile;

        private User user;

        @Override
        protected Void doInBackground(ImageView... params) {
            imageProfile = params[0];
            GotsSocialAuthentication authentication = new GoogleAuthentication(getApplicationContext());
            try {
                String token = authentication.getToken(gotsPrefs.getNuxeoLogin());
                user = authentication.getUser(token);
                if (user != null) {
                    downloadImage(user.getId(), user.getPictureURL());
                }
            } catch (UserRecoverableAuthException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (GoogleAuthException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            if (user != null && user.getId() != null) {
                File file = new File(getApplicationContext().getCacheDir() + "/"
                        + user.getId().toLowerCase().replaceAll("\\s", ""));
                if (file.exists()) {
//                        Drawable d = Drawable.createFromStream(getAssets().open(file.getAbsolutePath()), null);
                    Bitmap d = BitmapFactory.decodeFile(file.getAbsolutePath());
                    // Bitmap usrLogo = BitmapFactory.decodeFile(file.getAbsolutePath());
                    // imageProfile.setImageBitmap(usrLogo);
                    imageProfile.setImageBitmap(d);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, getResources().getString(R.string.dashboard_exit), Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    protected boolean requireAsyncDataRetrieval() {
        return true;
    }

    @Override
    protected Object retrieveNuxeoData() throws Exception {
        return "";
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {
        refreshAllCounter();
        displaySpinnerGarden();
        super.onNuxeoDataRetrieved(data);
    }

    @Override
    public void onCurrentGardenChanged(GardenInterface garden) {
        // displayDrawerMenu();
//        Toast.makeText(getApplicationContext(), "Loading garden " + garden.getName() + " please wait.",
//                Toast.LENGTH_LONG).show();
        showNotification(getResources().getString(R.string.gots_loading) + " " + garden.getName(), true);
        displayTitle();
        displayUserAvatar();
        displaySpinnerGarden();
        refreshAllCounter();
        refreshAllFragments();

    }

    protected void refreshAllCounter() {
        displayDrawerMenuActionsCounter();
        displayDrawerMenuAllotmentCounter();
        displayDrawerMenuCatalogCounter();
//        displayDrawerMenuProfileCounter();
        displayDrawerMenuRecognitionCounter();
//        if (gotsPrefs.getParrotToken() != null)
        displayDrawerMenuSensorCounter();
    }

    protected void refreshAllFragments() {
        displayTutorialFragment();
        displayIncredibleFragment();
        displayWeatherFragment();
        displayActionsFragment();
        displayCatalogueFragment();
//        displayRecognitionFragment();
        if (gotsPrefs.isConnectedToServer())
            displayWorkflowFragment();
        if (gotsPrefs.getParrotToken() != null)
            displaySensorFragment();
    }

    protected void displayActionsFragment() {
        ActionsResumeFragment actionFragment = (ActionsResumeFragment) getSupportFragmentManager().findFragmentById(
                R.id.idFragmentActions);
        if (actionFragment == null) {
            actionFragment = new ActionsResumeFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.idFragmentActions, actionFragment).commitAllowingStateLoss();
        } else
            actionFragment.update();
    }

    private void displayWorkflowFragment() {
        new AsyncTask<Void, Void, JSONArray>() {
            JSONArray tasksEntries = null;

            @Override
            protected JSONArray doInBackground(Void... params) {
                NuxeoWorkflowProvider nuxeoWorkflowProvider = new NuxeoWorkflowProvider(getApplicationContext());
                Blob tasks = nuxeoWorkflowProvider.getUserTaskPageProvider();
                if (tasks == null) {
                    return null;
                }
                try {
                    BufferedReader r = new BufferedReader(new InputStreamReader(tasks.getStream()));
                    StringBuilder total = new StringBuilder();
                    String line;
                    while ((line = r.readLine()) != null) {
                        total.append(line);
                    }

                    JSONObject json;
                    json = new JSONObject(String.valueOf(total.toString()));
                    tasksEntries = json.getJSONArray("entries");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return tasksEntries;
            }

            protected void onPostExecute(JSONArray result) {
                if (result != null && result.length() > 0) {
                    if (workflowResumeFragment == null) {
                        workflowResumeFragment = new WorkflowResumeFragment();
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.setCustomAnimations(R.anim.push_left_in, R.anim.push_right_out);
                        transaction.replace(R.id.idFragmentWorkflow, workflowResumeFragment).commitAllowingStateLoss();
                    } else
                        workflowResumeFragment.update();
                } else
                    findViewById(R.id.idFragmentWorkflow).setVisibility(View.GONE);
            }

            ;
        }.execute();

    }

    protected void displayWeatherFragment() {

        WeatherResumeFragment weatherResumeFragment = (WeatherResumeFragment) getSupportFragmentManager().findFragmentById(
                R.id.idFragmentWeather);
        if (weatherResumeFragment == null) {
            weatherResumeFragment = new WeatherResumeFragment();
            FragmentTransaction transactionWeather = getSupportFragmentManager().beginTransaction();
            transactionWeather.setCustomAnimations(R.anim.push_left_in, R.anim.push_right_out);
            transactionWeather.replace(R.id.idFragmentWeather, weatherResumeFragment).commitAllowingStateLoss();
        } else
            weatherResumeFragment.update();
    }

    private void displayTutorialFragment() {
        if (findViewById(R.id.idFragmentTutorial) != null
                && !gotsPrefs.get(GotsPreferences.ORG_GOTS_TUTORIAL_FINISHED, false)) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            Fragment tutorialResumeFragment = new TutorialResumeFragment();
            FragmentTransaction transactionTutorial = fragmentManager.beginTransaction();
            // transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            transactionTutorial.setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out);
            transactionTutorial.replace(R.id.idFragmentTutorial, tutorialResumeFragment).commitAllowingStateLoss();
        } else
            findViewById(R.id.idFragmentTutorial).setVisibility(View.GONE);
    }

//    private void displayRecognitionFragment() {
//        RecognitionFragment likeThatFragment = new RecognitionFragment();
//        FragmentTransaction transactionWeather = getSupportFragmentManager().beginTransaction();
//        transactionWeather.setCustomAnimations(R.anim.push_left_in, R.anim.push_right_out);
//        transactionWeather.replace(R.id.idFragmentRecognition, likeThatFragment).commitAllowingStateLoss();
//    }

    private void displayIncredibleFragment() {

        if (getCurrentGarden() == null || getCurrentGarden().isIncredibleEdible() == false)
            findViewById(R.id.idFragmentIncredible).setVisibility(View.GONE);
        else {
            findViewById(R.id.idFragmentIncredible).setVisibility(View.VISIBLE);
            Fragment incredibleResumeFragment = new IncredibleResumeFragment();
            FragmentTransaction incredibleCatalogue = getSupportFragmentManager().beginTransaction();
            incredibleCatalogue.setCustomAnimations(R.anim.push_left_in, R.anim.push_right_out);
            incredibleCatalogue.replace(R.id.idFragmentIncredible, incredibleResumeFragment).commitAllowingStateLoss();
        }
    }

    private void displaySensorFragment() {
        new AsyncTask<Void, Void, Boolean>() {
            boolean isStatusAlert = false;

            private AlertSensorResumeFragment sensorResumeFragment;

            @Override
            protected Boolean doInBackground(Void... params) {
                ParrotSensorProvider parrotSensorProvider = new ParrotSensorProvider(getApplicationContext());
                final List<ParrotLocationsStatus> status = parrotSensorProvider.getStatus();
                for (ParrotLocationsStatus parrotLocationsStatus : status) {
                    if (parrotLocationsStatus.isWarning() || parrotLocationsStatus.isCritical()) {
                        isStatusAlert = true;
                        break;
                    }
                }
                return isStatusAlert;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                sensorResumeFragment = (AlertSensorResumeFragment) getSupportFragmentManager().findFragmentById(
                        R.id.idFragmentSensor);
                FragmentTransaction sensorTransaction = getSupportFragmentManager().beginTransaction();
                if (result.booleanValue()) {
                    if (sensorResumeFragment == null) {
                        sensorResumeFragment = new AlertSensorResumeFragment();
                        sensorTransaction.setCustomAnimations(R.anim.push_left_in, R.anim.push_right_out);
                        sensorTransaction.replace(R.id.idFragmentSensor, sensorResumeFragment);
                        if (sensorResumeFragment.isAdded())
                            sensorTransaction.commitAllowingStateLoss();

                    } else {
                        sensorResumeFragment.update();
                    }
                } else if (sensorResumeFragment != null) {
                    // FragmentTransaction sensorTransaction = getSupportFragmentManager().beginTransaction();
                    // sensorTransaction.setCustomAnimations(R.anim.push_left_in, R.anim.push_right_out);
                    // sensorTransaction.remove(sensorResumeFragment).commit();
                    sensorTransaction.remove(sensorResumeFragment).commitAllowingStateLoss();
                }
                super.onPostExecute(result);
            }
        }.execute();
    }

    @Override
    public void onTutorialFinished() {
        gotsPrefs.set(GotsPreferences.ORG_GOTS_TUTORIAL_FINISHED, true);
        displayTutorialFragment();
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.tutorial_finished), Toast.LENGTH_LONG).show();
        GoogleAnalyticsTracker analyticsTracker = GoogleAnalyticsTracker.getInstance();
        analyticsTracker.trackEvent("Tutorial", "Finished", getCurrentGarden().getName(), 0);
    }

    @Override
    public void onActionClick(View v, BaseAction actionInterface) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onActionMenuClick(View v) {
        startActivity(new Intent(this, ActionActivity.class));
    }

    @Override
    public void OnSensorClick(ParrotLocation locationSensor) {
        startActivity(new Intent(this, SensorActivity.class));
    }

    protected void displayTitle() {
        if (getCurrentGarden() != null)
            getSupportActionBar().setTitle(getCurrentGarden().getName());
        else
            getSupportActionBar().setTitle(getResources().getString(R.string.garden_create));
    }

    @Override
    protected boolean requireFloatingButton() {
        return false;
    }

//    @Override
//    protected List<FloatingItem> onCreateFloatingMenu() {
//        List<FloatingItem> list = new ArrayList();
//        FloatingItem floatingItem = new FloatingItem();
//        floatingItem.setRessourceId(R.drawable.action_photo);
//        floatingItem.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                RecognitionFragment likeThatFragment = new RecognitionFragment();
//                FragmentTransaction transactionWeather = getSupportFragmentManager().beginTransaction();
//                transactionWeather.setCustomAnimations(R.anim.push_left_in, R.anim.push_right_out);
//                transactionWeather.replace(R.id.idFragmentWeather, likeThatFragment).commitAllowingStateLoss();
//            }
//        });
//        list.add(floatingItem);
//        return list;
//    }

}