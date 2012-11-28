package org.gots.seed.providers.local;

import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name="gots",strict=false)
public class LocalSeeds {

	@ElementList(name="seeds")
	List<LocalSeedInterface> seeds;
	
	public List<LocalSeedInterface> getSeeds() {
		return seeds;
	}
	
}
