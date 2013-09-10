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
package org.gots.action.sql;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.gots.DatabaseHelper;
import org.gots.action.BaseActionInterface;
import org.gots.action.util.ActionState;
import org.gots.seed.GrowingSeedInterface;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ActionSeedDBHelper {

	private DatabaseHelper actionSeedSQLite;
	private SQLiteDatabase bdd;
	Context mContext;

	public ActionSeedDBHelper(Context mContext) {
		actionSeedSQLite = DatabaseHelper.getInstance(mContext);
		this.mContext = mContext;
	}

	public void open() {
		bdd = actionSeedSQLite.getWritableDatabase();
	}

	

	public long insertAction(BaseActionInterface action, GrowingSeedInterface seed) {
		long rowid;
		open();
		ContentValues values = new ContentValues();

		values.put(DatabaseHelper.ACTIONSEED_GROWINGSEED_ID, seed.getGrowingSeedId());
		values.put(DatabaseHelper.ACTIONSEED_DURATION, action.getDuration());
		values.put(DatabaseHelper.ACTIONSEED_ACTION_ID, action.getId());
		// values.put(DatabaseHelper.ACTIONSEED_D, action.getId());

		if (action.getDateActionDone() != null)
			values.put(DatabaseHelper.ACTIONSEED_DATEACTIONDONE, action.getDateActionDone().getTime());

		rowid = bdd.insert(DatabaseHelper.ACTIONSEEDS_TABLE_NAME, null, values);
		return rowid;
	}

	public ArrayList<BaseActionInterface> getActions2() {
		ArrayList<BaseActionInterface> allActions = new ArrayList<BaseActionInterface>();
		// SeedActionInterface searchedSeed = new GrowingSeed();
		open();
		Cursor cursor = bdd.query(DatabaseHelper.ACTIONSEEDS_TABLE_NAME, null, null, null, null, null, null);

		if (cursor.moveToFirst()) {
			do {
				BaseActionInterface action = cursorToAction(cursor);
				if (action != null)
					allActions.add(action);
			} while (cursor.moveToNext());
		}
		cursor.close();
		return allActions;
	}

	// public boolean isExist(BaseActionInterface action) {
	// open();
	// boolean exists = false;
	// Cursor cursor = bdd.query(DatabaseHelper.ACTION_TABLE_NAME, null,
	// DatabaseHelper.ACTION_NAME + "='" + action.getName() + "'", null, null,
	// null, null);
	//
	// if (cursor.getCount() > 0)
	// exists = true;
	// else
	// exists = false;
	//
	// cursor.close();
	// close();
	// return exists;
	// }

	private BaseActionInterface cursorToAction(Cursor cursor) {
		BaseActionInterface seedAction = null;
		// ActionFactory factory = new ActionFactory();
		// seedAction = factory.buildAction(mContext,
		// cursor.getString(cursor.getColumnIndex(GardenSQLite.ACTION_NAME)));
		ActionDBHelper actionDBHelper = new ActionDBHelper(mContext);
		seedAction = actionDBHelper.getActionById(cursor.getInt(cursor
				.getColumnIndex(DatabaseHelper.ACTIONSEED_ACTION_ID)));
		if (seedAction != null) {
			seedAction.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ACTIONSEED_ACTION_ID)));
			seedAction.setGrowingSeedId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ACTIONSEED_GROWINGSEED_ID)));
			seedAction.setDuration(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ACTIONSEED_DURATION)));
			seedAction.setDateActionDone(new Date(cursor.getLong(cursor
					.getColumnIndex(DatabaseHelper.ACTIONSEED_DATEACTIONDONE))));
			seedAction.setLogId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ACTIONSEED_ID)));

			seedAction.setData(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ACTIONSEED_DATA)));
		}
		return seedAction;
	}

	public ArrayList<BaseActionInterface> getActionsDoneBySeed(GrowingSeedInterface seed) {
		ArrayList<BaseActionInterface> allActions = new ArrayList<BaseActionInterface>();
		if (seed != null) {
			// SeedActionInterface searchedSeed = new GrowingSeed();
			open();
			// Cursor cursor = bdd.query(DatabaseHelper.ACTIONSEEDS_TABLE_NAME,
			// null,
			// DatabaseHelper.ACTIONSEED_GROWINGSEED_ID+"="+seed.getId(),null,
			// null, null, null);
			//@formatter:off
		Cursor cursor = bdd.rawQuery("select * from " + DatabaseHelper.ACTIONSEEDS_TABLE_NAME + " actionseed"
				+ " WHERE actionseed." + DatabaseHelper.ACTIONSEED_GROWINGSEED_ID+ "=" + seed.getGrowingSeedId() 
				+ " AND actionseed." + DatabaseHelper.ACTIONSEED_DATEACTIONDONE+ " IS NOT NULL"
				, null);
		//@formatter:on
			if (cursor.moveToFirst()) {
				do {
					BaseActionInterface action = cursorToAction(cursor);
					if (action != null)
						allActions.add(action);
				} while (cursor.moveToNext());
			}
			cursor.close();
		}
		return allActions;
	}

	public ArrayList<BaseActionInterface> getActionsToDoBySeed(GrowingSeedInterface seed) {
		ArrayList<BaseActionInterface> allActions = new ArrayList<BaseActionInterface>();
		// SeedActionInterface searchedSeed = new GrowingSeed();
		open();
		// Cursor cursor = bdd.query(DatabaseHelper.ACTIONSEEDS_TABLE_NAME,
		// null, DatabaseHelper.ACTIONSEED_GROWINGSEED_ID+"="+seed.getId(),null,
		// null, null, null);
		Cursor cursor = bdd.rawQuery("select * from " + DatabaseHelper.ACTIONSEEDS_TABLE_NAME + " actionseed"
				+ " WHERE actionseed." + DatabaseHelper.ACTIONSEED_GROWINGSEED_ID + "=" + seed.getGrowingSeedId()
				+ " AND actionseed." + DatabaseHelper.ACTIONSEED_DATEACTIONDONE + " IS NULL", null);

		if (cursor.moveToFirst()) {
			do {
				BaseActionInterface action = cursorToAction(cursor);
				if (action != null) {
					populateState(action, seed);
					allActions.add(action);
				}
			} while (cursor.moveToNext());
		}

		cursor.close();
		return allActions;
	}

	private void populateState(BaseActionInterface action, GrowingSeedInterface seed) {
		int state = ActionState.UNDEFINED;

		if (seed != null && seed.getDateSowing() != null) {
			Calendar cal = new GregorianCalendar();
			Calendar now = Calendar.getInstance();

			cal.setTime(seed.getDateSowing());
			cal.add(Calendar.DAY_OF_YEAR, action.getDuration());

			long i = (now.getTimeInMillis() - cal.getTimeInMillis()) / 86400000;

			if (i == 0)
				state = ActionState.NORMAL;
			else if (i >= -7 && i <= 7)
				state = ActionState.WARNING;
			else
				state = ActionState.CRITICAL;
		}
		action.setState(state);
	}

	public ArrayList<BaseActionInterface> getActionsToDo() {
		ArrayList<BaseActionInterface> allActions = new ArrayList<BaseActionInterface>();
		// SeedActionInterface searchedSeed = new GrowingSeed();
		open();
		// Cursor cursor = bdd.query(DatabaseHelper.ACTIONSEEDS_TABLE_NAME,
		// null, DatabaseHelper.ACTIONSEED_GROWINGSEED_ID+"="+seed.getId(),null,
		// null, null, null);
		Cursor cursor = bdd.rawQuery("select * from " + DatabaseHelper.ACTIONSEEDS_TABLE_NAME + " actionseed"
				+ " WHERE actionseed." + DatabaseHelper.ACTIONSEED_DATEACTIONDONE + " IS NULL", null);

		if (cursor.moveToFirst()) {
			do {
				BaseActionInterface action = cursorToAction(cursor);
				if (action != null)
					allActions.add(action);
			} while (cursor.moveToNext());
		}

		
		cursor.close();
		return allActions;
	}

	public long doAction(BaseActionInterface action, GrowingSeedInterface seed) {
		long rowid;
		open();
		ContentValues values = new ContentValues();

		values.put(DatabaseHelper.ACTIONSEED_GROWINGSEED_ID, seed.getGrowingSeedId());
		values.put(DatabaseHelper.ACTIONSEED_ACTION_ID, action.getId());
		if (action.getData() != null)
			values.put(DatabaseHelper.ACTIONSEED_DATA, action.getData().toString());

		if (action.getDateActionDone() != null)
			values.put(DatabaseHelper.ACTIONSEED_DATEACTIONDONE, action.getDateActionDone().getTime());

		rowid = bdd.update(DatabaseHelper.ACTIONSEEDS_TABLE_NAME, values,
				DatabaseHelper.ACTIONSEED_ID + "=" + action.getLogId(), null);
		if (rowid == 0)
			rowid = bdd.insert(DatabaseHelper.ACTIONSEEDS_TABLE_NAME, null, values);
		return rowid;
	}

}
