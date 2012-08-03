/*******************************************************************************
 * Copyright (c) 2012 sfleury.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     sfleury - initial API and implementation
 ******************************************************************************/
package org.gots.bean;

import java.util.ArrayList;

public class Botanic {
	private ArrayList<String> botanicFamilyList = new ArrayList<String>();

	public Botanic() {
		botanicFamilyList.add("Apiacee");
		botanicFamilyList.add("Brassicacees");
		botanicFamilyList.add("Cucurbitacae");
		botanicFamilyList.add("Chénopodiacees");
		botanicFamilyList.add("Liliacees");
		botanicFamilyList.add("Polygonacees");
		botanicFamilyList.add("Solanacees");

	}

	public ArrayList<String> getBotanicFamilyList() {
		return botanicFamilyList;
	}
}
