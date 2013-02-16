package org.gots.seed.providers.prestashop;

import java.io.InputStream;
import java.util.List;

import org.gots.seed.BaseSeedInterface;
import org.gots.seed.providers.GotsConnector;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.util.Log;

public class PrestashopConnector implements GotsConnector{

	private PrestashopNetwork network = new PrestashopNetwork();
	
	public PrestashopConnector() {
	
		
	}
	
	@Override
	public void getAllFamilies() {
		InputStream result = network.callWebService2("categories");
		Serializer serializer = new Persister();
//		File source = new File("example.xml");

		try {
			PrestashopCategories categories = serializer.read(PrestashopCategories.class, result);
			for (int i = 0; i < categories.getRefCategories().size(); i++) {
				Log.i("getAllFamilies", ""+categories.getRefCategories().get(i).getCategoryId());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void getFamilyById(int id) {
		InputStream result = network.callWebService2("categories/"+id);
		Serializer serializer = new Persister();
//		File source = new File("example.xml");

		try {
			PrestashopCategory category = serializer.read(PrestashopCategory.class, result);
//			for (int i = 0; i < category.getRefCategories().size(); i++) {
//				Log.i("getAllFamilies", ""+category.getRefCategories().get(i).getCategoryId());
//			}
			Log.i("getFamilyById", ""+category.getName());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public BaseSeedInterface getSeedById() {
		return null;
	}

	@Override
	public List<BaseSeedInterface> getAllSeeds() {
		// TODO Auto-generated method stub
		return null;
	}

	 
}
