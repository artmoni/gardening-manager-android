package org.gots.ui.fragment;

import android.app.Activity;
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
import org.gots.seed.provider.nuxeo.NuxeoSeedConverter;
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

    public static final java.lang.String IMAGE_PATH = "org.gots.recognition.path";


    private ImageView imageView;
    private String TAG = RecognitionFragment.class.getSimpleName();
    private ListView listView;
    private TextView progressText;
    private int imageResultHeight = 0;
    private ImageView imageCompare;
    private OnRecognitionFinished mCallback;
    private File imageFile;
    private ImageView imageRefresh;
    private String nuxeoDocId;

    public interface OnRecognitionFinished {
        void onRecognitionSucceed();

        void onRecognitionFailed(String message);
    }

    @Override
    public void onAttach(Activity activity) {
        try {
            mCallback = (OnRecognitionFinished) activity;
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
        Bundle args = getArguments();
        if (args != null) {
            String filepath = args.getString(IMAGE_PATH);
            imageFile = new File(filepath);
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


        File reduceFile = getReduceFile();
        if (reduceFile != null) {
            Bitmap b = BitmapFactory.decodeFile(reduceFile.getAbsolutePath());
            imageView.setImageBitmap(b);
            sendServerAsync(reduceFile);
        } else {
            mCallback.onRecognitionFailed("Error converting image, disk space might be too low");
        }
        super.onViewCreated(view, savedInstanceState);
    }

    public File getReduceFile() {
        FileOutputStream out = null;
        File outFile = null;
        try {
            outFile = new File(getActivity().getCacheDir(), imageFile.getName() + "-400x300");
            out = new FileOutputStream(outFile);

            Bitmap bitmap = FileUtilities.decodeScaledBitmapFromSdCard(imageFile.getAbsolutePath(), 400, 300);
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

    private void sendServerAsync(final File imageFile) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                imageRefresh.setVisibility(View.VISIBLE);
                Animation myRotateAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
                myRotateAnimation.setRepeatCount(Animation.INFINITE);
                imageRefresh.startAnimation(myRotateAnimation);

                progressText.setVisibility(View.VISIBLE);
                progressText.setText(getResources().getString(R.string.plant_recognition_progress));
                Animation blinkAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.disappear);
                blinkAnimation.setRepeatCount(Animation.INFINITE);
                progressText.setAnimation(blinkAnimation);
                super.onPreExecute();

            }

            @Override
            protected String doInBackground(Void... voids) {
                Session session = getNuxeoClient().getSession();
                DocumentManager service = session.getAdapter(DocumentManager.class);
                try {

                    Document rootFolder = service.getDocument("/default-domain/workspaces/justvisual");
                    final Document imageDoc = service.createDocument(rootFolder, "VendorSeed", imageFile.getName());
                    final String serverImageUrl = "http://my.gardening-manager.com/nuxeo/nxfile/default/" + imageDoc.getId() + "/blobholder:0/";
                    nuxeoDocId = imageDoc.getId();
                    NuxeoUtils.uploadBlob(session, imageDoc, imageFile, new NuxeoUtils.OnBlobUpload() {
                        @Override
                        public void onUploadSuccess(Serializable data) {
                            processJustVisual(serverImageUrl);

                        }

                        @Override
                        public void onUploadError(String message) {
                            Log.w(TAG, "Upload image on server has failed: " + message);
                        }
                    });
                } catch (Exception e) {
                    Log.w(TAG, e.getMessage());
                    return e.getMessage();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String errormessage) {
                if (errormessage != null)
                    progressText.setText(errormessage);
                super.onPostExecute(errormessage);
            }
        }.execute();
    }

    private void processJustVisual(final String url) {
        new AsyncTask<Void, Void, Map<String, List<JustVisualResult>>>() {

            @Override
            protected Map<String, List<JustVisualResult>> doInBackground(Void... voids) {
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
                    progressText.setText("Try later, recognition server is not responding");
                }
                return species;
            }

            @Override
            protected void onPostExecute(Map<String, List<JustVisualResult>> results) {
                if (isAdded()) {
                    JustVisualAdapter arrayAdapter = new JustVisualAdapter(getActivity(), results);
                    arrayAdapter.setOnImageClick(RecognitionFragment.this);
                    listView.setAdapter(arrayAdapter);

                    progressText.clearAnimation();
                    progressText.setVisibility(View.GONE);

                    imageRefresh.clearAnimation();
                    imageRefresh.setVisibility(View.GONE);

                    mCallback.onRecognitionSucceed();
                }
                super.onPostExecute(results);
            }
        }.execute();
    }

    private Map<String, List<JustVisualResult>> parseJSON(String responseString) {
//        List<String> species = new ArrayList<>();
        Map<String, List<JustVisualResult>> species = new HashMap<>();
        try {
            JSONObject json = new JSONObject(responseString);
            JSONArray images = json.getJSONArray("images");
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ").create();
            for (int i = 0; i < images.length(); i++) {
                try {
                    JSONObject image = images.getJSONObject(i);
                    JustVisualResult result = gson.fromJson(image.toString(), JustVisualResult.class);
                    result.setUuid(nuxeoDocId);
                    if (species.get(result.getPlantNames()) == null)
                        species.put(result.getPlantNames(), new ArrayList<JustVisualResult>());
                    species.get(result.getPlantNames()).add(result);
//                    if (species.size() == 0 || (species.size() > 0 && !species.get(species.size() - 1).equals(result.getPlantNames())))
//                        species.add(result.getPlantNames());
                    Log.d(TAG, result.toString());

                } catch (JSONException jsonException) {
                    Log.w(TAG, jsonException.getMessage());
                }

            }
        } catch (JSONException e) {

            e.printStackTrace();
        }
        return species;
    }

    @Override
    public void update() {

    }

    @Override
    protected boolean requireAsyncDataRetrieval() {
        return true;
    }


    @Override
    public void onImageClick(Bitmap bitmap) {
        imageCompare.setVisibility(View.VISIBLE);
        imageCompare.setImageBitmap(bitmap);
    }

    @Override
    public void onConfirmeClicked(final JustVisualResult result) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                imageRefresh.setVisibility(View.VISIBLE);
                Animation myRotateAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
                myRotateAnimation.setRepeatCount(Animation.INFINITE);
                imageRefresh.startAnimation(myRotateAnimation);


                super.onPreExecute();

            }

            @Override
            protected String doInBackground(Void... voids) {
                Session session = getNuxeoClient().getSession();
                DocumentManager service = session.getAdapter(DocumentManager.class);
                try {

                    Document plantDoc = service.getDocument(result.getUuid());
                    PropertyMap props = new PropertyMap();
                    props.set("vendorseed:specie", result.getSpecies());
                    props.set("dc:title", result.getCommonName());
                    service.update(plantDoc, props);
//                    NuxeoWorkflowProvider nuxeoWorkflowProvider = new NuxeoWorkflowProvider(getActivity());
//                    nuxeoWorkflowProvider.startWorkflowValidation(plantDoc);
                } catch (Exception e) {
                    Log.w(TAG, e.getMessage());
                    return e.getMessage();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String errormessage) {
                if (errormessage != null)
                    progressText.setText(errormessage);
                imageRefresh.clearAnimation();
                imageRefresh.setVisibility(View.GONE);
                super.onPostExecute(errormessage);
            }
        }.execute();
    }

}
