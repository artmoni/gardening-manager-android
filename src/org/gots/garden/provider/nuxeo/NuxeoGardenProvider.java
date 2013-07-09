package org.gots.garden.provider.nuxeo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.gots.garden.GardenInterface;
import org.gots.garden.provider.local.LocalGardenProvider;
import org.gots.preferences.GotsPreferences;
import org.gots.ui.ProfileActivity;
import org.nuxeo.android.config.NuxeoServerConfig;
import org.nuxeo.android.context.NuxeoContext;
import org.nuxeo.android.context.NuxeoContextFactory;
import org.nuxeo.android.repository.DocumentManager;
import org.nuxeo.ecm.automation.client.android.AndroidAutomationClient;
import org.nuxeo.ecm.automation.client.android.CachedSession;
import org.nuxeo.ecm.automation.client.jaxrs.Constants;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.impl.NotAvailableOffline;
import org.nuxeo.ecm.automation.client.jaxrs.model.DocRef;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;
import org.nuxeo.ecm.automation.client.jaxrs.model.IdRef;
import org.nuxeo.ecm.automation.client.jaxrs.model.PropertyMap;
import org.nuxeo.ecm.automation.client.jaxrs.spi.DefaultSession;
import org.nuxeo.ecm.automation.client.jaxrs.spi.auth.TokenRequestInterceptor;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

/**
 * See <a href="http://doc.nuxeo.com/x/mQAz">Nuxeo documentation on Content
 * Automation</a>
 */
public class NuxeoGardenProvider extends LocalGardenProvider {

    private static final String TAG = "NuxeoGardenProvider";

    String myToken;

    String myLogin;

    String myDeviceId;

    String myApp;

    // private static final long TIMEOUT = 10;

    protected NuxeoServerConfig nxConfig;

    protected NuxeoContext nuxeoContext;

    protected AndroidAutomationClient nuxeoClient;

    /**
     * Android 11+: raises a {@link android.os.NetworkOnMainThreadException} if
     * called from the main thread and tries to
     * perform a network call (Nuxeo server)
     */
    public NuxeoGardenProvider(Context context) throws NotAvailableOffline {
        super(context);
        myToken = gotsPrefs.getToken();
        myLogin = gotsPrefs.getNuxeoLogin();
        myDeviceId = gotsPrefs.getDeviceId();
        myApp = gotsPrefs.getGardeningManagerAppname();
        nuxeoContext = NuxeoContextFactory.getNuxeoContext(context);
        nxConfig = nuxeoContext.getServerConfig();
        nxConfig.setLogin(myLogin);
        nxConfig.setPassword(gotsPrefs.getNuxeoPassword());
        nxConfig.setToken(myToken);
        nxConfig.setCacheKey(NuxeoServerConfig.PREF_SERVER_TOKEN);
        Uri nxAutomationURI = Uri.parse(Uri.encode(GotsPreferences.getGardeningManagerServerURI()));
        Log.d(TAG, "setServerBaseUrl: " + nxAutomationURI.toString());
        nxConfig.setServerBaseUrl(nxAutomationURI);
        nuxeoClient = getNuxeoClient();
        // Check connectivity with Nuxeo
        new AsyncTask<Void, Integer, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                getNuxeoSession();
                return null;
            }
        }.execute();
    }

    /**
     * @return {@link CachedSession} if available, else a {@link DefaultSession}
     * @throws NotAvailableOffline If no data in cache and the required online
     *             session fails
     */
    protected Session getNuxeoSession() throws NotAvailableOffline {
        return nuxeoContext.getSession();
    }

    protected AndroidAutomationClient getNuxeoClient() {
        if (nuxeoClient == null) {
            nuxeoClient = nuxeoContext.getNuxeoClient();
            nuxeoClient.setRequestInterceptor(new TokenRequestInterceptor(
                    myApp, myToken, myLogin, myDeviceId));
        }
        return nuxeoClient;
    }

    @Override
    public GardenInterface createGarden(GardenInterface garden) {
        return createRemoteGarden(createLocalGarden(garden));
    }

    protected GardenInterface createLocalGarden(GardenInterface garden) {
        return super.createGarden(garden);
    }

    protected GardenInterface createRemoteGarden(
            final GardenInterface localGarden) {
        new AsyncTask<GardenInterface, Integer, Document>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                // TODO show loading... icon
            }

            @Override
            protected Document doInBackground(GardenInterface... params) {
                Log.i(TAG, "createRemoteGarden " + localGarden);

                GardenInterface currentGarden = params[0];
                Session session = getNuxeoClient().getSession();
                PropertyMap props = new PropertyMap();
                props.set("dc:title", currentGarden.getLocality());
                DocumentManager service = session.getAdapter(DocumentManager.class);
                // TODO use service.getUserHome()
                // DocRef wsRef = new DocRef("/default-domain/UserWorkspaces/" +
                // myLogin);

                try {
                    Document home = service.getUserHome();

                    Document createDocument = service.createDocument(home,
                            "Garden", currentGarden.getLocality(), props);
                    return createDocument;

                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                    cancel(false);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Document newGarden) {
                if (newGarden != null) {
                    super.onPostExecute(newGarden);
                    localGarden.setUUID(newGarden.getId());
                    updateLocalGarden(localGarden);
                }
                // TODO show ok icon
            }

            @Override
            protected void onCancelled(Document result) {
                // TODO show error icon
            };
        }.execute(localGarden);
        return localGarden;
    }

    @Override
    public List<GardenInterface> getMyGardens() {
        return getMyRemoteGardens(getMyLocalGardens(), true);
    }

    protected List<GardenInterface> getMyLocalGardens() {
        return super.getMyGardens();
    }

    /**
     * Returns either the list of remote gardens or the full list of gardens
     * with synchronization between local and
     * remote
     * 
     * @param myLocalGardens can be null if not syncWithLocalGardens
     * @param syncWithLocalGardens whether to sync or not local and remote
     *            gardens
     * @return
     */
    protected List<GardenInterface> getMyRemoteGardens(
            final List<GardenInterface> myLocalGardens,
            final boolean syncWithLocalGardens) {
        final List<GardenInterface> myGardens = new ArrayList<GardenInterface>();
        AsyncTask<Void, Integer, List<GardenInterface>> task = new AsyncTask<Void, Integer, List<GardenInterface>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                // TODO show loading... icon
            }

            @Override
            protected List<GardenInterface> doInBackground(Void... none) {
                List<GardenInterface> remoteGardens = new ArrayList<GardenInterface>();
                Session session = getNuxeoClient().getSession();
                DocumentManager service = session.getAdapter(DocumentManager.class);
                // TODO use service.getUserHome()
                Documents gardensWorkspaces = null;

                try {
                    // gardensWorkspaces = service.getChildren(wsRef);
                    gardensWorkspaces = service.query("SELECT * FROM Garden WHERE ecm:currentLifeCycleState <> 'deleted' ORDER BY dc:modified DESC");
                    for (Iterator<Document> iterator = gardensWorkspaces.iterator(); iterator.hasNext();) {
                        Document gardenWorkspace = iterator.next();
                        Log.d(TAG, "Document=" + gardenWorkspace.getId());
                        GardenInterface garden = NuxeoGardenConvertor.convert(gardenWorkspace);
                        remoteGardens.add(garden);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                    // TODO check workaround need and consequences
                    // remoteGardens = getMyLocalGardens();
                    cancel(false);
                }
                return remoteGardens;
            }

            @Override
            protected void onPostExecute(List<GardenInterface> remoteGardens) {
                super.onPostExecute(remoteGardens);
                if (!syncWithLocalGardens) {
                    return;
                }

                // Synchronize remote garden with local gardens
                for (GardenInterface remoteGarden : remoteGardens) {
                    boolean found = false;
                    for (GardenInterface localGarden : myLocalGardens) {
                        if (remoteGarden.getUUID() != null
                                && remoteGarden.getUUID().equals(
                                        localGarden.getUUID())) {
                            found = true;
                            break;
                        }
                    }
                    if (found) { // local and remote => update local
                        // TODO check if remote can be out of date
                        // syncGardens(localGarden,remoteGarden);
                        myGardens.add(updateLocalGarden(remoteGarden));
                    } else { // remote only => create local
                        myGardens.add(createLocalGarden(remoteGarden));
                    }
                }

                // Create remote garden when not exist remotly and remote local
                // garden if no more reference online
                for (GardenInterface localGarden : myLocalGardens) {
                    if (localGarden.getUUID() == null) { // local only without
                                                         // UUID => create
                                                         // remote

                        GardenInterface createRemoteGarden = createRemoteGarden(localGarden);

                        myGardens.add(createRemoteGarden);
                    } else {
                        boolean found = false;
                        for (GardenInterface remoteGarden : remoteGardens) {
                            if (remoteGarden.getUUID() != null
                                    && remoteGarden.getUUID().equals(
                                            localGarden.getUUID())) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) { // local only with UUID -> delete local
                            removeLocalGarden(localGarden);
                        }
                    }
                }
                // TODO show ok icon
            };

            @Override
            protected void onCancelled(List<GardenInterface> remoteGardens) {
                // TODO show error icon
            }

        }.execute();
        if (!syncWithLocalGardens) {
            try {
                return task.get();
            } catch (InterruptedException e) {
                Log.e(TAG, e.getMessage());
            } catch (ExecutionException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        return myGardens;
    }

    @Override
    public int removeGarden(GardenInterface garden) {
        removeRemoteGarden(garden);
        return removeLocalGarden(garden);
    }

    protected int removeLocalGarden(GardenInterface garden) {
        Log.i(TAG, "removeLocalGarden " + garden);

        return super.removeGarden(garden);
    }

    protected void removeRemoteGarden(final GardenInterface garden) {
        new AsyncTask<Void, Integer, Void>() {
            Dialog dialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = ProgressDialog.show(mContext, "",
                        "Removing. Please wait...", true);
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
            }

            @Override
            protected Void doInBackground(Void... none) {
                Log.i(TAG, "removeRemoteGarden " + garden);

                Session session = getNuxeoClient().getSession();
                DocumentManager service = session.getAdapter(DocumentManager.class);
                try {
                    service.remove(new IdRef(garden.getUUID()));
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                    cancel(false);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void none) {
                super.onPostExecute(none);
                if (dialog.isShowing())
                    dialog.dismiss();
            }

            @Override
            protected void onCancelled(Void none) {
                // TODO show error icon
            };
        }.execute();
    }

    @Override
    public GardenInterface updateGarden(GardenInterface garden) {
        return updateRemoteGarden(updateLocalGarden(garden));
    }

    protected GardenInterface updateLocalGarden(GardenInterface garden) {
        return super.updateGarden(garden);
    }

    protected GardenInterface updateRemoteGarden(final GardenInterface garden) {
        new AsyncTask<Void, Integer, Document>() {
            Dialog dialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = ProgressDialog.show(mContext, "",
                        "Updating. Please wait...", true);
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
            }

            @Override
            protected Document doInBackground(Void... none) {
                Log.i(TAG, "updateRemoteGarden " + garden);

                // TODO get document by id
                IdRef idRef = new IdRef(garden.getUUID());
                Session session = getNuxeoClient().getSession();
                PropertyMap props = new PropertyMap();
                props.set("dc:title", garden.getLocality());
                DocumentManager service = session.getAdapter(DocumentManager.class);
                try {
                    return service.update(idRef, props);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                    cancel(false);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Document newGarden) {
                super.onPostExecute(newGarden);
                if (dialog.isShowing())
                    dialog.dismiss();
            }

            @Override
            protected void onCancelled(Document result) {
                // TODO show error icon
            };
        }.execute();
        return garden;
    }
}
