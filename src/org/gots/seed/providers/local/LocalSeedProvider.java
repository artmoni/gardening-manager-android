package org.gots.seed.providers.local;

import java.util.List;

import org.gots.seed.BaseSeedInterface;
import org.gots.seed.providers.GotsSeedProvider;
import org.gots.seed.providers.local.sql.VendorSeedDBHelper;

import android.content.Context;

public class LocalSeedProvider implements GotsSeedProvider {

	private Context mContext;

	public LocalSeedProvider(Context context) {
		this.mContext = context;
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
	public List<BaseSeedInterface> getAllSeeds() {
		VendorSeedDBHelper myBank = new VendorSeedDBHelper(mContext);		
		return myBank.getVendorSeeds();	
	}

}
