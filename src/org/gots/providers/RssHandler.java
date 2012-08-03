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
package org.gots.providers;
import static org.gots.providers.BaseFeedParser.ACTION_DESCRIPTION_1;
import static org.gots.providers.BaseFeedParser.ACTION_DURATION_1;
import static org.gots.providers.BaseFeedParser.ACTION_NAME_1;
import static org.gots.providers.BaseFeedParser.DESCRIPTION;
import static org.gots.providers.BaseFeedParser.DURATION_MAX;
import static org.gots.providers.BaseFeedParser.DURATION_MIN;
import static org.gots.providers.BaseFeedParser.ITEM;
import static org.gots.providers.BaseFeedParser.LINK;
import static org.gots.providers.BaseFeedParser.SOWING_DATE;
import static org.gots.providers.BaseFeedParser.TITLE;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.warriorpoint.androidxmlsimple.Message;

public class RssHandler extends DefaultHandler{
	private List<Message> messages;
	private Message currentMessage;
	private StringBuilder builder;
	
	public List<Message> getMessages(){
		return this.messages;
	}
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		builder.append(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		super.endElement(uri, localName, name);
		if (this.currentMessage != null){
			if (localName.equalsIgnoreCase(TITLE)){
				currentMessage.setTitle(builder.toString());
			} else if (localName.equalsIgnoreCase(LINK)){
				currentMessage.setLink(builder.toString());
			} else if (localName.equalsIgnoreCase(DESCRIPTION)){
				currentMessage.setDescription(builder.toString());
			} else if (localName.equalsIgnoreCase(DURATION_MIN)){
				currentMessage.setDate(builder.toString());
			} else if (localName.equalsIgnoreCase(DURATION_MAX)){
				currentMessage.setDate(builder.toString());
			} else if (localName.equalsIgnoreCase(SOWING_DATE)){
				currentMessage.setDate(builder.toString());
			} else if (localName.equalsIgnoreCase(ACTION_NAME_1)){
				currentMessage.setDescription(builder.toString());
			} else if (localName.equalsIgnoreCase(ACTION_DESCRIPTION_1)){
				currentMessage.setDescription(builder.toString());
			} else if (localName.equalsIgnoreCase(ACTION_DURATION_1)){
				currentMessage.setDescription(builder.toString());
			} else if (localName.equalsIgnoreCase(ITEM)){
				messages.add(currentMessage);
			}
			builder.setLength(0);	
		}
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		messages = new ArrayList<Message>();
		builder = new StringBuilder();
	}

	@Override
	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, name, attributes);
		if (localName.equalsIgnoreCase(ITEM)){
			this.currentMessage = new Message();
		}
	}
}
