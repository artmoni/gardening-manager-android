package org.gots.action;

import java.util.ArrayList;

import org.gots.action.provider.GotsActionProvider;
import org.gots.action.provider.local.LocalActionProvider;
import org.gots.provider.AbstractProvider;

import android.content.Context;

public class GotsActionManager extends AbstractProvider implements GotsActionProvider {

    GotsActionProvider provider;

    public GotsActionManager(Context context) {
        super(context);
        provider = new LocalActionProvider(context);
    }

    @Override
    public BaseActionInterface getActionById(int id) {
        return provider.getActionById(id);
    }

    @Override
    public BaseActionInterface getActionByName(String name) {
        return provider.getActionByName(name);
    }

    @Override
    public ArrayList<BaseActionInterface> getActions() {
        return provider.getActions();
    }

    @Override
    public long insertAction(BaseActionInterface action) {
        return provider.insertAction(action);
    }

}
