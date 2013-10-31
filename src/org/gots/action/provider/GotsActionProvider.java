package org.gots.action.provider;

import java.util.ArrayList;

import org.gots.action.BaseActionInterface;

public interface GotsActionProvider {

    public abstract BaseActionInterface getActionById(int id);

    public abstract BaseActionInterface getActionByName(String name);

    public abstract ArrayList<BaseActionInterface> getActions();

    public abstract long insertAction(BaseActionInterface action);


}
