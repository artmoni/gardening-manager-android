package org.gots.action.provider;

import org.gots.action.BaseAction;

import java.util.ArrayList;

public interface GotsActionProvider {

    public abstract BaseAction getActionById(int id);

    public abstract BaseAction getActionByName(String name);

    public abstract ArrayList<BaseAction> getActions(boolean force);

    public abstract BaseAction createAction(BaseAction action);

    public abstract BaseAction updateAction(BaseAction action);

}
