package org.gots.seed.providers;

import java.util.List;

import org.gots.seed.BaseSeedInterface;

public interface GotsSeedProvider {

	public List<BaseSeedInterface> getAllSeeds();
	
	public void getAllFamilies();
	
	public void getFamilyById(int id);
	
	public BaseSeedInterface getSeedById();
	
	
}
