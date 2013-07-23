package org.gots.seed.provider.simple;

import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name="gots",strict=false)
public class SimpleSeeds {

	@ElementList(name="seeds")
	List<SimpleSeedInterface> seeds;
	
	public List<SimpleSeedInterface> getSeeds() {
		return seeds;
	}
	
}
