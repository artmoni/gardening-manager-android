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

import java.util.Date;

public class WeatherCondition implements WeatherConditionInterface {

    private int id;

    private int dayofYear = 0;

    private Float tempCelciusMin = 0f;

    private Float tempCelciusMax = 0f;

    private Float tempFahrenheit = 0f;

    private String condition = null;

    private String windCondition = null;

    private Float humidity = null;

    private String iconURL = null;

    private Date date = null;

    private String uuid;

    public WeatherCondition() {
        super();
    }

    public WeatherCondition(Date date) {
        super();
        setDate(date);
    }

    /*
     * (non-Javadoc)
     * @see org.gots.bean.WeatherConditionInterface#getDayofWeek()
     */
    public int getDayofYear() {
        return this.dayofYear;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.gots.bean.WeatherConditionInterface#setDayofWeek(java.lang.String)
     */
    public void setDayofYear(int dayofWeek) {
        this.dayofYear = dayofWeek;
    }

    /*
     * (non-Javadoc)
     * @see org.gots.bean.WeatherConditionInterface#getTempCelcius()
     */
    public Float getTempCelciusMin() {
        return this.tempCelciusMin;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.gots.bean.WeatherConditionInterface#setTempCelcius(java.lang.Integer)
     */
    public void setTempCelciusMin(Float temp) {
        this.tempCelciusMin = temp;
    }

    /*
     * (non-Javadoc)
     * @see org.gots.bean.WeatherConditionInterface#getTempFahrenheit()
     */
    public Float getTempFahrenheit() {
        return this.tempFahrenheit;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.gots.bean.WeatherConditionInterface#setTempFahrenheit(java.lang.Integer
     * )
     */
    public void setTempFahrenheit(Float temp) {
        this.tempFahrenheit = temp;
    }

    /*
     * (non-Javadoc)
     * @see org.gots.bean.WeatherConditionInterface#getCondition()
     */
    public String getSummary() {
        return this.condition;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.gots.bean.WeatherConditionInterface#setCondition(java.lang.String)
     */
    public void setSummary(String condition) {
        this.condition = condition;
    }

    /*
     * (non-Javadoc)
     * @see org.gots.bean.WeatherConditionInterface#getWindCondition()
     */
    public String getWindCondition() {
        return this.windCondition;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.gots.bean.WeatherConditionInterface#setWindCondition(java.lang.String
     * )
     */
    public void setWindCondition(String windCondition) {
        this.windCondition = windCondition;
    }

    /*
     * (non-Javadoc)
     * @see org.gots.bean.WeatherConditionInterface#getHumidity()
     */
    public Float getHumidity() {
        return this.humidity;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.gots.bean.WeatherConditionInterface#setHumidity(java.lang.Integer)
     */
    public void setHumidity(Float humidity) {
        this.humidity = humidity;
    }

    /*
     * (non-Javadoc)
     * @see org.gots.bean.WeatherConditionInterface#getIconURL()
     */
    public String getIconURL() {
        return this.iconURL;
    }

    /*
     * (non-Javadoc)
     * @see org.gots.bean.WeatherConditionInterface#setIconURL(java.lang.String)
     */
    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDate(Date date) {
        this.date = date;

    }

    public Date getDate() {

        return this.date;
    }

    public Float getTempCelciusMax() {
        return this.tempCelciusMax;
    }

    public void setTempCelciusMax(Float temp) {
        this.tempCelciusMax = temp;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("#" + getId());
        builder.append("[" + getUUID() + "]");
        builder.append(", ");
        builder.append("Day=" + getDayofYear());
        builder.append(", ");
        builder.append("Forecast=" + getSummary());
        builder.append(", ");
        builder.append(getTempCelciusMin() + "<Temperature<" + getTempCelciusMax());
        return builder.toString();
    }

    public void setUUID(String id) {
        this.uuid = id;
    }

    public String getUUID() {
        return this.uuid;
    }
}
