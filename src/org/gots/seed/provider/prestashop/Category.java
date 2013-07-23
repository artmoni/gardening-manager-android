package org.gots.seed.provider.prestashop;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root(name="category",strict=false)
public class Category {
	@Element(name = "name")
	LanguageString name;
	
	public String getName(){
		return name.getTranstaledString();
	}
}
