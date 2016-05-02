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
package org.gots.provider;

import android.content.Context;

import org.gots.context.GotsContext;
import org.gots.preferences.GotsPreferences;

/**
 * @author jcarsique
 */
public abstract class AbstractProvider {

    protected final GotsPreferences gotsPrefs;

    protected Context mContext;

    /**
     * @param context
     */
    public AbstractProvider(Context context) {
        mContext = context;
        gotsPrefs = getGotsContext().getServerConfig();
    }

    protected GotsContext getGotsContext() {
        return GotsContext.get(mContext);
    }
}