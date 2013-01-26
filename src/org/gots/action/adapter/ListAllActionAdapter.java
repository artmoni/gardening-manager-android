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
package org.gots.action.adapter;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;

import org.gots.R;
import org.gots.action.BaseActionInterface;
import org.gots.action.SeedActionInterface;
import org.gots.action.bean.PhotoAction;
import org.gots.action.sql.ActionSeedDBHelper;
import org.gots.action.util.ActionState;
import org.gots.action.view.ActionWidget;
import org.gots.seed.GrowingSeedInterface;
import org.gots.seed.sql.GrowingSeedDBHelper;
import org.gots.seed.view.SeedWidget;
import org.gots.weather.WeatherManager;
import org.gots.weather.view.WeatherView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ListAllActionAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<BaseActionInterface> actions = new ArrayList<BaseActionInterface>();
	// private ArrayList<GrowingSeedInterface> seeds = new
	// ArrayList<GrowingSeedInterface>();
	// private ArrayList<WeatherConditionInterface> weathers = new
	// ArrayList<WeatherConditionInterface>();

	private int current_status = STATUS_DONE;
	public static final int STATUS_TODO = 0;
	public static final int STATUS_DONE = 1;
	private WeatherManager manager;

	public static final int THUMBNAIL_HEIGHT = 48;
	public static final int THUMBNAIL_WIDTH = 66;

	public ListAllActionAdapter(Context context, ArrayList<GrowingSeedInterface> allSeeds, int status) {
		this.mContext = context;
		current_status = status;

		for (Iterator<GrowingSeedInterface> iterator = allSeeds.iterator(); iterator.hasNext();) {
			GrowingSeedInterface seed = iterator.next();
			ActionSeedDBHelper helper = new ActionSeedDBHelper(context);
			ArrayList<BaseActionInterface> seedActions;

			if (current_status == STATUS_TODO) {
				seedActions = helper.getActionsToDoBySeed(seed);

			} else {
				seedActions = helper.getActionsDoneBySeed(seed);
			}

			actions.addAll(seedActions);
		}
		Collections.sort(actions, new IStatusUpdateComparator());
		manager = new WeatherManager(mContext);
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();

	}

	@Override
	public int getCount() {
		return actions.size();
	}

	@Override
	public BaseActionInterface getItem(int position) {
		return actions.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LinearLayout ll = (LinearLayout) convertView;

		if (convertView == null) {
			// ll = new LinearLayout(mContext);
			ll = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.list_action, parent, false);
		}

		GrowingSeedDBHelper helper = new GrowingSeedDBHelper(mContext);
		final BaseActionInterface currentAction = actions.get(position);

		final GrowingSeedInterface seed = helper.getSeedById(currentAction.getGrowingSeedId());
		if (seed != null && BaseActionInterface.class.isInstance(currentAction)) {
			ActionWidget actionWidget = (ActionWidget) ll.findViewById(R.id.idActionView);

			SeedWidget seedView = (SeedWidget) ll.findViewById(R.id.idSeedView);
			seedView.setSeed(seed);

			TextView textviewActionStatus = (TextView) ll.findViewById(R.id.IdSeedActionStatus);
			TextView textviewActionDate = (TextView) ll.findViewById(R.id.IdSeedActionDate);

			SimpleDateFormat dateFormat = new SimpleDateFormat(" dd/MM/yyyy", Locale.FRANCE);

			if (current_status == STATUS_TODO) {
				textviewActionStatus.setText(mContext.getResources().getString(R.string.seed_action_todo));

				Calendar rightNow = Calendar.getInstance();
				rightNow.setTime(seed.getDateSowing());
				rightNow.add(Calendar.DAY_OF_YEAR, currentAction.getDuration());
				textviewActionDate.setText(dateFormat.format(rightNow.getTime()));

				actionWidget.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						((SeedActionInterface) currentAction).execute(seed);
						actions.remove(position);
						// seeds.remove(position);
						notifyDataSetChanged();
					}
				});

				WeatherView weatherView = (WeatherView) ll.findViewById(R.id.idWeatherView);
				weatherView.setVisibility(View.GONE);

			} else {
				textviewActionStatus.setText(mContext.getResources().getString(R.string.seed_action_done));

				Calendar rightNow = Calendar.getInstance();
				rightNow.setTime(seed.getDateSowing());
				rightNow.add(Calendar.DAY_OF_YEAR, currentAction.getDuration());
				textviewActionDate.setText(dateFormat.format(currentAction.getDateActionDone()));

				WeatherView weatherView = (WeatherView) ll.findViewById(R.id.idWeatherView);
				weatherView.setWeather(manager.getCondition(rightNow.getTime()));

				currentAction.setState(ActionState.NORMAL);

				if (PhotoAction.class.isInstance(currentAction)) {
					final File imgFile = ((PhotoAction) currentAction).getImageFile(currentAction.getDateActionDone());
					Log.d("imageFile", imgFile.getAbsolutePath());
					if (imgFile.exists()) {

						// Bitmap myBitmap =
						// BitmapFactory.decodeFile(imgFile.getAbsolutePath());

						// final int THUMBNAIL_SIZE = 64;
						// myBitmap = Bitmap.createScaledBitmap(myBitmap,
						// THUMBNAIL_SIZE, THUMBNAIL_SIZE, false);
						Bitmap imageBitmap = getThumbnail(imgFile);
						ImageView myImage = (ImageView) ll.findViewById(R.id.imageviewPhoto);
						int padding = (THUMBNAIL_WIDTH - imageBitmap.getWidth()) / 2;
						myImage.setPadding(padding, 0, padding, 0);
						myImage.setImageBitmap(imageBitmap);

						myImage.setVisibility(View.VISIBLE);
						weatherView.setVisibility(View.GONE);

						myImage.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								Intent intent = new Intent();
								intent.setAction(Intent.ACTION_VIEW);
								intent.setDataAndType(Uri.parse("file://" + imgFile.getAbsolutePath()), "image/*");
								mContext.startActivity(intent);
							}
						});

					}
				}
			}
			actionWidget.setAction(currentAction);

		}
		return ll;
	}

	private Bitmap getThumbnail(File imgFile) {

		Bitmap imageBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
		// imageBitmap = BitmapFactory.decodeByteArray(mImageData, 0,
		// mImageData.length);
		Float width = new Float(imageBitmap.getWidth());
		Float height = new Float(imageBitmap.getHeight());
		Float ratio = width / height;
		imageBitmap = Bitmap.createScaledBitmap(imageBitmap, (int) (THUMBNAIL_HEIGHT * ratio), THUMBNAIL_HEIGHT, false);

		return imageBitmap;
	}

	class IStatusUpdateComparator implements Comparator<BaseActionInterface> {
		@Override
		public int compare(BaseActionInterface obj1, BaseActionInterface obj2) {
			int result = 0;
			if (obj1.getDuration() >= 0 && obj2.getDuration() >= 0) {
				Log.i("Compare", obj1.getDuration() + " | " + obj2.getDuration());
				result = obj1.getDuration() < obj2.getDuration() ? -1 : 0;
			}

			return result;
		}
	}

}
