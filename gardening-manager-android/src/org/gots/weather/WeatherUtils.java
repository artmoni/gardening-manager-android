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
package org.gots.weather;

/** Useful Utility in working with temperatures. (conversions). */
public class WeatherUtils {

    public static float fahrenheitToCelsius(float tFahrenheit) {
        return ((5.0f / 9.0f) * (tFahrenheit - 32));
    }

    public static float celsiusToFahrenheit(float tCelsius) {
        return ((9.0f / 5.0f) * tCelsius + 32);
    }
}
