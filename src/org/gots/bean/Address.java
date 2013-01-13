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


public class Address  {
	private String locality;
	private String adminArea;
	private String countryName;
	private String postalCode;

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	public String getAdminArea() {
		return adminArea;
	}

	public void setAdminArea(String adminArea) {
		this.adminArea = adminArea;
	}
@Override
public String toString() {
	// TODO Auto-generated method stub
	return getLocality()+" "+getAdminArea()+" "+getCountryName();
}

public String getPostalCode() {
	return postalCode;
}

public void setPostalCode(String postalCode) {
	this.postalCode = postalCode;
}
}
