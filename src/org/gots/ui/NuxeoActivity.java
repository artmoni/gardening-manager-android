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

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.gots.R;
import org.gots.action.service.ActionTODOBroadcastReceiver;
import org.gots.analytics.GotsAnalytics;
import org.gots.garden.GardenInterface;
import org.gots.garden.GardenManager;
import org.gots.garden.sql.GardenDBHelper;
import org.gots.preferences.GotsPreferences;
import org.gots.seed.providers.RetrieveNuxeoDocs;
import org.gots.weather.service.WeatherUpdateService;
import org.nuxeo.android.activities.BaseNuxeoActivity;
import org.nuxeo.android.context.NuxeoContext;
import org.nuxeo.android.context.NuxeoContextProvider;
import org.nuxeo.android.documentprovider.DocumentProvider;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public class NuxeoActivity extends BaseNuxeoActivity {

	private List<String> providerNames;
	private NuxeoContext nuxeoContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		nuxeoContext = new NuxeoContext(getApplicationContext());
		nuxeoContext.getServerConfig().setLogin("Administrator");
		nuxeoContext.getServerConfig().setPassword("Administrator");
		nuxeoContext.getServerConfig().setServerBaseUrl("http://192.168.10.200:8080/nuxeo/");
		runAsyncDataRetrieval();
	}

	@Override
	protected boolean requireAsyncDataRetrieval() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected Object retrieveNuxeoData() throws Exception {
		// be sure we won't start with an empty list
		registerProviders();

		// get declated providers
		DocumentProvider docProvider = getAutomationClient().getDocumentProvider();
		providerNames = docProvider.listProviderNames();

		return true;
	}

	protected void registerProviders() {

		Log.i(this.getClass().getSimpleName(), "register providers .......");
		DocumentProvider docProvider = getAutomationClient().getDocumentProvider();

		// register a query
		String providerName1 = "My Gardens";
		if (!docProvider.isRegistred(providerName1)) {
			String query = "select * from Document where ecm:mixinType != \"HiddenInNavigation\" AND ecm:isCheckedInVersion = 0 AND ecm:currentLifeCycleState != \"deleted\" order by dc:modified DESC";
			docProvider.registerNamedProvider(getNuxeoSession(), providerName1, query, 10, false, false, null);
		}

		// // register an operation
		// String providerName2 = "Get Worklist operation";
		// if (!docProvider.isRegistred(providerName2)) {
		// // create the fetch operation
		// OperationRequest getWorklistOperation =
		// getNuxeoSession().newRequest("Seam.FetchFromWorklist");
		// // define what properties are needed
		// getWorklistOperation.setHeader("X-NXDocumentProperties",
		// "common,dublincore");
		// // register provider from OperationRequest
		// docProvider.registerNamedProvider(providerName2,
		// getWorklistOperation, null, false, false, null);
		// }
		//
		// // register a documentList
		// String providerName3 = "My Documents";
		// if (!docProvider.isRegistred(providerName3)) {
		// String query2 = "SELECT * FROM Document WHERE dc:contributors = ?";
		// LazyUpdatableDocumentsList docList = new
		// LazyUpdatableDocumentsListImpl(getNuxeoSession(), query2,
		// new String[] { "Administrator" }, null, null, 10);
		// docList.setName(providerName3);
		// docProvider.registerNamedProvider(docList, false);
		// }
		//
		// // register a query
		// String providerName4 = "mypictures";
		// if (!docProvider.isRegistred(providerName4)) {
		// String query = "select * from Picture";
		// docProvider.registerNamedProvider(getNuxeoSession(), providerName4,
		// query, 10, false, false, "image");
		// }
		//
		// // register a query
		// String providerName5 = "mynotes";
		// if (!docProvider.isRegistred(providerName5)) {
		// String query = "select * from Note";
		// docProvider.registerNamedProvider(getNuxeoSession(), providerName5,
		// query, 10, false, false, "text");
		// }

	}

	@Override
	protected void onNuxeoDataRetrieved(Object data) {

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
				providerNames);
		Log.i("Nuxeo providerNames", providerNames.toString());
	}

	@Override
	protected NuxeoContext getNuxeoContext() {
		// TODO Auto-generated method stub
		return nuxeoContext;
	}
}
