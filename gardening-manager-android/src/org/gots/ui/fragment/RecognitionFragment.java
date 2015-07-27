package org.gots.ui.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import org.gots.preferences.GotsPreferences;
import org.gots.utils.FileUtilities;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.nuxeo.android.repository.DocumentManager;
import org.nuxeo.ecm.automation.client.android.AndroidAutomationClient;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;

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
public class RecognitionFragment extends BaseGotsFragment implements JustVisualAdapter.OnImageClick {

    private ImageView imageView;
    private String TAG = RecognitionFragment.class.getSimpleName();
    private ListView listView;
    private String imageUrl;
    private TextView progressText;
    private int imageResultHeight = 0;
    private ImageView imageCompare;
    private OnRecognitionFinished mCallback;

    public interface OnRecognitionFinished {
        void onRecognitionSucceed();
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

    private void sendServerAsync(final File imageFile) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected void onPreExecute() {
                progressText.setVisibility(View.VISIBLE);
                progressText.setText(getResources().getString(R.string.plant_recognition_progress));
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Void... voids) {
                Session session = getNuxeoClient().getSession();
                DocumentManager service = session.getAdapter(DocumentManager.class);
                try {

                    Document guestDoc = service.getDocument("/default-domain/UserWorkspaces/Guest/justvisual");
                    Document imageDoc = service.createDocument(guestDoc, "File", imageFile.getName());
                    Log.d(TAG, "new imageDoc " + imageDoc);
                    final String serverImageUrl = "http://my.gardening-manager.com/nuxeo/nxfile/default/" + imageDoc.getId() + "/blobholder:0/";

                    NuxeoUtils.uploadBlob(session, imageDoc, imageFile, new NuxeoUtils.OnBlobUpload() {
                        @Override
                        public void onUploadSuccess(Serializable data) {
                            imageUrl = serverImageUrl;
//                            progressText.setText("Recognition in progress ...");
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
                        Log.d(TAG, responseString);
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
                    progressText.setVisibility(View.GONE);
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

    public void setSearchImage(String picturePath) {
        Bitmap bitmap = FileUtilities.decodeScaledBitmapFromSdCard(picturePath, 400, 300);


        FileOutputStream out = null;
        try {
            File f = new File(picturePath);
            File outFile = new File(getActivity().getCacheDir(), f.getName() + "-400x300");
            out = new FileOutputStream(outFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
            imageView.setImageBitmap(bitmap);
            sendServerAsync(outFile);

        } catch (Exception e) {
            Log.e(TAG, "setSearchImage " + e.getMessage());
//            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
//                e.printStackTrace();
                Log.e(TAG, "setSearchImage finally " + e.getMessage());
            }
        }
    }
}
