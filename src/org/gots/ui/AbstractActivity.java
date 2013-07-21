/* *********************************************************************** *
 * project: org.gots.*
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 * copyright       : (C) 2013 by the members listed in the COPYING,        *
 *                   LICENSE and WARRANTY file.                            *
 * email           : contact at gardening-manager dot com                  *
 *                                                                         *
 * *********************************************************************** *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *   http://www.gnu.org/licenses/gpl-2.0.html                              *
 *   See also COPYING, LICENSE and WARRANTY file                           *
 *   Contributors:                                                         *
 *                - jcarsique                                                *
 *                                                                         *
 * *********************************************************************** */
package org.gots.ui;

import org.gots.garden.GardenManager;
import org.gots.preferences.GotsPreferences;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockActivity;

/**
 * @author jcarsique
 *
 */
public class AbstractActivity extends SherlockActivity {
    private static final String TAG = "AbstractActivity";

    protected GotsPreferences gotsPrefs;

    protected GardenManager gardenManager;

    public AbstractActivity() {
        // setGardenProvider();
        gotsPrefs = GotsPreferences.getInstance(this);
        gardenManager = new GardenManager(this);
        // NuxeoManager.init(this);
    }

    // public void setGardenProvider() {
    // new AsyncTask<Void, Integer, Void>() {
    // @Override
    // protected Void doInBackground(Void... params) {
    // if (GotsPreferences.getInstance(mContext).isConnectedToServer()) {
    // try {
    // gardenProvider = new NuxeoGardenProvider(mContext);
    // } catch (NotAvailableOffline e) {
    // Log.w(getClass().getName(),
    // "Failed to initialize NuxeoGardenProvider\n"
    // + e.getMessage());
    // Log.d(getClass().getName(), e.getMessage(), e);
    // } catch (Throwable e) {
    // Log.w(getClass().getName(), e.getMessage(), e);
    // }
    // }
    // return null;
    // }
    // }.execute();
    //
    // if (gardenProvider == null) {
    // gardenProvider = new LocalGardenProvider(mContext);
    // }
    // }

}
