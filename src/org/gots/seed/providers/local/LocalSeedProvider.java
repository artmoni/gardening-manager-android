package org.gots.seed.providers.local;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gots.R;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.providers.GotsSeedProvider;
import org.gots.seed.sql.VendorSeedDBHelper;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.content.Context;
import android.util.Log;

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
