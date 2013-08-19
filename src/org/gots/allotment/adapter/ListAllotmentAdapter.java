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
package org.gots.allotment.adapter;

import java.util.ArrayList;
import java.util.List;

import org.gots.R;
import org.gots.action.GardeningActionInterface;
import org.gots.action.bean.DeleteAction;
import org.gots.action.bean.SowingAction;
import org.gots.action.view.ActionWidget;
import org.gots.allotment.AllotmentManager;
import org.gots.allotment.provider.AllotmentProvider;
import org.gots.allotment.view.QuickAllotmentActionBuilder;
import org.gots.bean.BaseAllotmentInterface;
import org.gots.preferences.GotsPreferences;
import org.gots.seed.GrowingSeedInterface;
import org.gots.seed.adapter.ListGrowingSeedAdapter;
import org.gots.seed.provider.local.sql.GrowingSeedDBHelper;
import org.gots.ui.HutActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;

public class ListAllotmentAdapter extends BaseAdapter implements OnClickListener {
    Context mContext;

    private ListGrowingSeedAdapter lgsa;

    private GridView listSeeds;

    private List<BaseAllotmentInterface> myAllotments;

    @Override
    public void notifyDataSetChanged() {

        super.notifyDataSetChanged();
    }

    public ListAllotmentAdapter(Context mContext, List<BaseAllotmentInterface> allotments) {
        this.mContext = mContext;
        myAllotments = allotments;
    }

    public void setAllotments(List<BaseAllotmentInterface> allotments) {
        myAllotments = allotments;
        notifyDataSetChanged();
    };

    @Override
    public int getCount() {
        return myAllotments.size();
    }

    @Override
    public BaseAllotmentInterface getItem(int position) {
        return myAllotments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout ll = (LinearLayout) convertView;

        if (convertView == null) {
            // ll = new LinearLayout(mContext);
            ll = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.list_allotments, parent, false);
        }
        GrowingSeedDBHelper helper = new GrowingSeedDBHelper(mContext);
        List<GrowingSeedInterface> mySeeds = helper.getSeedsByAllotment(myAllotments.get(position).getName());

        lgsa = new ListGrowingSeedAdapter(mContext, mySeeds, this);
        listSeeds = (GridView) ll.findViewById(R.id.IdGrowingSeedList);
        listSeeds.setAdapter(lgsa);

        int nbcolumn = 4;
        int layoutsize = 100;
        if (listSeeds.getCount() == 0)
            listSeeds.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, layoutsize));
        else if (listSeeds.getCount() % nbcolumn == 0)
            listSeeds.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                    (listSeeds.getCount() / 4) * layoutsize));
        else

            listSeeds.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                    (listSeeds.getCount() / 4 + 1) * layoutsize));

        LinearLayout menu = (LinearLayout) ll.findViewById(R.id.idMenuAllotment);
        menu.removeAllViews();

        SowingAction sow = new SowingAction(mContext);
        ActionWidget widget = new ActionWidget(mContext, sow);
        widget.setTag(Integer.valueOf(position));
        widget.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AllotmentManager.getInstance().setCurrentAllotment(getItem(Integer.valueOf(v.getTag().toString())));

                Intent i = new Intent(mContext, HutActivity.class);
                i.putExtra("org.gots.allotment.reference",
                        myAllotments.get(Integer.valueOf(v.getTag().toString())).getId());
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(i);
            }
        });

        widget.setPadding(4, 4, 4, 8);
        menu.addView(widget);

        final DeleteAction delete = new DeleteAction(mContext);
        widget = new ActionWidget(mContext, delete);
        widget.setTag(Integer.valueOf(position));
        widget.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new AsyncTask<BaseAllotmentInterface, Integer, Void>() {
                    @Override
                    protected void onPreExecute() {

                        super.onPreExecute();
                    }

                    @Override
                    protected Void doInBackground(BaseAllotmentInterface... params) {
                        GardeningActionInterface actionItem = delete;
                        actionItem.execute(params[0], null);
                        myAllotments.remove(params[0]);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void result) {
                        notifyDataSetChanged();
                        super.onPostExecute(result);
                    }
                }.execute(getItem(Integer.valueOf(v.getTag().toString())));

                // AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                // builder.setMessage(v.getContext().getResources().getString(R.string.action_delete_allotment)).setCancelable(
                // false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                // public void onClick(DialogInterface dialog, int id) {
                // dialog.dismiss();
                // }
                // }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                // public void onClick(DialogInterface dialog, int id) {
                // dialog.cancel();
                // }
                // });
                //
                // builder.show();

            }
        });

        widget.setPadding(4, 4, 4, 8);

        menu.addView(widget);

        return ll;
    }

    @Override
    public void onClick(View v) {
        QuickAllotmentActionBuilder actionsBuilder = new QuickAllotmentActionBuilder(v);
        actionsBuilder.show();

    }
}
