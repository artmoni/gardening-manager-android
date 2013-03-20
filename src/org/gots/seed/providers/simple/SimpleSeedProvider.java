package org.gots.seed.providers.simple;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gots.seed.BaseSeedInterface;
import org.gots.seed.providers.GotsSeedProvider;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.util.Log;

public class SimpleSeedProvider implements GotsSeedProvider {

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

			SimpleNetwork network = new SimpleNetwork();
			InputStream is = network.execute("").get();
			
			Serializer serializer = new Persister();
			SimpleSeeds seeds = serializer.read(SimpleSeeds.class, is);

			for (Iterator iterator = seeds.getSeeds().iterator(); iterator.hasNext();) {
				BaseSeedInterface seed = (BaseSeedInterface) iterator.next();
				allSeeds.add(seed);
			}
			Log.i("SimpleSeedProvider", allSeeds.size()+" seeds have been parsed");
		} catch (Exception e) {
			e.printStackTrace();
			Log.w("SimpleSeedProvider", "Error loading seeds from XML");

		}
		return allSeeds;
	}

}
