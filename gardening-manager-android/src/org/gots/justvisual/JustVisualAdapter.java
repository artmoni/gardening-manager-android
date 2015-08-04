package org.gots.justvisual;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.getbase.floatingactionbutton.FloatingActionButton;

import org.gots.R;
import org.gots.ui.WebHelpActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by sfleury on 20/07/15.
 */
public class JustVisualAdapter extends BaseAdapter {

    private static final String TAG = JustVisualAdapter.class.getSimpleName();
    private Map<String, List<JustVisualResult>> visualResults;
    private Context mContext;
    private String[] mKeys;
    private OnAdapterClickListener mCallback;
    private boolean stopProcessing = false;

    public JustVisualAdapter(Context context, Map<String, List<JustVisualResult>> results) {
        visualResults = results;
        this.mContext = context;
        if (visualResults == null)
            visualResults = new HashMap<>();
        mKeys = visualResults.keySet().toArray(new String[visualResults.size()]);
    }

    public void stopProcessing() {
        stopProcessing = true;
    }

    public interface OnAdapterClickListener {
        void onImageClick(Bitmap bitmap);

        void onConfirmeClicked(JustVisualResult result);
    }

    @Override
    public int getCount() {
        return visualResults.size();
    }

    @Override
    public List<JustVisualResult> getItem(int i) {
        return visualResults.get(mKeys[i]);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public class Holder {

        public TextView textViewCommonName;
        public TextView textViewSpecies;
        public LinearLayout layoutResult;
        public FloatingActionButton floatingActionInformation;
        public Button buttonConfirme;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        Holder holder;

        if (view == null) {
            holder = new Holder();
            view = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.list_recognition, viewGroup, false);
            holder.textViewCommonName = (TextView) view.findViewById(R.id.textViewCommonName);
            holder.textViewSpecies = (TextView) view.findViewById(R.id.textViewSpecies);
            holder.layoutResult = (LinearLayout) view.findViewById(R.id.layoutResult);
            holder.floatingActionInformation = (FloatingActionButton) view.findViewById(R.id.buttonInformation);
            holder.floatingActionInformation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent webView = new Intent(mContext, WebHelpActivity.class);
                    if (getItem(i).size() > 0 && getItem(i).get(0).getPageUrl() != null) {
                        webView.putExtra(WebHelpActivity.URL_EXTERNAL, getItem(i).get(0).getPageUrl());
                        mContext.startActivity(webView);
                    } else
                        Toast.makeText(mContext, "No information found", Toast.LENGTH_LONG).show();

                }
            });
            holder.buttonConfirme = (Button) view.findViewById(R.id.buttonConfirme);
            holder.buttonConfirme.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mCallback != null && getItem(i).size() > 0)
                        mCallback.onConfirmeClicked(getItem(i).get(0));
                }
            });



            view.setTag(holder);
        } else
            holder = (Holder) view.getTag();

        String resultName = mKeys[i]; // Sample: camellias (camellia japonica - Camélia du Japon)
        JustVisualResult result = getItem(i).get(0);
        holder.textViewCommonName.setText(result.getCommonName());
        holder.textViewSpecies.setText(result.getSpecies());
        holder.layoutResult.removeAllViews();
         new AsyncTask<Object, Integer, Bitmap>() {
            List<Bitmap> images = new ArrayList<Bitmap>();
            ViewGroup layout;

            @Override
            protected Bitmap doInBackground(Object... parameters) {
//                JustVisualResult visualResult= getItem(i).get(0);
                layout = (ViewGroup) parameters[0];
                int numPlant = 0;
                for (JustVisualResult visualResult : getItem(i)) {
                    if (numPlant >= 5 || stopProcessing)
                        return null;
                    Log.d(JustVisualAdapter.class.getSimpleName(), visualResult.getPlantNames() + " nbResult=" + getItem(i).size());
                    Bitmap bp = getBitmapFromURL(visualResult.getImageUrl());

                    if (bp != null) {
                        images.add(bp);
                        publishProgress(numPlant++);
                    }
                    //stop loading TODO: load onScrollRight

                }
                return null;
            }

            @Override
            protected void onProgressUpdate(final Integer... values) {
                ImageView imageView = new ImageView(mContext);
                imageView.setImageBitmap(images.get(values[0]));
                int height = layout.getHeight();
                imageView.setLayoutParams(new LinearLayout.LayoutParams(images.get(values[0]).getWidth() * (height / images.get(values[0]).getHeight()), height));
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mCallback.onImageClick(images.get(values[0]));
                    }
                });
                layout.addView(imageView);
                layout.invalidate();
                super.onProgressUpdate(values);
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap == null) return;

                super.onPostExecute(bitmap);
            }

        }.execute(holder.layoutResult);

        return view;
    }

    private void confirmeSpecies() {

    }

    public Bitmap getBitmapFromURL(String src) {
        Log.d(TAG, "getBitmapFromURL: " + src);
        try {
            java.net.URL url = new java.net.URL(src);
            File f = new File(mContext.getCacheDir(), url.getFile().substring(url.getFile().lastIndexOf('/'), url.getFile().length() - 1));
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();

            if (!f.exists()) {
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap image = BitmapFactory.decodeStream(input);
                FileOutputStream stream = new FileOutputStream(f);
                image.compress(Bitmap.CompressFormat.JPEG, 90, stream);
                input.close();
                stream.close();
            }
            Bitmap myBitmap = BitmapFactory.decodeFile(f.getAbsolutePath());
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setOnImageClick(OnAdapterClickListener listener) {
        this.mCallback = listener;
    }


}
