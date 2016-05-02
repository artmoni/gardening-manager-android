/*******************************************************************************
 * Copyright (c) 2012 sfleury.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * <p/>
 * Contributors:
 * sfleury - initial API and implementation
 ******************************************************************************/
package org.gots.weather;

import java.util.Date;

public interface WeatherConditionInterface {

    public abstract int getDayofYear();

    public abstract void setDayofYear(int dayofYear);

    public abstract Float getTempCelciusMin();

    public abstract void setTempCelciusMin(Float temp);

    public abstract Float getTempCelciusMax();

    public abstract void setTempCelciusMax(Float temp);

    public abstract Float getTempFahrenheit();

    public abstract void setTempFahrenheit(Float temp);

    public abstract String getSummary();

    public abstract void setSummary(String summary);

    public abstract String getWindCondition();

    public abstract void setWindCondition(String windCondition);

    public abstract Float getHumidity();

    public abstract void setHumidity(Float humidity);

    public abstract String getIconURL();

    public abstract void setIconURL(String iconURL);

    public abstract int getId();

    public abstract void setId(int id);

    public abstract Date getDate();

    public abstract void setDate(Date date);

    public abstract String getUUID();

    public abstract void setUUID(String id);

}
