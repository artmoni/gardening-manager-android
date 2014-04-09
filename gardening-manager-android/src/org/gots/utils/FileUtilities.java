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
}
