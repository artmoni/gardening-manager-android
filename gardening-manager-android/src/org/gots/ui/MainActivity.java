package org.gots.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.gots.R;
import org.gots.authentication.GotsSocialAuthentication;
import org.gots.authentication.provider.google.GoogleAuthentication;
import org.gots.authentication.provider.google.User;
import org.gots.broadcast.BroadCastMessages;
import org.gots.garden.GardenInterface;
import org.gots.inapp.GotsBillingDialog;
import org.gots.inapp.GotsPurchaseItem;
import org.gots.provider.WeatherContentProvider;
import org.gots.ui.fragment.DashboardResumeFragment;
import org.gots.ui.slidingmenu.NavDrawerItem;
import org.gots.ui.slidingmenu.adapter.NavDrawerListAdapter;

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
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.UserRecoverableAuthException;

public class MainActivity extends AbstractActivity {
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

    private String TAG = "MainActivity";

    private List<GardenInterface> myGardens;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_drawer);

        mTitle = mDrawerTitle = getTitle();

        // load slide menu items
        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLinear = (RelativeLayout) findViewById(R.id.frame_menu);
        mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
        spinnerGarden = (Spinner) findViewById(R.id.spinnerGarden);

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
                getSupportActionBar().setTitle(mTitle);
                // calling onPrepareOptionsMenu() to show action bar icons
                supportInvalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
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
                gardenManager.setCurrentGarden(myGardens.get(itemPosition));
                sendBroadcast(new Intent(BroadCastMessages.GARDEN_CURRENT_CHANGED));

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
        registerReceiver(broadcastReceiver, new IntentFilter(BroadCastMessages.CONNECTION_SETTINGS_CHANGED));
        registerReceiver(broadcastReceiver, new IntentFilter(BroadCastMessages.GARDEN_EVENT));
        registerReceiver(broadcastReceiver, new IntentFilter(BroadCastMessages.GARDEN_CURRENT_CHANGED));
        registerReceiver(broadcastReceiver, new IntentFilter(BroadCastMessages.SEED_DISPLAYLIST));
        registerReceiver(broadcastReceiver, new IntentFilter(BroadCastMessages.ACTION_EVENT));
        registerReceiver(broadcastReceiver, new IntentFilter(BroadCastMessages.ALLOTMENT_EVENT));

    }

    protected void displayDrawerMenu() {
        // nav drawer icons from resources
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);
        navDrawerItems = new ArrayList<NavDrawerItem>();

        // *************************
        // Catalogue
        // *************************
        NavDrawerItem navDrawerItem = new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1));
        navDrawerItems.add(navDrawerItem);
        displayDrawerMenuCatalogCounter();

        // *************************
        // Allotments
        // *************************
        navDrawerItem = new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1));
        navDrawerItems.add(navDrawerItem);
        displayDrawerMenuAllotmentCounter();
        // *************************
        // Actions
        // *************************
        navDrawerItem = new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1));
        navDrawerItems.add(navDrawerItem);
        displayDrawerMenuActionsCounter();

        // *************************
        // Profiles
        // *************************
        navDrawerItem = new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1));
        navDrawerItems.add(navDrawerItem);
        displayDrawerMenuProfileCounter();

        // *************************
        // Sensors
        // *************************
        navDrawerItem = new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1));
        navDrawerItems.add(navDrawerItem);

        // What's hot, We will add a counter here
        // navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1)));

        // Recycle the typed array
        navMenuIcons.recycle();

        // setting the nav drawer list adapter
        adapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
        mDrawerList.removeAllViewsInLayout();
        mDrawerList.setAdapter(adapter);

        findViewById(R.id.bt_share).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new AsyncTask<NavDrawerItem, Void, Integer>() {
                    private ImageView imageShare;

                    protected void onPreExecute() {
                        imageShare = (ImageView) findViewById(R.id.bt_share);
                    };

                    @Override
                    protected Integer doInBackground(NavDrawerItem... params) {
                        return gardenManager.share(gardenManager.getCurrentGarden(), "sebastien.fleury@gmail.com",
                                "Read");
                    }

                    @Override
                    protected void onPostExecute(Integer result) {
                        if (result.intValue() == -1)
                            imageShare.setImageDrawable(getResources().getDrawable(R.drawable.garden_unshared));
                        else
                            imageShare.setImageDrawable(getResources().getDrawable(R.drawable.garden_shared));
                        super.onPostExecute(result);
                    }
                }.execute();
            }
        });
    }

    protected void displayDrawerMenuProfileCounter() {
        new AsyncTask<NavDrawerItem, Void, Integer>() {
            NavDrawerItem item;

            @Override
            protected Integer doInBackground(NavDrawerItem... params) {
                item = params[0];
                return gardenManager.getMyGardens(false).size();
            }

            @Override
            protected void onPostExecute(Integer result) {
                item.setCounterVisibility(result > 0);
                item.setCount(result.toString());
                adapter.notifyDataSetChanged();

                super.onPostExecute(result);
            }
        }.execute(navDrawerItems.get(3));
    }

    protected void displayDrawerMenuActionsCounter() {
        new AsyncTask<NavDrawerItem, Void, Integer>() {
            NavDrawerItem item;

            @Override
            protected Integer doInBackground(NavDrawerItem... params) {
                item = params[0];
                return actionseedProvider.getActionsToDo().size();
            }

            @Override
            protected void onPostExecute(Integer result) {
                item.setCounterVisibility(result > 0);
                item.setCount(result.toString());
                adapter.notifyDataSetChanged();
                super.onPostExecute(result);
            }
        }.execute(navDrawerItems.get(2));
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
        }.execute(navDrawerItems.get(1));
    }

    protected void displayDrawerMenuCatalogCounter() {
        new AsyncTask<NavDrawerItem, Void, Integer>() {
            NavDrawerItem item;

            @Override
            protected Integer doInBackground(NavDrawerItem... params) {
                item = params[0];
                return seedManager.getVendorSeeds(false).size();
            }

            @Override
            protected void onPostExecute(Integer result) {
                item.setCounterVisibility(result > 0);
                item.setCount(result.toString());
                adapter.notifyDataSetChanged();
                super.onPostExecute(result);
            }
        }.execute(navDrawerItems.get(0));
    }

    /**
     * Slide menu item click listener
     * */
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
            browserIntent.putExtra(WebHelpActivity.URL, getClass().getSimpleName());
            startActivity(browserIntent);

            return true;
        case R.id.about:
            Intent aboutIntent = new Intent(this, AboutActivity.class);
            startActivity(aboutIntent);

            return true;
        case R.id.premium:
            FragmentManager fm = getSupportFragmentManager();
            GotsBillingDialog editNameDialog = new GotsBillingDialog();
            editNameDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
            editNameDialog.show(fm, "fragment_edit_name");
            return true;

        case R.id.settings:
            Intent settingsIntent = new Intent(this, PreferenceActivity.class);
            startActivity(settingsIntent);
            // FragmentTransaction ft = getFragmentManager().beginTransaction();
            // ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
            // ft.replace(R.id.idContent, new PreferenceActivity()).addToBackStack("back").commit();
            return true;

        case R.id.connection:
            LoginDialogFragment login = new LoginDialogFragment();
            login.show(getSupportFragmentManager(), TAG);
            return true;

        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /***
     * Called when invalidateOptionsMenu() is triggered
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // if nav drawer is opened, hide the action items
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerLinear);
        // menu.findItem(R.id.action_settings).setVisible(!drawerOpen);

        MenuItem itemConnected = (MenuItem) menu.findItem(R.id.connection);
        if (gotsPrefs.isConnectedToServer())
            itemConnected.setIcon(getResources().getDrawable(R.drawable.garden_connected));
        else
            itemConnected.setIcon(getResources().getDrawable(R.drawable.garden_disconnected));

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
        refreshGardenMenu();
        if (gotsPrefs.isConnectedToServer()) {
            UserInfo userInfoTask = new UserInfo();
            userInfoTask.execute((ImageView) findViewById(R.id.imageAvatar));
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
     * */
    private void displayView(int position) {
        // update the main content by replacing fragments
        Intent i = null;
        switch (position) {
        case 0:
            i = new Intent(getApplicationContext(), HutActivity.class);
            break;
        case 1:
            i = new Intent(getApplicationContext(), MyMainGarden.class);
            break;
        case 2:
            // fragment = new ActionActivity();
            i = new Intent(getApplicationContext(), ActionActivity.class);

            break;
        case 3:
            // fragment = new ProfileActivity();
            i = new Intent(getApplicationContext(), org.gots.ui.ProfileActivity.class);

            break;
        case 4:
            // fragment = new PagesFragment();
            GotsPurchaseItem purchaseItem = new GotsPurchaseItem(this);

            if (purchaseItem.getFeatureParrot() ? true : purchaseItem.isPremium()) {
                i = new Intent(this, SensorActivity.class);
            } else {
                // if (!purchaseItem.getFeatureParrot() && !purchaseItem.isPremium()) {
                FragmentManager fm = getSupportFragmentManager();
                GotsBillingDialog editNameDialog = new GotsBillingDialog(GotsPurchaseItem.SKU_FEATURE_PARROT);
                editNameDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
                editNameDialog.show(fm, "fragment_edit_name");
            }
            break;
        case 5:
            // fragment = new PreferenceActivity();
            break;

        default:
            break;
        }
        if (i != null) {
            startActivity(i);
        }

        Fragment fragment = new DashboardResumeFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();

        // update selected item and title, then close the drawer
        if (position <= navMenuTitles.length) {
            mDrawerList.setItemChecked(position, true);
            mDrawerList.setSelection(position);
            setTitle(navMenuTitles[position]);

        }
    }

    protected void refreshGardenMenu() {

        new AsyncTask<Void, Void, GardenInterface>() {

            @Override
            protected GardenInterface doInBackground(Void... params) {
                myGardens = gardenManager.getMyGardens(false);
                GardenInterface currentGarden = gardenManager.getCurrentGarden();

                if (currentGarden == null)
                    myGardens = gardenManager.getMyGardens(true);

                return currentGarden;
            }

            protected void onPostExecute(GardenInterface currentGarden) {
                if (currentGarden == null)

                    if (myGardens.size() > 0) {
                        gardenManager.setCurrentGarden(myGardens.get(0));
                        sendBroadcast(new Intent(BroadCastMessages.GARDEN_CURRENT_CHANGED));
                    } else {
                        Intent intent = new Intent(getApplicationContext(), ProfileCreationActivity.class);
                        startActivity(intent);
                        // AccountManager accountManager = AccountManager.get(getApplicationContext()
                        // );
                        // accountManager.addAccount(accountType, authTokenType, requiredFeatures, addAccountOptions,
                        // activity, callback, handler)
                    }
                int selectedGardenIndex = 0;
                String[] dropdownValues = new String[myGardens.size()];
                for (int i = 0; i < myGardens.size(); i++) {
                    GardenInterface garden = myGardens.get(i);
                    dropdownValues[i] = garden.getName() != null ? garden.getName() : garden.getLocality();
                    if (garden != null && currentGarden != null && garden.getId() == currentGarden.getId()) {
                        selectedGardenIndex = i;
                    }
                }
                if (dropdownValues.length > 0) {
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,
                            android.R.layout.simple_spinner_item, android.R.id.text1, dropdownValues);

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                    // actionBar.setListNavigationCallbacks(adapter, MainActivity.this);

                    spinnerGarden.setAdapter(adapter);
                    spinnerGarden.setSelection(selectedGardenIndex);
                }
            };
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
                downloadImage(user.getId(), user.getPictureURL());
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
                Bitmap usrLogo = BitmapFactory.decodeFile(file.getAbsolutePath());
                imageProfile.setImageBitmap(usrLogo);
            }
        };
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BroadCastMessages.WEATHER_DISPLAY_EVENT.equals(intent.getAction())) {
                // refreshWeatherWidget(intent);
            } else if (BroadCastMessages.CONNECTION_SETTINGS_CHANGED.equals(intent.getAction())) {
                refreshGardenMenu();
                invalidateOptionsMenu();
                // refreshWeatherWidget(intent);
            } else if (BroadCastMessages.GARDEN_EVENT.equals(intent.getAction())) {
                refreshGardenMenu();
            } else if (BroadCastMessages.GARDEN_CURRENT_CHANGED.equals(intent.getAction())) {
                displayDrawerMenu();
            } else if (BroadCastMessages.SEED_DISPLAYLIST.equals(intent.getAction())) {
                displayDrawerMenuCatalogCounter();
            } else if (BroadCastMessages.GARDEN_EVENT.equals(intent.getAction())) {
                displayDrawerMenuProfileCounter();
            } else if (BroadCastMessages.ACTION_EVENT.equals(intent.getAction())) {
                displayDrawerMenuActionsCounter();
            } else if (BroadCastMessages.ALLOTMENT_EVENT.equals(intent.getAction())) {
                displayDrawerMenuAllotmentCounter();
            }
        }

    };

    private Spinner spinnerGarden;
}