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

import org.gots.R;
import org.gots.action.bean.BuyingAction;
import org.gots.help.HelpUriBuilder;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.GrowingSeed;
import org.gots.seed.GrowingSeedInterface;
import org.gots.seed.adapter.PlanningHarvestAdapter;
import org.gots.seed.adapter.PlanningSowAdapter;
import org.gots.seed.sql.VendorSeedDBHelper;
import org.gots.seed.view.PlanningWidget;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class NewSeedActivity extends SherlockActivity implements OnClickListener {
	private View currentView;
	private PlanningWidget planningSow;
	private PlanningWidget planningHarvest;
	private AutoCompleteTextView autoCompleteVariety;
	private AutoCompleteTextView autoCompleteSpecie;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.inputseed);

		ActionBar bar = getSupportActionBar();
		bar.setDisplayHomeAsUpEnabled(true);
		bar.setDisplayShowTitleEnabled(false);
		
		// initFamilyList();
		// initSpecieList();
		// initVarietyList();

		findViewById(R.id.imageBarCode).setOnClickListener(this);

		findViewById(R.id.buttonStock).setOnClickListener(this);
		findViewById(R.id.buttonCatalogue).setOnClickListener(this);

		planningSow = (PlanningWidget) findViewById(R.id.IdSeedSowingPlanning);
		planningSow.setAdapter(new PlanningSowAdapter(null));
		planningSow.setEditable(true);

		planningHarvest = (PlanningWidget) findViewById(R.id.IdSeedHarvestPlanning);
		planningHarvest.setAdapter(new PlanningHarvestAdapter(null));
		planningHarvest.setEditable(true);

		autoCompleteSpecie = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewSpecie);
		initSpecieList();

		autoCompleteSpecie.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				initSpecieList();
				showDropdown();
			}

		});

		autoCompleteVariety = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewVariety);
		initVarietyList();
		autoCompleteVariety.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				initVarietyList();
				autoCompleteVariety.showDropDown();
			}
		});

		super.onCreate(savedInstanceState);
	}

	private void showDropdown() {
		autoCompleteSpecie.showDropDown();
	}

	@Override
	public void onClick(View v) {
		currentView = v;
		switch (v.getId()) {
		case R.id.imageBarCode:
			scanBarCode();
			break;

		case R.id.buttonStock:
			long seedId = insertSeed();
			addToStock(seedId);
			finish();

			break;
		case R.id.buttonCatalogue:
			insertSeed();
			finish();

			break;
		default:
			break;
		}

	}

	private void addToStock(long seedId) {
		VendorSeedDBHelper helper = new VendorSeedDBHelper(this);
		if (seedId >= 0) {
			GrowingSeedInterface seed = (GrowingSeedInterface) helper.getSeedById((int) seedId);
			BuyingAction buy = new BuyingAction(this);
			buy.execute(seed);
		}

	}

	private long insertSeed() {
		// String family = (String) ((Spinner)
		// findViewById(R.id.spinnerFamily)).getSelectedItem();
		String variety = (String) ((AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewVariety)).getText()
				.toString();
		String specie = (String) ((AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewSpecie)).getText()
				.toString();

		// int sowmin = (Integer) ((TextView)
		// findViewById(R.id.buttonSowingMinDatePicker)).getTag();
		// int sowmax = (Integer) ((TextView)
		// findViewById(R.id.buttonSowingMaxDatePicker)).getTag();
		// int harvestmin = (Integer) ((TextView)
		// findViewById(R.id.buttonHarvestMinDatePicker)).getTag();
		// int harvestmax = (Integer) ((TextView)
		// findViewById(R.id.buttonHarvestMaxDatePicker)).getTag();
		String barcode = (((TextView) findViewById(R.id.textViewBarCode)).getText()).toString();

		// BaseSeedInterface seed = SeedFactory.createSeed(family);
		BaseSeedInterface seed = new GrowingSeed();

		ArrayList<Integer> sowMonth = planningSow.getSelectedMonth();
		if (sowMonth.size() == 0) {
			Toast.makeText(this, "Please select month to sow", 3000).show();
			return -1;
		}
		seed.setDateSowingMin(sowMonth.get(0));
		seed.setDateSowingMax(sowMonth.get(sowMonth.size() - 1));

		ArrayList<Integer> harvestMonth = planningHarvest.getSelectedMonth();
		if (harvestMonth.size() == 0) {
			Toast.makeText(this, "Please select month to harvest", 3000).show();
			return -1;
		}

		int durationmin = harvestMonth.get(0) - sowMonth.get(0);
		seed.setDurationMin(durationmin * 30);

		int durationmax = harvestMonth.get(harvestMonth.size() - 1) - sowMonth.get(sowMonth.size() - 1);
		seed.setDurationMax(durationmax * 30);

		TextView familyText = (TextView) findViewById(R.id.IdSeedFamily);

		seed.setFamily(familyText.getText().toString());
		seed.setSpecie(specie);
		seed.setVariety(variety);
		seed.setBareCode(barcode);
		seed.setReference(barcode);
		VendorSeedDBHelper helper = new VendorSeedDBHelper(this);

		return helper.insertSeed(seed);
	}

	/**
	 * 
	 */
	private void initSpecieList() {
		final VendorSeedDBHelper helper = new VendorSeedDBHelper(this);
		String[] specieList = helper.getArraySpecie();

		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, specieList);
		autoCompleteSpecie.setAdapter(adapter);

		autoCompleteSpecie.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			private String family;

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				String specie = adapter.getItem(arg2);
				int vegetableImageRessource = getResources().getIdentifier(
						"org.gots:drawable/specie_" + specie.trim().toLowerCase().replaceAll("\\s", ""), null, null);
				ImageView v = (ImageView) findViewById(R.id.imageViewSpecie);
				v.setImageResource(vegetableImageRessource);

				family = helper.getFamilyBySpecie(specie);
				LinearLayout vf = (LinearLayout) findViewById(R.id.layoutFamily);
				int vegetableImageRessourcef = getResources().getIdentifier(
						"org.gots:drawable/family_" + family.trim().toLowerCase().replaceAll("\\s", ""), null, null);
				vf.setBackgroundResource(vegetableImageRessourcef);

				TextView familyText = (TextView) findViewById(R.id.IdSeedFamily);
				familyText.setText(family);
			}
		});
		autoCompleteSpecie.invalidate();
	}

	/**
	 * 
	 */
	private void initVarietyList() {
		VendorSeedDBHelper helper = new VendorSeedDBHelper(this);
		AutoCompleteTextView textViewSpecie = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewSpecie);
		String specie = textViewSpecie.getText().toString();
		String[] referenceList = null;
		if (specie != null)
			referenceList = helper.getArrayVarietyBySpecie(specie);
		else
			referenceList = helper.getArrayVariety();

		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, referenceList);
		autoCompleteVariety.setAdapter(adapter);

		autoCompleteVariety.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				String variety = adapter.getItem(arg2).trim().toLowerCase().replaceAll("\\s", "");
				int vegetableImageRessource = getResources().getIdentifier("org.gots:drawable/veget_" + variety, null,
						null);

				ImageView v = (ImageView) findViewById(R.id.imageViewVariety);
				v.setImageResource(vegetableImageRessource);
			}
		});
		// autoCompleteVariety.showDropDown();
		autoCompleteVariety.invalidate();
	}

	private void scanBarCode() {
		IntentIntegrator integrator = new IntentIntegrator(this);
		integrator.initiateScan();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
		if (scanResult != null && scanResult.getContents() != "") {
			Log.i("Scan result", scanResult.toString());
			TextView textViewBarCode = (TextView) findViewById(R.id.textViewBarCode);
			textViewBarCode.setText(scanResult.getContents());

		}
		// super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.menu_newseed, menu);
		return true;
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
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
