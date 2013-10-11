package org.gots.allotment.provider.local;

import java.util.ArrayList;
import java.util.List;

import org.gots.DatabaseHelper;
import org.gots.allotment.provider.AllotmentProvider;
import org.gots.bean.Allotment;
import org.gots.bean.BaseAllotmentInterface;
import org.gots.preferences.GotsPreferences;
import org.gots.utils.GotsDBHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class LocalAllotmentProvider extends GotsDBHelper implements AllotmentProvider {

    public LocalAllotmentProvider(Context mContext) {
        super(mContext);

    }

    protected BaseAllotmentInterface convertToAllotment(Cursor cursor) {
        BaseAllotmentInterface lot = new Allotment();
        // ActionFactory factory = new ActionFactory();
        // lot = factory.buildAction(mContext,cursor.getString(cursor.getColumnIndex(DatabaseHelper.ACTION_NAME)));
        // lot.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ACTION_ID)));
        lot.setId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ALLOTMENT_ID)));
        lot.setName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ALLOTMENT_NAME)));
        lot.setUUID(cursor.getString(cursor.getColumnIndex(DatabaseHelper.ALLOTMENT_UUID)));
        return lot;
    }

    protected ContentValues convertToContentValues(BaseAllotmentInterface allotment) {
        ContentValues values = new ContentValues();
        // values.
        // values.put(ActionSeedSQLite.ACTION_NAME, action.getName());
        // values.put(DatabaseHelper.ALLOTMENT_ID, allotment.getId());
        values.put(DatabaseHelper.ALLOTMENT_NAME, allotment.getName());
        values.put(DatabaseHelper.ALLOTMENT_UUID, allotment.getUUID());
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
        // close();
        return allotment;
    }

    @Override
    public List<BaseAllotmentInterface> getMyAllotments() {

        ArrayList<BaseAllotmentInterface> allAllotment = new ArrayList<BaseAllotmentInterface>();
        // open();
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
            // close();
        }
        // close();
        return allAllotment;
    }

    @Override
    public BaseAllotmentInterface createAllotment(BaseAllotmentInterface allotment) {

        long rowid;
        // open();
        ContentValues values = convertToContentValues(allotment);

        // values.put(DatabaseHelper.ACTIONSEED_DATEACTIONDONE, Calendar.getInstance().getTimeInMillis());

        rowid = bdd.insert(DatabaseHelper.ALLOTMENT_TABLE_NAME, null, values);
        allotment.setId(Long.valueOf(rowid).intValue());
        // close();
        return allotment;
    }

    @Override
    public int removeAllotment(BaseAllotmentInterface allotment) {
        // open();
        int nbRow = bdd.delete(DatabaseHelper.ALLOTMENT_TABLE_NAME,
                DatabaseHelper.ALLOTMENT_ID + "=" + allotment.getId() + "", null);
        // close();
        return nbRow;
    }

    @Override
    public BaseAllotmentInterface updateAllotment(BaseAllotmentInterface allotment) {
        // open();
        ContentValues values = convertToContentValues(allotment);
        Cursor cursor = null;
        try {

            if (allotment.getUUID() != null) {
                int nbRow = bdd.update(DatabaseHelper.ALLOTMENT_TABLE_NAME, values, DatabaseHelper.ALLOTMENT_UUID
                        + "=\"" + allotment.getUUID() + "\"", null);

                cursor = bdd.query(DatabaseHelper.ALLOTMENT_TABLE_NAME, null, DatabaseHelper.ALLOTMENT_UUID + "='"
                        + allotment.getUUID() + "'", null, null, null, null);

            } else {
                int nbRow = bdd.update(DatabaseHelper.ALLOTMENT_TABLE_NAME, values, DatabaseHelper.ALLOTMENT_ID + "=\""
                        + allotment.getId() + "\"", null);
                cursor = bdd.query(DatabaseHelper.ALLOTMENT_TABLE_NAME, null, DatabaseHelper.ALLOTMENT_ID + "='"
                        + allotment.getId() + "'", null, null, null, null);

            }
            if (cursor.moveToFirst()) {
                int rowid = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.ALLOTMENT_ID));
                allotment.setId(rowid);
            }
        } finally {
            if (cursor != null)
                cursor.close();
            // close();
        }
        // close();

        return allotment;
    }

}
