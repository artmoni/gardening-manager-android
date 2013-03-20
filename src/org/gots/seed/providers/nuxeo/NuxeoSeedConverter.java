package org.gots.seed.providers.nuxeo;

import org.gots.seed.BaseSeedInterface;
import org.gots.seed.GrowingSeed;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

public class NuxeoSeedConverter {

	public static BaseSeedInterface convert(Document document) {
		BaseSeedInterface seed = new GrowingSeed();
		seed.setVariety(document.getTitle());
		seed.setFamily(document.getString("vendorseed:family"));
		seed.setSpecie(document.getString("vendorseed:specie"));
		seed.setDurationMin(Integer.valueOf(document.getString("vendorseed:durationmin")));
		seed.setDurationMax(Integer.valueOf(document.getString("vendorseed:durationmax")));
		seed.setDateSowingMin(Integer.valueOf(document.getString("vendorseed:datesowingmin")));
		seed.setDateSowingMax(Integer.valueOf(document.getString("vendorseed:datesowingmax")));
		seed.setDescriptionCultivation(document.getString("vendorseed:description_cultivation"));
		seed.setDescriptionDiseases(document.getString("vendorseed:description_diseases"));
		seed.setDescriptionGrowth(document.getString("vendorseed:description_growth"));
		seed.setDescriptionHarvest(document.getString("vendorseed:description_harvest"));

		return seed;
	}

}
