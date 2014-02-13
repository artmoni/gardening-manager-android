package org.gots.seed.provider.parrot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.gots.R.string;
import org.gots.authentication.ParrotAuthentication;
import org.gots.garden.GardenManager;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.provider.local.LocalSeedProvider;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

public class ParrotSeedProvider extends LocalSeedProvider {

    private ParrotAuthentication authentication;

    private String TAG = "ParrotSeedProvider";

    private String filterCriteria = "";

    public ParrotSeedProvider(Context context) {
        super(context);
        authentication = new ParrotAuthentication();
        authentication.getToken();
    }

    public void setSearchCriteria(String filterCriteria) {
        this.filterCriteria = filterCriteria;
    }

    public List<String> getVendorSeedsByName(String searchCriteria) {
        String api_4_02_search = "/search/v5/plants/" + searchCriteria + "?generate_index=ASC";
        List<String> listId = new ArrayList<String>();
        try {
            JSONObject json = (JSONObject) authentication.getJSON(api_4_02_search);
            JSONArray found = json.getJSONArray("found");
            for (int i = 0; i < found.length() && i <= 9; i++) {
                String plantId = found.getString(i);
                listId.add(plantId);
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return listId;
    }

    @Override
    public List<BaseSeedInterface> getVendorSeeds(boolean force) {
        List<BaseSeedInterface> parrotPlants = new ArrayList<BaseSeedInterface>();
        List<String> plantsId = getVendorSeedsByName(filterCriteria);
        try {

            StringBuilder builder = new StringBuilder();
            for (String plantId : plantsId) {
                builder.append(plantId);
                builder.append(",");
            }
            String api_5_06_plants = "/plant_library/v1/plants/" + builder.toString();
            JSONObject json_plants = (JSONObject) authentication.getJSON(api_5_06_plants);
            JSONArray plants = json_plants.getJSONArray("plants");
            for (int i = 0; i < plants.length(); i++) {
                JSONObject plant = plants.getJSONObject(i);
                ParrotSeedConverter converter = new ParrotSeedConverter(mContext);
                BaseSeedInterface seed = converter.convert(plant);
                parrotPlants.add(seed);
            }

        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return synchronize(super.getVendorSeeds(false), parrotPlants);
    }

    protected List<BaseSeedInterface> synchronize(List<BaseSeedInterface> localVendorSeeds,
            List<BaseSeedInterface> remoteVendorSeeds) {
        newSeeds.clear();
        List<BaseSeedInterface> myVendorSeeds = new ArrayList<BaseSeedInterface>();

        for (BaseSeedInterface remoteSeed : remoteVendorSeeds) {
            boolean found = false;
            for (BaseSeedInterface localSeed : localVendorSeeds) {
                if (remoteSeed.getUUID() != null && remoteSeed.getUUID().equals(localSeed.getUUID())) {
                    found = true;
//                    myVendorSeeds.add(localSeed);

                    break;
                }
            }
            if (found)
                // myVendorSeeds.add(super.updateSeed(remoteSeed));
                // myVendorSeeds.add();
                ;
            else {
                remoteSeed = super.createSeed(remoteSeed);
                newSeeds.add(remoteSeed);
            }
            myVendorSeeds.add(remoteSeed);
        }

        // for (BaseSeedInterface localSeed : localVendorSeeds) {
        //
        // if (localSeed.getUUID() == null) {
        // myVendorSeeds.add(localSeed);
        // } else {
        // boolean found = false;
        // for (BaseSeedInterface remoteSeed : remoteVendorSeeds) {
        // if (remoteSeed.getUUID() != null && remoteSeed.getUUID().equals(localSeed.getUUID())) {
        // found = true;
        // break;
        // }
        // }
        // if (!found) { // local only with UUID -> delete local
        // // TODO take a decision if local seed should be remove if the remote description is removed.
        // // super.remove(localSeed);
        // }
        // }
        //
        // }

        return myVendorSeeds;
    }

}
