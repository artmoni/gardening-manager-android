package org.gots.action.provider.nuxeo;

import java.util.ArrayList;
import java.util.Iterator;

import org.gots.action.BaseActionInterface;
import org.gots.action.provider.local.LocalActionProvider;
import org.gots.nuxeo.NuxeoManager;
import org.gots.seed.BaseSeedInterface;
import org.gots.seed.provider.nuxeo.NuxeoSeedConverter;
import org.nuxeo.android.repository.DocumentManager;
import org.nuxeo.ecm.automation.client.cache.CacheBehavior;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.model.Document;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;

import android.content.Context;
import android.util.Log;

public class NuxeoActionProvider extends LocalActionProvider {

    protected static final String TAG = "NuxeoActionProvider";

    public NuxeoActionProvider(Context mContext) {
        super(mContext);
    }

    @Override
    public ArrayList<BaseActionInterface> getActions() {
        // client.setRequestInterceptor(new TokenRequestInterceptor(myApp, myToken, myLogin, myDeviceId));

        try {
            NuxeoManager nuxeoManager = NuxeoManager.getInstance();
            nuxeoManager.initIfNew(mContext);
            Session session = nuxeoManager.getNuxeoClient().getSession();
            DocumentManager service = session.getAdapter(DocumentManager.class);

            // Session session = client.getSession();

            // Documents docs = (Documents) session.newRequest("Document.Query") //
            // .setHeader(Constants.HEADER_NX_SCHEMAS, "*") //
            // .set("query",
            // "SELECT * FROM VendorSeed WHERE ecm:currentLifeCycleState <> 'deleted' ORDER BY dc:modified DESC") //
            // .execute();
            byte cacheParam = CacheBehavior.STORE;
           boolean force = true;
            boolean refresh = force;
            if (refresh) {
                cacheParam = (byte) (cacheParam | CacheBehavior.FORCE_REFRESH);
                refresh = false;
            }
            Documents docs = service.query("SELECT * FROM Action WHERE ecm:currentLifeCycleState != \"deleted\"",
                    null, new String[] { "dc:modified DESC" }, "*", 0, 50, cacheParam);
            // documentsList = docs.asUpdatableDocumentsList();
            // Documents docs =
            // service.query("SELECT * FROM VendorSeed WHERE ecm:currentLifeCycleState <> 'deleted' ORDER BY dc:modified DESC");

            for (Iterator<Document> iterator = docs.iterator(); iterator.hasNext();) {
                Document document = iterator.next();
                BaseActionInterface action = NuxeoActionConverter.convert(document);
                if (seed != null) {

                    remoteVendorSeeds.add(seed);
                    Log.i(TAG, "Nuxeo Seed: " + seed);
                } else {
                    Log.w(TAG, "Nuxeo Seed conversion problem " + document.getTitle() + "- " + document.getId());
                }
            }
            // getNuxeoClient().shutdown();
            myVendorSeeds = synchronize(localVendorSeeds, remoteVendorSeeds);
        } catch (Exception e) {
            Log.e(TAG, "getAllSeeds " + e.getMessage(), e);
            myVendorSeeds=super.getVendorSeeds(force);
        }
        return super.getActions();
    }
}
