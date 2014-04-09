package org.gots.action.provider;

import java.util.ArrayList;

import org.gots.action.BaseActionInterface;

public interface GotsActionProvider {

    public abstract BaseActionInterface getActionById(int id);

    public abstract BaseActionInterface getActionByName(String name);

    public abstract ArrayList<BaseActionInterface> getActions();

    public abstract BaseActionInterface createAction(BaseActionInterface action);

    public abstract BaseActionInterface updateAction(BaseActionInterface action);

}
