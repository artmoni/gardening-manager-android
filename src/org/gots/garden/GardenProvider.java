package org.gots.garden;

import java.util.List;

public interface GardenProvider {

	public GardenInterface getCurrentGarden();
	
	public List<GardenInterface> getMyGardens();

	public void createGarden(GardenInterface garden);
	
	
}
