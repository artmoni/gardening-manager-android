package org.gots.ui.fragment;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.gots.R;
import org.gots.justvisual.ImageRecognition;
import org.gots.nuxeo.NuxeoManager;
import org.gots.nuxeo.NuxeoUtils;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sfleury on 13/07/15.
 */
public class LikeThatFragment extends BaseGotsFragment {

    private static final int REQUEST_TAKE_PHOTO = 1;
    private Button buttonPhoto;
    private ImageView imageView;
    private String TAG = LikeThatFragment.class.getSimpleName();
    private ListView listView;
    private String imageUrl;
    ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.recognition, null);
        buttonPhoto = (Button) v.findViewById(R.id.buttonPhoto);
        imageView = (ImageView) v.findViewById(R.id.imageViewPhoto);
        listView = (ListView) v.findViewById(R.id.listViewPhoto);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        buttonPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, getActivity().getCacheDir() + "/_tmp");
                startActivityForResult(cameraIntent, REQUEST_TAKE_PHOTO);
            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imageUrl != null)
                    processJustVisual(imageUrl);
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);

            Bitmap bitmap = FileUtilities.decodeScaledBitmapFromSdCard(picturePath, 100, 100);
            imageView.setImageBitmap(bitmap);
            sendServerAsync(picturePath);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    protected AndroidAutomationClient getNuxeoClient() {
        return NuxeoManager.getInstance().getNuxeoClient();
    }

    private void sendServerAsync(final String filePath) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                Session session = getNuxeoClient().getSession();
                DocumentManager service = session.getAdapter(DocumentManager.class);
                try {
                    File f = new File(filePath);

                    Document guestDoc = service.getDocument("/default-domain/UserWorkspaces/Guest/justvisual");
                    Document imageDoc = service.createDocument(guestDoc, "File", f.getName());
                    Log.d(TAG, "new imageDoc " + imageDoc);
                    final String serverImageUrl = "http://my.gardening-manager.com/nuxeo/nxfile/default/" + imageDoc.getId() + "/blobholder:0/";

                    NuxeoUtils.uploadBlob(session, imageDoc, f, new NuxeoUtils.OnBlobUpload() {
                        @Override
                        public void onUploadSuccess() {
                            //my.gardening-manager.com/nuxeo/nxfile/default/cfa401b8-5e38-42eb-9cc2-580f680f7a8d/blobholder:0/
                            imageUrl = serverImageUrl;
                            processJustVisual(serverImageUrl);
                        }

                        @Override
                        public void onUploadError() {

                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }


        }.execute();
    }

    private void processJustVisual(final String url) {
        new AsyncTask<Void, Void, List<String>>() {
            @Override
            protected List<String> doInBackground(Void... voids) {
                ImageRecognition imageRecognition = new ImageRecognition();
                List<String> species = new ArrayList<>();

                try {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpResponse response = null;
                    response = httpclient.execute(new HttpGet(imageRecognition.getURL(url)));
                    StatusLine statusLine = response.getStatusLine();
                    if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        response.getEntity().writeTo(out);
                        String responseString = out.toString();

                        species.addAll(parseJSON(responseString));
                        Log.d(TAG, responseString);
                        out.close();
                        //..more logic
                    } else {
                        //Closes the connection.
                        response.getEntity().getContent().close();
                        throw new IOException(statusLine.getReasonPhrase());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return species;
            }

            @Override
            protected void onPostExecute(List<String> strings) {

                ListAdapter arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, strings);
                listView.setAdapter(arrayAdapter);
                super.onPostExecute(strings);
            }
        }.execute();
    }

    private List<String> parseJSON(String responseString) {
        List<String> species = new ArrayList<>();
        try {
            JSONObject json = new JSONObject(responseString);
            JSONArray images = json.getJSONArray("images");
            for (int i = 0; i < images.length(); i++) {
                try {
                    JSONObject image = images.getJSONObject(i);
                    String id = image.getString("id");
                    String url = image.getString("imageUrl");
                    String title = image.getString("title");
//                String description = image.getString("description");
                    String pageUrl = image.getString("pageUrl");
                    String plantNames = image.getString("plantNames");
                    if (species.size() == 0 || (species.size() > 0 &&!species.get(species.size() - 1).equals(plantNames)))
                        species.add(plantNames);
                    Log.d(TAG, plantNames + " - " + id + " - " + url + " - " + title);

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


}
