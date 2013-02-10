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


import org.gots.weather.WeatherSet;
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

	private boolean problem_cause = false;
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