package org.gots.action.view;

import java.util.List;

import org.gots.R;
import org.gots.action.BaseActionInterface;
import org.gots.seed.view.SeedWidget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class ListActionAdapter extends BaseAdapter {

    private List<BaseActionInterface> actions;

    private Context mContext;

    public ListActionAdapter(Context context, List<BaseActionInterface> actions) {
        this.actions = actions;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return actions.size();
    }

    @Override
    public BaseActionInterface getItem(int position) {
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
