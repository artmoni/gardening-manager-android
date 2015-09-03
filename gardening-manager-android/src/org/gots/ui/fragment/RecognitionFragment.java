package org.gots.ui.fragment;

import android.app.Activity;
import android.content.Context;
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
import android.widget.LinearLayout;
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
import org.gots.nuxeo.NuxeoManager;
import org.gots.nuxeo.NuxeoWorkflowProvider;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by sfleury on 13/07/15.
 */
public class RecognitionFragment extends BaseGotsFragment implements JustVisualAdapter.OnAdapterClickListener {

    public static final String DOCUMENT_ID = "org.gots.recognition.docid";
    private static final String RECOGNITION_SUCCESS = "org.gots.recognition.success";
    private static final String RECOGNITION_FAILED = "org.gots.recognition.failed";
    public static final String IMAGE_PATH = "org.gots.recognition.path";


    private ImageView imageView;
    private String TAG = RecognitionFragment.class.getSimpleName();
    private ListView listView;
    private TextView progressText;
    private int imageResultHeight = 0;
    private ImageView imageCompare;
    private OnRecognitionFinished mCallback;
    private ImageView imageRefresh;
    //    File imageFile;
//    private boolean uploading;
    private Context mContext;
    private JustVisualAdapter arrayAdapter;
    private LinearLayout layoutNotification;


    //    private BroadcastReceiver mBroadcast = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (isAdded()) {
//                if (RECOGNITION_SUCCESS.equals(intent.getAction())) {
//                    hideNotification();
//
//                    arrayAdapter = new JustVisualAdapter(getActivity(), listMap);
//                    arrayAdapter.setOnImageClick(RecognitionFragment.this);
//                    listView.setAdapter(arrayAdapter);
//                } else if (RECOGNITION_FAILED.equals(intent.getAction())) {
//                    String errorMessage = "Recognition is not working on this picture, try another one";
//                    showNotification(errorMessage, false);
//                }
//
//            }
//        }
//    };

    private String docId;
    private File imageFile;
    private Map<String, List<JustVisualResult>> listMap;

    public void hideNotification() {
        progressText.clearAnimation();
        imageRefresh.clearAnimation();
        layoutNotification.setVisibility(View.GONE);
    }

    public void showNotification(String message, boolean animation) {
        progressText.setText(message);
        if (animation) {
            Animation myRotateAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
            myRotateAnimation.setRepeatCount(Animation.INFINITE);
            imageRefresh.startAnimation(myRotateAnimation);

            Animation blinkAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.disappear);
            blinkAnimation.setRepeatCount(Animation.INFINITE);
            progressText.setAnimation(blinkAnimation);
        } else {
            imageRefresh.clearAnimation();
            progressText.clearAnimation();
        }

        layoutNotification.setVisibility(View.VISIBLE);
    }

    public interface OnRecognitionFinished {
        void onRecognitionSucceed();

        void onRecognitionFailed(String message);

        void onRecognitionConfirmed(Document plantDoc);
    }

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
        layoutNotification = (LinearLayout) v.findViewById(R.id.layoutNotification);
//        uploading = false;
        Bundle args = getArguments();
        if (args != null) {
            docId = args.getString(DOCUMENT_ID);
            imageFile = new File(args.getString(IMAGE_PATH));
        } else {
            Log.w(TAG, "You should have both args "+DOCUMENT_ID+" and "+IMAGE_PATH);
        }
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

    private Map<String, List<JustVisualResult>> processJustVisual(final String url) {
        ImageRecognition imageRecognition = new ImageRecognition();
        Map<String, List<JustVisualResult>> species = null;

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = null;
            response = httpclient.execute(new HttpGet(imageRecognition.getURL(url)));
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                String responseString = out.toString();

                species = parseJSON(responseString);
//                        Log.d(TAG, responseString);
                out.close();
                //..more logic
            } else {
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }

        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        } catch (JSONException e) {
            if (mCallback != null)
                mCallback.onRecognitionFailed("parseJSON: " + e.getMessage());

        }
        return species;
//    priv
    }

    private Map<String, List<JustVisualResult>> parseJSON(String responseString) throws JSONException {
        Map<String, List<JustVisualResult>> species = new HashMap<>();
        JSONObject json = new JSONObject(responseString);
        JSONArray images = json.getJSONArray("images");
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
        for (int i = 0; i < images.length(); i++) {
            try {
                JSONObject image = images.getJSONObject(i);
                JustVisualResult result = gson.fromJson(image.toString(), JustVisualResult.class);
                result.setUuid(docId);
                if (species.get(result.getPlantNames()) == null)
                    species.put(result.getPlantNames(), new ArrayList<JustVisualResult>());
                species.get(result.getPlantNames()).add(result);

            } catch (JSONException jsonException) {
                Log.w(TAG, jsonException.getMessage() + ": " + responseString);
            }

        }
        return species;
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
        showNotification(getResources().getString(R.string.plant_recognition_progress), true);

        if (imageFile != null) {
            Bitmap b = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
            imageView.setImageBitmap(b);
        } else {
            mCallback.onRecognitionFailed("Error converting image, disk space might be too low");
        }
        super.onNuxeoDataRetrievalStarted();
    }

    @Override
    protected Object retrieveNuxeoData() throws Exception {

        final String serverImageUrl = "http://my.gardening-manager.com/nuxeo/nxfile/default/" + docId + "/blobholder:0/";

//        mContext.sendBroadcast(new Intent(RECOGNITION_SUCCESS));
        if (listMap == null) {

            return processJustVisual(serverImageUrl);
        } else
            return listMap;
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {
        listMap = (Map<String, List<JustVisualResult>>) data;

        JustVisualAdapter arrayAdapter = new JustVisualAdapter(getActivity(), listMap);
        arrayAdapter.setOnImageClick(RecognitionFragment.this);
        listView.setAdapter(arrayAdapter);

        hideNotification();
        Log.d(TAG, "onUploadSuccess listMap size=" + listMap.size());
        if (mCallback != null)
            mCallback.onRecognitionSucceed();

        super.onNuxeoDataRetrieved(data);
    }

    @Override
    protected void onNuxeoDataRetrieveFailed() {
        listMap = new HashMap<>();

        showNotification("No result found, try another picture", false);
        if (mCallback != null) {
            mCallback.onRecognitionFailed("No result found, try another picture");
        }
        super.onNuxeoDataRetrieveFailed();
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
                showNotification("Confirmation in progress...", true);
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
                    props.set("vendorseed:variety", result.getCommonName());
                    props.set("vendorseed:specie", result.getSpecies());
                    props.set("vendorseed:language", Locale.getDefault().getCountry().toLowerCase());
                    props.set("dc:title", result.getCommonName());
                    props.set("vendorseed:url", result.getPageUrl());

                    service.update(plantDoc, props);
                    NuxeoWorkflowProvider nuxeoWorkflowProvider = new NuxeoWorkflowProvider(getActivity());
                    nuxeoWorkflowProvider.startWorkflowValidation(plantDoc);
                } catch (Exception e) {
                    Log.w(TAG, e.getMessage());
                    return null;
                }
                return plantDoc;
            }

            @Override
            protected void onPostExecute(Document plantDoc) {
                if (plantDoc != null)
                    hideNotification();
                else
                    showNotification("Confirmation problem, please try later.", false);

                if (mCallback != null)
                    mCallback.onRecognitionConfirmed(plantDoc);

                super.onPostExecute(plantDoc);
            }
        }.execute();
    }


    @Override
    public void onPause() {
        if (arrayAdapter != null)
            arrayAdapter.stopProcessing();
        super.onPause();
    }
}
