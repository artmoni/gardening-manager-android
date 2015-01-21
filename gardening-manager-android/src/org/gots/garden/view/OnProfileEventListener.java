package org.gots.garden.view;

import org.gots.garden.GardenInterface;

public interface OnProfileEventListener {
    public void onProfileSelected(GardenInterface garden);

    public void onProfileEdited(GardenInterface garden);
    
    public void onProfileCreated(GardenInterface garden);
    
}
