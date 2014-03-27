package org.gots.seed.provider.prestashop;

import java.io.InputStream;
import java.util.List;

import org.gots.exception.GotsException;
import org.gots.garden.GardenInterface;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.LikeStatus;
import org.gots.seed.provider.GotsSeedProvider;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.util.Log;

class PrestashopConnector implements GotsSeedProvider {

    private PrestashopNetwork network = new PrestashopNetwork();

    public PrestashopConnector() {

    }

    @Override
    public void getAllFamilies() {
        InputStream result = network.callWebService2("categories");
        Serializer serializer = new Persister();
        // File source = new File("example.xml");

        try {
            PrestashopCategories categories = serializer.read(PrestashopCategories.class, result);
            for (int i = 0; i < categories.getRefCategories().size(); i++) {
                Log.i("getAllFamilies", "" + categories.getRefCategories().get(i).getCategoryId());
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public void getFamilyById(int id) {
        InputStream result = network.callWebService2("categories/" + id);
        Serializer serializer = new Persister();
        // File source = new File("example.xml");

        try {
            PrestashopCategory category = serializer.read(PrestashopCategory.class, result);
            // for (int i = 0; i < category.getRefCategories().size(); i++) {
            // Log.i("getAllFamilies", ""+category.getRefCategories().get(i).getCategoryId());
            // }
            Log.i("getFamilyById", "" + category.getName());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public BaseSeedInterface getSeedById(int id) {
        return null;
    }

    @Override
    public List<BaseSeedInterface> getVendorSeeds(boolean force) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BaseSeedInterface createSeed(BaseSeedInterface seed) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BaseSeedInterface updateSeed(BaseSeedInterface newSeed) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addToStock(BaseSeedInterface vendorSeed, GardenInterface garden) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeToStock(BaseSeedInterface vendorSeed, GardenInterface garden) {
        // TODO Auto-generated method stub

    }

    @Override
    public List<BaseSeedInterface> getMyStock(GardenInterface garden) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void deleteSeed(BaseSeedInterface vendorSeed) {
        // TODO Auto-generated method stub

    }

    public List<BaseSeedInterface> getNewSeeds() {
        return null;
    }

    public void force_refresh(boolean refresh) {
    }

    public synchronized BaseSeedInterface getSeedByBarCode(String barecode) {
        return null;
    }

    @Override
    public LikeStatus like(BaseSeedInterface mSeed, boolean b) throws GotsException {
        return null;
    }
}
