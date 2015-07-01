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
package org.gots.ui;

import java.util.ArrayList;
import java.util.List;

import org.gots.R;
import org.gots.action.ActionOnSeed;
import org.gots.action.adapter.ListAllActionAdapter;
import org.gots.ads.GotsAdvertisement;
import org.gots.bean.BaseAllotmentInterface;
import org.gots.provider.ActionsContentProvider;
import org.gots.seed.GotsGrowingSeedManager;
import org.gots.seed.GrowingSeed;
import org.gots.ui.fragment.ActionsListFragment;
import org.gots.ui.fragment.AllotmentEditorFragment.OnAllotmentListener;
import org.gots.ui.fragment.AllotmentListFragment;
import org.gots.ui.fragment.AllotmentListFragment.OnAllotmentSelected;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

public class ActionActivity extends BaseGotsActivity implements OnAllotmentSelected {

    ListView listviewActions;

    ArrayList<GrowingSeed> allSeeds = new ArrayList<GrowingSeed>();

    List<ActionOnSeed> seedActions = new ArrayList<ActionOnSeed>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitleBar(R.string.dashboard_actions_name);

        addMainLayout(new ActionsListFragment(), null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_action, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected String requireRefreshSyncAuthority() {
        return ActionsContentProvider.AUTHORITY;
    }

    @Override
    protected boolean requireFloatingButton() {
        return true;
    }

    @Override
    protected List<FloatingItem> onCreateFloatingMenu() {
        List<FloatingItem> floatingItems = new ArrayList<>();
        FloatingItem floatingItem = new FloatingItem();
        floatingItem.setTitle(getResources().getString(R.string.action_planning));
        floatingItem.setRessourceId(R.drawable.action_schedule);
        floatingItem.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                addContentLayout(new AllotmentListFragment(), null);
            }
        });
        floatingItems.add(floatingItem);
        return floatingItems;

    }

    @Override
    public void onAllotmentClick(BaseAllotmentInterface allotmentInterface) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onAllotmentLongClick(BaseAllotmentInterface allotmentInterface) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onAllotmentMenuClick(View v, BaseAllotmentInterface allotmentInterface) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onGrowingSeedClick(View v, GrowingSeed growingSeedInterface) {

    }

    @Override
    public void onGrowingSeedLongClick(View v, GrowingSeed growingSeedInterface) {
        // TODO Auto-generated method stub

    }
}
