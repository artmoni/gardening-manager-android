package org.gots.seed.provider.parrot;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;

import org.gots.seed.BaseSeedInterface;
import org.gots.seed.GrowingSeed;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

public class ParrotSeedConverter {
    Context mContext;

    public ParrotSeedConverter(Context context) {
        mContext = context;
    }

    public BaseSeedInterface convert(JSONObject plant) {
        BaseSeedInterface seed = new GrowingSeed();
        try {
            seed.setName(plant.getString("preferred_common_name"));
            seed.setDescriptionCultivation(plant.getString("description"));
            seed.setDescriptionGrowth(plant.getString("growth"));
            seed.setDescriptionDiseases(plant.getString("pests"));
            seed.setDescriptionHarvest(plant.getString("harvesting"));
            seed.setSpecie(plant.getString("latin_name"));
            if (plant.getString("subspecies_name") != null && !"null".equals(plant.getString("subspecies_name")))
                seed.setVariety(plant.getString("subspecies_name"));

//            seed.setUUID(plant.getString("id"));
            // JSONArray common_names = (JSONArray)plant.getJSONArray("common_names");
            // for (int i = 0; i < common_names.length(); i++) {
            // JSONObject common_name = common_names.getJSONObject(i);
            // if (common_name.getBoolean("preferred"))
            // seed.setName(common_name.getString("common_name"));
            // }
            JSONArray images = plant.getJSONArray("images");
            if (images != null) {
                String url = ((JSONObject) images.get(0)).getString("image_path");
                downloadImage(seed.getSpecie(), url);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return seed;
    }

    private void downloadImage(final String plantName, final String url) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                File file = new File(mContext.getCacheDir() + "/" + plantName.toLowerCase().replaceAll("\\s", ""));
                if (!file.exists()) {
                    try {
                        URLConnection conn = new URL(url).openConnection();
                        conn.connect();
                        Bitmap image = BitmapFactory.decodeStream(conn.getInputStream());
                        FileOutputStream out = new FileOutputStream(file);
                        image.compress(Bitmap.CompressFormat.JPEG, 90, out);
                        out.flush();
                        out.close();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        }.execute();

    }
}
