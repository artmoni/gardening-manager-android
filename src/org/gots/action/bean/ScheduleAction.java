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
package org.gots.action.bean;

import java.util.Calendar;
import java.util.Date;

import org.gots.action.AbstractActionSeed;
import org.gots.action.SeedActionInterface;
import org.gots.action.sql.ActionSeedDBHelper;
import org.gots.seed.GrowingSeedInterface;

import android.content.Context;

public class ScheduleAction extends AbstractActionSeed implements SeedActionInterface {
	Context mContext;

	public ScheduleAction(	Context mContext) {
		setName("schedule");
		this.mContext=mContext;
	}

	@Override
	public int execute(GrowingSeedInterface seed) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(seed.getDateSowing());
		cal.add(Calendar.DAY_OF_YEAR, getDuration());
		setDateActionTodo(cal.getTime());
		return super.execute(seed);
	}

	public void setDateActionDone(Date dateActionDone) {
		super.setDateActionDone(dateActionDone);
	}

	public Date getDateActionDone() {
		return super.getDateActionDone();
	}

	public void setDuration(int duration) {
		super.setDuration(duration);
	}

	public int getDuration() {
		return super.getDuration();
	}

	public void setDescription(String description) {
		super.setDescription(description);
	}

	public String getDescription() {
		return super.getDescription();
	}

	public void setName(String name) {
		super.setName(name);
	}

	public String getName() {
		return super.getName();
	}

}
