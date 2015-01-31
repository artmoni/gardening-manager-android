package org.gots.action.provider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.gots.action.ActionOnSeed;
import org.gots.exception.GotsServerRestrictedException;
import org.gots.seed.GrowingSeed;

public interface GotsActionSeedProvider {

    public abstract ActionOnSeed doAction(ActionOnSeed action, GrowingSeed seed);

    public abstract ArrayList<ActionOnSeed> getActionsToDo();

    public abstract List<ActionOnSeed> getActionsToDoBySeed(GrowingSeed seed, boolean force);

    public abstract List<ActionOnSeed> getActionsDoneBySeed(GrowingSeed seed, boolean force);

    public abstract ActionOnSeed insertAction(GrowingSeed seed, ActionOnSeed action);

    public abstract File uploadPicture(GrowingSeed seed, File imageFile);

    public abstract File downloadHistory(GrowingSeed mSeed) throws GotsServerRestrictedException;

    public abstract List<File> getPicture(GrowingSeed mSeed) throws GotsServerRestrictedException;

}
