package org.gots.seed.provider.prestashop;

import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name="prestashop")
public class PrestashopCategories {

	@ElementList
	List<CategoryReference> categories;

	public List<CategoryReference> getRefCategories(){
		return categories;
	}
}
