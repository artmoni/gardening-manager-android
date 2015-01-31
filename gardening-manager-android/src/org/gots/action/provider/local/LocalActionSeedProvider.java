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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.crypto.Mac;

import org.gots.DatabaseHelper;
import org.gots.action.BaseAction;
import org.gots.action.ActionOnSeed;
import org.gots.action.provider.GotsActionProvider;
import org.gots.action.provider.GotsActionSeedProvider;
import org.gots.action.util.ActionState;
import org.gots.exception.GotsServerRestrictedException;
import org.gots.seed.GrowingSeed;
import org.gots.utils.FileUtilities;
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
    public ActionOnSeed insertAction(GrowingSeed seed, ActionOnSeed action) {
        long rowid;
        ContentValues values = getContentValues(action, seed);
        values.remove(DatabaseHelper.ACTIONSEED_ID);
        rowid = bdd.insert(DatabaseHelper.ACTIONSEEDS_TABLE_NAME, null, values);
        action.setActionSeedId((int) rowid);
        return (ActionOnSeed) action;
    }

    protected ContentValues getContentValues(ActionOnSeed action, GrowingSeed seed) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.ACTIONSEED_GROWINGSEED_ID, seed.getGrowingSeedId());
        values.put(DatabaseHelper.ACTIONSEED_DURATION, action.getDuration());
        values.put(DatabaseHelper.ACTIONSEED_ACTION_ID, action.getId());
        values.put(DatabaseHelper.ACTIONSEED_ID, action.getActionSeedId());
        values.put(DatabaseHelper.ACTIONSEED_UUID, action.getUUID());

        if (action.getData() != null)
            values.put(DatabaseHelper.ACTIONSEED_DATA, action.getData().toString());

        if (action.getDateActionDone() != null)
            values.put(DatabaseHelper.ACTIONSEED_DATEACTIONDONE, action.getDateActionDone().getTime());

        return values;
    }

    private ActionOnSeed cursorToAction(Cursor cursor) {
        ActionOnSeed seedAction = null;
        GotsActionProvider actionDBHelper = new LocalActionProvider(mContext);
        BaseAction baseAction = actionDBHelper.getActionById(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ACTIONSEED_ACTION_ID)));
        if (baseAction instanceof ActionOnSeed)
            seedAction = (ActionOnSeed) baseAction;
        if (seedAction != null) {
            seedAction.setActionSeedId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ACTIONSEED_ID)));
            seedAction.setGrowingSeedId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ACTIONSEED_GROWINGSEED_ID)));
            seedAction.setDuration(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ACTIONSEED_DURATION)));
            if (cursor.getLong(cursor.getColumnIndex(DatabaseHelper.ACTIONSEED_DATEACTIONDONE)) != 0)
                seedAction.setDateActionDone(new Date(
                        cursor.getLong(cursor.getColumnIndex(DatabaseHelper.ACTIONSEED_DATEACTIONDONE))));
            seedAction.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ACTIONSEED_ACTION_ID)));
            seedAction.setData(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ACTIONSEED_DATA)));
            seedAction.setUUID(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ACTIONSEED_UUID)));
        }
        return seedAction;
    }

    public synchronized ActionOnSeed update(GrowingSeed seed, ActionOnSeed actionSeed) {
        ContentValues values = getContentValues(actionSeed, seed);
        int nbRows;
        Cursor cursor;
        if (actionSeed.getActionSeedId() >= 0) {
            nbRows = bdd.update(DatabaseHelper.ACTIONSEEDS_TABLE_NAME, values, DatabaseHelper.ACTIONSEED_ID + "='"
                    + actionSeed.getActionSeedId() + "'", null);
            cursor = bdd.query(DatabaseHelper.ACTIONSEEDS_TABLE_NAME, null, DatabaseHelper.ACTIONSEED_ID + "='"
                    + actionSeed.getActionSeedId() + "'", null, null, null, null);
        } else {

            nbRows = bdd.update(DatabaseHelper.ACTIONSEEDS_TABLE_NAME, values, DatabaseHelper.ACTIONSEED_UUID + "='"
                    + actionSeed.getUUID() + "'", null);

            cursor = bdd.query(DatabaseHelper.ACTIONSEEDS_TABLE_NAME, null, DatabaseHelper.ACTIONSEED_UUID + "='"
                    + actionSeed.getUUID() + "'", null, null, null, null);

            if (cursor.moveToFirst()) {
                int rowid = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ACTIONSEED_ID));
                actionSeed.setActionSeedId(rowid);
            }
            cursor.close();
        }
        Log.d(TAG, "Updating " + nbRows + " rows > " + actionSeed);
        return actionSeed;
    }

    @Override
    public List<ActionOnSeed> getActionsDoneBySeed(GrowingSeed seed, boolean force) {
        ArrayList<ActionOnSeed> allActions = new ArrayList<ActionOnSeed>();
        if (seed != null) {
            //@formatter:off
		Cursor cursor = bdd.rawQuery("select * from " + DatabaseHelper.ACTIONSEEDS_TABLE_NAME + " actionseed"
				+ " WHERE actionseed." + DatabaseHelper.ACTIONSEED_GROWINGSEED_ID+ "=" + seed.getGrowingSeedId() 
				+ " AND actionseed." + DatabaseHelper.ACTIONSEED_DATEACTIONDONE+ " IS NOT NULL"
				, null);
		//@formatter:on
            if (cursor.moveToFirst()) {
                do {
                    ActionOnSeed action = cursorToAction(cursor);
                    if (action != null)
                        allActions.add(action);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return allActions;
    }

    @Override
    public List<ActionOnSeed> getActionsToDoBySeed(GrowingSeed seed, boolean force) {
        List<ActionOnSeed> allActions = new ArrayList<ActionOnSeed>();
        Cursor cursor = null;
        try {
            cursor = bdd.rawQuery("select * from " + DatabaseHelper.ACTIONSEEDS_TABLE_NAME + " actionseed"
                    + " WHERE actionseed." + DatabaseHelper.ACTIONSEED_GROWINGSEED_ID + "=" + seed.getGrowingSeedId()
                    + " AND actionseed." + DatabaseHelper.ACTIONSEED_DATEACTIONDONE + " IS NULL", null);

            if (cursor.moveToFirst()) {
                do {
                    ActionOnSeed action = cursorToAction(cursor);
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

    protected ActionOnSeed populateState(ActionOnSeed action, GrowingSeed seed) {
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
        return action;
    }

    @Override
    public ArrayList<ActionOnSeed> getActionsToDo() {
        ArrayList<ActionOnSeed> allActions = new ArrayList<ActionOnSeed>();
        Cursor cursor = bdd.rawQuery("select * from " + DatabaseHelper.ACTIONSEEDS_TABLE_NAME + " actionseed"
                + " WHERE actionseed." + DatabaseHelper.ACTIONSEED_DATEACTIONDONE + " IS NULL", null);

        if (cursor.moveToFirst()) {
            do {
                ActionOnSeed action = cursorToAction(cursor);
                if (action != null)
                    allActions.add(action);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return allActions;
    }

    @Override
    public ActionOnSeed doAction(ActionOnSeed action, GrowingSeed seed) {
        ActionOnSeed mAction;
        // ContentValues values = getContentValues(action, seed);
        if (action.getActionSeedId() >= 0)
            // rowid = bdd.update(DatabaseHelper.ACTIONSEEDS_TABLE_NAME, values, DatabaseHelper.ACTIONSEED_ID + "="
            // + action.getActionSeedId(), null);
            mAction = update(seed, action);
        else
            mAction = insertAction(seed, action);
        // rowid = bdd.insert(DatabaseHelper.ACTIONSEEDS_TABLE_NAME, null, values);
        return mAction;
    }

    @Override
    public File uploadPicture(GrowingSeed seed, File f) {
        File seedDir = new File(gotsPrefs.getGotsExternalFileDir(), String.valueOf(seed.getGrowingSeedId()));
        if (!seedDir.exists())
            seedDir.mkdir();
        File newfile = new File(seedDir, f.getName());
        try {
            FileUtilities.copy(f, newfile);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return newfile;
    }

    @Override
    public File downloadHistory(GrowingSeed mSeed) throws GotsServerRestrictedException {
        throw new GotsServerRestrictedException(mContext);
    }

    public List<File> getPicture(GrowingSeed mSeed) throws GotsServerRestrictedException {
        File seedDir = new File(gotsPrefs.getGotsExternalFileDir(), String.valueOf(mSeed.getGrowingSeedId()));
        File[] files = seedDir.listFiles();

        List<File> myPictures = new ArrayList<File>();
        if (files != null) {
            myPictures = Arrays.asList(files);
        }
        return myPictures;
    }

}
