package org.gots.action.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.gots.R;
import org.gots.action.BaseAction;
import org.gots.action.view.ActionWidget;

import java.util.ArrayList;
import java.util.List;

public class SimpleListActionAdapter extends BaseAdapter {

    private final Context mContext;
    List<BaseAction> mActions = new ArrayList<BaseAction>();
    private Holder holder;

    public SimpleListActionAdapter(Context context, List<BaseAction> actions) {
        this.mActions.addAll(actions);
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mActions.size();
    }

    @Override
    public BaseAction getItem(int arg0) {
        return mActions.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return getItem(arg0).getId();
    }

    public class Holder {
        public ActionWidget actionWidget;
        public TextView textViewActionName;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        // getItem(arg0).setState(ActionState.NORMAL);
        if (view == null) {
            holder = new Holder();
            view = (RelativeLayout) LayoutInflater.from(mContext).inflate(R.layout.list_action_simple, viewGroup, false);
            holder.actionWidget = (ActionWidget)view.findViewById(R.id.idActionView);
            holder.textViewActionName = (TextView)view.findViewById(R.id.textViewActionName);
            view.setTag(holder);
        } else
            holder = (Holder) view.getTag();
        holder.actionWidget.setAction(getItem(position));
        holder.textViewActionName.setText(getItem(position).getName());
        return view;
    }
}
