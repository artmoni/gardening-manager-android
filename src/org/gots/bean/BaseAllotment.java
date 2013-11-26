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
package org.gots.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.gots.seed.GrowingSeedInterface;

public class BaseAllotment implements Serializable, BaseAllotmentInterface {

    private static final long serialVersionUID = 1L;

    private int id;

    private String name;

    private String description;

    private List<GrowingSeedInterface> seeds = new ArrayList<GrowingSeedInterface>();

    private String uuid;

    public BaseAllotment() {
        super();
    }

    @Override
    public List<GrowingSeedInterface> getSeeds() {
        return seeds;
    }

    @Override
    public void setSeeds(List<GrowingSeedInterface> seeds) {
        this.seeds = seeds;
    }

    /*
     * (non-Javadoc)
     * @see org.gots.bean.AllotmentInterface#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     * @see org.gots.bean.AllotmentInterface#setName(java.lang.String)
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }

    /*
     * (non-Javadoc)
     * @see org.gots.bean.AllotmentInterface#getDescription()
     */
    @Override
    public String getDescription() {
        return description;
    }

    /*
     * (non-Javadoc)
     * @see org.gots.bean.AllotmentInterface#setDescription(java.lang.String)
     */
    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;

    }

    @Override
    public String toString() {
        String desc = new String();
        desc.concat("(" + getId() + ")");
        desc.concat("[" + getUUID() + "]");
        desc.concat("[" + getName() + "]");
        return desc;
    }

    @Override
    public String getUUID() {
        return uuid;
    }

    @Override
    public void setUUID(String id) {
        uuid = id;
    }

}
