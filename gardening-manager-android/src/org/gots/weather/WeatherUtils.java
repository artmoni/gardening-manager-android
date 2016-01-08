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

import android.content.Context;

import org.gots.R;

/** Useful Utility in working with temperatures. (conversions). */
public class WeatherUtils {

    public static float fahrenheitToCelsius(float tFahrenheit) {
        return ((5.0f / 9.0f) * (tFahrenheit - 32));
    }

    public static float celsiusToFahrenheit(float tCelsius) {
        return ((9.0f / 5.0f) * tCelsius + 32);
    }

    public static int getWeatherResource(Context mContext, WeatherConditionInterface weatherCondition) {
        int weatherImageRessource = 0;
        if (weatherCondition.getIconURL() != null)
            weatherImageRessource = mContext.getResources().getIdentifier(
                    "org.gots:drawable/weather_" + weatherCondition.getIconURL().toLowerCase().replaceAll("-", ""), null, null);
        if (weatherImageRessource == 0)
            weatherImageRessource = R.drawable.weather_nonet;

//        if (weatherCondition.getIconURL() == null)
//            return R.drawable.weather_nonet;
//
//        if (weatherCondition.getIconURL().contains("rain"))
//            return R.drawable.weather_rain;
//        else if (weatherCondition.getIconURL().contains("mostly_sunny"))
//            return R.drawable.weather_mostlysunny;
//        else if (weatherCondition.getIconURL().contains("cloud") || weatherCondition.getIconURL().contains("mist"))
//            return R.drawable.weather_cloud;
//        else if (weatherCondition.getIconURL().contains("snow"))
//            return R.drawable.weather_snow;
//        else if (weatherCondition.getIconURL().contains("sunny"))
//            return R.drawable.weather_mostlysunny;
//        else if (weatherCondition.getIconURL().contains("storm") || weatherCondition.getIconURL().contains("thunder"))
//            return R.drawable.weather_thunder;
        return weatherImageRessource;
    }
}
