package org.gots.garden.provider;

import org.gots.garden.GardenInterface;

import java.util.List;

public interface GardenProvider {

    public GardenInterface getCurrentGarden();

    public void setCurrentGarden(GardenInterface garden);

    public List<GardenInterface> getMyGardens(boolean force);

    public GardenInterface createGarden(GardenInterface garden);

    public void removeGarden(GardenInterface garden);

    public GardenInterface updateGarden(GardenInterface garden);

    public abstract int share(GardenInterface garden, String user, String permission);

    public abstract void getUsersAndGroups(GardenInterface garden);


}
