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
/*
 * (C) Copyright 2011 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Julien Carsique
 *
 */

package org.gots.seed;

import java.util.ArrayList;
import java.util.Date;

import org.gots.action.BaseActionInterface;
import org.gots.bean.SeedCategory;

/**
 *
 */
public interface BaseSeedInterface {
    public abstract int getSeedId();

    public abstract void setId(int id);

    public abstract String getUrlDescription();

    public abstract void setUrlDescription(String urlDescription);

    public abstract String getName();

    public abstract void setName(String name);

    public abstract String getDescriptionGrowth();

    public abstract void setDescriptionGrowth(String description_growth);

    public abstract String getDescriptionCultivation();

    public abstract void setDescriptionCultivation(String description_cultivation);

    public abstract String getDescriptionHarvest();

    public abstract void setDescriptionHarvest(String description_harvest);

    public abstract String getDescriptionDiseases();

    public abstract void setDescriptionDiseases(String description_diseases);

    public abstract String getUUID();

    public abstract void setUUID(String reference);

    public abstract int getDateSowingMin();

    public abstract void setDateSowingMin(int dateSowing);

    public abstract void setCategory(SeedCategory category);

    public abstract SeedCategory getCategory();

    public abstract void setDateSowingMax(int dateSowingMax);

    public abstract int getDateSowingMax();

    public abstract Date getDateLastWatering();

    public abstract void setDateLastWatering(Date dateLastWatering);

    public abstract int getDurationMin();

    public abstract void setDurationMin(int durationMin);

    public abstract int getDurationMax();

    public abstract void setDurationMax(int durationMax);

    public abstract String getOrder();

    public abstract void setOrder(String order);

    public abstract String getFamily();

    public abstract void setFamily(String family);

    public abstract String getGenus();

    public abstract void setGenus(String genus);

    public abstract String getSpecie();

    public abstract void setSpecie(String species);

    public abstract String getVariety();

    public abstract void setVariety(String variety);

    public abstract ArrayList<BaseActionInterface> getActionToDo();

    public abstract void setActionToDo(ArrayList<BaseActionInterface> actionToDo);

    public abstract ArrayList<BaseActionInterface> getActionDone();

    public abstract void setActionDone(ArrayList<BaseActionInterface> actionDone);

    public abstract void setBareCode(String bare_code);

    public abstract String getBareCode();

    public abstract void setNbSachet(int nbSeedBag);

    public abstract Integer getNbSachet();

    public abstract void setLanguage(String string);

    public abstract String getLanguage();

    public abstract void setLikeStatus(LikeStatus likes);

    public abstract LikeStatus getLikeStatus();

}
