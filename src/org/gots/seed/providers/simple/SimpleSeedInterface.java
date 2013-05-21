package org.gots.seed.providers.simple;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.gots.action.BaseActionInterface;
import org.gots.bean.SeedCategory;
import org.gots.seed.GrowingSeedInterface;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root(name = "seed", strict = false)
public class SimpleSeedInterface implements GrowingSeedInterface {

	// @Element(name="reference")
	// String reference;

	@Element(name = "family")
	String family;

	@Element(name = "specie")
	String specie;

	@Element(name = "variety", required = false)
	String variety;

	@Element(name = "description_growth", required = false)
	String description_growth;

	@Element(name = "description_cultivation", required = false)
	String description_cultivation;

	@Element(name = "description_harvest", required = false)
	String description_harvest;

	@Element(name = "description_diseases", required = false)
	String description_diseases;

	@Element(name = "sowingDateMin")
	int sowingDateMin;

	@Element(name = "sowingDateMax")
	int sowingDateMax;

	@Element(name = "durationMin")
	int durationMin;

	@Element(name = "durationMax")
	int durationMax;

	@Element(name = "reference", required = false)
	String reference;

	@ElementList(name = "actions", required = false)
	List<SimpleAction> actions;

	private int growinseed_id;

	private Date date_sowing;

	private String barecode;

	private Integer nbSachet = 0;

	private int seed_id;

	@Override
	public Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

	@Override
	public int getSeedId() {
		// TODO Auto-generated method stub
		return seed_id;
	}

	@Override
	public void setId(int id) {
		seed_id = id;
	}

	@Override
	public String getUrlDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setUrlDescription(String urlDescription) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getDescriptionGrowth() {
		return description_growth;
	}

	@Override
	public void setDescriptionGrowth(String description) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getUUID() {
		return reference;
	}

	@Override
	public void setUUID(String reference) {
		this.reference = reference;
	}

	@Override
	public int getDateSowingMin() {
		return sowingDateMin;
	}

	@Override
	public void setDateSowingMin(int dateSowing) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setCategory(SeedCategory category) {
		// TODO Auto-generated method stub

	}

	@Override
	public SeedCategory getCategory() {
		return null;
	}

	@Override
	public void setDateSowingMax(int dateSowingMax) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getDateSowingMax() {
		return sowingDateMax;
	}

	@Override
	public Date getDateLastWatering() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDateLastWatering(Date dateLastWatering) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getDurationMin() {
		return durationMin;
	}

	@Override
	public void setDurationMin(int durationMin) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getDurationMax() {
		return durationMax;
	}

	@Override
	public void setDurationMax(int durationMax) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getOrder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setOrder(String order) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getFamily() {
		return family;
	}

	@Override
	public void setFamily(String family) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getGenus() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setGenus(String genus) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getSpecie() {
		return specie;
	}

	@Override
	public void setSpecie(String species) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getVariety() {
		return variety;
	}

	@Override
	public void setVariety(String variety) {
		// TODO Auto-generated method stub
	}

	@Override
	public ArrayList<BaseActionInterface> getActionToDo() {
		ArrayList<BaseActionInterface> actionsToDo = new ArrayList<BaseActionInterface>();
		if (actions != null)
			for (Iterator iterator = actions.iterator(); iterator.hasNext();) {
				BaseActionInterface baseActionInterface = (BaseActionInterface) iterator.next();
				actionsToDo.add(baseActionInterface);
			}
		return actionsToDo;
	}

	@Override
	public void setActionToDo(ArrayList<BaseActionInterface> actionToDo) {
		// TODO Auto-generated method stub

	}

	@Override
	public ArrayList<BaseActionInterface> getActionDone() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setActionDone(ArrayList<BaseActionInterface> actionDone) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onActionAlert() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onActionWarning() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void performNextAction() {
		// TODO Auto-generated method stub

	}

	@Override
	public void undoLastAction() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setBareCode(String bare_code) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getBareCode() {
		return barecode;
	}

	@Override
	public void setNbSachet(int nbSeedBag) {
		// TODO Auto-generated method stub

	}

	@Override
	public Integer getNbSachet() {
		return nbSachet;
	}

	@Override
	public String getDescriptionCultivation() {

		return description_cultivation;
	}

	@Override
	public void setDescriptionCultivation(String description_cultivation) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getDescriptionHarvest() {

		return description_harvest;
	}

	@Override
	public void setDescriptionHarvest(String description_harvest) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getDescriptionDiseases() {
		return description_diseases;
	}

	@Override
	public void setDescriptionDiseases(String description_diseases) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setGrowingSeedId(int id) {
		this.growinseed_id = id;

	}

	@Override
	public int getGrowingSeedId() {
		return this.growinseed_id;
	}

	@Override
	public Date getDateSowing() {
		return date_sowing;
	}

	@Override
	public void setDateSowing(Date dateSowing) {
		date_sowing = dateSowing;
	}

}
