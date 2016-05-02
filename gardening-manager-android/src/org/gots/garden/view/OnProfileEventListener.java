package org.gots.garden.view;

import org.gots.garden.GardenInterface;
import org.gots.ui.fragment.BaseGotsFragment;

public interface OnProfileEventListener {
    public void onProfileSelected(GardenInterface garden);

    public void onProfileEdited(BaseGotsFragment fragment, GardenInterface garden);

    public void onProfileCreated(GardenInterface garden);

}
