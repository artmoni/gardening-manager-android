package org.gots.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.gots.R;
import org.gots.action.BaseAction;
import org.gots.action.GotsActionManager;
import org.gots.action.adapter.SimpleListActionAdapter;

import java.util.List;

public class ActionsChoiceFragment extends BaseGotsFragment {

    private GridView gridView;
    private OnActionSelectedListener mCallback;
    private GotsActionManager actionManager;

    @Override
    public void onAttach(Activity activity) {
        try {
            mCallback = (OnActionSelectedListener) activity;
        } catch (ClassCastException castException) {
            throw new ClassCastException(ActionsChoiceFragment.class.getSimpleName()
                    + " must implements OnActionSelectedListener");
        }
        super.onAttach(activity);
    }


//    private LinearLayout layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.actions_list_fragment, null);
        actionManager = GotsActionManager.getInstance().initIfNew(getActivity());
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        gridView = (GridView) view.findViewById(R.id.gridViewActions);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void update() {

    }

    @Override
    protected boolean requireAsyncDataRetrieval() {
        return true;
    }

    @Override
    protected void onNuxeoDataRetrievalStarted() {
        super.onNuxeoDataRetrievalStarted();
    }

    @Override
    protected Object retrieveNuxeoData() throws Exception {
        return actionManager.getActions(false);
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {
        final List<BaseAction> actionInterfaces = (List<BaseAction>) data;
//        for (final BaseAction baseActionInterface : actionInterfaces) {
//            if (!(baseActionInterface instanceof ActionOnSeed))
//                continue;
//            ActionWidget actionWidget = new ActionWidget(getActivity(), baseActionInterface);
//            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT);
//            lp.setMargins(5, 5, 5, 5);
//            actionWidget.setLayoutParams(lp);
//            layout.addView(actionWidget);
//            actionWidget.setOnClickListener(new View.OnClickListener() {
//
//                @Override
//                public void onClick(View v) {
//                    mCallback.onActionClick(baseActionInterface);
//                }
//            });
//            actionWidget.setOnLongClickListener(new View.OnLongClickListener() {
//
//                @Override
//                public boolean onLongClick(View v) {
//                    mCallback.onActionLongClick(baseActionInterface);
//                    return true;
//                }
//            });
//        }
//        ArrayAdapter<Object> arrayAdapter = new ArrayAdapter<Object>(getActivity(), android.R.layout.simple_list_item_1, actionInterfaces.toArray());
        SimpleListActionAdapter simpleListActionAdapter = new SimpleListActionAdapter(getActivity(), actionInterfaces);
        gridView.setAdapter(simpleListActionAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCallback.onActionClick(actionInterfaces.get(position));

            }
        });
        super.onNuxeoDataRetrieved(data);
    }

    public interface OnActionSelectedListener {

        public void onActionClick(BaseAction actionInterface);

        public void onActionLongClick(BaseAction actionInterface);

    }
}
