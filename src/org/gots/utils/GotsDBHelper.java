package org.gots.utils;

import org.gots.DatabaseHelper;
import org.gots.preferences.GotsPreferences;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GotsDBHelper {

    protected SQLiteDatabase bdd;

    private SQLiteOpenHelper actionSeedSQLite;

    protected Context mContext;

    protected GotsPreferences gotsPrefs;

    public GotsDBHelper(Context mContext) {
        this.mContext = mContext;
        gotsPrefs = GotsPreferences.getInstance().initIfNew(mContext);
        open();
    }

    private void open() {
        actionSeedSQLite = new DatabaseHelper(mContext,
                GotsPreferences.getInstance().initIfNew(mContext).getCurrentGardenId());
        bdd = actionSeedSQLite.getWritableDatabase();
    }

    public void close() {
        if (bdd != null && bdd.isOpen())
            bdd.close();
    }

    @Override
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }

}
