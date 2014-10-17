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
package org.gots.garden.adapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.gots.R;
import org.gots.authentication.GotsSocialAuthentication;
import org.gots.authentication.provider.google.GoogleAuthentication;
import org.gots.authentication.provider.google.User;
import org.gots.broadcast.BroadCastMessages;
import org.gots.context.GotsContext;
import org.gots.garden.GardenInterface;
import org.gots.garden.GotsGardenManager;
import org.gots.preferences.GotsPreferences;
import org.gots.seed.service.SeedUpdateService;
import org.gots.weather.WeatherCondition;
import org.gots.weather.WeatherConditionInterface;
import org.gots.weather.WeatherManager;
import org.gots.weather.view.WeatherView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.UserRecoverableAuthException;

public class ProfileAdapter extends BaseAdapter {

    private Context mContext;

    private List<GardenInterface> myGardens = new ArrayList<GardenInterface>();

    private LayoutInflater inflater;

    // private Intent weatherIntent;
    private WeatherManager weatherManager;

    private GotsGardenManager gardenManager;

    private GardenInterface selectedGarden;

    protected GotsContext getGotsContext() {
        return GotsContext.get(mContext);
    }

    public ProfileAdapter(Context context, List<GardenInterface> myGardens) {
        mContext = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        weatherManager = new WeatherManager(mContext);
        gardenManager = GotsGardenManager.getInstance();

        this.myGardens = myGardens;
        selectedGarden = gardenManager.getCurrentGarden();

        gotsPreferences = getGotsContext().getServerConfig();

    }

    @Override
    public int getCount() {
        return myGardens.size();
    }

    @Override
    public GardenInterface getItem(int position) {
        // if (position % frequencyAds > 0 &&
        // !GotsPreferences.getInstance(mContext).isPremium())
        // position = position - (position / frequencyAds + 1);
        return myGardens.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    private void downloadImage(String userid, String url) {
        if (userid == null)
            return;
        File file = new File(mContext.getCacheDir() + "/" + userid.toLowerCase().replaceAll("\\s", ""));
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

        @Override
        protected Void doInBackground(ImageView... params) {
            imageProfile = params[0];
            GotsSocialAuthentication authentication = new GoogleAuthentication(mContext);
            try {
                String token = authentication.getToken(gotsPreferences.getNuxeoLogin());
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
                File file = new File(mContext.getCacheDir() + "/" + user.getId().toLowerCase().replaceAll("\\s", ""));
                Bitmap usrLogo = BitmapFactory.decodeFile(file.getAbsolutePath());
                imageProfile.setImageBitmap(usrLogo);
            }
        };
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View vi = convertView;

        if (convertView == null || convertView.findViewById(R.id.idGardenName) == null)
            vi = inflater.inflate(R.layout.list_garden, null);

        TextView gardenName = (TextView) vi.findViewById(R.id.idGardenName);
        TextView gardenLocality = (TextView) vi.findViewById(R.id.textCity);
        TextView gardenDescription = (TextView) vi.findViewById(R.id.textGardenDescription);
        ImageView imageProfile = (ImageView) vi.findViewById(R.id.imageProfile);
        weatherHistory = (LinearLayout) vi.findViewById(R.id.layoutWeatherHistory);
        View weatherChart = vi.findViewById(R.id.idWeatherChart);

        // final HorizontalScrollView weatherHistoryContainer = (HorizontalScrollView)
        // vi.findViewById(R.id.scrollWeatherHistory);

        final GardenInterface currentGarden = getItem(position);
        // weatherIntent = new Intent(mContext, WeatherUpdateService.class);

        if (selectedGarden != null && currentGarden != null && selectedGarden.getId() == currentGarden.getId()) {
            vi.setSelected(true);
            imageProfile.setVisibility(View.VISIBLE);
            weatherChart.setVisibility(View.VISIBLE);
            // weatherHistory.setVisibility(View.VISIBLE);
            // weatherHistoryContainer.setVisibility(View.VISIBLE);
            int sdk = android.os.Build.VERSION.SDK_INT;
            if (gotsPreferences.isConnectedToServer()) {
                UserInfo userInfoTask = new UserInfo();
                userInfoTask.execute(imageProfile);
            }// mContext.startService(weatherIntent);
             // mContext.registerReceiver(weatherBroadcastReceiver, new
             // IntentFilter(
             // WeatherUpdateService.BROADCAST_ACTION));

        } else {
            vi.setSelected(false);
            weatherChart.setVisibility(View.GONE);

            // vi.getBackground().setAlpha(200);
            imageProfile.setVisibility(View.GONE);
            // weatherHistory.setVisibility(View.GONE);
            // weatherHistoryContainer.setVisibility(View.GONE);

        }

        if (GotsPreferences.DEBUG)
            gardenName.setText(currentGarden.toString());
        else if (currentGarden.getName() != null) {
            gardenName.setText(currentGarden.getName());
            gardenLocality.setText(currentGarden.getAddress().getLocality());
        } else
            gardenName.setText(currentGarden.getAddress().getLocality());

        if (currentGarden.getDescription() != null) {
            gardenDescription.setText(currentGarden.getDescription());
        } else
            gardenDescription.setVisibility(View.GONE);
        // weatherState.setOnClickListener(new View.OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // weatherState.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_weather));
        // weatherState.setImageDrawable(mContext.getResources().getDrawable(R.drawable.weather_updating));
        // mContext.startService(weatherIntent);
        // mContext.registerReceiver(weatherBroadcastReceiver, new
        // IntentFilter(
        // WeatherUpdateService.BROADCAST_ACTION));
        // // currentView = conve;
        //
        // }
        // });

        // *************** WEATHER HISTORY
        if (vi.isSelected()) {
            // weatherHistoryContainer.post(new Runnable() {
            // @Override
            // public void run() {
            // weatherHistoryContainer.scrollTo(weatherHistoryContainer.getWidth(),
            // weatherHistoryContainer.getHeight());
            // }
            // });
            if (weatherHistory.getChildCount() > 0)
                weatherHistory.removeAllViews();

            for (int i = -10; i <= 0; i++) {
                WeatherConditionInterface condition;
                try {
                    condition = weatherManager.getCondition(i);
                } catch (Exception e) {
                    Calendar weatherday = new GregorianCalendar();
                    weatherday.setTime(Calendar.getInstance().getTime());
                    weatherday.add(Calendar.DAY_OF_YEAR, i);

                    condition = new WeatherCondition();
                    condition.setDate(weatherday.getTime());
                }

                WeatherView view = new WeatherView(mContext);
                view.setWeather(condition);
                view.setPadding(2, 0, 2, 0);
                weatherHistory.addView(view);

            }
            displayWeatherChart(vi);
        } else
            vi.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    selectedGarden = getItem(position);
                    gardenManager.setCurrentGarden(selectedGarden);
                    mContext.sendBroadcast(new Intent(BroadCastMessages.GARDEN_EVENT));
                    mContext.sendBroadcast(new Intent(BroadCastMessages.GARDEN_CURRENT_CHANGED));
                    notifyDataSetChanged();

                    if (gardenManager.getCurrentGarden() != null) {
                        GoogleAnalyticsTracker tracker = GoogleAnalyticsTracker.getInstance();
                        tracker.trackEvent("Garden", "Select", gardenManager.getCurrentGarden().getLocality(),
                                position + 1);
                        Intent seedIntent = new Intent(mContext, SeedUpdateService.class);
                        mContext.startService(seedIntent);
                    }
                }
            });

        return vi;

    }

    private void displayWeatherChart(View parent) {
        // idWeatherConnected
        final WebView webView = (WebView) parent.findViewById(R.id.idWeatherChart);
        String serieTempMin = new String();
        String serieTempMax = new String();
        String chl = new String();

        for (int i = -10; i <= 0; i++) {
            WeatherConditionInterface condition;
            try {
                condition = weatherManager.getCondition(i);
            } catch (Exception e) {
                Calendar weatherday = new GregorianCalendar();
                weatherday.setTime(Calendar.getInstance().getTime());
                weatherday.add(Calendar.DAY_OF_YEAR, i);

                condition = new WeatherCondition();
                condition.setDate(weatherday.getTime());
            }

            serieTempMin = serieTempMin.concat(String.valueOf(condition.getTempCelciusMin()));
            serieTempMin = serieTempMin.concat(",");
            serieTempMax = serieTempMax.concat(String.valueOf(condition.getTempCelciusMax()));
            serieTempMax = serieTempMax.concat(",");
            if (condition.getDate() != null)
                chl = chl.concat(DateFormat.format("dd", condition.getDate()).toString());

            else
                chl = chl.concat("?");
            chl = chl.concat(",");
        }
        if (serieTempMin.length() > 1)
            serieTempMin = serieTempMin.substring(0, serieTempMin.length() - 1);
        if (serieTempMax.length() > 1)
            serieTempMax = serieTempMax.substring(0, serieTempMax.length() - 1);
        if (chl.length() > 1)
            chl = chl.substring(0, chl.length() - 1);

        Calendar min = Calendar.getInstance();
        if (weatherManager.getCondition(-10).getDate() != null)
            min.setTime(weatherManager.getCondition(-10).getDate());
        Calendar max = Calendar.getInstance();
        if (weatherManager.getCondition(0).getDate() != null)
            max.setTime(weatherManager.getCondition(0).getDate());

        String url = "http://chart.apis.google.com/chart?cht=lc&chs=250x100&chd=t:" + serieTempMin + "|" + serieTempMax
                + "&chxt=x,y&chxr=0," + min.get(Calendar.DAY_OF_MONTH) + "," + max.get(Calendar.DAY_OF_MONTH)
                + ",1|1,-50,50&chds=-50,50&chco=009999,B65635";
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                // Log.i("WEB_VIEW_TEST", "error code:" + errorCode);
                super.onReceivedError(view, errorCode, description, failingUrl);
                webView.setVisibility(View.GONE);
            }
        });
        webView.loadUrl(url);
    }

    private ViewGroup weatherHistory;

    private User user;

    private GotsPreferences gotsPreferences;

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

}
