/**
 * ****************************************************************************
 * Copyright (c) 2012 sfleury.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * <p>
 * Contributors:
 * sfleury - initial API and implementation
 * ****************************************************************************
 */
package org.gots.seed;

import android.content.Context;
import android.graphics.Bitmap;

import org.gots.R;
import org.gots.action.BaseAction;
import org.gots.utils.FileUtilities;

import java.io.File;
import java.util.ArrayList;

public class SeedUtil {
    @SuppressWarnings("unchecked")
    public BaseSeed copy(BaseSeed originalSeed) {
        BaseSeed copy = new GrowingSeedImpl();
        copy.setName(originalSeed.getName());
        copy.setDescriptionEnvironment(originalSeed.getDescriptionEnvironment());
        copy.setDescriptionDiseases(originalSeed.getDescriptionDiseases());
        copy.setDescriptionCultivation(originalSeed.getDescriptionCultivation());
        copy.setDescriptionHarvest(originalSeed.getDescriptionHarvest());
        copy.setDurationMin(originalSeed.getDurationMin());
        copy.setDurationMax(originalSeed.getDurationMax());
        copy.setDateSowingMin(originalSeed.getDateSowingMin());
        copy.setDateSowingMax(originalSeed.getDateSowingMax());
        copy.setUrlDescription(originalSeed.getUrlDescription());
        copy.setUUID(originalSeed.getUUID());
        copy.setActionToDo((ArrayList<BaseAction>) originalSeed.getActionToDo().clone());
        // copy.getActionToDo().addAll((ArrayList<Action>)originalSeed.getActionToDo().clone());
        copy.setActionDone((ArrayList<BaseAction>) originalSeed.getActionDone().clone());
        copy.setFamily(originalSeed.getFamily());
        copy.setSpecie(originalSeed.getSpecie());
        copy.setVariety(originalSeed.getVariety());
        copy.setBareCode(originalSeed.getBareCode());
        copy.setLanguage(originalSeed.getLanguage());
        copy.setState(originalSeed.getState());
        return copy;

    }

    public static String translateSpecie(Context context, BaseSeed growingSeedInterface) {
        if (growingSeedInterface == null)
            return "";
        String translateSpecie = growingSeedInterface.getSpecie();
        if (translateSpecie == null || "null".equals(translateSpecie))
            return "";
        int specieRessourceString = context.getResources().getIdentifier(
                "org.gots:string/specie." + translateSpecie.toLowerCase().replaceAll("\\s", ""), null, null);
        if (specieRessourceString != 0)
            translateSpecie = context.getResources().getString(specieRessourceString);
        return translateSpecie;
    }

    public static String translateAction(Context context, BaseAction action) {
        String translateAction = action.getName();
        if (translateAction == null || "null".equals(translateAction))
            return "";
        int actionRessourceString = context.getResources().getIdentifier(
                "org.gots:string/action." + translateAction.toLowerCase().replaceAll("\\s", ""), null, null);
        if (actionRessourceString != 0)
            translateAction = context.getResources().getString(actionRessourceString);
        return translateAction;
    }

    public static int getSeedDrawable(Context context, BaseSeed seed) {
        int vegetableImageRessource = 0;

        if (seed.getUUID() != null)
            vegetableImageRessource = context.getResources().getIdentifier(
                    "org.gots:drawable/veget_" + seed.getUUID().toLowerCase(), null, null);

        if (vegetableImageRessource == 0 && seed.getSpecie() != null)
            vegetableImageRessource = context.getResources().getIdentifier(
                    "org.gots:drawable/specie_" + seed.getSpecie().toLowerCase().replaceAll("\\s", ""), null, null);
        else
            vegetableImageRessource = R.drawable.no_picture;
        return vegetableImageRessource;
    }

    public static Bitmap getSeedBitmap(File rootPath, BaseSeed mSeed) {
        File imageFile = null;
        Bitmap image = null;
                /* Check custom image for this variety */
        if (mSeed.getVariety() != null && !"".equals(mSeed.getVariety()))
            imageFile = new File(rootPath, mSeed.getVariety().toLowerCase().replaceAll("\\s", ""));
        if (imageFile != null && !imageFile.exists() && mSeed.getUUID() != null)
            imageFile = new File(rootPath, mSeed.getUUID());
        if (imageFile != null && imageFile.exists()) {
            image = FileUtilities.decodeScaledBitmapFromSdCard(imageFile.getAbsolutePath(), 100, 100);
        }
                /* Check custom image for this species */
        else if (mSeed.getSpecie() != null) {
            imageFile = new File(rootPath, mSeed.getSpecie().toLowerCase().replaceAll("\\s", ""));
            if (imageFile != null && imageFile.exists()) {
                image = FileUtilities.decodeScaledBitmapFromSdCard(imageFile.getAbsolutePath(), 100, 100);
            }
        }
        return image;
    }
}
