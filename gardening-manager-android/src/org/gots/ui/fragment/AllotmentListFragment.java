package org.gots.ui.fragment;

import java.util.ArrayList;
import java.util.List;

import org.gots.R;
import org.gots.allotment.adapter.ListAllotmentAdapter;
import org.gots.bean.BaseAllotmentInterface;
import org.gots.seed.GotsGrowingSeedManager;
import org.gots.seed.GrowingSeed;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

public class AllotmentListFragment extends AbstractListFragment {
    private ListAllotmentAdapter lsa;

    ListView listAllotments;

    private OnAllotmentSelected mCallback;

    public interface OnAllotmentSelected {
        public abstract void onAllotmentClick(BaseAllotmentInterface allotmentInterface);

        public abstract void onAllotmentLongClick(BaseAllotmentInterface allotmentInterface);

        public abstract void onGrowingSeedClick(View v, GrowingSeed growingSeedInterface);

        public abstract void onGrowingSeedLongClick(View v, GrowingSeed growingSeedInterface);

        public abstract void onAllotmentMenuClick(View v, BaseAllotmentInterface allotmentInterface);

    }

    @Override
    public void onAttach(Activity activity) {
        try {
            mCallback = (OnAllotmentSelected) activity;
        } catch (ClassCastException castException) {
            throw new ClassCastException(activity.toString() + " must implement OnAllotmentSelected");
        }
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.allotment_list_fragment, null);
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        listAllotments = (ListView) v.findViewById(R.id.IdGardenAllotmentsList);
        lsa = new ListAllotmentAdapter(getActivity(), new ArrayList<BaseAllotmentInterface>());
        listAllotments.setAdapter(lsa);
        listAllotments.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
//        listAllotments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                mCallback.onAllotmentClick(lsa.getItem(position));
//               
//            }
//
//        });
        listAllotments.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                mCallback.onAllotmentLongClick(lsa.getItem(position));
                return true;
            }
        });
        lsa.setOnGrowingSeedClickListener(new ListAllotmentAdapter.OnGrowingSeedClickListener() {

            @Override
            public void onGrowingSeedLongClick(View v, GrowingSeed seedInterface) {
                mCallback.onGrowingSeedLongClick(v,seedInterface);
            }

            @Override
            public void onGrowingSeedClick(View v, GrowingSeed seedInterface) {
                mCallback.onGrowingSeedClick(v,seedInterface);
            }

            @Override
            public void onAllotmentMenuClick(View v, BaseAllotmentInterface allotmentInterface) {
                mCallback.onAllotmentMenuClick(v, allotmentInterface);
            }

            @Override
            public void onAllotmentClick(View v, BaseAllotmentInterface allotmentInterface) {
                mCallback.onAllotmentClick(allotmentInterface);

            }
        });
        super.onViewCreated(v, savedInstanceState);
    }

    @Override
    protected boolean requireAsyncDataRetrieval() {
        return true;
    }

    @Override
    protected Object retrieveNuxeoData() throws Exception {

        List<BaseAllotmentInterface> allotments = allotmentManager.getMyAllotments(false);
        GotsGrowingSeedManager growingSeedManager = GotsGrowingSeedManager.getInstance().initIfNew(getActivity());

        for (int i = 0; i < allotments.size(); i++) {
            allotments.get(i).setSeeds(growingSeedManager.getGrowingSeedsByAllotment(allotments.get(i), false));
        }
        return allotments;
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {
        List<BaseAllotmentInterface> result = (List<BaseAllotmentInterface>) data;
        lsa.setAllotments(result);
        super.onNuxeoDataRetrieved(data);
    }

    public void update() {
        runAsyncDataRetrieval();
    }

}
