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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import org.gots.action.AbstractActionSeed;
import org.gots.action.GardeningActionInterface;
import org.gots.action.PermanentActionInterface;
import org.gots.action.SeedActionInterface;
import org.gots.action.sql.ActionSeedDBHelper;
import org.gots.bean.BaseAllotmentInterface;
import org.gots.seed.GrowingSeedInterface;
import org.gots.seed.sql.GrowingSeedDBHelper;

import android.content.Context;
import android.os.Environment;

public class PhotoAction extends AbstractActionSeed implements PermanentActionInterface, SeedActionInterface,
		GardeningActionInterface {
	Context mContext;
	private File storageDir;
	private static final String JPEG_FILE_PREFIX = "GOTS";
	private static final String JPEG_FILE_SUFFIX = ".JPG";

	public PhotoAction(Context context) {
		setName("photo");
		mContext = context;
		storageDir = new File(Environment.getExternalStorageDirectory(), "Gardening-Manager");

	}

	@Override
	public int execute(GrowingSeedInterface seed) {
		super.execute(seed);

		seed.getActionToDo().remove(this);
		seed.getActionDone().add(this);

		ActionSeedDBHelper asdh = new ActionSeedDBHelper(mContext);
		asdh.doAction(this, seed);

		return 0;
	}

	public void setDateActionDone(Date dateActionDone) {
		super.setDateActionDone(dateActionDone);
	}

	public Date getDateActionDone() {
		return super.getDateActionDone();
	}

	public void setDuration(int duration) {
		super.setDuration(duration);
	}

	public int getDuration() {
		return super.getDuration();
	}

	public void setDescription(String description) {
		super.setDescription(description);
	}

	public String getDescription() {
		return super.getDescription();
	}

	public void setName(String name) {
		super.setName(name);
	}

	public String getName() {
		return super.getName();
	}

	@Override
	public int execute(BaseAllotmentInterface allotment, GrowingSeedInterface seed) {

		return 0;
	}

	@Override
	public void setId(int id) {
		super.setId(id);
	}

	@Override
	public int getId() {
		return super.getId();
	}

	public File getImageFile(Date date) {
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HH").format(date);
		String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";

		File imageFile = new File(storageDir, imageFileName + JPEG_FILE_SUFFIX);
		return imageFile;
	}
}
