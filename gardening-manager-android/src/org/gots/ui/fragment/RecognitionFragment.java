package org.gots.ui.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.gots.R;
import org.gots.justvisual.ImageRecognition;
import org.gots.justvisual.JustVisualAdapter;
import org.gots.justvisual.JustVisualResult;
import org.gots.justvisual.OnRecognitionFinished;
import org.gots.nuxeo.NuxeoManager;
import org.gots.nuxeo.NuxeoUtils;
import org.gots.nuxeo.NuxeoWorkflowProvider;
import org.gots.utils.FileUtilities;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.nuxeo.android.repository.DocumentManager;
import org.nuxeo.ecm.automation.client.android.AndroidAutomationClient;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.PropertyMap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sfleury on 13/07/15.
 */
public class RecognitionFragment extends BaseGotsFragment implements JustVisualAdapter.OnAdapterClickListener {

    //    public static final String IMAGE_PATH = "org.gots.recognition.path";
    private static final String RECOGNITION_SUCCESS = "org.gots.recognition.success";


    private ImageView imageView;
    private String TAG = RecognitionFragment.class.getSimpleName();
    private ListView listView;
    private TextView progressText;
    private int imageResultHeight = 0;
    private ImageView imageCompare;
    private OnRecognitionFinished mCallback;
    private ImageView imageRefresh;
    Document lastDocument = null;
    //    File imageFile;
    private boolean uploading;
    private Context mContext;


    @Override
    public void onAttach(Activity activity) {
        try {
            mCallback = (OnRecognitionFinished) activity;
            mContext = activity;
        } catch (ClassCastException castException) {
            throw new ClassCastException(activity.toString() + " must implement OnRecognitionFinished");
        }

        super.onAttach(activity);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.recognition, null);
        imageView = (ImageView) v.findViewById(R.id.imageViewPhoto);
        listView = (ListView) v.findViewById(R.id.listViewPhoto);
        progressText = (TextView) v.findViewById(R.id.textViewProgress);
        imageCompare = (ImageView) v.findViewById(R.id.imageViewCompare);
        imageRefresh = (ImageView) v.findViewById(R.id.imageViewRefresh);
        uploading = false;
//        Bundle args = getArguments();
//        if (args != null) {
//            String filepath = args.getString(IMAGE_PATH);
//            imageFile = getReduceFile(new File(filepath));
//
//            if (!imageFile.exists())
//                mCallback.onRecognitionFailed("File does not exists: " + imageFile.getAbsolutePath());
//        } else {
//            Log.w(TAG, "You should pass an argument");
//        }
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageResultHeight == 0) {
                    imageResultHeight = view.getHeight();
                    view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
                } else {
                    view.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, imageResultHeight));
                    imageResultHeight = 0;
                }
            }
        });
        imageCompare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.setVisibility(View.GONE);
            }
        });


        super.onViewCreated(view, savedInstanceState);
    }


    protected AndroidAutomationClient getNuxeoClient() {
        return NuxeoManager.getInstance().getNuxeoClient();
    }






    @Override
    public void update() {
        runAsyncDataRetrieval();
    }

    @Override
    protected boolean requireAsyncDataRetrieval() {
        return true;
    }

    @Override
    protected void onNuxeoDataRetrievalStarted() {
        imageRefresh.setVisibility(View.VISIBLE);
        Animation myRotateAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
        myRotateAnimation.setRepeatCount(Animation.INFINITE);
        imageRefresh.startAnimation(myRotateAnimation);

        progressText.setVisibility(View.VISIBLE);
        progressText.setText(getResources().getString(R.string.plant_recognition_progress));
        Animation blinkAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.disappear);
        blinkAnimation.setRepeatCount(Animation.INFINITE);
        progressText.setAnimation(blinkAnimation);


        super.onNuxeoDataRetrievalStarted();
    }

    @Override
    protected Object retrieveNuxeoData() throws Exception {

        return "";
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {

        super.onNuxeoDataRetrieved(data);
    }

    @Override
    public void onImageClick(Bitmap bitmap) {
        imageCompare.setVisibility(View.VISIBLE);
        imageCompare.setImageBitmap(bitmap);
    }

    @Override
    public void onConfirmeClicked(final JustVisualResult result) {
        new AsyncTask<Void, Void, Document>() {
            @Override
            protected void onPreExecute() {
                imageRefresh.setVisibility(View.VISIBLE);
                Animation myRotateAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
                myRotateAnimation.setRepeatCount(Animation.INFINITE);
                imageRefresh.startAnimation(myRotateAnimation);


                super.onPreExecute();

            }

            @Override
            protected Document doInBackground(Void... voids) {
                Session session = getNuxeoClient().getSession();
                DocumentManager service = session.getAdapter(DocumentManager.class);
                Document plantDoc = null;
                try {

                    plantDoc = service.getDocument(result.getUuid());
                    PropertyMap props = new PropertyMap();
                    props.set("vendorseed:specie", result.getSpecies());
                    props.set("dc:title", result.getCommonName());
                    service.update(plantDoc, props);
                    NuxeoWorkflowProvider nuxeoWorkflowProvider = new NuxeoWorkflowProvider(getActivity());
                    nuxeoWorkflowProvider.startWorkflowValidation(plantDoc);
                } catch (Exception e) {
                    Log.w(TAG, e.getMessage());
                }
                return plantDoc;
            }

            @Override
            protected void onPostExecute(Document plantDoc) {
                imageRefresh.clearAnimation();
                imageRefresh.setVisibility(View.GONE);

                if (mCallback != null)
                    mCallback.onRecognitionConfirmed(plantDoc);

                super.onPostExecute(plantDoc);
            }
        }.execute();
    }

    public void setData(File imageFile, Map<String, List<JustVisualResult>> listMap) {
        if (imageFile != null) {
            Bitmap b = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            imageView.setImageBitmap(b);
        } else {
            mCallback.onRecognitionFailed("Error converting image, disk space might be too low");
        }

        if (progressText != null) {
            progressText.clearAnimation();
            progressText.setVisibility(View.GONE);
        }

        if (imageRefresh != null) {
            imageRefresh.clearAnimation();
            imageRefresh.setVisibility(View.GONE);
        }

        JustVisualAdapter arrayAdapter = new JustVisualAdapter(getActivity(), listMap);
        arrayAdapter.setOnImageClick(RecognitionFragment.this);
        if (listView != null)
            listView.setAdapter(arrayAdapter);
    }
}
