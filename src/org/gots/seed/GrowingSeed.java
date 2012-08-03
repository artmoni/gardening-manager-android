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
package org.gots.seed;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;

import org.gots.action.BaseActionInterface;

public class GrowingSeed extends BaseSeed implements Serializable, IActionSeedAlert, GrowingSeedInterface {
	private int growingSeedId;

	private static final long serialVersionUID = 1L;

	private Date dateSowing;

	private Date dateLastWatering;

	public static final int NB_DAY_ALERT = 10;

	public static final int NB_DAY_WARNING = 5;

	@Override
	public boolean onActionAlert() {

		for (Iterator<BaseActionInterface> iterator = getActionToDo().iterator(); iterator.hasNext();) {
			BaseActionInterface currentAction = iterator.next();

			// Check Action Alert
			Calendar actionTime = new GregorianCalendar();
			actionTime.setTime(this.getDateSowing());
			actionTime.add(Calendar.DAY_OF_MONTH, currentAction.getDuration());

			if (actionTime.getTimeInMillis() < Calendar.getInstance().getTimeInMillis() - (10 * 86400000)) {
				return true;
			}

		}
		return false;
	}

	@Override
	public boolean onActionWarning() {

		for (Iterator<BaseActionInterface> iterator = getActionToDo().iterator(); iterator.hasNext();) {
			BaseActionInterface currentAction = iterator.next();

			// Check Action Alert
			Calendar actionTime = new GregorianCalendar();
			actionTime.setTime(this.getDateSowing());
			actionTime.add(Calendar.DAY_OF_MONTH, currentAction.getDuration());

			if (actionTime.getTimeInMillis() > Calendar.getInstance().getTimeInMillis() - (NB_DAY_WARNING * 86400000)
					&& actionTime.getTimeInMillis() < Calendar.getInstance().getTimeInMillis()
							+ (NB_DAY_WARNING * 86400000)) {
				return true;
			}

		}
		return false;
	}

	/**
     *
     */
	public void performNextAction() {
		if (getActionToDo().size() > 0) {
			BaseActionInterface currentAction = getActionToDo().get(getActionToDo().size() - 1);
			currentAction.setDateActionDone(Calendar.getInstance().getTime());
			getActionDone().add(currentAction);
			getActionToDo().remove(getActionToDo().size() - 1);

		}
	}

	/**
     *
     */
	public void undoLastAction() {
		if (getActionDone().size() > 0) {
			BaseActionInterface currentAction = getActionDone().get(getActionDone().size() - 1);
			currentAction.setDateActionDone(null);
			getActionToDo().add(currentAction);
			getActionDone().remove(getActionDone().size() - 1);

		}
	}

	@Override
	public Date getDateLastWatering() {
		return dateLastWatering;
	}

	@Override
	public String toString() {

		return super.toString() + "\n" + "Semé le " + getDateSowing();
	}

	@Override
	public void setDateLastWatering(Date dateLastWatering) {
		this.dateLastWatering = dateLastWatering;
	}

	@Override
	public Date getDateSowing() {
		return dateSowing;
	}

	@Override
	public void setDateSowing(Date dateSowing) {
		this.dateSowing = dateSowing;
	}

	@Override
	public void setGrowingSeedId(int id) {
		this.growingSeedId = id;
	}

	@Override
	public int getGrowingSeedId() {

		return growingSeedId;
	}



}
