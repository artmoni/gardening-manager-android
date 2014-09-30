package org.gots.action.provider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.gots.action.BaseActionInterface;
import org.gots.action.SeedActionInterface;
import org.gots.exception.GotsServerRestrictedException;
import org.gots.seed.GrowingSeedInterface;

public interface GotsActionSeedProvider {

    public abstract long doAction(SeedActionInterface action, GrowingSeedInterface seed);

    public abstract ArrayList<SeedActionInterface> getActionsToDo();

    public abstract List<SeedActionInterface> getActionsToDoBySeed(GrowingSeedInterface seed, boolean force);

    public abstract List<SeedActionInterface> getActionsDoneBySeed(GrowingSeedInterface seed, boolean force);

    public abstract SeedActionInterface insertAction(GrowingSeedInterface seed, BaseActionInterface action);

    public abstract File uploadPicture(GrowingSeedInterface seed, File imageFile);

    public abstract File downloadHistory(GrowingSeedInterface mSeed) throws GotsServerRestrictedException;

    public abstract List<File> getPicture(GrowingSeedInterface mSeed) throws GotsServerRestrictedException;

}
