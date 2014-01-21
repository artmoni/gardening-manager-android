package org.gots.action.adapter;

import java.util.ArrayList;
import java.util.List;

import org.gots.action.BaseActionInterface;
import org.gots.action.util.ActionState;
import org.gots.action.view.ActionWidget;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class SimpleListActionAdapter extends BaseAdapter {

	List<BaseActionInterface> mActions = new ArrayList<BaseActionInterface>();

	public SimpleListActionAdapter(List<BaseActionInterface> actions) {
		this.mActions.addAll(actions);
	}

	@Override
	public int getCount() {
		return mActions.size();
	}

	@Override
	public BaseActionInterface getItem(int arg0) {
		return mActions.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
//		TODO ACTION STATE
//	    getItem(arg0).setState(ActionState.NORMAL);
		ActionWidget actionWidget = new ActionWidget(arg2.getContext(),getItem(arg0));
		

		return actionWidget;
	}

}
