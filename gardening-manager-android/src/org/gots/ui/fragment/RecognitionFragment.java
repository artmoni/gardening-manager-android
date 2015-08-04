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
public class RecognitionFragment extends BaseGotsFragment implements JustVisualAdapter.OnAdapterClickListener, NuxeoUtils.OnBlobUpload {

    public static final String IMAGE_PATH = "org.gots.recognition.path";
    private static final String RECOGNITION_SUCCESS = "org.gots.recognition.success";
    private static final String RECOGNITION_FAILED = "org.gots.recognition.failed";


    private ImageView imageView;
    private String TAG = RecognitionFragment.class.getSimpleName();
    private ListView listView;
    private TextView progressText;
    private int imageResultHeight = 0;
    private ImageView imageCompare;
    private OnRecognitionFinished mCallback;
    private ImageView imageRefresh;
    Document lastDocument = null;
    File imageFile;
    private boolean uploading;
    private Context mContext;
    private JustVisualAdapter arrayAdapter;

    private Map<String, List<JustVisualResult>> listMap;
    private BroadcastReceiver mBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isAdded()) {
                if (RECOGNITION_SUCCESS.equals(intent.getAction())) {
                    progressText.clearAnimation();
                    progressText.setVisibility(View.GONE);

                    arrayAdapter = new JustVisualAdapter(getActivity(), listMap);
                    arrayAdapter.setOnImageClick(RecognitionFragment.this);
                    listView.setAdapter(arrayAdapter);
                } else if (RECOGNITION_FAILED.equals(intent.getAction())) {
                    progressText.clearAnimation();
                    progressText.setText("Recognition is not working on this picture, try another one");
                    progressText.setVisibility(View.VISIBLE);
                }
                imageRefresh.clearAnimation();
                imageRefresh.setVisibility(View.GONE);
            }
        }
    };

    @Override
    public void onUploadSuccess(Serializable data) {
        final String serverImageUrl = "http://my.gardening-manager.com/nuxeo/nxfile/default/" + lastDocument.getId() + "/blobholder:0/";
        listMap = processJustVisual(serverImageUrl);
        if (listMap == null) {
            listMap = new HashMap<>();
            mContext.sendBroadcast(new Intent(RECOGNITION_FAILED));
            if (mCallback != null) {
                mCallback.onRecognitionFailed("No result found, try another picture");
            }
            return;
        }

        Log.d(TAG, "onUploadSuccess listMap size=" + listMap.size());
        if (mCallback != null)
            mCallback.onRecognitionSucceed();
        mContext.sendBroadcast(new Intent(RECOGNITION_SUCCESS));
        uploading = false;

    }

    @Override
    public void onUploadFailed(String message) {
        if (mCallback != null) {
            mCallback.onRecognitionFailed("Upload image on server has failed: " + message);
        }
        mContext.sendBroadcast(new Intent(RECOGNITION_FAILED));
        uploading = false;
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
        getActivity().registerReceiver(mBroadcast, new IntentFilter(RECOGNITION_SUCCESS));

        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        getActivity().unregisterReceiver(mBroadcast);
        super.onDetach();
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
        Bundle args = getArguments();
        if (args != null) {
            String filepath = args.getString(IMAGE_PATH);
            imageFile = getReduceFile(new File(filepath));

            if (!imageFile.exists())
                mCallback.onRecognitionFailed("File does not exists: " + imageFile.getAbsolutePath());
        } else {
            Log.w(TAG, "You should pass an argument");
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

        uploadImage(imageFile);

        super.onViewCreated(view, savedInstanceState);
    }

    public File getReduceFile(File originalFile) {
        FileOutputStream out = null;
        File outFile = null;
        try {
            outFile = new File(getActivity().getCacheDir(), originalFile.getName() + "-400x300");
            out = new FileOutputStream(outFile);

            Bitmap bitmap = FileUtilities.decodeScaledBitmapFromSdCard(originalFile.getAbsolutePath(), 400, 300);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored


        } catch (Exception e) {
            Log.e(TAG, "setSearchImage " + e.getMessage());
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "setSearchImage finally " + e.getMessage());
            }
        }
        return outFile;
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
                result.setUuid(lastDocument.getId());
                if (species.get(result.getPlantNames()) == null)
                    species.put(result.getPlantNames(), new ArrayList<JustVisualResult>());
                species.get(result.getPlantNames()).add(result);
                Log.d(TAG, result.toString());

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
        imageRefresh.setVisibility(View.VISIBLE);
        Animation myRotateAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
        myRotateAnimation.setRepeatCount(Animation.INFINITE);
        imageRefresh.startAnimation(myRotateAnimation);

        progressText.setVisibility(View.VISIBLE);
        progressText.setText(getResources().getString(R.string.plant_recognition_progress));
        Animation blinkAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.disappear);
        blinkAnimation.setRepeatCount(Animation.INFINITE);
        progressText.setAnimation(blinkAnimation);

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


        return "";
    }

    @Override
    protected void onNuxeoDataRetrieved(Object data) {
        if (listMap != null) {
            JustVisualAdapter arrayAdapter = new JustVisualAdapter(getActivity(), listMap);
            arrayAdapter.setOnImageClick(RecognitionFragment.this);
            listView.setAdapter(arrayAdapter);

            progressText.clearAnimation();
            progressText.setVisibility(View.GONE);

            imageRefresh.clearAnimation();
            imageRefresh.setVisibility(View.GONE);
        }

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
                    props.set("vendorseed:url", result.getPageUrl());

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

    public void uploadImage(final File imageFile) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                if (lastDocument == null && !uploading) {
                    try {
                        Session session = getNuxeoClient().getSession();
                        DocumentManager service = session.getAdapter(DocumentManager.class);

                        Document rootFolder = service.getDocument("/default-domain/workspaces/justvisual");
                        final Document imageDoc = service.createDocument(rootFolder, "VendorSeed", imageFile.getName());
                        lastDocument = imageDoc;
                        if (imageDoc != null) {
                            uploading = true;
                            NuxeoUtils nuxeoUtils = new NuxeoUtils();
                            nuxeoUtils.uploadBlob(session, imageDoc, imageFile, RecognitionFragment.this);
                            Log.d(TAG, "uploadImage " + imageDoc.getTitle() + " - " + imageFile.getAbsolutePath());
                        }
                    } catch (Exception e) {
                        Log.w(TAG, "uploadImage: " + e.getMessage());
                        return e.getMessage();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(String errorMessage) {
                if (errorMessage != null) {
                    progressText.setVisibility(View.VISIBLE);
                    progressText.setText(errorMessage);
                    imageRefresh.clearAnimation();
                    imageRefresh.setVisibility(View.GONE);
                }
                super.onPostExecute(errorMessage);
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
