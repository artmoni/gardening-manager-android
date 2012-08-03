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
		 copy.setReference(originalSeed.getReference());
		 copy.setActionToDo((ArrayList<BaseActionInterface>)originalSeed.getActionToDo().clone());
//		 copy.getActionToDo().addAll((ArrayList<Action>)originalSeed.getActionToDo().clone());
		 copy.setActionDone((ArrayList<BaseActionInterface>)originalSeed.getActionDone().clone());
		 copy.setFamily(originalSeed.getFamily());
		 copy.setSpecie(originalSeed.getSpecie());
		 copy.setVariety(originalSeed.getVariety());		
		 copy.setBareCode(originalSeed.getBareCode());
		 return copy;
		
		 }
}
