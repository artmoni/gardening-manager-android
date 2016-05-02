package org.gots.ui;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.nuxeo.android.context.NuxeoContext;
import org.nuxeo.ecm.automation.client.jaxrs.impl.NotAvailableOffline;

/**
 * Created by sfleury on 02/05/16.
 */
public abstract class GotsCoreActivity extends AppCompatActivity {

    protected boolean loadingInProgress = false;

    protected NuxeoContext getNuxeoContext() {
        return NuxeoContext.get(getApplicationContext());
    }

    protected abstract boolean requireAsyncDataRetrieval();

    @Override
    protected void onResume() {
        super.onResume();
        if (requireAsyncDataRetrieval()) {
            runAsyncDataRetrieval();
        }
    }

    protected void runAsyncDataRetrieval() {
        new NuxeoAsyncTask().execute((Void[]) null);
    }

    /**
     * Should be overridden to include Async process.
     * Returning a null result will cancel the callback
     */
    protected Object retrieveNuxeoData() throws Exception {
        return null;
    }

    /**
     * Called on the UI Thread to notify that async process is started
     * This may be used to display a waiting message
     */
    protected void onNuxeoDataRetrievalStarted() {
    }

    /**
     * Called on the UI Thread when the async process is completed.
     * The input object will be the output of the retrieveNuxeoData
     */
    protected void onNuxeoDataRetrieved(Object data) {

    }

    /**
     * Called on the UI Thread when the async process is completed in error.
     */
    protected void onNuxeoDataRetrieveFailed() {

    }

    protected class NuxeoAsyncTask extends AsyncTask<Void, Integer, Object> {

        @Override
        protected void onPreExecute() {
            loadingInProgress = true;
            onNuxeoDataRetrievalStarted();
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Void... arg0) {
            try {
                Object result = retrieveNuxeoData();
                return result;
            } catch (NotAvailableOffline naoe) {
                GotsCoreActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(GotsCoreActivity.this,
                                "This screen can bot be displayed offline",
                                Toast.LENGTH_LONG).show();
                    }
                });
                return null;
            } catch (Exception e) {
                Log.e("NuxeoAsyncTask",
                        "Error while executing async Nuxeo task in activity", e);
                try {
                    cancel(true);
                } catch (Throwable t) {
                    // NOP
                }
                return null;
            }
        }

        @Override
        protected void onPostExecute(Object result) {
            loadingInProgress = false;
            if (result != null) {
                onNuxeoDataRetrieved(result);
            } else {
                onNuxeoDataRetrieveFailed();
            }
        }
    }
}
