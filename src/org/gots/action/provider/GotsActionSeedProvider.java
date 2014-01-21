package org.gots.action.provider;

import java.util.ArrayList;

import org.gots.action.BaseActionInterface;
import org.gots.action.SeedActionInterface;
import org.gots.seed.GrowingSeedInterface;

public interface GotsActionSeedProvider {

    public abstract long doAction(SeedActionInterface action, GrowingSeedInterface seed);

    public abstract ArrayList<SeedActionInterface> getActionsToDo();

    public abstract ArrayList<SeedActionInterface> getActionsToDoBySeed(GrowingSeedInterface seed);

    public abstract ArrayList<SeedActionInterface> getActionsDoneBySeed(GrowingSeedInterface seed);

    public abstract SeedActionInterface insertAction(BaseActionInterface action, GrowingSeedInterface seed);

   

}
