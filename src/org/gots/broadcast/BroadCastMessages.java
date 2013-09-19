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
 *                - jcarsique                                              *
 *                                                                         *
 * *********************************************************************** */
package org.gots.broadcast;

/**
 * @author jcarsique
 * 
 */
public final class BroadCastMessages {

    public final static String SEED_DISPLAYLIST = "org.gots.seed.broadcastevent";

    public final static String GARDEN_EVENT = "org.gots.garden.broadcastevent";

    public final static String WEATHER_DISPLAY_EVENT = "org.gots.weather.broadcastevent";

    public final static String CONNECTION_SETTINGS_CHANGED = "org.gots.settings.connection";

    public static final String GARDEN_SETTINGS_CHANGED = "org.gots.settings.garden";

    private BroadCastMessages() {
        throw new AssertionError();
    }
}
