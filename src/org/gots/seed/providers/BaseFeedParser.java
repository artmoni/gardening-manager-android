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
package org.gots.seed.providers;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.gots.action.ActionFactory;
import org.gots.action.BaseActionInterface;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.GrowingSeed;
import org.gots.seed.SeedUtil;

import android.content.Context;
import android.sax.Element;
import android.sax.EndElementListener;
import android.sax.EndTextElementListener;
import android.sax.RootElement;
import android.util.Xml;

public class BaseFeedParser {

	// static String feedUrlString =
	// "http://www.androidster.com/android_news.rss";
	// static String feedUrlString = Resources.getSystem().getXml();
	// names of the XML tags
	static final String RSS = "seeds";
	static final String CHANNEL = "channel";
	static final String ITEM = "item";

	static final String DURATION_MIN = "durationMin";
	static final String DURATION_MAX = "durationMax";

	static final String SOWING_DATE = "sowingDate";
	static final String DESCRIPTION = "description";
	static final String REFERENCE = "reference";
	static final String BARECODE = "barecode";

	
	static final String VARIETY = "variety";
	static final String SPECIE = "specie";
	static final String FAMILY = "family";

	static final String LINK = "link";
	static final String TITLE = "title";
	private final String MIN_SOWING_DATE = "sowingDateMin";
	private final String MAX_SOWING_DATE = "sowingDateMax";

	static final String ACTION_NAME_1 = "action_name_1";
	static final String ACTION_DESCRIPTION_1 = "action_description_1";
	static final String ACTION_DURATION_1 = "action_duration_1";

	// private final URL feedUrl;
	Context mContext;

	public BaseFeedParser(Context mContext) {
		this.mContext = mContext;
	}

	protected InputStream getInputStream(int ressourceXML) {
		return mContext.getResources().openRawResource(ressourceXML);
	}

	public List<BaseSeedInterface> parse(int ressourceXML) {
		final BaseSeedInterface currentSeed = new GrowingSeed();
		RootElement root = new RootElement(RSS);
		final List<BaseSeedInterface> seeds = new ArrayList<BaseSeedInterface>();
		Element itemlist = root.getChild(CHANNEL);

		Element item = itemlist.getChild(ITEM);

		item.setEndElementListener(new EndElementListener() {
			public void end() {

				seeds.add(new SeedUtil().copy(currentSeed));
				currentSeed.setActionToDo(new ArrayList<BaseActionInterface>());
			}
		});
		item.getChild(TITLE).setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {
				currentSeed.setName(body);
			}
		});
		item.getChild(LINK).setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {
				currentSeed.setUrlDescription(body);
			}
		});
		item.getChild(DESCRIPTION).setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {
				currentSeed.setDescriptionGrowth(body);
			}
		});
		item.getChild(REFERENCE).setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {
				currentSeed.setUUID(body);
			}
		});
		item.getChild(DURATION_MIN).setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {
				currentSeed.setDurationMin(new Integer(body));
			}
		});
		item.getChild(BARECODE).setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {
				currentSeed.setBareCode(body);
			}
		});
		item.getChild(DURATION_MAX).setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {
				currentSeed.setDurationMax(new Integer(body));
			}
		});
		item.getChild(MIN_SOWING_DATE).setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {
				currentSeed.setDateSowingMin(new Integer(body));
			}
		});
		item.getChild(MAX_SOWING_DATE).setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {
				currentSeed.setDateSowingMax(new Integer(body));
			}
		});
		item.getChild(ACTION_NAME_1).setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {
				if (currentSeed.getActionToDo().size() < 1) {
					currentSeed.setActionToDo(new ArrayList<BaseActionInterface>());
				}
				ActionFactory factory = new ActionFactory();
				BaseActionInterface action = factory.buildAction(mContext, body);
				currentSeed.getActionToDo().add(action);
			}
		});
		item.getChild(ACTION_DESCRIPTION_1).setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {
				if (currentSeed.getActionToDo().size() < 1) {
					currentSeed.setActionToDo(new ArrayList<BaseActionInterface>());
					// currentSeed.getActionToDo().add(new Action());
				}
				// currentSeed.getActionToDo().get(0).setDescription(body);
			}
		});
		item.getChild(ACTION_DURATION_1).setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {
				if (currentSeed.getActionToDo().size() < 1) {
					currentSeed.setActionToDo(new ArrayList<BaseActionInterface>());
				}
				if (body != "")
					currentSeed.getActionToDo().get(currentSeed.getActionToDo().size() - 1)
							.setDuration(Integer.parseInt(body));
			}
		});
		item.getChild(VARIETY).setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {

				currentSeed.setVariety(body);
			}
		});
		item.getChild(SPECIE).setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {
				currentSeed.setSpecie(body);
			}
		});
		item.getChild(FAMILY).setEndTextElementListener(new EndTextElementListener() {
			public void end(String body) {
				currentSeed.setFamily(body);
			}
		});
		try {
			Xml.parse(this.getInputStream(ressourceXML), Xml.Encoding.UTF_8, root.getContentHandler());

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return seeds;
	}
}
