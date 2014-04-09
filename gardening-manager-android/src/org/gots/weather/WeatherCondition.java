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

    private Integer tempCelciusMin = 0;

    private Integer tempCelciusMax = 0;

    private Integer tempFahrenheit = 0;

    private String condition = null;

    private String windCondition = null;

    private Integer humidity = null;

    private String iconURL = null;

    private Date date = null;

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
    @Override
    public int getDayofYear() {
        return this.dayofYear;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.gots.bean.WeatherConditionInterface#setDayofWeek(java.lang.String)
     */
    @Override
    public void setDayofYear(int dayofWeek) {
        this.dayofYear = dayofWeek;
    }

    /*
     * (non-Javadoc)
     * @see org.gots.bean.WeatherConditionInterface#getTempCelcius()
     */
    @Override
    public Integer getTempCelciusMin() {
        return this.tempCelciusMin;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.gots.bean.WeatherConditionInterface#setTempCelcius(java.lang.Integer)
     */
    @Override
    public void setTempCelciusMin(Integer temp) {
        this.tempCelciusMin = temp;
    }

    /*
     * (non-Javadoc)
     * @see org.gots.bean.WeatherConditionInterface#getTempFahrenheit()
     */
    @Override
    public Integer getTempFahrenheit() {
        return this.tempFahrenheit;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.gots.bean.WeatherConditionInterface#setTempFahrenheit(java.lang.Integer
     * )
     */
    @Override
    public void setTempFahrenheit(Integer temp) {
        this.tempFahrenheit = temp;
    }

    /*
     * (non-Javadoc)
     * @see org.gots.bean.WeatherConditionInterface#getCondition()
     */
    @Override
    public String getCondition() {
        return this.condition;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.gots.bean.WeatherConditionInterface#setCondition(java.lang.String)
     */
    @Override
    public void setCondition(String condition) {
        this.condition = condition;
    }

    /*
     * (non-Javadoc)
     * @see org.gots.bean.WeatherConditionInterface#getWindCondition()
     */
    @Override
    public String getWindCondition() {
        return this.windCondition;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.gots.bean.WeatherConditionInterface#setWindCondition(java.lang.String
     * )
     */
    @Override
    public void setWindCondition(String windCondition) {
        this.windCondition = windCondition;
    }

    /*
     * (non-Javadoc)
     * @see org.gots.bean.WeatherConditionInterface#getHumidity()
     */
    @Override
    public Integer getHumidity() {
        return this.humidity;
    }

    /*
     * (non-Javadoc)
     * @see
     * org.gots.bean.WeatherConditionInterface#setHumidity(java.lang.Integer)
     */
    @Override
    public void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }

    /*
     * (non-Javadoc)
     * @see org.gots.bean.WeatherConditionInterface#getIconURL()
     */
    @Override
    public String getIconURL() {
        return this.iconURL;
    }

    /*
     * (non-Javadoc)
     * @see org.gots.bean.WeatherConditionInterface#setIconURL(java.lang.String)
     */
    @Override
    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public void setDate(Date date) {
        this.date = date;

    }

    @Override
    public Date getDate() {

        return this.date;
    }

    @Override
    public Integer getTempCelciusMax() {
        return this.tempCelciusMax;
    }

    @Override
    public void setTempCelciusMax(Integer temp) {
        this.tempCelciusMax = temp;
    }

    @Override
    public String toString() {
        String txt = new String();
        txt = txt.concat("[" + getDayofYear() + "]");
        txt = txt.concat(getCondition());
        txt = txt.concat(" - ");
        txt = txt.concat(getTempCelciusMin() + "/" + getTempCelciusMax());
        return txt;
    }
}
