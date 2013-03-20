package org.gots.garden.provider;

import java.util.List;

import org.gots.garden.GardenInterface;

public interface GardenProvider {

	public GardenInterface getCurrentGarden();
	
	public List<GardenInterface> getMyGardens();

	public GardenInterface createGarden(GardenInterface garden);
	
	
}
