package org.gots.garden.provider;

import java.util.List;

import org.gots.garden.GardenInterface;

public interface GardenProvider {

    public void setCurrentGarden(GardenInterface garden);

	public GardenInterface getCurrentGarden();
	
	public List<GardenInterface> getMyGardens(boolean force);

	public GardenInterface createGarden(GardenInterface garden);

	public void removeGarden(GardenInterface garden);

	public GardenInterface updateGarden(GardenInterface garden);

	
	
}
