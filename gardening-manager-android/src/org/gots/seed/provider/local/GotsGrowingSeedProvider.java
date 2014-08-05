package org.gots.seed.provider.local;

import java.util.ArrayList;
import java.util.List;

import org.gots.bean.BaseAllotmentInterface;
import org.gots.seed.GrowingSeedInterface;

public interface GotsGrowingSeedProvider {

    public abstract GrowingSeedInterface plantingSeed(GrowingSeedInterface seed, BaseAllotmentInterface allotment);

    public abstract ArrayList<GrowingSeedInterface> getGrowingSeeds();

    public abstract List<GrowingSeedInterface> getGrowingSeedsByAllotment(BaseAllotmentInterface allotment, boolean force);

    public abstract GrowingSeedInterface getGrowingSeedById(int growingSeedId);

    public abstract void deleteGrowingSeed(GrowingSeedInterface seed);

}