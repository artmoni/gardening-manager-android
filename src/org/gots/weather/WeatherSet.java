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

     
    import java.util.ArrayList;
     
    /**
     * Combines one WeatherCurrentCondition with a List of
     * WeatherForecastConditions.
     */
    public class WeatherSet {
           
            // ===========================================================
            // Fields
            // ===========================================================
     
            private WeatherCurrentCondition myCurrentCondition = null;
            private ArrayList<WeatherForecastCondition> myForecastConditions =
                    new ArrayList<WeatherForecastCondition>(4);
     
            // ===========================================================
            // Getter & Setter
            // ===========================================================
     
            public WeatherCurrentCondition getWeatherCurrentCondition() {
                    return myCurrentCondition;
            }
     
            public void setWeatherCurrentCondition(
            		WeatherCurrentCondition myCurrentWeather) {
                    this.myCurrentCondition = myCurrentWeather;
            }
     
            public ArrayList<WeatherForecastCondition> getWeatherForecastConditions() {
                    return this.myForecastConditions;
            }
     
            public WeatherForecastCondition getLastWeatherForecastCondition() {
                    return this.myForecastConditions
                                    .get(this.myForecastConditions.size() - 1);
            }
    }

