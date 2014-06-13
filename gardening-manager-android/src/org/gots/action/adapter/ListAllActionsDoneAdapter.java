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
package org.gots.action.adapter;

import java.util.ArrayList;

import org.gots.action.SeedActionInterface;

import android.content.Context;

public class ListAllActionsDoneAdapter extends ListAllActionAdapter {

	public ListAllActionsDoneAdapter(Context context, ArrayList<SeedActionInterface> allActions, int status) {
		super(context, allActions, status);
	}

}
