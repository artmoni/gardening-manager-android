package org.gots.seed.provider;

import org.gots.exception.GotsException;
import org.gots.garden.GardenInterface;
import org.gots.nuxeo.NuxeoUtils;
import org.gots.seed.BaseSeed;
import org.gots.seed.BotanicFamily;
import org.gots.seed.BotanicSpecie;
import org.gots.seed.LikeStatus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public interface GotsSeedProvider {

    public List<BaseSeed> getVendorSeeds(boolean force, int page, int pageSize);

    public BaseSeed getSeedById(int seedId);

    public BaseSeed createSeed(BaseSeed newSeed, File imageFile);

    public BaseSeed updateSeed(BaseSeed currentSeed);

    public void deleteSeed(BaseSeed currentSeed);

    public BaseSeed addToStock(BaseSeed vendorSeed, GardenInterface garden);

    public BaseSeed removeToStock(BaseSeed vendorSeed, GardenInterface garden);

    public List<BaseSeed> getMyStock(GardenInterface garden, boolean force);

    public abstract List<BaseSeed> getNewSeeds();

    public List<BotanicFamily> getAllFamilies();

    public void getFamilyById(int id);


    public abstract ArrayList<BaseSeed> getSeedByBarCode(String barecode);

    public List<BaseSeed> getVendorSeedsByName(String currentFilter, boolean force);

    public abstract LikeStatus like(BaseSeed mSeed, boolean cancelLike) throws GotsException;

    public abstract List<BaseSeed> getMyFavorites();

    public abstract List<BaseSeed> getSeedBySowingMonth(int month);

//    public abstract String[] getArraySpecies(boolean force);

    public abstract String getFamilyBySpecie(String specie);

    public abstract List<BotanicSpecie> getSpecies(boolean force);

    public abstract BaseSeed getSeedByUUID(String uuid);

    List<BaseSeed> getRecognitionSeeds(boolean force);

    public void createRecognitionSeed(File file, NuxeoUtils.OnBlobUpload callback);
}
