package org.gots.ui.fragment;

import java.util.List;

import org.gots.R;
import org.gots.action.ActionFactory;
import org.gots.action.ActionOnSeed;
import org.gots.action.BaseAction;
import org.gots.action.GotsActionManager;
import org.gots.action.view.ActionWidget;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class ActionsChoiceFragment extends BaseGotsFragment {

    public interface OnActionSelectedListener {

        public void onActionClick(BaseAction actionInterface);

        public void onActionLongClick(BaseAction actionInterface);

    }

    private OnActionSelectedListener mCallback;

    private GotsActionManager actionManager;

    private ScrollView parent;

    private LinearLayout layout;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.actions_list_fragment, null);
        actionManager = GotsActionManager.getInstance().initIfNew(getActivity());
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        parent = (ScrollView) view.findViewById(R.id.scrollviewActions);
        layout = (LinearLayout) parent.getChildAt(0);

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
        List<BaseAction> actionInterfaces = (List<BaseAction>) data;
        for (final BaseAction baseActionInterface : actionInterfaces) {
            if (!(baseActionInterface instanceof ActionOnSeed))
                continue;
            ActionWidget actionWidget = new ActionWidget(getActivity(), baseActionInterface);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(5, 5, 5, 5);
            actionWidget.setLayoutParams(lp);
            layout.addView(actionWidget);
            actionWidget.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    final BaseAction newAction = ActionFactory.buildAction(getActivity(), baseActionInterface.getName());
                    newAction.setId(baseActionInterface.getId());
                    mCallback.onActionClick(newAction);

                }
            });
            actionWidget.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    mCallback.onActionLongClick(baseActionInterface);
                    return true;
                }
            });
        }
        super.onNuxeoDataRetrieved(data);
    }
}
