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

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.gots.R;
import org.gots.analytics.GotsAnalytics;
import org.gots.garden.GardenInterface;
import org.gots.garden.GardenManager;
import org.gots.garden.sql.GardenDBHelper;
import org.gots.help.HelpUriBuilder;
import org.gots.weather.WeatherCondition;
import org.gots.weather.WeatherManager;
import org.gots.weather.view.WeatherView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class ProfileActivity extends Activity implements LocationListener, OnClickListener {
	private LocationManager mlocManager;
	private Location location;
	private Address address;
	private String tag = "ProfileActivity";
	// EditText locality;
	private String choix_source = "";
	private ProgressDialog pd;
	private int gardenId;
	private GardenManager gardenManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		GotsAnalytics.getInstance(getApplication()).incrementActivityCount();
		GoogleAnalyticsTracker.getInstance().trackPageView(getClass().getSimpleName());

		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.profile);

		buildProfile();
		WeatherManager manager = new WeatherManager(this);

		LinearLayout weatherHistory = (LinearLayout) findViewById(R.id.layoutWeatherHistory);
		final HorizontalScrollView scrollView = (HorizontalScrollView) findViewById(R.id.scrollWeatherHistory);

		//set scrollview to the right end where today weather is displayed
		scrollView.post(new Runnable() {
			@Override
			public void run() {
				scrollView.scrollTo(scrollView.getWidth(), scrollView.getHeight());
			}
		});

		for (int i = -10; i <= 0; i++) {
			WeatherView view = new WeatherView(this);
			try {
				view.setWeather(manager.getCondition(i));
			} catch (Exception e) {
				Calendar weatherday = new GregorianCalendar();

				weatherday.setTime(Calendar.getInstance().getTime());
				weatherday.add(Calendar.DAY_OF_YEAR, i);
				WeatherCondition condition = new WeatherCondition();
				condition.setDate(weatherday.getTime());

				view.setWeather(condition);

			}
			weatherHistory.addView(view);

		}
	}

	private void buildProfile() {

		findViewById(R.id.buttonLocalize).setOnClickListener(this);

		findViewById(R.id.buttonValidatePosition).setOnClickListener(this);

		findViewById(R.id.buttonAddGarden).setOnClickListener(this);
		gardenManager = new GardenManager(this);

		if (gardenManager.getcurrentGarden() != null) {
			// TextView gardenName = (TextView)
			// findViewById(R.id.textGardenName);
			// gardenName.setText(gardenManager.getcurrentGarden().getLocality());

			findViewById(R.id.layoutMultiGarden).setVisibility(View.VISIBLE);
			findViewById(R.id.idGardenSelector).setVisibility(View.VISIBLE);

			Spinner gardenSelector = (Spinner) findViewById(R.id.idGardenSelector);
			initGardenList(gardenSelector);
		}
		findViewById(R.id.buttonAddGarden).setVisibility(View.VISIBLE);

		mlocManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		LinearLayout dashboardButton = (LinearLayout) findViewById(R.id.btReturn);
		dashboardButton.setOnClickListener(new LinearLayout.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

	}

	private void initGardenList(Spinner gardenSelector) {
		GardenDBHelper helper = new GardenDBHelper(this);

		String[] referenceList = helper.getGardens();

		if (referenceList == null)
			return;

		gardenId = (int) gardenManager.getcurrentGarden().getId();

		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, referenceList);
		gardenSelector.setAdapter(adapter);
		gardenSelector.setSelection(gardenId - 1);
		gardenSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
				gardenManager.selectGarden(position + 1);
				buildProfile();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void getPosition() {
		// on démarre le cercle de chargement
		setProgressBarIndeterminateVisibility(true);

		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);

		// LocationManager lm = (LocationManager)
		// getSystemService(LOCATION_SERVICE);
		pd = ProgressDialog.show(this, "", getResources().getString(R.string.gots_loading), false);
		pd.setCanceledOnTouchOutside(true);

		String bestProvider = mlocManager.getBestProvider(criteria, true);

		mlocManager.requestLocationUpdates(bestProvider, 60000, 0, this);

	}

	private void displayAddress() {

		// Le geocoder permet de récupérer ou chercher des adresses
		// gràce à un mot clé ou une position
		Geocoder geo = new Geocoder(ProfileActivity.this);
		try {
			// Ici on récupère la premiere adresse trouvé gràce à la position
			// que l'on a récupéré
			List<Address> adresses = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

			if (adresses != null && adresses.size() == 1) {
				address = adresses.get(0);
				// Si le geocoder a trouver une adresse, alors on l'affiche
				((TextView) findViewById(R.id.editTextLocality)).setHint(String.format("%s", address.getLocality()));
			} else {
				// sinon on affiche un message d'erreur
				((TextView) findViewById(R.id.editTextLocality)).setHint("L'adresse n'a pu être déterminée");
			}
		} catch (IOException e) {
			e.printStackTrace();
			((TextView) findViewById(R.id.editTextLocality)).setHint("L'adresse n'a pu être déterminée");
		}
		// on stop le cercle de chargement
		setProgressBarIndeterminateVisibility(false);
	}

	@Override
	public void onLocationChanged(Location location) {

		setProgressBarIndeterminateVisibility(false);
		this.location = location;
		displayAddress();
		pd.dismiss();
		mlocManager.removeUpdates(this);
	}

	@Override
	public void onProviderDisabled(String provider) {
		/* this is called if/when the GPS is disabled in settings */
		Log.v(tag, "Disabled");

		/* bring up the GPS settings */
		Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivity(intent);
	}

	@Override
	public void onProviderEnabled(String provider) {
		Log.v(tag, "Enabled");
		Toast.makeText(this, "GPS Enabled", Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		switch (status) {
		case LocationProvider.OUT_OF_SERVICE:
			Log.v(tag, "Status Changed: Out of Service");
			Toast.makeText(this, "Status Changed: Out of Service", Toast.LENGTH_SHORT).show();
			break;
		case LocationProvider.TEMPORARILY_UNAVAILABLE:
			Log.v(tag, "Status Changed: Temporarily Unavailable");
			Toast.makeText(this, "Status Changed: Temporarily Unavailable", Toast.LENGTH_SHORT).show();
			break;
		case LocationProvider.AVAILABLE:
			Log.v(tag, "Status Changed: Available");
			Toast.makeText(this, "Status Changed: Available", Toast.LENGTH_SHORT).show();
			break;
		}
	}

	@Override
	protected void onResume() {
		// mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
		// 1000, 10f, this);
		super.onResume();
	}

	@Override
	protected void onPause() {
		// mlocManager.removeUpdates(this);
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// case R.id.buttonSelectSource:
		// selectSource();
		// break;
		case R.id.buttonLocalize:

			// GardenLocator locator = new GardenLocator(v.getContext());
			// locator.localizeGarden();
			displayInputAddress();
			getPosition();
			buildProfile();
			break;
		case R.id.buttonValidatePosition:

			// GardenLocator locator = new GardenLocator(v.getContext());
			// locator.localizeGarden();
			validatePosition();
			break;
		case R.id.buttonAddGarden:

			gardenManager = new GardenManager(this);
			gardenManager.addGarden();
			buildProfile();

			break;

		default:
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_profile, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.help:
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(HelpUriBuilder.getUri(getClass()
					.getSimpleName())));
			startActivity(browserIntent);

			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void displayInputAddress() {

		findViewById(R.id.layoutCityLocalisation).setVisibility(View.VISIBLE);
	}

	private void validatePosition() {

		GardenInterface garden = gardenManager.getcurrentGarden();

		if (garden == null) {
			gardenManager.addGarden();
			garden = gardenManager.getcurrentGarden();
		}
		if (location != null) {
			garden.setGpsLatitude(location.getLatitude());
			garden.setGpsLongitude(location.getLongitude());
			garden.setGpsAltitude(location.getAltitude());
		}

		String locality = ((TextView) (findViewById(R.id.editTextLocality))).getText().toString();

		if ("".equals(locality))
			locality = ((TextView) (findViewById(R.id.editTextLocality))).getHint().toString();
		// Toast.makeText(this, "La ville ne doit pas être vide",
		// Toast.LENGTH_SHORT).show();

		garden.setLocality(locality);
		garden.setCountryName(Locale.getDefault().getDisplayCountry());
		GardenDBHelper helper = new GardenDBHelper(this);
		helper.updateGarden(garden);

		WeatherManager weatherManager = new WeatherManager(this);
		weatherManager.update();
		this.finish();

	}

	@Override
	protected void onDestroy() {
		GotsAnalytics.getInstance(getApplication()).decrementActivityCount();
		super.onDestroy();
	}
}
