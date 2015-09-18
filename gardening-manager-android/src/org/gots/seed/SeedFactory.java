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
package org.gots.seed;


public class SeedFactory {
	// private static SeedFactory instance = null;

	public static BaseSeed createSeed(String family) {
		BaseSeed productedSeed = new GrowingSeedImpl();
		family = family.toLowerCase();
		if ("cucurbitacae".equals(family))
			productedSeed = new CucurbitacaeSeedImpl();
		else if ("basellacee".equals(family))
			productedSeed = new BasellaceeSeedImpl();
		else if ("brassicacee".equals(family))
			productedSeed = new BrassicaceeSeedImpl();
		return productedSeed;
	}

}
