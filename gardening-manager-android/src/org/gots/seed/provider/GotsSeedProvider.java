package org.gots.seed.provider;

import java.util.List;

import org.gots.exception.GotsException;
import org.gots.garden.GardenInterface;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.LikeStatus;

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

    public abstract BaseSeedInterface getSeedByBarCode(String barecode);

    public List<BaseSeedInterface> getVendorSeedsByName(String currentFilter);

    public abstract LikeStatus like(BaseSeedInterface mSeed, boolean cancelLike) throws GotsException;

    public abstract List<BaseSeedInterface> getMyFavorites();

    public abstract List<BaseSeedInterface> getSeedBySowingMonth(int month);
}
