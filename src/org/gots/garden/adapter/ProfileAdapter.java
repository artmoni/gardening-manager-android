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
import org.gots.authentication.GoogleAuthentication;
import org.gots.authentication.GotsSocialAuthentication;
import org.gots.authentication.User;
import org.gots.garden.GardenInterface;
import org.gots.garden.GardenManager;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.google.android.gms.auth.UserRecoverableAuthException;

public class ProfileAdapter extends BaseAdapter {

    private Context mContext;

    private List<GardenInterface> myGardens = new ArrayList<GardenInterface>();

    private LayoutInflater inflater;

    private ImageView weatherState;

    // private Intent weatherIntent;
    private WeatherManager weatherManager;

    private GardenManager gardenManager;

    private GardenInterface selectedGarden;

    public ProfileAdapter(Context context, List<GardenInterface> myGardens) {
        mContext = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        weatherManager = new WeatherManager(mContext);
        gardenManager = GardenManager.getInstance();

        this.myGardens = myGardens;
        selectedGarden = gardenManager.getCurrentGarden();

        gotsPreferences = GotsPreferences.getInstance().initIfNew(mContext);

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
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            if (user != null) {
                File file = new File(mContext.getCacheDir() + "/" + user.getId().toLowerCase().replaceAll("\\s", ""));
                Bitmap usrLogo = BitmapFactory.decodeFile(file.getAbsolutePath());
                imageProfile.setImageBitmap(usrLogo);
            }
        };
    }

    @SuppressWarnings("deprecation")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View vi = convertView;

        if (convertView == null || convertView.findViewById(R.id.idGardenName) == null)
            vi = inflater.inflate(R.layout.list_garden, null);

        TextView gardenName = (TextView) vi.findViewById(R.id.idGardenName);
        weatherState = (ImageView) vi.findViewById(R.id.idWeatherConnected);
        ImageView imageProfile = (ImageView) vi.findViewById(R.id.imageProfile);
        weatherHistory = (LinearLayout) vi.findViewById(R.id.layoutWeatherHistory);
        final HorizontalScrollView weatherHistoryContainer = (HorizontalScrollView) vi.findViewById(R.id.scrollWeatherHistory);

        final GardenInterface currentGarden = getItem(position);
        // weatherIntent = new Intent(mContext, WeatherUpdateService.class);

        if (selectedGarden != null && currentGarden != null && selectedGarden.getId() == currentGarden.getId()) {
            vi.setSelected(true);
            imageProfile.setVisibility(View.VISIBLE);
            weatherState.setVisibility(View.VISIBLE);
            // weatherHistory.setVisibility(View.VISIBLE);
            weatherHistoryContainer.setVisibility(View.VISIBLE);
            int sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                weatherState.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_weather));
            } else {
                weatherState.setBackground(mContext.getResources().getDrawable(R.drawable.bg_weather));
            }
            UserInfo userInfoTask = new UserInfo();
            userInfoTask.execute(imageProfile);
            // mContext.startService(weatherIntent);
            // mContext.registerReceiver(weatherBroadcastReceiver, new
            // IntentFilter(
            // WeatherUpdateService.BROADCAST_ACTION));

        } else {
            vi.setSelected(false);
            // vi.getBackground().setAlpha(200);
            imageProfile.setVisibility(View.GONE);
            weatherState.setVisibility(View.GONE);
            // weatherHistory.setVisibility(View.GONE);
            weatherHistoryContainer.setVisibility(View.GONE);

        }

        if (GotsPreferences.DEBUG)
            gardenName.setText(currentGarden.toString());
        else if (currentGarden.getName() != null)
            gardenName.setText(currentGarden.getName().substring(0, 1).toUpperCase()
                    + currentGarden.getName().substring(1));
        else
            gardenName.setText(currentGarden.getAddress().getLocality().substring(0, 1).toUpperCase()
                    + currentGarden.getAddress().getLocality().substring(1));

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
            weatherHistoryContainer.post(new Runnable() {
                @Override
                public void run() {
                    weatherHistoryContainer.scrollTo(weatherHistoryContainer.getWidth(),
                            weatherHistoryContainer.getHeight());
                }
            });
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
        } else
            vi.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    selectedGarden = getItem(position);
                    gardenManager.setCurrentGarden(selectedGarden);
                    notifyDataSetChanged();

                    weatherState.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.bg_weather));
                    weatherState.setImageDrawable(mContext.getResources().getDrawable(R.drawable.weather_updating));
                    // mContext.startService(weatherIntent);
                    // mContext.registerReceiver(weatherBroadcastReceiver, new
                    // IntentFilter(
                    // WeatherUpdateService.BROADCAST_ACTION));

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

    private ViewGroup weatherHistory;

    private User user;

    private GotsPreferences gotsPreferences;

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

}
