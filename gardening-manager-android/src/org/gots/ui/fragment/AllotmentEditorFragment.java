package org.gots.ui.fragment;

import org.gots.R;
import org.gots.allotment.GotsAllotmentManager;
import org.gots.bean.Allotment;
import org.gots.bean.BaseAllotmentInterface;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class AllotmentEditorFragment extends BaseGotsFragment {

    public interface OnAllotmentListener {
        public void onAllotmentCreated(BaseAllotmentInterface allotment);
    }

    private OnAllotmentListener mCallback;

    private GotsAllotmentManager allotmentManager;

    private TextView textviewAllotmentName;

    private Button buttonNewAllotment;

    private BaseAllotmentInterface allotment;

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

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        textviewAllotmentName = (TextView) view.findViewById(R.id.editTextAllotmentName);
        buttonNewAllotment = (Button) view.findViewById(R.id.buttonAllotmentNew);
        if (allotment == null) {
            buttonNewAllotment.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    new AsyncTask<Void, Void, BaseAllotmentInterface>() {

                        protected void onPreExecute() {
                            allotment = new Allotment();
                            allotment.setName(textviewAllotmentName.getText().toString());
                        };

                        @Override
                        protected BaseAllotmentInterface doInBackground(Void... params) {
                            return allotmentManager.createAllotment(allotment);
                        }

                        protected void onPostExecute(BaseAllotmentInterface newAllotment) {
                            mCallback.onAllotmentCreated(newAllotment);
                        };

                    }.execute();
                };
            });
        } else {
            buttonNewAllotment.setText(getResources().getString(R.string.menu_edit));
            buttonNewAllotment.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    new AsyncTask<Void, Void, BaseAllotmentInterface>() {

                        protected void onPreExecute() {
                            allotment.setName(textviewAllotmentName.getText().toString());
                        };

                        @Override
                        protected BaseAllotmentInterface doInBackground(Void... params) {
                            return allotmentManager.updateAllotment(allotment);
                        }

                        protected void onPostExecute(BaseAllotmentInterface result) {
                            mCallback.onAllotmentCreated(allotment);
                        };

                    }.execute();
                };
            });
        }

        textviewAllotmentName.setText(allotment != null ? allotment.getName() : "");
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

}
