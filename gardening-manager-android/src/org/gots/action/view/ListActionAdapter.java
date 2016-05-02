package org.gots.action.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.gots.action.BaseAction;

import java.util.List;

public class ListActionAdapter extends BaseAdapter {

    private List<BaseAction> actions;

    private Context mContext;

    public ListActionAdapter(Context context, List<BaseAction> actions) {
        this.actions = actions;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return actions.size();
    }

    @Override
    public BaseAction getItem(int position) {
        return actions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return actions.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ActionWidget actionWidget = new ActionWidget(mContext, getItem(position));
        return actionWidget;
    }

}
