/*******************************************************************************
 * Copyright (c) 2012 sfleury.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * <p/>
 * Contributors:
 * sfleury - initial API and implementation
 ******************************************************************************/
package org.gots.action.adapter;

import android.content.Context;

import org.gots.action.ActionOnSeed;

import java.util.ArrayList;

public class ListAllActionsDoneAdapter extends ListAllActionAdapter {

    public ListAllActionsDoneAdapter(Context context, ArrayList<ActionOnSeed> allActions, int status) {
        super(context, allActions, status);
    }

}
