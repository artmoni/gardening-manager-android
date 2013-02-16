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
package org.gots.allotment.sql;

import java.util.ArrayList;

import org.gots.DatabaseHelper;
import org.gots.bean.Allotment;
import org.gots.bean.BaseAllotmentInterface;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AllotmentDBHelper {

//	private List<BaseAllotmentInterface> allAllotment = new ArrayList<BaseAllotmentInterface>();

	private DatabaseHelper actionSeedSQLite;
	private SQLiteDatabase bdd;
	Context mContext;

	public AllotmentDBHelper(Context mContext) {
		actionSeedSQLite = new DatabaseHelper(mContext);
		this.mContext = mContext;
	}

	public void open() {
		// on ouvre la BDD en écriture
		bdd = actionSeedSQLite.getWritableDatabase();
	}

	public void close() {
		// on ferme l'accès à la BDD
		bdd.close();
	}

	public long insertAllotment(BaseAllotmentInterface allotment) {
		long rowid;
		open();
		ContentValues values = new ContentValues();
//		values.
//		values.put(ActionSeedSQLite.ACTION_NAME, action.getName());
//		values.put(DatabaseHelper.ALLOTMENT_ID, allotment.getId());
		values.put(DatabaseHelper.ALLOTMENT_NAME, allotment.getName());
//		values.put(DatabaseHelper.ACTIONSEED_DATEACTIONDONE, Calendar.getInstance().getTimeInMillis());

		rowid = bdd.insert(DatabaseHelper.ALLOTMENT_TABLE_NAME, null, values);
		close();
		return rowid;
	}

	public ArrayList<BaseAllotmentInterface> getAllotments() {
		ArrayList<BaseAllotmentInterface> allAllotment = new ArrayList<BaseAllotmentInterface>();
		// SeedActionInterface searchedSeed = new GrowingSeed();
		open();
		Cursor cursor = bdd.query(DatabaseHelper.ALLOTMENT_TABLE_NAME, null, null, null, null, null, null);

		if (cursor.moveToFirst()) {
			do {
				BaseAllotmentInterface allotment = cursorToAllotment(cursor);
				allAllotment.add(allotment);
			} while (cursor.moveToNext());
		}
		cursor.close();
		close();
		return allAllotment;
	}

	

	private BaseAllotmentInterface cursorToAllotment(Cursor cursor) {
		BaseAllotmentInterface lot = new Allotment();
//		ActionFactory factory = new ActionFactory();
//		lot = factory.buildAction(mContext,cursor.getString(cursor.getColumnIndex(DatabaseHelper.ACTION_NAME)));
//		lot.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ACTION_ID)));
		lot.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ALLOTMENT_ID)));
		lot.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ALLOTMENT_NAME)));
		return lot;
	}

	public BaseAllotmentInterface getAllotmentByName(String name) {
		BaseAllotmentInterface allotment = new Allotment();
		// SeedActionInterface searchedSeed = new GrowingSeed();
		open();
		Cursor cursor = bdd.query(DatabaseHelper.ALLOTMENT_TABLE_NAME, null, DatabaseHelper.ALLOTMENT_NAME+"="+name, null, null, null, null);

		if (cursor.getCount() > 0 && cursor.moveToFirst()) {			
				allotment = cursorToAllotment(cursor);
		}
		cursor.close();
		close();
		return allotment;
	}
	
	public void deleteAllotment( BaseAllotmentInterface allotment) {
		open();
		bdd.delete(DatabaseHelper.ALLOTMENT_TABLE_NAME, DatabaseHelper.ALLOTMENT_ID + "=" + allotment.getId(), null);
		close();
	}

}
