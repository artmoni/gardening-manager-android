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
package org.gots.action;

import java.util.Date;

public interface BaseActionInterface {

	public abstract Date getDateActionTodo();

	public abstract void setDateActionTodo(Date dateActionTodo);

	public abstract void setDateActionDone(Date dateActionDone);

	public abstract Date getDateActionDone();

	public abstract void setDuration(int duration);

	public abstract int getDuration();

	public abstract void setDescription(String description);

	public abstract String getDescription();

	public abstract void setName(String name);

	public abstract String getName();

	public abstract void setId(int id);

	public abstract int getId();

	public abstract int getState();

	public abstract void setState(int state);

	public abstract void setLogId(int id);

	public abstract int getLogId();

	public abstract void setGrowingSeedId(int id);

	public abstract int getGrowingSeedId();

	public abstract void setData(Object data);

	public abstract Object getData();
}
