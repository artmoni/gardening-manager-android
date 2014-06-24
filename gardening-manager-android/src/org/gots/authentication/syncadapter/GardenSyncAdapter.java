package org.gots.authentication.syncadapter;

import java.util.ArrayList;
import java.util.List;

import org.gots.authentication.GotsSyncAdapter;
import org.gots.broadcast.BroadCastMessages;
import org.gots.garden.GardenInterface;
import org.gots.garden.provider.local.LocalGardenProvider;
import org.gots.garden.provider.nuxeo.NuxeoGardenProvider;

import android.accounts.Account;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

public class GardenSyncAdapter extends GotsSyncAdapter {

    private List<GardenInterface> remoteGardens = new ArrayList<GardenInterface>();

    private Context mContext;

    public GardenSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        this.mContext = context;
    }

    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider,
            SyncResult syncResult) {
        Log.d("GardenSyncAdapter", "onPerformSync for account[" + account.name + "]");

        final Intent intent = new Intent();
        intent.setAction(BroadCastMessages.PROGRESS_UPDATE);
        intent.putExtra("AUTHORITY", authority);
        getContext().sendBroadcast(intent);

        // GardenManager gardenManager = GardenManager.getInstance().initIfNew(getContext());
        LocalGardenProvider localGardenProvider = new LocalGardenProvider(getContext());
        List<GardenInterface> myLocalGardens = localGardenProvider.getMyGardens(true);

        if (gotsPrefs.isConnectedToServer()) {
            NuxeoGardenProvider nuxeoGardenProvider = new NuxeoGardenProvider(getContext());
            remoteGardens = nuxeoGardenProvider.getMyGardens(true);

            List<GardenInterface> myGardens = new ArrayList<GardenInterface>();
            // Synchronize remote garden with local gardens
            for (GardenInterface remoteGarden : remoteGardens) {
                boolean found = false;
                for (GardenInterface localGarden : myLocalGardens) {
                    if (remoteGarden.getUUID() != null && remoteGarden.getUUID().equals(localGarden.getUUID())) {
                        found = true;
                        break;
                    }
                }
                if (found) { // local and remote => update local
                    // TODO check if remote can be out of date
                    // syncGardens(localGarden,remoteGarden);
                    myGardens.add(localGardenProvider.updateGarden(remoteGarden));
                } else { // remote only => create local
                    myGardens.add(localGardenProvider.createGarden(remoteGarden));
                }
            }

            // Create remote garden when not exist remotely and remove local
            // garden if no more referenced online
            for (GardenInterface localGarden : myLocalGardens) {
                if (localGarden.getUUID() == null) { // local only without
                                                     // UUID => create
                                                     // remote
                    myGardens.add(nuxeoGardenProvider.createGarden(localGarden));
                } else {
                    boolean found = false;
                    for (GardenInterface remoteGarden : remoteGardens) {
                        if (remoteGarden.getUUID() != null && remoteGarden.getUUID().equals(localGarden.getUUID())) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) { // local only with UUID -> delete local
                        nuxeoGardenProvider.removeGarden(localGarden);
                    }
                }
            }
        }
        intent.setAction(BroadCastMessages.PROGRESS_FINISHED);
        getContext().sendBroadcast(intent);
        getContext().sendBroadcast(new Intent(BroadCastMessages.GARDEN_EVENT));

    }
}
