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
package org.gots.action;

import org.gots.bean.BaseAllotmentInterface;
import org.gots.seed.BaseSeed;

public interface PermanentActionInterface {

    public void execute(BaseAllotmentInterface allotment, BaseSeed seed);
}
