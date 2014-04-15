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
package org.gots.utils;

/**
 * @author jcarsique
 *
 */
public class NotConfiguredException extends IllegalStateException {

    @SuppressWarnings("unused")
    private NotConfiguredException() {
    }

    /**
     * @param firstCall
     */
    public NotConfiguredException(Exception firstCall) {
        super("Instance called twice whereas it was not configured on first call. See root stack trace.", firstCall);
    }

    private static final long serialVersionUID = 1L;

}
