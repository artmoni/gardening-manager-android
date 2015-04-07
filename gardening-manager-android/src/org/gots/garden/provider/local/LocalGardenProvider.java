package org.gots.garden.provider.local;

import java.util.List;

import org.gots.garden.GardenInterface;
import org.gots.garden.provider.GardenProvider;
import org.gots.preferences.GotsPreferences;
import org.gots.provider.AbstractProvider;

import android.content.Context;

public class LocalGardenProvider extends AbstractProvider implements GardenProvider {

    protected GardenDBHelper helper;

    public LocalGardenProvider(Context context) {
        super(context);
        helper = new GardenDBHelper(context);
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
        GardenInterface garden = helper.getGarden(gotsPrefs.get(GotsPreferences.ORG_GOTS_CURRENT_GARDENID, -1));
        return garden;
    }

    @Override
    public List<GardenInterface> getMyGardens(boolean force) {
        return helper.getGardens();
    }

    @Override
    public void removeGarden(GardenInterface garden) {
        helper.deleteGarden(garden);
    }

    public GardenInterface getGardenById(Integer id) {
        return helper.getGarden(id);
    }

    @Override
    public void setCurrentGarden(GardenInterface garden) {
        gotsPrefs.set(GotsPreferences.ORG_GOTS_CURRENT_GARDENID, (int) garden.getId());
    }

    public int share(GardenInterface garden, String user, String permission) {
        return -1;
    }

    public void getUsersAndGroups(GardenInterface garden) {
    }

}
