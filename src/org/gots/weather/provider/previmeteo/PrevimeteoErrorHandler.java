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
package org.gots.weather.provider.previmeteo;


import org.gots.weather.WeatherCurrentCondition;
import org.gots.weather.WeatherForecastCondition;
import org.gots.weather.WeatherSet;
import org.gots.weather.WeatherUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * SAXHandler capable of extracting information out of the xml-data returned by
 * the Google Weather API.
 */
public class PrevimeteoErrorHandler extends DefaultHandler {

	// ===========================================================
	// Fields
	// ===========================================================

	private WeatherSet myWeatherSet = null;
	private boolean problem_cause = false;
	private boolean in_current_conditions = false;
	private boolean in_forecast_conditions = false;

	private boolean usingSITemperature = true; // false means Fahrenheit
	private String errorMessage;

	// ===========================================================
	// Constructors
	// ===========================================================

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public boolean isError() {
		return problem_cause;
	}
	
	public String getMessage(){
		return errorMessage;
	}

	// ===========================================================
	// Methods
	// ===========================================================
	@Override
	public void startDocument() throws SAXException {
		this.myWeatherSet = new WeatherSet();
	}

	@Override
	public void endDocument() throws SAXException {
		// Nothing
	}

	@Override
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {
		// 'Outer' Tags
		if (localName.equals("problem_cause")) {
			this.problem_cause = true;
			errorMessage = atts.getValue("data");
		
		} 
	}

	@Override
	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
		if (localName.equals("problem_cause")) {
//			this.problem_cause = false;
		} 
	}

	@Override
	public void characters(char ch[], int start, int length) {
		/*
		 * Would be called on the following structure:
		 * <element>characters</element>
		 */
	}
}