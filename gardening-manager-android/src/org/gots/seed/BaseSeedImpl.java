/*******************************************************************************
 * Copyright (c) 2012 sfleury.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * <p>
 * Contributors:
 * sfleury - initial API and implementation
 ******************************************************************************/
package org.gots.seed;

import org.gots.action.BaseAction;

import java.io.Serializable;
import java.util.ArrayList;

public class BaseSeedImpl implements Serializable, BaseSeed, Cloneable {

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

    private ArrayList<BaseAction> actionToDo = new ArrayList<BaseAction>();

    private ArrayList<BaseAction> actionDone = new ArrayList<BaseAction>();

    private String reference;

    private int dateSowingMin = -1;

    private int dateSowingMax = -1;

    private int durationMin = 0;

    private int durationMax = 0;

    private String urlDescription;

    private String bare_code;

    private int nb_sachet = 0;

    private String language;

    private LikeStatus likeStatus;

    private String state;

    public BaseSeedImpl() {
        super();
    }

    @Override
    public void setLanguage(String string) {
        language = string;
    }

    @Override
    public String getLanguage() {
        return language;
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
    public String getDescriptionEnvironment() {
        return description_growth;
    }

    @Override
    public void setDescriptionEnvironment(String description) {
        this.description_growth = description;
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
        return dateSowingMin;
    }

    @Override
    public void setDateSowingMin(int dateSowing) {
        this.dateSowingMin = dateSowing;
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
        seed = seed.concat("[Id: " + getSeedId() + "] ");
        seed = seed.concat("[UUID: " + getUUID() + "]");
        seed = seed.concat("<" + getFamily() + " " + getSpecie() + " " + getVariety()) + ">";
        return seed;
    }

//    @Override
//    public Object clone() throws CloneNotSupportedException {
//        GrowingSeed newSeed = new GrowingSeedImpl();
//        newSeed.setActionDone(new ArrayList<BaseAction>());
//        newSeed.getActionDone().addAll(getActionDone());
//        newSeed.setActionToDo(new ArrayList<BaseAction>());
//        newSeed.getActionToDo().addAll(getActionToDo());
//        newSeed.setDateSowingMax(dateSowingMax);
//        newSeed.setDateSowingMin(dateSowingMin);
//        newSeed.setDescriptionEnvironment(description_growth);
//        newSeed.setDurationMax(durationMax);
//        newSeed.setDurationMin(durationMin);
//        newSeed.setFamily(family);
//        newSeed.setGenus(genus);
//        newSeed.setOrder(order);
//        newSeed.setName(name);
//        newSeed.setUUID(reference);
//        newSeed.setSpecie(species);
//        newSeed.setUrlDescription(urlDescription);
//        newSeed.setVariety(variety);
//        newSeed.setSeedId(id);
//        newSeed.setNbSachet(getNbSachet());
//        return newSeed;
//    }

    @Override
    public void copy(BaseSeed original) {
        setActionDone(original.getActionDone());
        setActionToDo(original.getActionToDo());
        setDateSowingMax(original.getDateSowingMax());
        setDateSowingMin(original.getDateSowingMin());
        setDescriptionEnvironment(original.getDescriptionEnvironment());
        setDurationMax(original.getDurationMax());
        setDurationMin(original.getDurationMin());
        setFamily(original.getFamily());
        setGenus(original.getGenus());
        setOrder(original.getOrder());
        setName(original.getName());
        setUUID(original.getUUID());
        setSpecie(original.getSpecie());
        setUrlDescription(original.getUrlDescription());
        setVariety(original.getVariety());
        setSeedId(original.getSeedId());
        setNbSachet(original.getNbSachet());
        setLikeStatus(original.getLikeStatus());
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
    public ArrayList<BaseAction> getActionToDo() {
        return actionToDo;
    }

    @Override
    public void setActionToDo(ArrayList<BaseAction> actionToDo) {
        this.actionToDo = actionToDo;
    }

    @Override
    public ArrayList<BaseAction> getActionDone() {
        return actionDone;
    }

    @Override
    public void setActionDone(ArrayList<BaseAction> actionDone) {
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
    public int getSeedId() {
        return this.id;
    }

    @Override
    public void setSeedId(int id) {
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

    @Override
    public LikeStatus getLikeStatus() {
        return likeStatus;
    }

    @Override
    public void setLikeStatus(LikeStatus likeStatus) {
        this.likeStatus = likeStatus;
    }

    @Override
    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String getState() {
        return this.state;
    }

}
