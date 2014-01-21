package org.gots.seed.provider.simple;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.gots.action.BaseActionInterface;
import org.gots.action.provider.simple.SimpleAction;
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
        return super.clone();
    }

    @Override
    public int getSeedId() {
        return seed_id;
    }

    @Override
    public void setId(int id) {
        seed_id = id;
    }

    @Override
    public String getUrlDescription() {
        // TODO ?
        return null;
    }

    @Override
    public void setUrlDescription(String urlDescription) {
        // TODO ?
    }

    @Override
    public String getName() {
        // TODO ?
        return null;
    }

    @Override
    public void setName(String name) {
        // TODO ?
    }

    @Override
    public String getDescriptionGrowth() {
        return description_growth;
    }

    @Override
    public void setDescriptionGrowth(String description) {
        description_growth = description;
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
        sowingDateMin = dateSowing;
    }

    @Override
    public void setCategory(SeedCategory category) {
        // TODO ?
    }

    @Override
    public SeedCategory getCategory() {
        // TODO ?
        return null;
    }

    @Override
    public void setDateSowingMax(int dateSowingMax) {
        sowingDateMax = dateSowingMax;
    }

    @Override
    public int getDateSowingMax() {
        return sowingDateMax;
    }

    @Override
    public Date getDateLastWatering() {
        // TODO ?
        return null;
    }

    @Override
    public void setDateLastWatering(Date dateLastWatering) {
        // TODO ?
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
        // TODO ?
        return null;
    }

    @Override
    public void setOrder(String order) {
        // TODO ?
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
        // TODO ?
        return null;
    }

    @Override
    public void setGenus(String genus) {
        // TODO ?
    }

    @Override
    public String getSpecie() {
        return specie;
    }

    @Override
    public void setSpecie(String species) {
        this.specie = species;
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
        ArrayList<BaseActionInterface> actionsToDo = new ArrayList<BaseActionInterface>();
        if (actions != null)
            for (Iterator<SimpleAction> iterator = actions.iterator(); iterator.hasNext();) {
                BaseActionInterface baseActionInterface = iterator.next();
                actionsToDo.add(baseActionInterface);
            }
        return actionsToDo;
    }

    @Override
    public void setActionToDo(ArrayList<BaseActionInterface> actionToDo) {
        // TODO ?
    }

    @Override
    public ArrayList<BaseActionInterface> getActionDone() {
        // TODO ?
        return null;
    }

    @Override
    public void setActionDone(ArrayList<BaseActionInterface> actionDone) {
        // TODO ?
    }

    @Override
    public void setBareCode(String bare_code) {
        barecode = bare_code;
    }

    @Override
    public String getBareCode() {
        return barecode;
    }

    @Override
    public void setNbSachet(int nbSeedBag) {
        nbSachet = nbSeedBag;
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
