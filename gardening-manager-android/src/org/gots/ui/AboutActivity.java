package org.gots.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import org.gots.R;
import org.gots.action.service.ActionTODOBroadcastReceiver;
import org.gots.analytics.GotsAnalytics;
import org.gots.broadcast.BroadCastMessages;
import org.gots.inapp.GotsPurchaseItem;
import org.gots.preferences.GotsPreferences;
import org.gots.provider.ActionsContentProvider;
import org.gots.provider.AllotmentContentProvider;
import org.gots.provider.GardenContentProvider;
import org.gots.provider.SeedsContentProvider;
import org.gots.provider.SensorContentProvider;
import org.gots.provider.WeatherContentProvider;
import org.gots.seed.service.SeedUpdateService;

import android.accounts.Account;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.vending.billing.util.IabHelper;
import com.android.vending.billing.util.IabResult;
import com.android.vending.billing.util.Inventory;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class AboutActivity extends AbstractActivity {
    private String TAG = "AboutActivity";

    protected static Handler splashHandler;

    private View progressSeed;

    private View progressWeather;

    private View progressAction;

    private View progressGarden;

    private View progressPurchase;

    protected int refreshCounter;

    private TextView textprogressWeather;

    private TextView textprogressAction;

    private TextView textprogressSeed;

    private TextView textprogressGarden;

    private TextView textprogressPurchase;

    private IabHelper buyHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        new AsyncTask<Void, Integer, String>() {
            private TextView name;

            @Override
            protected void onPreExecute() {
                name = (TextView) findViewById(R.id.textVersion);
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Void... params) {
                PackageInfo pInfo;
                String version = "";
                try {
                    pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    version = pInfo.versionName;

                } catch (NameNotFoundException e) {
                    e.printStackTrace();
                }
                setRecurringAlarm(getApplicationContext());

                return version;
            }

            protected void onPostExecute(String version) {
                name.setText("version " + version);
            };
        }.execute();

        setButtonClickable(R.id.webArtmoni, GotsPreferences.URL_ARTMONI);
        setButtonClickable(R.id.webSauterDansLesFlaques, GotsPreferences.URL_SAUTERDANSLESFLAQUES);
        setButtonClickable(R.id.idSocialGoogle, GotsPreferences.URL_GOOGLEPLUS_GARDENING_MANAGER);
        setButtonClickable(R.id.idSocialFacebook, GotsPreferences.URL_FACEBOOK_GARDENING_MANAGER);
        setButtonClickable(R.id.idSocialTwitter, GotsPreferences.URL_TWITTER_GARDENING_MANAGER);
        setButtonClickable(R.id.idTranslateButton, GotsPreferences.URL_TRANSLATE_GARDENING_MANAGER);

        progressWeather = findViewById(R.id.imageProgressWeather);
        progressSeed = findViewById(R.id.imageProgressSeed);
        progressAction = findViewById(R.id.imageProgressAction);
        progressGarden = findViewById(R.id.imageProgressGarden);
        progressPurchase = findViewById(R.id.imageProgressPurchase);

        textprogressWeather = (TextView) findViewById(R.id.textProgressWeather);
        textprogressSeed = (TextView) findViewById(R.id.textProgressSeed);
        textprogressAction = (TextView) findViewById(R.id.textProgressAction);
        textprogressGarden = (TextView) findViewById(R.id.textProgressGarden);
        textprogressPurchase = (TextView) findViewById(R.id.textProgressPurchase);

        ImageView flag = (ImageView) findViewById(R.id.imageTranslateFlag);
        int flagRessource = getResources().getIdentifier(
                "org.gots:drawable/" + Locale.getDefault().getCountry().toLowerCase(), null, null);
        flag.setImageResource(flagRessource);

        registerReceiver(updateReceiver, new IntentFilter(BroadCastMessages.PROGRESS_UPDATE));
        registerReceiver(updateReceiver, new IntentFilter(BroadCastMessages.PROGRESS_FINISHED));
    }

    private BroadcastReceiver updateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String authority = "";
            if (intent.getExtras() != null)
                authority = intent.getExtras().getString("AUTHORITY");

            if (BroadCastMessages.PROGRESS_UPDATE.equals(intent.getAction())) {
                addProgress();
                if (SeedsContentProvider.AUTHORITY.equals(authority)) {
                    Animation myFadeInAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.tween);
                    progressSeed.startAnimation(myFadeInAnimation);
                    textprogressSeed.setText(getResources().getString(R.string.synchro_seeds_checking));
                    textprogressSeed.setTextColor(getResources().getColor(R.color.action_warning_color));

                } else if (WeatherContentProvider.AUTHORITY.equals(authority)) {
                    Animation myFadeInAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.tween);
                    progressWeather.startAnimation(myFadeInAnimation);
                    textprogressWeather.setText(getResources().getString(R.string.synchro_weather_checking));
                    textprogressWeather.setTextColor(getResources().getColor(R.color.action_warning_color));

                } else if (GardenContentProvider.AUTHORITY.equals(authority)) {
                    Animation myFadeInAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.tween);
                    progressGarden.startAnimation(myFadeInAnimation);
                    textprogressGarden.setText(getResources().getString(R.string.synchro_garden_checking));
                    textprogressGarden.setTextColor(getResources().getColor(R.color.action_warning_color));

                } else if (ActionsContentProvider.AUTHORITY.equals(authority)) {
                    Animation myFadeInAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.tween);
                    progressAction.startAnimation(myFadeInAnimation);
                    textprogressAction.setText(getResources().getString(R.string.synchro_actions_checking));
                    textprogressAction.setTextColor(getResources().getColor(R.color.action_warning_color));

                }
                // else if (WeatherContentProvider.AUTHORITY.equals(authority)) {
                // Animation myFadeInAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.tween);
                // progressWeather.startAnimation(myFadeInAnimation);
                // textprogressWeather.setTextColor(getResources().getColor(R.color.action_warning_color));
                // textprogressWeather.setText(getResources().getString(R.string.synchro_weather_checking));
                // }
            } else if (BroadCastMessages.PROGRESS_FINISHED.equals(intent.getAction())) {
                if (SeedsContentProvider.AUTHORITY.equals(authority)) {
                    progressSeed.clearAnimation();
                    progressSeed.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_state_ok));
                    textprogressSeed.setText(getResources().getString(R.string.synchro_seeds_ok));
                    textprogressSeed.setTextColor(getResources().getColor(R.color.text_color_dark));

                } else if (AllotmentContentProvider.AUTHORITY.equals(authority)) {
                    progressWeather.clearAnimation();
                    progressWeather.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_state_ok));
                    textprogressWeather.setText(getResources().getString(R.string.synchro_weather_ok));
                    textprogressWeather.setTextColor(getResources().getColor(R.color.text_color_dark));
                } else if (GardenContentProvider.AUTHORITY.equals(authority)) {
                    progressGarden.clearAnimation();
                    progressGarden.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_state_ok));
                    textprogressGarden.setText(getResources().getString(R.string.synchro_garden_ok));
                    textprogressGarden.setTextColor(getResources().getColor(R.color.text_color_dark));

                } else if (ActionsContentProvider.AUTHORITY.equals(authority)) {
                    progressAction.clearAnimation();
                    progressAction.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_state_ok));
                    textprogressAction.setText(getResources().getString(R.string.synchro_actions_ok));
                    textprogressAction.setTextColor(getResources().getColor(R.color.text_color_dark));

                }
                // else if (WeatherContentProvider.AUTHORITY.equals(authority)) {
                // progressWeather.clearAnimation();
                // progressWeather.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_state_ok));
                //
                // textprogressWeather.setText(getResources().getString(R.string.synchro_weather_ok));
                // textprogressWeather.setTextColor(getResources().getColor(R.color.text_color_dark));
                // }
                removeProgress();
            }
        }
    };

    protected void setButtonClickable(int viewId, final String url) {
        View button = (View) findViewById(viewId);
        if (button != null)
            button.setOnClickListener(new LinearLayout.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
                    GotsAnalytics.getInstance(getApplication()).incrementActivityCount();
                    GoogleAnalyticsTracker.getInstance().trackPageView(url);
                }
            });
    }

    protected void addProgress() {
        refreshCounter++;
    };

    protected void removeProgress() {
        refreshCounter--;
    };

    @Override
    protected void onRefresh(String AUTHORITY) {
        refreshCounter = 0;

        // weatherServiceIntent = new Intent(getApplicationContext(), WeatherUpdateService.class);

        // getApplicationContext().startService(weatherServiceIntent);

        /*
         * Synchronize Weather
         */
        // new AsyncTask<Void, Integer, Void>() {
        //
        // protected void onPreExecute() {
        // addProgress();

        //
        // };
        //
        // @Override
        // protected Void doInBackground(Void... params) {
        // WeatherManager manager = new WeatherManager(getApplicationContext());
        // manager.getConditionSet(2);
        // return null;
        // }
        //
        // @Override
        // protected void onPostExecute(Void result) {

        //
        // removeProgress();
        // super.onPostExecute(result);
        //
        // }
        // }.execute();

        /*
         * Synchronize Seeds
         */

        Account newAccount = gotsPrefs.getUserAccount();
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);

        ContentResolver.setSyncAutomatically(newAccount, SeedsContentProvider.AUTHORITY, true);
        ContentResolver.requestSync(newAccount, SeedsContentProvider.AUTHORITY, bundle);
        ContentResolver.setSyncAutomatically(newAccount, GardenContentProvider.AUTHORITY, true);
        ContentResolver.requestSync(newAccount, GardenContentProvider.AUTHORITY, bundle);
        ContentResolver.setSyncAutomatically(newAccount, ActionsContentProvider.AUTHORITY, true);
        ContentResolver.requestSync(newAccount, ActionsContentProvider.AUTHORITY, bundle);
        ContentResolver.setSyncAutomatically(newAccount, SensorContentProvider.AUTHORITY, true);
        ContentResolver.requestSync(newAccount, SensorContentProvider.AUTHORITY, bundle);
        ContentResolver.setSyncAutomatically(newAccount, WeatherContentProvider.AUTHORITY, true);
        ContentResolver.requestSync(newAccount, WeatherContentProvider.AUTHORITY, bundle);

        /*
         * Synchronize Purchase feature
         */
        final ArrayList<String> moreSkus = new ArrayList<String>();
        moreSkus.add(GotsPurchaseItem.SKU_PREMIUM);
        moreSkus.add(GotsPurchaseItem.SKU_FEATURE_PDFHISTORY);
        moreSkus.add(GotsPurchaseItem.SKU_FEATURE_PARROT);
        buyHelper = new IabHelper(getApplicationContext(), gotsPrefs.getPlayStorePubKey());

        Animation myFadeInAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.tween);
        progressPurchase.startAnimation(myFadeInAnimation);
        textprogressPurchase.setText(getResources().getString(R.string.synchro_purchase_checking));
        textprogressPurchase.setTextColor(getResources().getColor(R.color.action_warning_color));

        try {
            addProgress();
            buyHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {

                @Override
                public void onIabSetupFinished(IabResult result) {
                    // Toast.makeText(getApplicationContext(), "Set up finished!", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "Set up finished!");

                    if (result.isSuccess())
                        buyHelper.queryInventoryAsync(true, moreSkus, new IabHelper.QueryInventoryFinishedListener() {
                            @Override
                            public void onQueryInventoryFinished(IabResult result, Inventory inv) {
                                if (result.isSuccess()) {
                                    gotsPurchase.setPremium(inv.hasPurchase(GotsPurchaseItem.SKU_PREMIUM));
                                    gotsPurchase.setFeatureExportPDF(inv.hasPurchase(GotsPurchaseItem.SKU_FEATURE_PDFHISTORY));
                                    gotsPurchase.setFeatureParrot(inv.hasPurchase(GotsPurchaseItem.SKU_FEATURE_PARROT));
                                    Log.i(TAG, "Successful got inventory!");

                                } else {
                                    Log.i(TAG, "Error getting inventory!");
                                }
                                progressPurchase.clearAnimation();
                                progressPurchase.setBackgroundDrawable(getResources().getDrawable(
                                        R.drawable.bg_state_ok));
                                textprogressPurchase.setText(getResources().getString(R.string.synchro_purchase_ok));
                                textprogressPurchase.setTextColor(getResources().getColor(R.color.text_color_dark));

                                // getApplicationContext().stopService(startServiceIntent4);
                                removeProgress();
                            }
                        });
                    else {
                        removeProgress();
                    }

                }
            });
        } catch (Exception e) {
            Log.e(TAG, "IabHelper can not be initialized" + e.getMessage());
            removeProgress();

        }
        // new AsyncTask<Void, Integer, Void>() {
        // // Intent startServiceIntent4 = new Intent(getApplicationContext(), GotsBillingService.class);
        //
        //
        // protected void onPreExecute() {
        // Animation myFadeInAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.tween);
        // progressPurchase.startAnimation(myFadeInAnimation);
        // textprogressAction.setText(getResources().getString(R.string.synchro_purchase_checking));
        //
        // addProgress();
        // };
        //
        // @Override
        // protected Void doInBackground(Void... params) {
        // // getApplicationContext().startService(startServiceIntent4);
        // final ArrayList<String> moreSkus = new ArrayList<String>();
        // moreSkus.add(GotsPurchaseItem.SKU_PREMIUM);
        // buyHelper = new IabHelper(getApplicationContext(), gotsPrefs.getPlayStorePubKey());
        //
        // buyHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
        //
        // @Override
        // public void onIabSetupFinished(IabResult result) {
        // // Toast.makeText(getApplicationContext(), "Set up finished!", Toast.LENGTH_SHORT).show();
        // Log.i(TAG, "Set up finished!");
        //
        // if (result.isSuccess())
        // buyHelper.queryInventoryAsync(true, moreSkus,
        // new IabHelper.QueryInventoryFinishedListener() {
        // @Override
        // public void onQueryInventoryFinished(IabResult result, Inventory inv) {
        // if (result.isSuccess()) {
        // boolean isPremium = inv.hasPurchase(GotsPurchaseItem.SKU_PREMIUM);
        // gotsPrefs.setPremium(isPremium);
        // Log.i(TAG, "Successful got inventory!");
        //
        // } else {
        // Log.i(TAG, "Error getting inventory!");
        // }
        // }
        // });
        // }
        // });
        // return null;
        // }
        //
        // @Override
        // protected void onPostExecute(Void result) {
        // progressPurchase.clearAnimation();
        // progressPurchase.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_state_ok));
        // textprogressAction.setText(getResources().getString(R.string.synchro_purchase_ok));
        // // getApplicationContext().stopService(startServiceIntent4);
        // removeProgress();
        // super.onPostExecute(result);
        // }
        // }.execute();
        /*
         * Synchronize Server
         */
        // if (gotsPrefs.isConnectedToServer()) {
        // findViewById(R.id.layoutProgressGarden).setVisibility(View.VISIBLE);
        //
        // new AsyncTask<Context, Void, List<GardenInterface>>() {
        //
        // private List<GardenInterface> myGardens;
        //
        // @Override
        // protected void onPreExecute() {
        // Animation myFadeInAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.tween);
        // progressGarden.startAnimation(myFadeInAnimation);
        // textprogressGarden.setText(getResources().getString(R.string.synchro_garden_checking));
        // textprogressGarden.setTextColor(getResources().getColor(R.color.action_warning_color));
        //
        // super.onPreExecute();
        // }
        //
        // @Override
        // protected List<GardenInterface> doInBackground(Context... params) {
        //
        // myGardens = gardenManager.getMyGardens(true);
        // return myGardens;
        // }
        //
        // @Override
        // protected void onPostExecute(List<GardenInterface> result) {

        // super.onPostExecute(result);
        // }
        //
        // }.execute();
        // } else {
        // findViewById(R.id.layoutProgressGarden).setVisibility(View.GONE);
        // }
    }

    private void setRecurringAlarm(Context context) {
        // we know mobiletuts updates at right around 1130 GMT.
        // let's grab new stuff at around 11:45 GMT, inexactly
        Calendar updateTime = Calendar.getInstance();

        Intent actionBroadcastReceiver = new Intent(context, ActionTODOBroadcastReceiver.class);
        PendingIntent actionTODOIntent = PendingIntent.getBroadcast(context, 0, actionBroadcastReceiver,
                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarms = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (GotsPreferences.isDevelopment())
            alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP, updateTime.getTimeInMillis(), 120000, actionTODOIntent);
        else {
            updateTime.set(Calendar.HOUR_OF_DAY, 20);
            alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP, updateTime.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, actionTODOIntent);
        }

        Intent seedBroadcastReceiver = new Intent(context, SeedUpdateService.class);
        PendingIntent seedIntent = PendingIntent.getBroadcast(context, 0, seedBroadcastReceiver,
                PendingIntent.FLAG_UPDATE_CURRENT);

        if (GotsPreferences.isDevelopment())
            alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP, updateTime.getTimeInMillis(), 120000, seedIntent);
        else {
            updateTime.set(Calendar.HOUR_OF_DAY, 12);
            alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP, updateTime.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, seedIntent);
        }
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(updateReceiver);
        super.onDestroy();
    }
}
