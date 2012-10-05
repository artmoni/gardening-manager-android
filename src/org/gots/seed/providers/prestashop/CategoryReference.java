package org.gots.seed.providers.prestashop;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name="category")
public class CategoryReference {

	@Attribute(name="id")
	private int categoryId;
	
	@Attribute(name="href")
	private String href;
	
	public int getCategoryId(){
		return categoryId;
	}
	
}
