package org.gots.seed;

import java.util.ArrayList;
import java.util.List;

import org.gots.seed.providers.GotsSeedProvider;
import org.gots.seed.providers.local.LocalSeedProvider;
import org.gots.seed.providers.simple.SimpleSeedProvider;

import android.content.Context;

public class GotsSeedManager implements GotsSeedProvider {

	private Context mContext;
	private GotsSeedProvider mSeedProvider;

	public GotsSeedManager(Context mContext) {
		this.mContext = mContext;
	}

	@Override
	public List<BaseSeedInterface> getAllSeeds() {
		List<BaseSeedInterface> listSeeds;
		
		mSeedProvider = new LocalSeedProvider(mContext);
		listSeeds = mSeedProvider.getAllSeeds();
		
		if (listSeeds.size() < 1) {
			mSeedProvider = new SimpleSeedProvider();
			listSeeds = mSeedProvider.getAllSeeds();
		}
		return listSeeds;
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

}
