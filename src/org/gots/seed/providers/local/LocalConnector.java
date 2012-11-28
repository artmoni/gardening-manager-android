package org.gots.seed.providers.local;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gots.R;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.providers.GotsConnector;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.content.Context;
import android.util.Log;

public class LocalConnector implements GotsConnector {

	private Context mContext;

	public LocalConnector(Context context) {
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
		List<BaseSeedInterface> allSeeds = new ArrayList<BaseSeedInterface>();

		try {

			
			InputStream is = mContext.getResources().openRawResource(R.raw.vendor_seeds);
			
			Serializer serializer = new Persister();
			LocalSeeds seeds = serializer.read(LocalSeeds.class, is);

			for (Iterator iterator = seeds.getSeeds().iterator(); iterator.hasNext();) {
				BaseSeedInterface seed = (BaseSeedInterface) iterator.next();
				Log.i("getFamily", "" + seed);
				allSeeds.add(seed);
			}
			// allSeeds = seeds.getSeeds();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return allSeeds;
	}

}
