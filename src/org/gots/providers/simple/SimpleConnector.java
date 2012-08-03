package org.gots.providers.simple;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.gots.providers.GotsConnector;
import org.gots.seed.BaseSeedInterface;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.util.Log;

public class SimpleConnector implements GotsConnector {

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
			SimpleSeeds seeds= serializer.read(SimpleSeeds.class, is);
			
			for (Iterator iterator = seeds.getSeeds().iterator(); iterator.hasNext();) {
				BaseSeedInterface seed = (BaseSeedInterface) iterator.next();
				Log.i("getFamily", "" + seed);	
				allSeeds.add(seed);
			}
//			allSeeds = seeds.getSeeds();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return allSeeds;
	}

}
