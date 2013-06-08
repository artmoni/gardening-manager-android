package org.gots.garden.provider.local;

import java.util.List;

import org.gots.garden.GardenInterface;
import org.gots.garden.provider.GardenProvider;
import org.gots.garden.sql.GardenDBHelper;
import org.gots.preferences.GotsPreferences;

import android.content.Context;
import android.content.SharedPreferences;

public class LocalGardenProvider implements GardenProvider {

    protected final GotsPreferences gotsPrefs;

    protected final SharedPreferences sharedPrefs;

    protected Context mContext;

    protected GardenDBHelper helper;

    public LocalGardenProvider(Context context) {
        mContext = context;
        gotsPrefs = GotsPreferences.getInstance(mContext);
        sharedPrefs = gotsPrefs.getSharedPrefs();
        helper = new GardenDBHelper(mContext);
    }

    @Override
    public GardenInterface createGarden(GardenInterface garden) {
        GardenInterface newGarden = helper.insertGarden(garden);
        return newGarden;
    }

    @Override
    public GardenInterface updateGarden(GardenInterface garden) {
        GardenInterface newGarden = helper.updateGarden(garden);
        return newGarden;
    }

    @Override
    public GardenInterface getCurrentGarden() {
        GardenInterface garden = helper.getGarden(gotsPrefs.get(GotsPreferences.ORG_GOTS_CURRENT_GARDENID, 0));
        return garden;
    }

    @Override
    public List<GardenInterface> getMyGardens() {
        return helper.getGardens();
    }

    @Override
    public int removeGarden(GardenInterface garden) {
        return helper.deleteGarden(garden);
    }

}
