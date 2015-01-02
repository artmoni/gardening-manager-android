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
package org.gots.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

public class FileUtilities {
    public static boolean StoreByteImage(Context mContext, byte[] imageData, int quality, String directory,
            String expName) {

        File sdImageMainDirectory = new File("/sdcard/Gots/" + directory);
        FileOutputStream fileOutputStream = null;
        // String nameFile="test";
        try {

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 5;

            Bitmap myImage = BitmapFactory.decodeByteArray(imageData, 0, imageData.length, options);

            if (!sdImageMainDirectory.isDirectory())
                sdImageMainDirectory.mkdir();

            fileOutputStream = new FileOutputStream(sdImageMainDirectory.toString() + "/" + expName + ".jpg");

            BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);

            myImage.compress(CompressFormat.JPEG, quality, bos);

            bos.flush();
            bos.close();

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return true;
    }

    public static void copy(File src, File dst) throws IOException {
        FileInputStream inStream = new FileInputStream(src);
        FileOutputStream outStream = new FileOutputStream(dst);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();
    }
    
    public static Bitmap decodeScaledBitmapFromSdCard(String filePath,
            int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        
        Bitmap imageBitmap=BitmapFactory.decodeFile(filePath, options);
        int rotate = getImageOrientation(filePath);
        Matrix matrix = new Matrix();
        matrix.postRotate(rotate);
        imageBitmap = Bitmap.createBitmap(imageBitmap , 0, 0, imageBitmap.getWidth(), imageBitmap.getHeight(), matrix, true);
        return imageBitmap;
    }

    public static  int getImageOrientation(String sourcepath){
        int rotate = 0;
        try {
            File imageFile = new File(sourcepath);
            ExifInterface exif = new ExifInterface(
                    imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate = 270;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotate = 90;
                break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }
}
