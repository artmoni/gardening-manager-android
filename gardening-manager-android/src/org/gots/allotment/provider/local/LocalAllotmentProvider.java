package org.gots.allotment.provider.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import org.gots.DatabaseHelper;
import org.gots.allotment.provider.AllotmentProvider;
import org.gots.bean.Allotment;
import org.gots.bean.BaseAllotmentInterface;
import org.gots.preferences.GotsPreferences;
import org.gots.utils.GotsDBHelper;

import java.util.ArrayList;
import java.util.List;

public class LocalAllotmentProvider extends GotsDBHelper implements AllotmentProvider {

    public LocalAllotmentProvider(Context mContext) {
        super(mContext);

    }

    protected BaseAllotmentInterface convertToAllotment(Cursor cursor) {
        BaseAllotmentInterface allotment = new Allotment();
        allotment.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ALLOTMENT_ID)));
        allotment.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ALLOTMENT_NAME)));
        allotment.setUUID(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ALLOTMENT_UUID)));
        allotment.setImagePath(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ALLOTMENT_IMAGE_PATH)));
        return allotment;
    }

    protected ContentValues convertToContentValues(BaseAllotmentInterface allotment) {
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.ALLOTMENT_NAME, allotment.getName());
        values.put(DatabaseHelper.ALLOTMENT_UUID, allotment.getUUID());
        values.put(DatabaseHelper.ALLOTMENT_IMAGE_PATH, allotment.getImagePath());

        return values;
    }

    @Override
    public BaseAllotmentInterface getCurrentAllotment() {
        int currentAllotmentId = gotsPrefs.get(GotsPreferences.ORG_GOTS_CURRENT_ALLOTMENT, -1);
        return getAllotment(currentAllotmentId);
    }

    @Override
    public void setCurrentAllotment(BaseAllotmentInterface allotmentInterface) {
        gotsPrefs.initIfNew(mContext).set(GotsPreferences.ORG_GOTS_CURRENT_ALLOTMENT, allotmentInterface.getId());
    }

    public BaseAllotmentInterface getAllotment(int id) {
        BaseAllotmentInterface allotment = null;
        // open();
        Cursor cursor;

        cursor = bdd.query(DatabaseHelper.ALLOTMENT_TABLE_NAME, null, DatabaseHelper.ALLOTMENT_ID + "=" + id, null,
                null, null, null);

        if (cursor.moveToFirst()) {
            allotment = convertToAllotment(cursor);
        }
        cursor.close();

        return allotment;
    }

    @Override
    public List<BaseAllotmentInterface> getMyAllotments(boolean force) {

        ArrayList<BaseAllotmentInterface> allAllotment = new ArrayList<BaseAllotmentInterface>();
        Cursor cursor = null;
        try {
            cursor = bdd.query(DatabaseHelper.ALLOTMENT_TABLE_NAME, null, null, null, null, null, null);

            if (cursor.moveToFirst()) {
                do {
                    BaseAllotmentInterface allotment = convertToAllotment(cursor);
                    allAllotment.add(allotment);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return allAllotment;
    }

    @Override
    public BaseAllotmentInterface createAllotment(BaseAllotmentInterface allotment) {

        long rowid;
        ContentValues values = convertToContentValues(allotment);

        rowid = bdd.insert(DatabaseHelper.ALLOTMENT_TABLE_NAME, null, values);
        allotment.setId(Long.valueOf(rowid).intValue());
        return allotment;
    }

    @Override
    public int removeAllotment(BaseAllotmentInterface allotment) {
        int nbRow = bdd.delete(DatabaseHelper.ALLOTMENT_TABLE_NAME,
                DatabaseHelper.ALLOTMENT_ID + "=" + allotment.getId() + "", null);
        return nbRow;
    }

    @Override
    public BaseAllotmentInterface updateAllotment(BaseAllotmentInterface allotment) {
        ContentValues values = convertToContentValues(allotment);
        Cursor cursor = null;
        try {
            if (allotment.getId() >= 0) {
                bdd.update(DatabaseHelper.ALLOTMENT_TABLE_NAME, values,
                        DatabaseHelper.ALLOTMENT_ID + "=\"" + allotment.getId() + "\"", null);
                cursor = bdd.query(DatabaseHelper.ALLOTMENT_TABLE_NAME, null, DatabaseHelper.ALLOTMENT_ID + "='"
                        + allotment.getId() + "'", null, null, null, null);
            } else {
                bdd.update(DatabaseHelper.ALLOTMENT_TABLE_NAME, values,
                        DatabaseHelper.ALLOTMENT_UUID + "=\"" + allotment.getUUID() + "\"", null);
                cursor = bdd.query(DatabaseHelper.ALLOTMENT_TABLE_NAME, null, DatabaseHelper.ALLOTMENT_UUID + "='"
                        + allotment.getUUID() + "'", null, null, null, null);
            }

            if (cursor.moveToFirst()) {
                int rowid = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ALLOTMENT_ID));
                allotment.setId(rowid);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return allotment;
    }

    public BaseAllotmentInterface getAllotmentByUUID(String uuid) {
        BaseAllotmentInterface allotment = null;
        Cursor cursor = null;
        if (uuid != null) {

            cursor = bdd.query(DatabaseHelper.ALLOTMENT_TABLE_NAME, null, DatabaseHelper.ALLOTMENT_UUID + "='" + uuid
                    + "'", null, null, null, null);
            if (cursor.moveToFirst()) {
                allotment = convertToAllotment(cursor);
            }
        }
        return allotment;
    }

    public BaseAllotmentInterface getAllotmentByID(Integer id) {
        return null;
    }

}
