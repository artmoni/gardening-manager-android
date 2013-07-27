package org.gots.seed.provider.simple;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.gots.garden.GardenInterface;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.provider.GotsSeedProvider;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import android.os.AsyncTask;
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
    public List<BaseSeedInterface> getVendorSeeds() {
        List<BaseSeedInterface> allSeeds = new ArrayList<BaseSeedInterface>();

        try {
            allSeeds = new AsyncTask<Object, Integer, List<BaseSeedInterface>>() {

                @Override
                protected List<BaseSeedInterface> doInBackground(Object... params) {
                    List<BaseSeedInterface> vendorSeeds = new ArrayList<BaseSeedInterface>();

                    try {

                        // SimpleNetwork network = new SimpleNetwork();
                        // InputStream is = network.execute("").get();

                        CredentialsProvider credProvider = new BasicCredentialsProvider();
                        credProvider.setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT),
                                new UsernamePasswordCredentials("PY0XHE11WE4VQNJ18DXUQFZ7OJR5YVBR", ""));

                        DefaultHttpClient httpClient = new DefaultHttpClient();
                        httpClient.setCredentialsProvider(credProvider);

                        HttpResponse response;
                        InputStream is = null;
                        try {

                            HttpGet httpGet = new HttpGet(urlFormatter());

                            response = httpClient.execute(httpGet);

                            StatusLine statusLine = response.getStatusLine();
                            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                                is = response.getEntity().getContent();

                            } else {
                                // Closes the connection.
                                response.getEntity().getContent().close();
                                throw new IOException(statusLine.getReasonPhrase());
                            }
                        } catch (ClientProtocolException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        // ********
                        Serializer serializer = new Persister();
                        SimpleSeeds seeds = serializer.read(SimpleSeeds.class, is);

                        for (Iterator<SimpleSeedInterface> iterator = seeds.getSeeds().iterator(); iterator.hasNext();) {
                            BaseSeedInterface seed = iterator.next();
                            vendorSeeds.add(seed);
                        }
                        Log.i("SimpleSeedProvider", vendorSeeds.size() + " seeds have been parsed");
                    } catch (Exception e) {
                        Log.e("getAllSeeds", e.getMessage());
                    }
                    return vendorSeeds;
                }
            }.execute(new Object()).get();

        } catch (Exception e) {
            e.printStackTrace();
            Log.w("SimpleSeedProvider", "Error loading seeds from XML");

        }
        return allSeeds;
    }

    private String HOST = "services.gardening-manager.com";

    private String PATH = "/seeds/";

    private String URL = "http://" + HOST + PATH;

    private String urlFormatter() {
        String lang = Locale.getDefault().getLanguage();
        String filename = "seed";
        String fileextension = ".xml";
        if ("fr".equals(lang))
            filename += "-" + lang + fileextension;
        else
            filename += fileextension;

        return URL + filename;
    }

    @Override
    public BaseSeedInterface createSeed(BaseSeedInterface seed) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BaseSeedInterface updateSeed(BaseSeedInterface newSeed) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addToStock(BaseSeedInterface vendorSeed, GardenInterface garden) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeToStock(BaseSeedInterface vendorSeed) {
        // TODO Auto-generated method stub

    }@Override
    public List<BaseSeedInterface> getMyStock(GardenInterface garden) {
        // TODO Auto-generated method stub
        return null;
    }
}
