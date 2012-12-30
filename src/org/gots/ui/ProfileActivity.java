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

import org.gots.R;
import org.gots.analytics.GotsAnalytics;
import org.gots.garden.GardenManager;
import org.gots.garden.sql.GardenDBHelper;
import org.gots.help.HelpUriBuilder;
import org.gots.weather.WeatherCondition;
import org.gots.weather.WeatherManager;
import org.gots.weather.view.WeatherView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class ProfileActivity extends SherlockActivity implements  OnClickListener {
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
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.profile);

		ActionBar bar = getSupportActionBar();
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setTitle(R.string.dashboard_profile_name);

		GotsAnalytics.getInstance(getApplication()).incrementActivityCount();
		GoogleAnalyticsTracker.getInstance().trackPageView(getClass().getSimpleName());

		buildProfile();
		WeatherManager manager = new WeatherManager(this);

		LinearLayout weatherHistory = (LinearLayout) findViewById(R.id.layoutWeatherHistory);
		final HorizontalScrollView scrollView = (HorizontalScrollView) findViewById(R.id.scrollWeatherHistory);

		// set scrollview to the right end where today weather is displayed
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

		gardenManager = new GardenManager(this);

		Spinner gardenSelector = (Spinner) findViewById(R.id.idGardenSelector);
		initGardenList(gardenSelector);
		

	}

	private void initGardenList(Spinner gardenSelector) {
		GardenDBHelper helper = new GardenDBHelper(this);

		String[] referenceList = helper.getGardens();

		if (referenceList == null)
			return;

		gardenId = (int) gardenManager.getcurrentGarden().getId();

		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item_text, referenceList);
		gardenSelector.setAdapter(adapter);
		gardenSelector.setSelection(gardenId - 1);
		gardenSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
				gardenManager.selectGarden(position + 1);
				// buildProfile();

				GoogleAnalyticsTracker tracker = GoogleAnalyticsTracker.getInstance();
				tracker.trackEvent("Garden", "Select", gardenManager.getcurrentGarden().getLocality(), position + 1);
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


	}

	private void displayAddress() {

		// Le geocoder permet de récupérer ou chercher des adresses
		// gràce à un mot clé ou une position
		Geocoder geo = new Geocoder(ProfileActivity.this);
		try {
			// Ici on récupère la premiere adresse trouvé gràce à la
			// position
			// que l'on a récupéré
			List<Address> adresses = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

			if (adresses != null && adresses.size() == 1) {
				address = adresses.get(0);
				// Si le geocoder a trouver une adresse, alors on l'affiche
				((TextView) findViewById(R.id.editTextLocality)).setHint(String.format("%s", address.getLocality()));
				((TextView) findViewById(R.id.editTextLocality)).setText(String.format("%s", address.getLocality()));

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
	protected void onResume() {
		buildProfile();
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
		case android.R.id.home:
			finish();
			break;
		
		

		default:
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// MenuInflater inflater = getMenuInflater();
		if (gardenManager.getcurrentGarden() != null) {

			MenuInflater inflater = getSupportMenuInflater();
			inflater.inflate(R.menu.menu_profile, menu);
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {

		case android.R.id.home:
			finish();
			return true;

		case R.id.help:
			Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(HelpUriBuilder.getUri(getClass()
					.getSimpleName())));
			startActivity(browserIntent);

			return true;
		case R.id.new_gaden:

			Intent i = new Intent(this, ProfileCreationActivity.class);
			startActivity(i);

			return true;
		case R.id.delete_garden:

//			deleteGarden();

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void deleteGarden() {
		gardenManager.removeCurrentGarden();
	}


	@Override
	protected void onDestroy() {
		GotsAnalytics.getInstance(getApplication()).decrementActivityCount();
		super.onDestroy();
	}
}
