package org.gots.seed.provider;

import java.util.List;

import org.gots.garden.GardenInterface;
import org.gots.seed.BaseSeedInterface;

public interface GotsSeedProvider {

    public List<BaseSeedInterface> getVendorSeeds(boolean force);

    public void getAllFamilies();

    public void getFamilyById(int id);

    public BaseSeedInterface getSeedById();

    public BaseSeedInterface createSeed(BaseSeedInterface seed);

    public BaseSeedInterface updateSeed(BaseSeedInterface newSeed);

    public void addToStock(BaseSeedInterface vendorSeed, GardenInterface garden);

    public void removeToStock(BaseSeedInterface vendorSeed);
    
    public List<BaseSeedInterface> getMyStock(GardenInterface garden);
    
    public void remove(BaseSeedInterface vendorSeed);

    public abstract List<BaseSeedInterface> getNewSeeds();

}
