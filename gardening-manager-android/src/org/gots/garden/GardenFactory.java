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
package org.gots.garden;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.GregorianCalendar;

import org.gots.action.bean.BeakeringAction;
import org.gots.bean.Address;
import org.gots.bean.Allotment;
import org.gots.bean.Garden;
import org.gots.seed.GrowingSeed;
import org.gots.seed.GrowingSeedImpl;

import android.content.Context;
import android.util.Log;

public class GardenFactory {

    private GardenInterface myGarden = new Garden();

    private static String GARDEN_STORE_FILE = "my_garden1";

    private Context mContext;

    public GardenFactory(Context context) {
        mContext = context;
        try {
            FileInputStream fis = context.openFileInput(GARDEN_STORE_FILE);

            ObjectInputStream ois = new ObjectInputStream(fis);
            try {
                myGarden = (GardenInterface) ois.readObject();
            } catch (ClassNotFoundException cnfe) {
                Log.v("onCreate", "ClassNotFoundException" + cnfe.getMessage());
            }
        } catch (FileNotFoundException e) {
            Log.v("onCreate", "FileNotFoundException" + e.getMessage());

            myGarden = sampleGarden();
            // saveGarden(myGarden);

        } catch (IOException ioe) {
            Log.v("onCreate", "IOException FIS" + ioe.getMessage());
        }
    }

    // public void saveGarden(GardenInterface garden) {
    // try {
    // FileOutputStream fos = mContext.openFileOutput(GARDEN_STORE_FILE,
    // mContext.MODE_PRIVATE);
    // ObjectOutputStream oos = new ObjectOutputStream(fos);
    // oos.writeObject(garden);
    // oos.close();
    // } catch (IOException ioe) {
    // Log.v("saveGarden", "IOException FOS" + ioe.getMessage());
    // }
    // }

    private GardenInterface sampleGarden() {
        GardenInterface sampleGarden = new Garden();
        // ##############LOT A ############
        sampleGarden.setName("Jardin de Boissy l'Aillerie");
        Address sampleAddress = new Address();
        sampleAddress.setLocality("Boissy l'Aillerie");
        sampleGarden.setAddress(sampleAddress);

        Allotment allotment = new Allotment();
        allotment.setName("Lot A");
        allotment.setDescription("en haut du jardin");

        // sampleGarden.getAllotments().add(allotment);
        GrowingSeed mySeed = new GrowingSeedImpl();
        mySeed.setName("tomates");
        mySeed.setUUID("23432LK23");
        mySeed.setDescriptionGrowth("Variété la plus précoce pour culture de plein champ. Croissance indéterminée. Feuillage de pomme de terre. Fruit de taille moyenne (90 à 150 g), rouge brillant par bouquet de 5 à 8 fruits.");
        mySeed.setDateSowingMin(03);
        mySeed.setDateSowing(new GregorianCalendar(2011, 03, 12).getTime());
        mySeed.setDurationMin(150);
        mySeed.getActionToDo().add(new BeakeringAction(mContext));
        mySeed.getActionToDo().get(0).setDuration(10);

        // mySeed.setDatePicking("Aou-Oct");

        // mySeed.setCategory(SeedCategory.CATEGORY_TOMATE);
        allotment.getSeeds().add(mySeed);

        mySeed = new GrowingSeedImpl();
        mySeed.setName("Navet précoce de Croissy");
        mySeed.setUUID("concombregynialhybridef1");
        mySeed.setDescriptionGrowth("Variété pour fin de printemps, début d'été. Racine très blanche, cylindrique et pointue, demi – longue, pouvant devenir volumineuse. Saveur sucrée. Résiste bien à la chaleur et ne creuse pas. Développement rapide (1,5 à 2 mois). Semis en mars – avril.");
        mySeed.setDateSowingMin(4);
        mySeed.setDateSowing(new GregorianCalendar(2011, 03, 23).getTime());
        mySeed.setDurationMin(90);
        // mySeed.setDatePicking("Avr-Jui");
        allotment.getSeeds().add(mySeed);

        // ##############LOT B ############

        allotment = new Allotment();
        allotment.setName("Lot B");
        allotment.setDescription("en bas du jardin");
        // sampleGarden.getAllotments().add(allotment);

        mySeed = new GrowingSeedImpl();
        mySeed.setName("patates");
        mySeed.setUUID("betteravedegypte");
        mySeed.setDescriptionGrowth("La pomme de terre, ou patate (langage familier, canadianisme et français régional), est un tubercule comestible produit par l'espèce Solanum tuberosum, appartenant à la famille des solanacées. Le terme désigne également la plante elle-même, plante herbacée, vivace par ses tubercules en l'absence de gel mais cultivée comme une plante annuelle..");
        mySeed.setDateSowingMin(10);
        mySeed.setDateSowing(new GregorianCalendar(2011, 01, 11).getTime());
        mySeed.setDurationMin(100);
        // mySeed.setDatePicking("Aou-Sept");
        allotment.getSeeds().add(mySeed);

        mySeed = new GrowingSeedImpl();
        mySeed.setName("Courgette");
        mySeed.setUUID("fdgsdf989");
        mySeed.setDescriptionGrowth("Composée de 95 % d'eau, elle contient une quantité record de minéraux et d'oligo-éléments. Une portion de 100 g de ce légume, de préférence cuit à la vapeur ou à l'étuvée afin d'éviter les pertes, apporte jusqu'à 700 mg de ces précieux nutriments, et notamment du potassium (230 mg), du phosphore, du magnésium et du calcium.");
        mySeed.setDateSowingMin(9);
        mySeed.setDateSowing(new GregorianCalendar(2011, 05, 27).getTime());
        mySeed.setDurationMin(160);
        mySeed.getActionToDo().add(new BeakeringAction(mContext));
        mySeed.getActionToDo().get(0).setDuration(10);
        // mySeed.setDatePicking("Avr-Mai");
        allotment.getSeeds().add(mySeed);

        // ####################
        return sampleGarden;

    }

    public GardenInterface getMyGarden() {
        return myGarden;
    }

}
