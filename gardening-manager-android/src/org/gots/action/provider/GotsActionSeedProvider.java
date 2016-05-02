package org.gots.action.provider;

import org.gots.action.ActionOnSeed;
import org.gots.exception.GotsServerRestrictedException;
import org.gots.seed.GrowingSeed;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public interface GotsActionSeedProvider {

    public abstract ActionOnSeed doAction(ActionOnSeed action, GrowingSeed seed);

    public abstract ArrayList<ActionOnSeed> getActionsToDo(boolean force);

    public abstract List<ActionOnSeed> getActionsToDoBySeed(GrowingSeed seed, boolean force);

    public abstract List<ActionOnSeed> getActionsDoneBySeed(GrowingSeed seed, boolean force);

    public abstract ActionOnSeed insertAction(GrowingSeed seed, ActionOnSeed action);

    public abstract File uploadPicture(GrowingSeed seed, File imageFile);

    public abstract File downloadHistory(GrowingSeed mSeed) throws GotsServerRestrictedException;

    public abstract List<File> getPicture(GrowingSeed mSeed) throws GotsServerRestrictedException;

}
