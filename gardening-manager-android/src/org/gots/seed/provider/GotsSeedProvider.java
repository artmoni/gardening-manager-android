package org.gots.seed.provider;

import java.io.File;
import java.util.List;

import org.gots.exception.GotsException;
import org.gots.exception.NotImplementedException;
import org.gots.garden.GardenInterface;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.LikeStatus;
import org.gots.seed.SpeciesDocument;

public interface GotsSeedProvider {

    public List<BaseSeedInterface> getVendorSeeds(boolean force, int page, int pageSize);

    public BaseSeedInterface getSeedById(int seedId);

    public BaseSeedInterface createSeed(BaseSeedInterface newSeed, File imageFile);

    public BaseSeedInterface updateSeed(BaseSeedInterface currentSeed);

    public void deleteSeed(BaseSeedInterface currentSeed);

    public BaseSeedInterface addToStock(BaseSeedInterface vendorSeed, GardenInterface garden);

    public BaseSeedInterface removeToStock(BaseSeedInterface vendorSeed, GardenInterface garden);

    public List<BaseSeedInterface> getMyStock(GardenInterface garden, boolean force);

    public abstract List<BaseSeedInterface> getNewSeeds();

    public void getAllFamilies();

    public void getFamilyById(int id);


    public abstract BaseSeedInterface getSeedByBarCode(String barecode);

    public List<BaseSeedInterface> getVendorSeedsByName(String currentFilter, boolean force);

    public abstract LikeStatus like(BaseSeedInterface mSeed, boolean cancelLike) throws GotsException;

    public abstract List<BaseSeedInterface> getMyFavorites();

    public abstract List<BaseSeedInterface> getSeedBySowingMonth(int month);

    public abstract String[] getArraySpecies(boolean force);

    public abstract  String getFamilyBySpecie(String specie);
    
    public abstract SpeciesDocument getSpecies(boolean force) throws NotImplementedException;

    public abstract BaseSeedInterface getSeedByUUID(String uuid);

    List<BaseSeedInterface> getRecognitionSeeds(boolean force);
}
