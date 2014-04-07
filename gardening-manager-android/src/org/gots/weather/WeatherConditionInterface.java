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

public interface WeatherConditionInterface {

	public abstract int getDayofYear();

	public abstract void setDayofYear(int dayofYear);

	public abstract Integer getTempCelciusMin();

	public abstract void setTempCelciusMin(Integer temp);

	public abstract Integer getTempCelciusMax();

	public abstract void setTempCelciusMax(Integer temp);

	public abstract Integer getTempFahrenheit();

	public abstract void setTempFahrenheit(Integer temp);

	public abstract String getCondition();

	public abstract void setCondition(String condition);

	public abstract String getWindCondition();

	public abstract void setWindCondition(String windCondition);

	public abstract Integer getHumidity();

	public abstract void setHumidity(Integer humidity);

	public abstract String getIconURL();

	public abstract void setIconURL(String iconURL);

	public abstract void setId(int id);

	public abstract int getId();

	public abstract void setDate(Date date);

	public abstract Date getDate();

}
