package org.gots.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;

public class GalleryImageAdapter implements SpinnerAdapter {

    private static final String TAG = "GalleryImageAdapter";

    List<File> imagesList = new ArrayList<File>();

    private Context mContext;

    public GalleryImageAdapter(Context context, List<File> images) {
        mContext = context;
        imagesList=images;
    }

    @Override
    public int getCount() {
        return imagesList.size();
    }

    @Override
    public File getItem(int position) {
        return imagesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView image = new ImageView(mContext);
        try{
        Bitmap bitmap = BitmapFactory.decodeFile(getItem(position).getAbsolutePath());
        Bitmap bitmapResized = Bitmap.createScaledBitmap(bitmap, parent.getHeight(), parent.getHeight(), false);

        image.setImageBitmap(bitmapResized);
        }catch(Exception e){
            Log.w(TAG, e.getMessage());
        }
        return image;
    }

    @Override
    public int getViewTypeCount() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isEmpty() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        // TODO Auto-generated method stub

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        // TODO Auto-generated method stub

    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        return null;
    }

}
