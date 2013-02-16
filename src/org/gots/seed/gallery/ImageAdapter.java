/*******************************************************************************
 * Copyright (c) 2012 sfleury.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     sfleury - initial API and implementation
 ******************************************************************************/
package org.gots.seed.gallery;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.gots.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {
	int mGalleryItemBackground;
	private Context mContext;

	// private Integer[] mImageIds = { R.drawable.action_arroser
	//
	// };
	List<File> imagelist=new ArrayList<File>();
	// private List<Bitmap> bMap = new ArrayList<Bitmap>();

//	private ImageView image;

	public ImageAdapter(Context c, String directory) {
		mContext = c;
		TypedArray attr = mContext
				.obtainStyledAttributes(R.styleable.GalleryTheme);
		mGalleryItemBackground = attr.getResourceId(
				R.styleable.GalleryTheme_android_galleryItemBackground, 0);
		attr.recycle();

		File images = new File("/sdcard/Gots/" + directory);
		if (images.isDirectory()) {
			File [] files= images.listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String filename) {
					// TODO Auto-generated method stub
					return ((filename.endsWith(".jpg")) || (filename
							.endsWith(".png")));
				}
			});
			Collections.addAll(imagelist,files);
		}

	}

	public int getCount() {
		return imagelist.size();
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView = new ImageView(mContext);
		imageView.setImageBitmap(BitmapFactory.decodeFile(imagelist.get(position)
				.getPath()));

		// imageView.setImageResource(mImageIds[position]);
		imageView.setLayoutParams(new Gallery.LayoutParams(150, 100));
		imageView.setScaleType(ImageView.ScaleType.FIT_XY);
		// imageView.setBackgroundResource(mGalleryItemBackground);

		return imageView;
	}
}
