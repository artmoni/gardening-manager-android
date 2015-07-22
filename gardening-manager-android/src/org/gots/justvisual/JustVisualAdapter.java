package org.gots.justvisual;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.gots.R;
import org.gots.bean.BaseAllotmentInterface;
import org.gots.ui.ExpandableHeightGridView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by sfleury on 20/07/15.
 */
public class JustVisualAdapter extends BaseAdapter {

    private Map<String, List<JustVisualResult>> visualResults;
    private Context mContext;
    private String[] mKeys;
    private OnImageClick listener;

    public JustVisualAdapter(Context context, Map<String, List<JustVisualResult>> results) {
        this.visualResults = results;
        this.mContext = context;
        mKeys = results.keySet().toArray(new String[results.size()]);
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

        public TextView textViewResult;

        public LinearLayout layoutResult;

    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        Holder holder;

        if (view == null) {
            holder = new Holder();
            view = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.list_recognition, viewGroup, false);
            holder.textViewResult = (TextView) view.findViewById(R.id.textViewResult);
            holder.layoutResult = (LinearLayout) view.findViewById(R.id.layoutResult);
            view.setTag(holder);
        } else
            holder = (Holder) view.getTag();

        holder.textViewResult.setText(mKeys[i]);

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
                    if (numPlant>=5)
                        break;
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(final Integer... values) {
                ImageView imageView = new ImageView(mContext);
                imageView.setImageBitmap(images.get(values[0]));
                imageView.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        listener.onImageClick(images.get(values[0]));
                    }
                });
                layout.addView(imageView);
                super.onProgressUpdate(values);
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap == null) return;
                ImageView imageView = new ImageView(mContext);
                imageView.setImageBitmap(bitmap);
                layout.addView(imageView);
                super.onPostExecute(bitmap);
            }

        }.execute(holder.layoutResult);

        return view;
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

    public void setOnImageClick(OnImageClick listener) {
        this.listener = listener;
    }

    public interface OnImageClick {
        void onImageClick(Bitmap bitmap);
    }
}
