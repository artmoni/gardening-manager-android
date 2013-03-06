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
package org.gots.seed;

import java.io.Serializable;
import java.util.ArrayList;

import org.gots.action.BaseActionInterface;
import org.gots.bean.SeedCategory;

public abstract class BaseSeed implements Serializable, BaseSeedInterface {

	private static final long serialVersionUID = 1L;
	private int id;

	private String name;

	private String description_growth;
	private String description_harvest;
	private String description_cultivation;
	private String description_diseases;

	private String order;

	private String family;

	private String genus;

	private String species;

	private String variety;

	private ArrayList<BaseActionInterface> actionToDo = new ArrayList<BaseActionInterface>();

	private ArrayList<BaseActionInterface> actionDone = new ArrayList<BaseActionInterface>();

	private String reference;

	private int dateSowingMin=-1;

	private int dateSowingMax=-1;

	private int durationMin;

	private int durationMax;

	private SeedCategory category;

	private String urlDescription;

	private String bare_code;

	private int nb_sachet;

	public BaseSeed() {
		super();
	}

	@Override
	public String getUrlDescription() {
		return urlDescription;
	}

	@Override
	public void setUrlDescription(String urlDescription) {
		this.urlDescription = urlDescription;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getDescriptionGrowth() {
		return description_growth;
	}

	@Override
	public void setDescriptionGrowth(String description) {
		this.description_growth = description;
	}

	@Override
	public String getReference() {
		return reference;
	}

	@Override
	public void setReference(String reference) {
		this.reference = reference;
	}

	@Override
	public int getDateSowingMin() {
		return dateSowingMin;
	}

	@Override
	public void setDateSowingMin(int dateSowing) {
		this.dateSowingMin = dateSowing;
	}

	@Override
	public void setCategory(SeedCategory category) {
		this.category = category;
	}

	@Override
	public SeedCategory getCategory() {
		return category;
	}

	@Override
	public void setDateSowingMax(int dateSowingMax) {
		this.dateSowingMax = dateSowingMax;
	}

	@Override
	public int getDateSowingMax() {
		return dateSowingMax;
	}

	@Override
	public String toString() {
		String seed = new String();
		seed = "-" + getId() + "--------- " + getFamily()+" " + getSpecie()+" " + getVariety()+" ------------";
		seed += "\n-- Date Arrosage:" + getDateLastWatering();
		seed += "\n-- Délais récolte: " + getDurationMin() + "/" + getDurationMax();
		seed += "\n-- Nb Sachets:" + getNbSachet();
		seed += "\n";
		return seed;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		GrowingSeed newSeed = new GrowingSeed();
		newSeed.setActionDone(new ArrayList<BaseActionInterface>());
		newSeed.getActionDone().addAll(getActionDone());
		newSeed.setActionToDo(new ArrayList<BaseActionInterface>());
		newSeed.getActionToDo().addAll(getActionToDo());
		newSeed.setCategory(category);
		newSeed.setDateSowingMax(dateSowingMax);
		newSeed.setDateSowingMin(dateSowingMin);
		newSeed.setDescriptionGrowth(description_growth);
		newSeed.setDurationMax(durationMax);
		newSeed.setDurationMin(durationMin);
		newSeed.setFamily(family);
		newSeed.setGenus(genus);
		newSeed.setOrder(order);
		newSeed.setName(name);
		newSeed.setReference(reference);
		newSeed.setSpecie(species);
		newSeed.setUrlDescription(urlDescription);
		newSeed.setVariety(variety);
		newSeed.setId(id);
		newSeed.setNbSachet(getNbSachet());
		return newSeed;
	}

	@Override
	public int getDurationMin() {
		return durationMin;
	}

	@Override
	public void setDurationMin(int durationMin) {
		this.durationMin = durationMin;
	}

	@Override
	public int getDurationMax() {
		return durationMax;
	}

	@Override
	public void setDurationMax(int durationMax) {
		this.durationMax = durationMax;
	}

	@Override
	public String getOrder() {
		return order;
	}

	@Override
	public void setOrder(String order) {
		this.order = order;
	}

	@Override
	public String getFamily() {
		return family;
	}

	@Override
	public void setFamily(String family) {
		this.family = family;
	}

	@Override
	public String getGenus() {
		return genus;
	}

	@Override
	public void setGenus(String genus) {
		this.genus = genus;
	}

	@Override
	public String getSpecie() {
		return species;
	}

	@Override
	public void setSpecie(String species) {
		this.species = species;
	}

	@Override
	public String getVariety() {
		return variety;
	}

	@Override
	public void setVariety(String variety) {
		this.variety = variety;
	}

	@Override
	public ArrayList<BaseActionInterface> getActionToDo() {
		return actionToDo;
	}

	@Override
	public void setActionToDo(ArrayList<BaseActionInterface> actionToDo) {
		this.actionToDo = actionToDo;
	}

	@Override
	public ArrayList<BaseActionInterface> getActionDone() {
		return actionDone;
	}

	@Override
	public void setActionDone(ArrayList<BaseActionInterface> actionDone) {
		this.actionDone = actionDone;
	}

	@Override
	public String getBareCode() {
		return bare_code;
	}

	@Override
	public void setBareCode(String bare_code) {
		this.bare_code = bare_code;
	}

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	public Integer getNbSachet() {
		return nb_sachet;
	}

	public void setNbSachet(int nb_sachet) {
		this.nb_sachet = nb_sachet;
	}

	@Override
	public String getDescriptionCultivation() {

		return description_cultivation;
	}

	@Override
	public void setDescriptionCultivation(String description_cultivation) {
		this.description_cultivation = description_cultivation;
	}

	@Override
	public String getDescriptionHarvest() {
		return description_harvest;
	}

	@Override
	public void setDescriptionHarvest(String description_harvest) {
		this.description_harvest = description_harvest;
	}

	@Override
	public String getDescriptionDiseases() {

		return description_diseases;
	}

	@Override
	public void setDescriptionDiseases(String description_diseases) {
		this.description_diseases = description_diseases;

	}

}
