package org.gots.seed.provider;

import java.util.List;

import org.gots.garden.GardenInterface;
import org.gots.seed.BaseSeedInterface;

public interface GotsSeedProvider {

    public List<BaseSeedInterface> getVendorSeeds(boolean force);

    public BaseSeedInterface getSeedById(int seedId);

    public BaseSeedInterface createSeed(BaseSeedInterface newSeed);

    public BaseSeedInterface updateSeed(BaseSeedInterface currentSeed);

    public void deleteSeed(BaseSeedInterface currentSeed);

    public void addToStock(BaseSeedInterface vendorSeed, GardenInterface garden);

    public void removeToStock(BaseSeedInterface vendorSeed, GardenInterface garden);

    public List<BaseSeedInterface> getMyStock(GardenInterface garden);

    public abstract List<BaseSeedInterface> getNewSeeds();

    public void getAllFamilies();

    public void getFamilyById(int id);

    public abstract void force_refresh(boolean refresh);
}
