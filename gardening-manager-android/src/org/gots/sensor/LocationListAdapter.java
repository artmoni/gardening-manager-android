package org.gots.sensor;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.gots.R;
import org.gots.sensor.parrot.ParrotLocation;
import org.gots.utils.FileUtilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

public class LocationListAdapter extends BaseAdapter {

    private Context mContext;

    private List<ParrotLocation> mLocation;

    public LocationListAdapter(Context context, List<ParrotLocation> sensors) {
        mContext = context;
        mLocation = sensors;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = convertView;
        SensorHolder h = null;
        LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (v == null) {
            v = vi.inflate(R.layout.sensor_location, null);
            h = new SensorHolder();
            h.sensorImg = (ImageView) v.findViewById(R.id.sensorImage);
            h.sensorName = (TextView) v.findViewById(R.id.sensorLocationName);
            h.sensorPlantAssignedDate = (TextView) v.findViewById(R.id.sensorLocationPlantAssignedDate);
            // h.sensorDescription = (TextView) v.findViewById(R.id.sensorLocationDescription);

            v.setTag(h);
        } else {
            h = (SensorHolder) v.getTag();
        }
        // h.sensorImg.setImageDrawable(mContext.getResources().getDrawable(getItem(position).getResourceDrawable()));

        new AsyncTask<Object, Void, File>() {
            ImageView imageView;

            @Override
            protected File doInBackground(Object... params) {
                if (params[0] instanceof ImageView)
                    imageView = (ImageView) params[0];
                if (getItem(position).getAvatar_url() != null)
                    return downloadBitmap(getItem(position).getAvatar_url(), getItem(position).getLocation_identifier());
                else
                    return null;
            }

            @Override
            protected void onPostExecute(File result) {
                if (imageView != null && result != null) {
                    Bitmap scaleBitmap = FileUtilities.decodeScaledBitmapFromSdCard(result.getAbsolutePath(),
                            imageView.getWidth(), imageView.getHeight());
                    imageView.setImageBitmap(scaleBitmap);
                }
                super.onPostExecute(result);
            }
        }.execute(h.sensorImg);

        File locationImageFile = new File(mContext.getCacheDir() + "/" + getItem(position).getLocation_identifier());
        if (locationImageFile.exists()) {
            Bitmap scaleBitmap = FileUtilities.decodeScaledBitmapFromSdCard(locationImageFile.getAbsolutePath(),
                    h.sensorImg.getDrawable().getIntrinsicWidth(), h.sensorImg.getDrawable().getIntrinsicHeight());
            h.sensorImg.setImageBitmap(scaleBitmap);
        }

        if (getItem(position).getLocation_name() != null)
            h.sensorName.setText("" + getItem(position).getLocation_name());
        else
            h.sensorName.setText("" + getItem(position).getPlant_nickname());

        if (getItem(position).getPlant_assigned_date() != null)
            h.sensorPlantAssignedDate.setText(DateFormat.format("yyyy-MM-dd",
                    getItem(position).getPlant_assigned_date()));
        else
            h.sensorPlantAssignedDate.setText("");
        // h.sensorDescription.setText("" + getItem(position).getDescription());
        return v;
    }

    @Override
    public int getCount() {
        return mLocation.size();
    }

    @Override
    public ParrotLocation getItem(int position) {
        return mLocation.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private File downloadBitmap(String url, String bitmapFilename) {
        // initilize the default HTTP client object
        final DefaultHttpClient client = new DefaultHttpClient();
        final File imageFile = new File(mContext.getCacheDir() + "/" + bitmapFilename);

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

    class SensorHolder {
        ImageView sensorImg;

        TextView sensorName;

        TextView sensorPlantAssignedDate;

        // TextView sensorDescription;

    }
}