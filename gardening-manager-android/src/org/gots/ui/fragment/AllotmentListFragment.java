package org.gots.ui.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import org.gots.allotment.adapter.ListAllotmentAdapter;
import org.gots.bean.BaseAllotmentInterface;
import org.gots.seed.GotsGrowingSeedManager;
import org.gots.seed.GrowingSeed;

import java.util.ArrayList;
import java.util.List;

public class AllotmentListFragment extends BaseGotsListFragment {
    private ListAllotmentAdapter lsa;

//    ListView listAllotments;

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

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.allotment_list_fragment, null);
//    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lsa = new ListAllotmentAdapter(getActivity(), new ArrayList<BaseAllotmentInterface>());

    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        listView.setAdapter(lsa);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                mCallback.onAllotmentLongClick(lsa.getItem(position));
                return true;
            }
        });
        lsa.setOnGrowingSeedClickListener(new ListAllotmentAdapter.OnGrowingSeedClickListener() {

            @Override
            public void onGrowingSeedLongClick(View v, GrowingSeed seedInterface) {
                mCallback.onGrowingSeedLongClick(v, seedInterface);
            }

            @Override
            public void onGrowingSeedClick(View v, GrowingSeed seedInterface) {
                mCallback.onGrowingSeedClick(v, seedInterface);
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
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                                              private int currentVisibleItemCount;
                                              private int currentScrollState;
                                              private int currentFirstVisibleItem;
                                              private int totalItem;


                                              @Override
                                              public void onScrollStateChanged(AbsListView view, int scrollState) {
                                                  this.currentScrollState = scrollState;
                                                  this.isScrollCompleted();
                                              }

                                              @Override
                                              public void onScroll(AbsListView view, int firstVisibleItem,
                                                                   int visibleItemCount, int totalItemCount) {
                                                  this.currentFirstVisibleItem = firstVisibleItem;
                                                  this.currentVisibleItemCount = visibleItemCount;
                                                  this.totalItem = totalItemCount;


                                              }

                                              private void isScrollCompleted() {
                                                  if (currentFirstVisibleItem == 0
                                                          && this.currentScrollState == SCROLL_STATE_IDLE) {
                                                      /** To do code here*/
                                                      Log.d(AllotmentListFragment.class.getSimpleName(), "totalItem " + totalItem + " - currentFirstVisibleItem " + currentFirstVisibleItem + "== currentVisibleItemCount " + currentFirstVisibleItem);


                                                  }
                                              }
                                          }
        );
        super.onViewCreated(v, savedInstanceState);
    }

    @Override
    protected boolean requireAsyncDataRetrieval() {
        return true;
    }

    @Override
    protected Object retrieveNuxeoData() throws Exception {
        if (allotmentManager == null)
            return null;

        List<BaseAllotmentInterface> allotments = allotmentManager.getMyAllotments(false);
        GotsGrowingSeedManager growingSeedManager = GotsGrowingSeedManager.getInstance().initIfNew(getActivity());

        for (int i = 0; i < allotments.size(); i++) {
            allotments.get(i).setSeeds(growingSeedManager.getGrowingSeedsByAllotment(allotments.get(i), false));

        }
        return allotments;
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {
        List<BaseAllotmentInterface> allotments = (List<BaseAllotmentInterface>) data;
        lsa.setAllotments(allotments);
        super.onNuxeoDataRetrieved(data);
    }

    public void update() {
        runAsyncDataRetrieval();
    }

    @Override
    protected void onListItemClicked(int i) {

    }

    @Override
    protected void doRefresh() {

    }
}
