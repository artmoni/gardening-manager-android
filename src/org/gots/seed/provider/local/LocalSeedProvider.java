package org.gots.seed.provider.local;

import java.util.List;

import org.gots.garden.GardenInterface;
import org.gots.provider.AbstractProvider;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.provider.GotsSeedProvider;
import org.gots.seed.provider.local.sql.VendorSeedDBHelper;

import android.content.Context;

public class LocalSeedProvider extends AbstractProvider implements GotsSeedProvider {

    VendorSeedDBHelper myBank;

    public LocalSeedProvider(Context context) {
        super(context);
        myBank = new VendorSeedDBHelper(mContext);
    }

    @Override
    public void getAllFamilies() {
        // TODO
    }

    @Override
    public void getFamilyById(int id) {
        // TODO
    }

    @Override
    public BaseSeedInterface getSeedById() {
        // TODO
        return null;
    }

    @Override
    public List<BaseSeedInterface> getVendorSeeds() {
        return myBank.getVendorSeeds();
    }

    @Override
    public BaseSeedInterface createSeed(BaseSeedInterface seed) {
        return myBank.insertSeed(seed);
    }

    @Override
    public BaseSeedInterface updateSeed(BaseSeedInterface newSeed) {
        myBank.updateSeed(newSeed);
        return newSeed;
    }

    @Override
    public void addToStock(BaseSeedInterface vendorSeed, GardenInterface garden) {
        vendorSeed.setNbSachet(vendorSeed.getNbSachet() + 1);
        myBank.updateSeed(vendorSeed);
    }

    @Override
    public void removeToStock(BaseSeedInterface vendorSeed) {
        vendorSeed.setNbSachet(vendorSeed.getNbSachet() - 1);
        myBank.updateSeed(vendorSeed);

    }

    @Override
    public List<BaseSeedInterface> getMyStock(GardenInterface garden) {
        return myBank.getMySeeds();
    }

    @Override 
    public void remove(BaseSeedInterface vendorSeed) {
        myBank.remove(vendorSeed);
        
    }
}
