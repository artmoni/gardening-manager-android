/*******************************************************************************
 * Copyright (c) 2012 sfleury.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * <p/>
 * Contributors:
 * sfleury - initial API and implementation
 ******************************************************************************/
package org.gots.seed;

import java.util.Date;

public class GrowingSeedImpl implements GrowingSeed {
    public static final int NB_DAY_ALERT = 10;
    public static final int NB_DAY_WARNING = 5;
    private static final long serialVersionUID = 1L;
    private int id = -1;
    private BaseSeed plant;
    private Date dateSowing;
    private Date dateHarvest;
    private Date dateLastWatering;
    private String uuid;

    @Override
    public Date getDateLastWatering() {
        return dateLastWatering;
    }

    @Override
    public void setDateLastWatering(Date dateLastWatering) {
        this.dateLastWatering = dateLastWatering;
    }

    @Override
    public String toString() {

        return super.toString() + "\n" + "Semé le " + getDateSowing();
    }

    @Override
    public Date getDateSowing() {
        return dateSowing;
    }

    @Override
    public void setDateSowing(Date dateSowing) {
        this.dateSowing = dateSowing;
    }

//    @Override
//    public void setGrowingSeedId(int id) {
//        this.growingSeedId = id;
//    }
//
//    @Override
//    public int getGrowingSeedId() {
//
//        return growingSeedId;
//    }

    @Override
    public BaseSeed getPlant() {
        return plant;
    }

    @Override
    public void setPlant(BaseSeed plant) {
        this.plant = plant;
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public Date getDateHarvest() {
        return dateHarvest;
    }

    @Override
    public void setDateHarvest(Date dateHarvest) {
        this.dateHarvest = dateHarvest;
    }

    @Override
    public String getUUID() {
        return this.uuid;
    }

    @Override
    public void setUUID(String id) {
        this.uuid = id;
    }
}
