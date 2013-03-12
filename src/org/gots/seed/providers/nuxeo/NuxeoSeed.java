package org.gots.seed.providers.nuxeo;

import java.util.ArrayList;
import java.util.Date;

import org.gots.action.BaseActionInterface;
import org.gots.bean.SeedCategory;
import org.gots.seed.BaseSeedInterface;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.PropertyMap;

public class NuxeoSeed extends Document implements BaseSeedInterface {

	/** 
	 * 
	 */
	private static final long serialVersionUID = -5085980323953815686L;

	public NuxeoSeed(String parentPath, String name, String type) {
		super(parentPath, name, type);
	}

	@Override
	public String getId() {
		// TODO Auto-generated method stub
		return super.getId();
	}

	@Override
	public void setId(int id) {
		// TODO Auto-generated method stub

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
	public void setName(String name) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getDescriptionGrowth() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDescriptionGrowth(String description_growth) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getDescriptionCultivation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDescriptionCultivation(String description_cultivation) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getDescriptionHarvest() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDescriptionHarvest(String description_harvest) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getDescriptionDiseases() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDescriptionDiseases(String description_diseases) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getReference() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setReference(String reference) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getDateSowingMin() {
		// TODO Auto-generated method stub
		return 0;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setDateSowingMax(int dateSowingMax) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getDateSowingMax() {
		// TODO Auto-generated method stub
		return 0;
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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setDurationMin(int durationMin) {
		// TODO Auto-generated method stub

	}

	@Override
	public int getDurationMax() {
		// TODO Auto-generated method stub
		return 0;
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
		
		PropertyMap properties = getProperties();
		PropertyMap languageMap = properties.getMap("dc:language");
		String languageValue = languageMap.getString("data");
		return null;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSpecie(String species) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getVariety() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setVariety(String variety) {
		// TODO Auto-generated method stub

	}

	@Override
	public ArrayList<BaseActionInterface> getActionToDo() {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setNbSachet(int nbSeedBag) {
		// TODO Auto-generated method stub

	}

	@Override
	public Integer getNbSachet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getSeedId() {
		// TODO Auto-generated method stub
		return 0;
	}

}
