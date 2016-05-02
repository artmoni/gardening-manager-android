/*******************************************************************************
 * Copyright (c) 2012 sfleury.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * <p/>
 * Contributors:
 * sfleury - initial API and implementation
 ******************************************************************************/
package org.gots.action.provider.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import org.gots.action.ActionFactory;
import org.gots.action.BaseAction;
import org.gots.action.provider.GotsActionProvider;
import org.gots.garden.provider.local.GardenSQLite;
import org.gots.utils.GotsDBHelper;

import java.util.ArrayList;

public class LocalActionProvider extends GotsDBHelper implements GotsActionProvider {

    private String TAG = "LocalActionProvider";

    public LocalActionProvider(Context mContext) {
        super(mContext, GotsDBHelper.DATABASE_GARDEN_TYPE);
    }

    @Override
    public BaseAction createAction(BaseAction action) {
        long rowid;
        ContentValues values = getActionContentValues(action);

        rowid = bdd.insert(GardenSQLite.ACTION_TABLE_NAME, null, values);
        action.setId((int) rowid);
        return action;
    }

    @Override
    public BaseAction updateAction(BaseAction action) {
        ContentValues values = getActionContentValues(action);
        int nbRows;
        Cursor cursor;
        if (action.getId() >= 0) {
            nbRows = bdd.update(GardenSQLite.ACTION_TABLE_NAME, values, GardenSQLite.ACTION_ID + "='" + action.getId()
                    + "'", null);
            cursor = bdd.query(GardenSQLite.ACTION_TABLE_NAME, null, GardenSQLite.ACTION_ID + "='" + action.getId()
                    + "'", null, null, null, null);
        } else {

            nbRows = bdd.update(GardenSQLite.ACTION_TABLE_NAME, values,
                    GardenSQLite.ACTION_UUID + "='" + action.getUUID() + "'", null);

            cursor = bdd.query(GardenSQLite.ACTION_TABLE_NAME, null, GardenSQLite.ACTION_UUID + "='" + action.getUUID()
                    + "'", null, null, null, null);

            if (cursor.moveToFirst()) {
                int rowid = cursor.getInt(cursor.getColumnIndex(GardenSQLite.ACTION_ID));
                action.setId(rowid);
            }
            cursor.close();
        }
        Log.d(TAG, "Updating " + nbRows + " rows > " + action);
        return action;
    }

    @Override
    public ArrayList<BaseAction> getActions(boolean force) {
        ArrayList<BaseAction> allActions = new ArrayList<BaseAction>();
        Cursor cursor = null;
        try {
            cursor = bdd.query(GardenSQLite.ACTION_TABLE_NAME, null, null, null, null, null, null);

            if (cursor.moveToFirst()) {
                do {
                    BaseAction action = cursorToAction(cursor);
                    allActions.add(action);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return allActions;
    }

    public boolean isExist(BaseAction action) {
        boolean exists = false;
        Cursor cursor = null;

        try {
            cursor = bdd.query(GardenSQLite.ACTION_TABLE_NAME, null, GardenSQLite.ACTION_NAME + "='" + action.getName()
                    + "'", null, null, null, null);

            if (cursor.getCount() > 0)
                exists = true;
            else
                exists = false;

        } finally {
            if (cursor != null)
                cursor.close();
        }
        return exists;
    }

    @Override
    public BaseAction getActionByName(String name) {
        BaseAction action = null;
        Cursor cursor = null;
        try {
            cursor = bdd.query(GardenSQLite.ACTION_TABLE_NAME, null, GardenSQLite.ACTION_NAME + "='" + name + "'",
                    null, null, null, null);

            if (cursor.moveToFirst()) {
                action = cursorToAction(cursor);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return action;

    }

    @Override
    public BaseAction getActionById(int id) {
        BaseAction action = null;
        Cursor cursor = null;

        try {
            cursor = bdd.query(GardenSQLite.ACTION_TABLE_NAME, null, GardenSQLite.ACTION_ID + "='" + id + "'", null,
                    null, null, null);

            if (cursor.moveToFirst()) {
                action = cursorToAction(cursor);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return action;
    }

    protected BaseAction cursorToAction(Cursor cursor) {
        BaseAction bsi;
        bsi = ActionFactory.buildAction(mContext, cursor.getString(cursor.getColumnIndex(GardenSQLite.ACTION_NAME)));
        bsi.setDescription(cursor.getString(cursor.getColumnIndex(GardenSQLite.ACTION_DESCRIPTION)));
        bsi.setDuration(cursor.getInt(cursor.getColumnIndex(GardenSQLite.ACTION_DURATION)));
        bsi.setId(cursor.getInt(cursor.getColumnIndex(GardenSQLite.ACTION_ID)));
        bsi.setUUID(cursor.getString(cursor.getColumnIndex(GardenSQLite.ACTION_UUID)));
        return bsi;
    }

    protected ContentValues getActionContentValues(BaseAction action) {
        ContentValues values = new ContentValues();
        values.put(GardenSQLite.ACTION_NAME, action.getName());
        values.put(GardenSQLite.ACTION_DESCRIPTION, action.getDescription());
        values.put(GardenSQLite.ACTION_DURATION, action.getDuration());
        values.put(GardenSQLite.ACTION_UUID, action.getUUID());
        return values;
    }

}
