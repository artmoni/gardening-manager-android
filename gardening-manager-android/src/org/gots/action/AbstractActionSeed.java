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
package org.gots.action;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

import org.gots.action.provider.GotsActionSeedProvider;
import org.gots.allotment.AllotmentManager;
import org.gots.garden.GardenManager;
import org.gots.preferences.GotsPreferences;
import org.gots.seed.GotsGrowingSeedManager;
import org.gots.seed.GotsSeedManager;
import org.gots.seed.GrowingSeedInterface;
import org.gots.seed.provider.GotsSeedProvider;

import android.content.Context;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;

public abstract class AbstractActionSeed implements SeedActionInterface, Comparator<AbstractActionSeed> {

    private String name;

    private String description;

    private int duration; // nb days before doing action

    private Date dateActionDone;

    private int id;

    private int actionSeedId;

    private String UUID;

    private int state;

    private Date dateActionTodo;

    // private GrowingSeedInterface growingSeed;
    private int growingSeedId;

    private Object data;

    protected GotsPreferences gotsPrefs;

    protected AllotmentManager allotmentProvider;

    private Context mContext;

    protected GotsSeedProvider seedManager;

    protected GardenManager gardenManager;

    protected GotsActionSeedProvider actionSeedManager;

    protected GotsGrowingSeedManager growingSeedManager;

    public AbstractActionSeed(Context context) {
        mContext = context;
        gotsPrefs = GotsPreferences.getInstance().initIfNew(context);
        allotmentProvider = AllotmentManager.getInstance().initIfNew(context);
        seedManager = GotsSeedManager.getInstance().initIfNew(mContext);
        growingSeedManager = GotsGrowingSeedManager.getInstance().initIfNew(mContext);
        // Gardenmanager might not be declared here
        gardenManager = GardenManager.getInstance().initIfNew(mContext);

        actionSeedManager = GotsActionSeedManager.getInstance().initIfNew(mContext);
    }

    public AbstractActionSeed(String name) {
        this.name = name;
    }

    @Override
    public int getGrowingSeedId() {
        return growingSeedId;
    }

    @Override
    public void setGrowingSeedId(int id) {
        growingSeedId = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int getDuration() {
        return duration;
    }

    @Override
    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public Date getDateActionDone() {
        return dateActionDone;
    }

    @Override
    public void setDateActionDone(Date dateActionDone) {
        this.dateActionDone = dateActionDone;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public int execute(GrowingSeedInterface seed) {
        setDateActionDone(Calendar.getInstance().getTime());

        GoogleAnalyticsTracker tracker = GoogleAnalyticsTracker.getInstance();
        tracker.trackEvent("Seed", getName(), seed.getSpecie(), 0);
        // tracker.dispatch();

        return 1;
    }

    @Override
    public int getState() {

        return state;
    }

    @Override
    public void setState(int state) {
        this.state = state;
    }

    @Override
    public Date getDateActionTodo() {

        return this.dateActionTodo;
    }

    @Override
    public void setDateActionTodo(Date dateActionTodo) {
        this.dateActionTodo = dateActionTodo;
    }

    @Override
    public int compare(AbstractActionSeed lhs, AbstractActionSeed rhs) {

        return lhs.getDateActionDone().getTime() > rhs.getDateActionDone().getTime() ? -1 : 1;
    }

    @Override
    public Object getData() {
        return data;
    }

    @Override
    public void setData(Object data) {
        this.data = data;
    }

    protected Context getContext() {
        return mContext;
    }

    @Override
    public String getUUID() {
        return this.UUID;
    }

    @Override
    public void setUUID(String uuid) {
        this.UUID = uuid;
    }

    @Override
    public String toString() {
        String txt = new String();
        txt = txt.concat("[" + getId() + "]" + getName());
        txt = txt.concat(getUUID());
        txt = txt.concat("\n");
        txt = txt.concat("Duration=" + getDuration());
        if (getDateActionDone() != null)
            txt = txt.concat("\nDone on " + getDateActionDone());
        return super.toString();
    }

    @Override
    public int getActionSeedId() {
        return actionSeedId;
    }

    @Override
    public void setActionSeedId(int id) {
        actionSeedId = id;
    }
}
