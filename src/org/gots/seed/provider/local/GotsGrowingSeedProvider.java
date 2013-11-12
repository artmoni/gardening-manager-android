package org.gots.seed.provider.local;

import java.util.ArrayList;

import org.gots.bean.BaseAllotmentInterface;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.GrowingSeedInterface;

public interface GotsGrowingSeedProvider {

    public abstract GrowingSeedInterface insertSeed(GrowingSeedInterface seed, BaseAllotmentInterface allotment);

    public abstract ArrayList<GrowingSeedInterface> getGrowingSeeds();

    public abstract ArrayList<GrowingSeedInterface> getSeedsByAllotment(BaseAllotmentInterface allotment);

    public abstract GrowingSeedInterface getSeedById(int growingSeedId);

    public abstract void deleteGrowingSeed(GrowingSeedInterface seed);

}