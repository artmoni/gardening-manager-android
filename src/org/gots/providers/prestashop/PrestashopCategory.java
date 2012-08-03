package org.gots.providers.prestashop;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="prestashop",strict=false)
public class PrestashopCategory {

	@Element
	Category category;
	

	public String getName(){
		return category.getName();
	}
	
}
