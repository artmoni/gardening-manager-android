package org.gots.seed.providers.local;

import java.util.List;

import org.gots.provider.AbstractProvider;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.providers.GotsSeedProvider;
import org.gots.seed.providers.local.sql.VendorSeedDBHelper;

import android.content.Context;

public class LocalSeedProvider extends AbstractProvider implements GotsSeedProvider {

    VendorSeedDBHelper myBank;

    public LocalSeedProvider(Context context) {
        super(context);
    }

    @Override
    public void getAllFamilies() {
        // TODO Auto-generated method stub

    }

    @Override
    public void getFamilyById(int id) {
        // TODO Auto-generated method stub

    }

    @Override
    public BaseSeedInterface getSeedById() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<BaseSeedInterface> getVendorSeeds() {
        myBank = new VendorSeedDBHelper(mContext);

        return myBank.getVendorSeeds();
    }

    @Override
    public BaseSeedInterface createSeed(BaseSeedInterface seed) {
        myBank = new VendorSeedDBHelper(mContext);

        seed.setId(Long.valueOf(myBank.insertSeed(seed)).intValue());
        return seed;
    }

    @Override
    public BaseSeedInterface updateSeed(BaseSeedInterface newSeed) {
        myBank = new VendorSeedDBHelper(mContext);
        myBank.updateSeed(newSeed);
        return newSeed;
    }

}
