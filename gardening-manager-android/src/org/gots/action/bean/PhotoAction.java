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
package org.gots.action.bean;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.gots.action.AbstractActionSeed;
import org.gots.action.GardeningActionInterface;
import org.gots.action.PermanentActionInterface;
import org.gots.action.ActionOnSeed;
import org.gots.bean.BaseAllotmentInterface;
import org.gots.seed.GrowingSeed;

import android.content.Context;

public class PhotoAction extends AbstractActionSeed implements PermanentActionInterface, ActionOnSeed,
        GardeningActionInterface {

    private static final String JPEG_FILE_PREFIX = "GOTS_";

    private static final String JPEG_FILE_SUFFIX = ".JPG";

    public PhotoAction(Context context) {
        super(context);
        setName("photo");
    }


    public File getImageFile(Date date) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);
        String imageFileName = JPEG_FILE_PREFIX + timeStamp;

        File imageFile = new File(gotsPrefs.getGotsExternalFileDir(), imageFileName + JPEG_FILE_SUFFIX);
        return imageFile;
    }


    @Override
    public int execute(BaseAllotmentInterface allotment, GrowingSeed seed) {
        // TODO Auto-generated method stub
        return 0;
    }

}
