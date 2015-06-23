package org.gots.ui.fragment;

import org.gots.R;
import org.gots.allotment.GotsAllotmentManager;
import org.gots.allotment.provider.AllotmentProvider;
import org.gots.bean.Allotment;
import org.gots.bean.BaseAllotmentInterface;
import org.gots.seed.GrowingSeed;
import org.gots.seed.adapter.ListGrowingSeedAdapter;

import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory.Adapter;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

public class AllotmentEditorFragment extends BaseGotsFragment {

    public interface OnAllotmentListener {
        public void onAllotmentCreated(BaseAllotmentInterface allotment);

        public void onAllotmentModified(BaseAllotmentInterface allotment);

        public void onAllotmentSeedClicked(BaseAllotmentInterface allotment, GrowingSeed seed);

        public void onAllotmentAddPlantClicked(BaseGotsFragment fragment, BaseAllotmentInterface allotment);

    }

    private OnAllotmentListener mCallback;

    private AllotmentProvider allotmentManager;

    private TextView textviewAllotmentName;

    // private Button buttonNewAllotment;

    private BaseAllotmentInterface allotment;

    private TextView textviewPlantCount;

    private GridView gridView;

    @Override
    public void onAttach(Activity activity) {
        try {
            mCallback = (OnAllotmentListener) activity;
        } catch (ClassCastException castException) {
            throw new ClassCastException(AllotmentEditorFragment.class.getSimpleName()
                    + " must implements OnActionSelectedListener");
        }
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.allotment_content, null);
        allotmentManager = GotsAllotmentManager.getInstance().initIfNew(getActivity());

        textviewAllotmentName = (TextView) view.findViewById(R.id.editTextAllotmentName);
        // buttonNewAllotment = (Button) view.findViewById(R.id.buttonAllotmentNew);
        textviewPlantCount = (TextView) view.findViewById(R.id.textViewNbPlants);
        gridView = (GridView) view.findViewById(R.id.IdGrowingSeedList);

        if (allotment == null) {
            allotment = new Allotment();
            // buttonNewAllotment.setOnClickListener(new View.OnClickListener() {
            //
            // @Override
            // public void onClick(View v) {
            // new AsyncTask<Void, Void, BaseAllotmentInterface>() {
            //
            // protected void onPreExecute() {
            // allotment = new Allotment();
            // allotment.setName(textviewAllotmentName.getText().toString());
            // };
            //
            // @Override
            // protected BaseAllotmentInterface doInBackground(Void... params) {
            // return allotmentManager.createAllotment(allotment);
            // }
            //
            // protected void onPostExecute(BaseAllotmentInterface newAllotment) {
            // mCallback.onAllotmentCreated(newAllotment);
            // };
            //
            // }.execute();
            // };
            // });
        }
        // else {
        // buttonNewAllotment.setText(getResources().getString(R.string.menu_edit));
        // buttonNewAllotment.setOnClickListener(new View.OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // new AsyncTask<Void, Void, BaseAllotmentInterface>() {
        //
        // protected void onPreExecute() {
        // allotment.setName(textviewAllotmentName.getText().toString());
        // };
        //
        // @Override
        // protected BaseAllotmentInterface doInBackground(Void... params) {
        // return allotmentManager.updateAllotment(allotment);
        // }
        //
        // protected void onPostExecute(BaseAllotmentInterface result) {
        // mCallback.onAllotmentCreated(allotment);
        // };
        //
        // }.execute();
        // };
        // });
        else {
            final ListGrowingSeedAdapter adapter = new ListGrowingSeedAdapter(getActivity(), allotment.getSeeds());
            gridView.setAdapter(adapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position < adapter.getCount() - 1) {
                        mCallback.onAllotmentSeedClicked(allotment, adapter.getItem(position));

                    } else
                        mCallback.onAllotmentAddPlantClicked(AllotmentEditorFragment.this, allotment);
                }
            });
        }
        // }

        textviewAllotmentName.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    allotment.setName(textviewAllotmentName.getText().toString());
                    if (allotment.getId() >= 0)
                        mCallback.onAllotmentModified(allotment);
                    else
                        mCallback.onAllotmentCreated(allotment);
                }

            }
        });
      
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void update() {
    }

    public void setAllotment(BaseAllotmentInterface allotment) {
        this.allotment = allotment;
    }

    @Override
    protected boolean requireAsyncDataRetrieval() {
        return false;
    }

    @Override
    public void onResume() {
        textviewPlantCount.setText(allotment != null ? "" + allotment.getSeeds().size() : "0");
        textviewAllotmentName.setText(allotment != null ? allotment.getName() : "");
        super.onResume();
    }
}
