package org.gots.seed;

import java.util.List;

import org.gots.seed.providers.GotsSeedProvider;
import org.gots.seed.providers.local.LocalSeedProvider;
import org.gots.seed.providers.nuxeo.NuxeoSeedProvider;

import android.content.Context;

public class GotsSeedManager implements GotsSeedProvider {

	private Context mContext;
	private GotsSeedProvider mLocalProvider;
	private GotsSeedProvider mRemoteProvider;

	public GotsSeedManager(Context mContext) {
		this.mContext = mContext;
		mLocalProvider = new LocalSeedProvider(mContext);
		mRemoteProvider = new NuxeoSeedProvider(mContext);
	}

	@Override
	public List<BaseSeedInterface> getAllSeeds() {
		List<BaseSeedInterface> listSeeds = mLocalProvider.getAllSeeds();
//
//		// mSeedProvider = new LocalSeedProvider(mContext);
//		// mSeedProvider = new SimpleSeedProvider();
//
//		// listSeeds = mSeedProvider.getAllSeeds();
//
//		if (listSeeds.size() < 1 || true) {
//			VendorSeedDBHelper helper = new VendorSeedDBHelper(mContext);
//
//			for (Iterator<BaseSeedInterface> iterator = mRemoteProvider.getAllSeeds().iterator(); iterator.hasNext();) {
//				BaseSeedInterface baseSeedInterface = iterator.next();
//				if (helper.getSeedByReference(baseSeedInterface.getReference()) != null) {
//					helper.updateSeed(baseSeedInterface);
//				} else {
//					helper.insertSeed(baseSeedInterface);
//				}
//			}
//			// listSeeds = mSeedProvider.getAllSeeds();
//			listSeeds = mLocalProvider.getAllSeeds();
//		}
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
