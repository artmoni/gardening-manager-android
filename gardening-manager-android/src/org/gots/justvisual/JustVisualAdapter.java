package org.gots.justvisual;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
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

    private Map<String, List<JustVisualResult>> visualResults;
    private Context mContext;
    private String[] mKeys;
    private OnAdapterClickListener mCallback;

    public JustVisualAdapter(Context context, Map<String, List<JustVisualResult>> results) {
        visualResults = results;
        this.mContext = context;
        if (visualResults == null)
            visualResults = new HashMap<>();
        mKeys = visualResults.keySet().toArray(new String[visualResults.size()]);
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
                    Bundle args = new Bundle();
                    if (getItem(i).size() > 0)
                        args.putString(WebHelpActivity.URL, getItem(i).get(0).getPageUrl());
                    else
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

        new AsyncTask<Object, Integer, Bitmap>() {
            List<Bitmap> images = new ArrayList<Bitmap>();
            ViewGroup layout;

            @Override
            protected Bitmap doInBackground(Object... parameters) {
//                JustVisualResult visualResult= getItem(i).get(0);
                layout = (ViewGroup) parameters[0];
                int numPlant = 0;
                for (JustVisualResult visualResult : getItem(i)) {
                    Log.d(JustVisualAdapter.class.getSimpleName(), visualResult.getPlantNames() + " nbResult=" + getItem(i).size());
                    Bitmap bp = getBitmapFromURL(visualResult.getImageUrl());

                    if (bp != null) {
                        images.add(bp);
                        publishProgress(numPlant++);
                    }
                    //stop loading TODO: load onScrollRight
                    if (numPlant >= 5)
                        return null;
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(final Integer... values) {
                ImageView imageView = new ImageView(mContext);
                imageView.setImageBitmap(images.get(values[0]));
                imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
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
        try {
            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
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
