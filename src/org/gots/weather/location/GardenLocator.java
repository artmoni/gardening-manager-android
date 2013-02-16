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
 package org.gots.weather.location;

import java.util.List;
import java.util.Locale;

import org.gots.R;
import org.gots.bean.Address;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

public class GardenLocator implements LocationListener {
	Context mContext;
	Address address;
	LocationManager mlocManager;

	public GardenLocator(Context context) {
		mContext = context;

	}

	private void updateWithNewLocation(Location location) {
		try {
			if (location != null) {
				String latLongString = "";
				final double lat = location.getLatitude();
				final double lng = location.getLongitude();
				latLongString = "Lat:" + lat + "\nLong:" + lng;

				Geocoder gcd = new Geocoder(mContext, Locale.getDefault());
				List<android.location.Address> gAddress = gcd.getFromLocation(lat, lng, 1);

				if (gAddress.size() > 0) {
					address.setLocality(gAddress.get(0).getLocality());
					address.setAdminArea(gAddress.get(0).getAdminArea());
					address.setCountryName(gAddress.get(0).getCountryName());
				}

				Log.i("GPS", latLongString);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		mlocManager.removeUpdates(this);
	}

	@Override
	public void onLocationChanged(Location location) {
		updateWithNewLocation(location);
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	public Address localizeGarden() {
		mlocManager = (LocationManager) mContext.getSystemService(mContext.LOCATION_SERVICE);

		Location location = mlocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		if (location == null) {
			// Fall back to coarse location.
			location = mlocManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		}

		updateWithNewLocation(location);

		// ************

		final AlertDialog.Builder alert2 = new AlertDialog.Builder(mContext);
		final Geocoder gc = new Geocoder(mContext);

		alert2.setTitle(mContext.getString(R.string.garden_cityname_inputtitle));
		alert2.setMessage(mContext.getString(R.string.garden_cityname_inputmessage));

		// Set an EditText view to get user input
		final EditText input2 = new EditText(mContext);
		alert2.setView(input2);

		alert2.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				try {
					android.location.Address add = gc.getFromLocationName(input2.getText().toString(), 5).get(0);
					org.gots.bean.Address add2 = new org.gots.bean.Address();
					add2.setLocality(add.getLocality());
					add2.setAdminArea(add.getAdminArea());
					add2.setCountryName(add.getCountryName());
					address = add2;

				} catch (Exception e) {
					Log.e(" GEOLOCATOR", e.getMessage());
				}
			}
		});

		alert2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// Canceled.
			}
		});
		// ***************
		
		// builder.setIcon(R.drawable.dialog_question);
		if (address != null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

			builder.setCancelable(true);
			builder.setTitle("Your Garden is based in " + address.getLocality() + " ?");

			builder.setInverseBackgroundForced(true);
			builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					// mlocManager.removeUpdates(locationListener);
					// MyMainGarden.myGarden.setGpsLatitude(lat);
					// MyMainGarden.myGarden.setGpsLongitude(lng);
					// DashboardActivity.myGarden.setAddress(gardenAddress);
					// address = gardenAddress;
				}
			});

			builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					alert2.show();

					dialog.dismiss();
				}
			});
			AlertDialog gardenNameAlert = builder.create();
			gardenNameAlert.show();
		} else
			alert2.show();

		
		
		return address;
	}
}
