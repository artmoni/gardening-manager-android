package org.gots.seed.provider.local;

import org.gots.bean.BaseAllotmentInterface;
import org.gots.seed.GrowingSeed;

import java.util.ArrayList;
import java.util.List;

public interface GotsGrowingSeedProvider {

    public abstract GrowingSeed plantingSeed(GrowingSeed seed, BaseAllotmentInterface allotment);

    public abstract ArrayList<GrowingSeed> getGrowingSeeds();

    public abstract List<GrowingSeed> getGrowingSeedsByAllotment(BaseAllotmentInterface allotment, boolean force);

    public abstract GrowingSeed getGrowingSeedById(int growingSeedId);

    public abstract void deleteGrowingSeed(GrowingSeed seed);

}