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
package org.gots.action;

import java.util.Date;

public interface BaseAction {

    public abstract int getDuration();

    public abstract void setDuration(int duration);

    public abstract String getDescription();

    public abstract void setDescription(String description);

    public abstract String getName();

    public abstract void setName(String name);

    public abstract int getId();

    public abstract void setId(int id);

    public abstract String getUUID();

    public abstract void setUUID(String uuid);

    public abstract Date getDateActionTodo();

    public abstract void setDateActionTodo(Date dateActionTodo);

    public abstract Date getDateActionDone();

    public abstract void setDateActionDone(Date dateActionDone);

    public abstract int getState();

    public abstract void setState(int state);

    public abstract Object getData();

    public abstract void setData(Object data);
}
