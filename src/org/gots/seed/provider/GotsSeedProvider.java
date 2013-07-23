package org.gots.seed.provider;

import java.util.List;

import org.gots.seed.BaseSeedInterface;

public interface GotsSeedProvider {

	public List<BaseSeedInterface> getVendorSeeds();
	
	public void getAllFamilies();
	
	public void getFamilyById(int id);
	
	public BaseSeedInterface getSeedById();
	
	public BaseSeedInterface createSeed(BaseSeedInterface seed);

	public BaseSeedInterface updateSeed(BaseSeedInterface newSeed);
	
	
}
