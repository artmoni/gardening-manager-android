package org.gots.authentication.syncadapter;

import java.util.ArrayList;
import java.util.List;

import org.gots.action.BaseActionInterface;
import org.gots.action.GotsActionManager;
import org.gots.action.SeedActionInterface;
import org.gots.action.provider.local.LocalActionProvider;
import org.gots.action.provider.local.LocalActionSeedProvider;
import org.gots.action.provider.nuxeo.NuxeoActionProvider;
import org.gots.action.provider.nuxeo.NuxeoActionSeedProvider;
import org.gots.authentication.GotsSyncAdapter;
import org.gots.bean.BaseAllotmentInterface;
import org.gots.broadcast.BroadCastMessages;
import org.gots.seed.GrowingSeedInterface;

import android.accounts.Account;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

public class ActionsSyncAdapter extends GotsSyncAdapter {
    private LocalActionProvider localActionProvider;

    private NuxeoActionProvider nuxeoActionProvider;

    private LocalActionSeedProvider localActionSeedProvider;

    private NuxeoActionSeedProvider nuxeoActionSeedProvider;

    public ActionsSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        localActionProvider = new LocalActionProvider(context);
        nuxeoActionProvider = new NuxeoActionProvider(context);
        localActionSeedProvider = new LocalActionSeedProvider(context);
        nuxeoActionSeedProvider = new NuxeoActionSeedProvider(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider,
            SyncResult syncResult) {
        Log.d("ActionsSyncAdapter", "onPerformSync for account[" + account.name + "]");
        final Intent intent = new Intent();
        intent.setAction(BroadCastMessages.PROGRESS_UPDATE);
        intent.putExtra("AUTHORITY", authority);
        getContext().sendBroadcast(intent);
        //
        //
        // seedManager.force_refresh(true);
        // seedManager.getVendorSeeds(true);
        //
        // seedManager.getMyStock(gardenManager.getCurrentGarden());
        //
        // List<BaseSeedInterface> newSeeds = seedManager.getNewSeeds();
        // if (newSeeds != null && newSeeds.size() > 0) {
        // SeedNotification notification = new SeedNotification(getContext());
        // notification.createNotification(newSeeds);
        // }
        synchronizeActions(localActionProvider.getActions(true), nuxeoActionProvider.getActions(true));

        // GotsActionSeedProvider actionseedManager = GotsActionSeedManager.getInstance().initIfNew(getContext());
        // actionseedManager.getActionsToDo();
        if (gotsPrefs.isConnectedToServer())
            for (BaseAllotmentInterface allotmentInterface : allotmentManager.getMyAllotments(true)) {
                for (GrowingSeedInterface seedInterface : growingSeedManager.getGrowingSeedsByAllotment(
                        allotmentInterface, true)) {
                    synchronizeActionSeed(seedInterface,
                            localActionSeedProvider.getActionsToDoBySeed(seedInterface, true),
                            nuxeoActionSeedProvider.getActionsToDoBySeed(seedInterface, true));
                }
            }
        intent.setAction(BroadCastMessages.PROGRESS_FINISHED);
        getContext().sendBroadcast(intent);
    }

    private ArrayList<BaseActionInterface> synchronizeActions(List<BaseActionInterface> localActions,
            ArrayList<BaseActionInterface> remoteActions2) {
        ArrayList<BaseActionInterface> myActions = new ArrayList<BaseActionInterface>();
        // Synchronize remote action with local gardens
        for (BaseActionInterface remoteAction : remoteActions2) {
            boolean found = false;
            for (BaseActionInterface localAction : localActions) {
                if (remoteAction.getUUID() != null && remoteAction.getUUID().equals(localAction.getUUID())) {
                    found = true;
                    break;
                }
            }
            if (found) { // local and remote => update local
                myActions.add(localActionProvider.updateAction(remoteAction));
            } else { // remote only => create local
                myActions.add(localActionProvider.createAction(remoteAction));
            }
        }

//        for (BaseActionInterface localAction : localActions) {
//            if (localAction.getUUID() == null) { // local only without
//                                                 // UUID => create
//                                                 // remote
//                                                 // myActions.add(createNuxeoGarden(localAction));
//            } else {
//                boolean found = false;
//                for (BaseActionInterface remoteAction : remoteActions2) {
//                    if (remoteAction.getUUID() != null && remoteAction.getUUID().equals(localAction.getUUID())) {
//                        found = true;
//                        break;
//                    }
//                }
//                if (!found) { // local only with UUID -> delete local
//                    // super.removeGarden(localAction);
//                }
//            }
//        }
        return myActions;
    }

    protected List<SeedActionInterface> synchronizeActionSeed(GrowingSeedInterface seed,
            List<SeedActionInterface> myLocalActions, List<SeedActionInterface> remoteActions) {
        List<SeedActionInterface> myActions = new ArrayList<SeedActionInterface>();
        // Synchronize remote actions with local gardens
        for (SeedActionInterface remoteAction : remoteActions) {
            boolean found = false;
            for (SeedActionInterface localAction : myLocalActions) {
                if (remoteAction.getUUID() != null && remoteAction.getUUID().equals(localAction.getUUID())) {
                    found = true;
                    break;
                }
            }
            if (found) { // local and remote => update local
                // TODO check if remote can be out of date
                // syncGardens(localGarden,remoteGarden);
                myActions.add(localActionSeedProvider.update(seed, remoteAction));
            } else { // remote only => create local
                // SeedActionInterface newAction = super.insertAction(seed,
                // GotsActionManager.getInstance().initIfNew(mContext).getActionByName(remoteAction.getName()));
                // remoteAction.setActionSeedId(newAction.getActionSeedId());
                myActions.add(localActionSeedProvider.insertAction(seed, remoteAction));
            }
        }

        // Create remote garden when not exist remotely and remove local
        // garden if no more referenced online
        for (SeedActionInterface localAction : myLocalActions) {
            if (localAction.getUUID() == null) { // local only without
                                                 // UUID => create
                                                 // remote
                BaseActionInterface newAction = GotsActionManager.getInstance().initIfNew(getContext()).getActionByName(
                        localAction.getName());
                newAction.setDuration(localAction.getDuration());
                myActions.add(nuxeoActionSeedProvider.insertNuxeoAction(seed, newAction));
            } else {
                boolean found = false;
                for (SeedActionInterface remoteAction : remoteActions) {
                    if (remoteAction.getUUID() != null && remoteAction.getUUID().equals(localAction.getUUID())) {
                        found = true;
                        break;
                    }
                }
                if (!found) { // local only with UUID -> delete local
                }
            }
        }
        return myActions;
    }
}
