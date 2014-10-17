package org.gots.utils;

import org.gots.DatabaseHelper;
import org.gots.context.GotsContext;
import org.gots.garden.provider.local.GardenSQLite;
import org.gots.preferences.GotsPreferences;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GotsDBHelper {

    protected SQLiteDatabase bdd;

    private SQLiteOpenHelper actionSeedSQLite;

    protected Context mContext;

    protected GotsPreferences gotsPrefs;

    private int databaseType = 0;

    public final static int DATABASE_GARDEN_TYPE = 100;
    protected GotsContext getGotsContext() {
        return GotsContext.get(mContext);
    }
    public GotsDBHelper(Context mContext) {
        this.mContext = mContext;
        gotsPrefs = getGotsContext().getServerConfig();
        open();
    }

    public GotsDBHelper(Context mContext, int databaseType) {
        this.mContext = mContext;
        this.databaseType = databaseType;
        gotsPrefs = getGotsContext().getServerConfig();
        open();
    }

    private synchronized void open() {
        if (databaseType == DATABASE_GARDEN_TYPE)
            actionSeedSQLite =  GardenSQLite.getInstance(mContext);
        else
            actionSeedSQLite = DatabaseHelper.getInstance(mContext,
                    getGotsContext().getServerConfig().getCurrentGardenId());
        bdd = actionSeedSQLite.getWritableDatabase();
    }

    public synchronized void close() {
        if (bdd != null && bdd.isOpen())
            bdd.close();
    }

    @Override
    protected void finalize() throws Throwable {
        // close();
        super.finalize();
    }

}
