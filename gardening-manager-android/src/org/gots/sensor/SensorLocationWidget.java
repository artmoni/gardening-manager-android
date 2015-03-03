package org.gots.sensor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.gots.R;
import org.gots.sensor.parrot.ParrotLocation;
import org.gots.utils.FileUtilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SensorLocationWidget extends RelativeLayout {

    ParrotLocation sensorLocation;

    private ImageView sensorImg;

    private TextView sensorName;

    private String TAG = SensorLocationWidget.class.getSimpleName();

    private LinearLayout sensorLayoutStatus;

    public SensorLocationWidget(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.sensor_location, this);
    }

    private void setupView() {
        sensorImg = (ImageView) findViewById(R.id.sensorImage);
        sensorName = (TextView) findViewById(R.id.sensorLocationName);
        sensorLayoutStatus = (LinearLayout) findViewById(R.id.sensorLayoutStatus);

        if (sensorLocation == null) {
            Log.w(TAG, "Sensor can not be displayed");
            return;
        }

        sensorName.setText(sensorLocation.getPlant_nickname());

        new AsyncTask<Object, Void, File>() {

            @Override
            protected File doInBackground(Object... params) {
                if (sensorLocation.getAvatar_url() != null)
                    return downloadBitmap(sensorLocation.getAvatar_url(), sensorLocation.getLocation_identifier());
                else
                    return null;
            }

            @Override
            protected void onPostExecute(File result) {
                if (sensorImg != null && result != null) {
                    Bitmap scaleBitmap = FileUtilities.decodeScaledBitmapFromSdCard(result.getAbsolutePath(),
                            sensorImg.getWidth(), sensorImg.getHeight());
                    sensorImg.setImageBitmap(scaleBitmap);
                }
                super.onPostExecute(result);
            }
        }.execute();
    }

    public void setSensor(ParrotLocation location, String moisture_status_key, String fertilizer_status_key,
            String airtemperature_status_key, Object light_status_key) {
        this.sensorLocation = location;
        setupView();
        if ("status_warning".equals(moisture_status_key) || "status_warning".equals(fertilizer_status_key)
                || "status_warning".equals(airtemperature_status_key) || "status_warning".equals(light_status_key)) {
            sensorLayoutStatus.setBackgroundColor(getContext().getResources().getColor(R.color.action_warning_color));
        } else if ("status_critical".equals(moisture_status_key) || "status_critical".equals(fertilizer_status_key)
                || "status_critical".equals(airtemperature_status_key) || "status_critical".equals(light_status_key)) {
            sensorLayoutStatus.setBackgroundColor(getContext().getResources().getColor(R.color.action_error_color));

        }
        Log.d(TAG, moisture_status_key + " " + fertilizer_status_key + " " + airtemperature_status_key + " "
                + light_status_key);
    }

    private File downloadBitmap(String url, String bitmapFilename) {
        // initilize the default HTTP client object
        final DefaultHttpClient client = new DefaultHttpClient();
        final File imageFile = new File(getContext().getCacheDir() + "/" + bitmapFilename);

        // forming a HttoGet request
        final HttpGet getRequest = new HttpGet(url);
        try {

            HttpResponse response = client.execute(getRequest);

            // check 200 OK for success
            final int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode != HttpStatus.SC_OK) {
                Log.w("ImageDownloader", "Error " + statusCode + " while retrieving bitmap from " + url);
                return null;

            }

            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = null;
                try {
                    // getting contents from the stream
                    inputStream = entity.getContent();

                    // decoding stream data back into image Bitmap that android understands
                    final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    FileOutputStream out = new FileOutputStream(imageFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    out.flush();
                    out.close();
                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    entity.consumeContent();
                }
            }
        } catch (Exception e) {
            // You Could provide a more explicit error message for IOException
            getRequest.abort();
            Log.e("ImageDownloader", "Something went wrong while" + " retrieving bitmap from " + url + e.toString());
        }

        return imageFile;
    }
}
