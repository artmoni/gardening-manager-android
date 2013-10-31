package org.gots.action.provider;

import java.util.ArrayList;

import org.gots.action.BaseActionInterface;
import org.gots.seed.GrowingSeedInterface;

public interface GotsActionSeedProvider {

    public abstract long doAction(BaseActionInterface action, GrowingSeedInterface seed);

    public abstract ArrayList<BaseActionInterface> getActionsToDo();

    public abstract ArrayList<BaseActionInterface> getActionsToDoBySeed(GrowingSeedInterface seed);

    public abstract ArrayList<BaseActionInterface> getActionsDoneBySeed(GrowingSeedInterface seed);

    public abstract long insertAction(BaseActionInterface action, GrowingSeedInterface seed);

   

}
