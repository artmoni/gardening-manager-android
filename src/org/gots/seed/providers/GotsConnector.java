package org.gots.seed.providers;

import java.util.List;

import org.gots.seed.BaseSeedInterface;

public interface GotsConnector {

	public List<BaseSeedInterface> getAllSeeds();
	
	public void getAllFamilies();
	
	public void getFamilyById(int id);
	
	public BaseSeedInterface getSeedById();
	
	
}
