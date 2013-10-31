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

import java.util.ArrayList;

import org.gots.action.ActionFactory;
import org.gots.action.BaseActionInterface;
import org.gots.action.PermanentActionInterface;
import org.gots.action.provider.GotsActionProvider;
import org.gots.garden.provider.local.GardenSQLite;
import org.gots.utils.GotsDBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class LocalActionProvider extends GotsDBHelper implements GotsActionProvider{

    public LocalActionProvider(Context mContext) {
        super(mContext, GotsDBHelper.DATABASE_GARDEN_TYPE);
    }

    @Override
    public long insertAction(BaseActionInterface action) {
        long rowid;
        ContentValues values = getActionContentValues(action);

        rowid = bdd.insert(GardenSQLite.ACTION_TABLE_NAME, null, values);

        return rowid;
    }

    @Override
    public ArrayList<BaseActionInterface> getActions() {
        ArrayList<BaseActionInterface> allActions = new ArrayList<BaseActionInterface>();
        Cursor cursor = null;
        try {
            cursor = bdd.query(GardenSQLite.ACTION_TABLE_NAME, null, null, null, null, null, null);

            if (cursor.moveToFirst()) {
                do {
                    BaseActionInterface action = cursorToAction(cursor);
                    if (!PermanentActionInterface.class.isInstance(action))
                        allActions.add(action);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return allActions;
    }

    public boolean isExist(BaseActionInterface action) {
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
    public BaseActionInterface getActionByName(String name) {
        BaseActionInterface action = null;
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
    public BaseActionInterface getActionById(int id) {
        BaseActionInterface action = null;
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

    protected BaseActionInterface cursorToAction(Cursor cursor) {
        BaseActionInterface bsi;
        ActionFactory factory = new ActionFactory();
        bsi = factory.buildAction(mContext, cursor.getString(cursor.getColumnIndex(GardenSQLite.ACTION_NAME)));
        bsi.setDescription(cursor.getString(cursor.getColumnIndex(GardenSQLite.ACTION_DESCRIPTION)));
        bsi.setDuration(cursor.getInt(cursor.getColumnIndex(GardenSQLite.ACTION_DURATION)));
        bsi.setId(cursor.getInt(cursor.getColumnIndex(GardenSQLite.ACTION_ID)));
        bsi.setUUID(cursor.getString(cursor.getColumnIndex(GardenSQLite.ACTION_UUID)));
        return bsi;
    }

    protected ContentValues getActionContentValues(BaseActionInterface action) {
        ContentValues values = new ContentValues();
        values.put(GardenSQLite.ACTION_NAME, action.getName());
        values.put(GardenSQLite.ACTION_DESCRIPTION, action.getDescription());
        values.put(GardenSQLite.ACTION_DURATION, action.getDuration());
        values.put(GardenSQLite.ACTION_UUID, action.getUUID());
        return values;
    }

}
