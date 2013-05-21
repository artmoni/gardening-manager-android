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

import java.util.ArrayList;

import org.gots.action.BaseActionInterface;

import android.content.Context;

public class SeedUtil {
	public BaseSeedInterface copy(BaseSeedInterface originalSeed) {
		BaseSeedInterface copy = new GrowingSeed();
		copy.setName(originalSeed.getName());
		copy.setDescriptionGrowth(originalSeed.getDescriptionGrowth());
		copy.setDurationMin(originalSeed.getDurationMin());
		copy.setDurationMax(originalSeed.getDurationMax());
		copy.setDateSowingMin(originalSeed.getDateSowingMin());
		copy.setDateSowingMax(originalSeed.getDateSowingMax());
		copy.setUrlDescription(originalSeed.getUrlDescription());
		copy.setCategory(originalSeed.getCategory());
		copy.setUUID(originalSeed.getUUID());
		copy.setActionToDo((ArrayList<BaseActionInterface>) originalSeed.getActionToDo().clone());
		// copy.getActionToDo().addAll((ArrayList<Action>)originalSeed.getActionToDo().clone());
		copy.setActionDone((ArrayList<BaseActionInterface>) originalSeed.getActionDone().clone());
		copy.setFamily(originalSeed.getFamily());
		copy.setSpecie(originalSeed.getSpecie());
		copy.setVariety(originalSeed.getVariety());
		copy.setBareCode(originalSeed.getBareCode());
		return copy;

	}

	public static String translateSpecie(Context context, BaseSeedInterface growingSeedInterface) {
		String translateSpecie = growingSeedInterface.getSpecie();
		if (translateSpecie== null)
			return "";
		int specieRessourceString = context.getResources().getIdentifier(
				"org.gots:string/specie." + translateSpecie.toLowerCase().replaceAll("\\s", ""), null, null);
		if (specieRessourceString != 0)
			translateSpecie = context.getResources().getString(specieRessourceString);
		return translateSpecie;
	}
	
	public static String translateAction(Context context, BaseActionInterface action) {
		String translateAction = action.getName();
		if (translateAction==null)
			return "";
		int actionRessourceString = context.getResources().getIdentifier(
				"org.gots:string/action." + translateAction.toLowerCase().replaceAll("\\s", ""), null, null);
		if (actionRessourceString != 0)
			translateAction = context.getResources().getString(actionRessourceString);
		return translateAction;
	}
}
