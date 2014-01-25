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
package org.gots.action.provider.local;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.gots.DatabaseHelper;
import org.gots.action.BaseActionInterface;
import org.gots.action.GotsActionManager;
import org.gots.action.SeedActionInterface;
import org.gots.action.provider.GotsActionProvider;
import org.gots.action.provider.GotsActionSeedProvider;
import org.gots.action.util.ActionState;
import org.gots.seed.GrowingSeedInterface;
import org.gots.utils.GotsDBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

public class LocalActionSeedProvider extends GotsDBHelper implements GotsActionSeedProvider {

    private String TAG = "LocalActionSeedProvider";

    public LocalActionSeedProvider(Context mContext) {
        super(mContext);
    }

    @Override
    public SeedActionInterface insertAction(BaseActionInterface action, GrowingSeedInterface seed) {
        long rowid;
        ContentValues values = getContentValues((SeedActionInterface) action, seed);

        rowid = bdd.insert(DatabaseHelper.ACTIONSEEDS_TABLE_NAME, null, values);
        action.setId((int) rowid);
        return (SeedActionInterface) action;
    }

    protected ContentValues getContentValues(SeedActionInterface action, GrowingSeedInterface seed) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.ACTIONSEED_GROWINGSEED_ID, seed.getGrowingSeedId());
        values.put(DatabaseHelper.ACTIONSEED_DURATION, action.getDuration());
        values.put(DatabaseHelper.ACTIONSEED_ACTION_ID, action.getId());
        values.put(DatabaseHelper.ACTIONSEED_UUID, action.getUUID());

        if (action.getData() != null)
            values.put(DatabaseHelper.ACTIONSEED_DATA, action.getData().toString());

        if (action.getDateActionDone() != null)
            values.put(DatabaseHelper.ACTIONSEED_DATEACTIONDONE, action.getDateActionDone().getTime());

        return values;
    }

    private SeedActionInterface cursorToAction(Cursor cursor) {
        SeedActionInterface seedAction = null;
        GotsActionProvider actionDBHelper = new LocalActionProvider(mContext);
        seedAction = (SeedActionInterface) actionDBHelper.getActionById(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ACTIONSEED_ACTION_ID)));
        if (seedAction != null) {
            seedAction.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ACTIONSEED_ACTION_ID)));
            seedAction.setGrowingSeedId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ACTIONSEED_GROWINGSEED_ID)));
            seedAction.setDuration(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ACTIONSEED_DURATION)));
            seedAction.setDateActionDone(new Date(
                    cursor.getLong(cursor.getColumnIndex(DatabaseHelper.ACTIONSEED_DATEACTIONDONE))));
            seedAction.setActionSeedId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ACTIONSEED_ID)));
            seedAction.setData(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ACTIONSEED_DATA)));
            seedAction.setUUID(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ACTIONSEED_UUID)));
        }
        return seedAction;
    }

    @Override
    public ArrayList<SeedActionInterface> getActionsDoneBySeed(GrowingSeedInterface seed) {
        ArrayList<SeedActionInterface> allActions = new ArrayList<SeedActionInterface>();
        if (seed != null) {
            //@formatter:off
		Cursor cursor = bdd.rawQuery("select * from " + DatabaseHelper.ACTIONSEEDS_TABLE_NAME + " actionseed"
				+ " WHERE actionseed." + DatabaseHelper.ACTIONSEED_GROWINGSEED_ID+ "=" + seed.getGrowingSeedId() 
				+ " AND actionseed." + DatabaseHelper.ACTIONSEED_DATEACTIONDONE+ " IS NOT NULL"
				, null);
		//@formatter:on
            if (cursor.moveToFirst()) {
                do {
                    SeedActionInterface action = cursorToAction(cursor);
                    if (action != null)
                        allActions.add(action);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return allActions;
    }

    @Override
    public ArrayList<SeedActionInterface> getActionsToDoBySeed(GrowingSeedInterface seed) {
        ArrayList<SeedActionInterface> allActions = new ArrayList<SeedActionInterface>();
        Cursor cursor = null;
        try {
            cursor = bdd.rawQuery("select * from " + DatabaseHelper.ACTIONSEEDS_TABLE_NAME + " actionseed"
                    + " WHERE actionseed." + DatabaseHelper.ACTIONSEED_GROWINGSEED_ID + "=" + seed.getGrowingSeedId()
                    + " AND actionseed." + DatabaseHelper.ACTIONSEED_DATEACTIONDONE + " IS NULL", null);

            if (cursor.moveToFirst()) {
                do {
                    SeedActionInterface action = cursorToAction(cursor);
                    if (action != null) {
                        populateState(action, seed);
                        allActions.add(action);
                    }
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return allActions;
    }

    protected void populateState(SeedActionInterface action, GrowingSeedInterface seed) {
        int state = ActionState.UNDEFINED;

        if (seed != null && seed.getDateSowing() != null) {
            Calendar cal = new GregorianCalendar();
            Calendar now = Calendar.getInstance();

            cal.setTime(seed.getDateSowing());
            cal.add(Calendar.DAY_OF_YEAR, action.getDuration());

            long i = (now.getTimeInMillis() - cal.getTimeInMillis()) / 86400000;

            if (i == 0)
                state = ActionState.CRITICAL;
            else if (i >= -7 && i <= 7)
                state = ActionState.WARNING;
            else
                state = ActionState.NORMAL;
        }
        action.setState(state);
    }

    @Override
    public ArrayList<SeedActionInterface> getActionsToDo() {
        ArrayList<SeedActionInterface> allActions = new ArrayList<SeedActionInterface>();
        Cursor cursor = bdd.rawQuery("select * from " + DatabaseHelper.ACTIONSEEDS_TABLE_NAME + " actionseed"
                + " WHERE actionseed." + DatabaseHelper.ACTIONSEED_DATEACTIONDONE + " IS NULL", null);

        if (cursor.moveToFirst()) {
            do {
                SeedActionInterface action = cursorToAction(cursor);
                if (action != null)
                    allActions.add(action);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return allActions;
    }

    @Override
    public long doAction(SeedActionInterface action, GrowingSeedInterface seed) {
        long rowid;
        ContentValues values = getContentValues(action, seed);

        rowid = bdd.update(DatabaseHelper.ACTIONSEEDS_TABLE_NAME, values,
                DatabaseHelper.ACTIONSEED_ID + "=" + action.getId(), null);
        if (rowid == 0)
            rowid = bdd.insert(DatabaseHelper.ACTIONSEEDS_TABLE_NAME, null, values);
        return rowid;
    }

    @Override
    public void uploadPicture(GrowingSeedInterface seed,File f) {
        Log.d(TAG, "uploadPicture not implemented");
    }

}
