package org.gots.authentication.syncadapter;

import java.util.ArrayList;
import java.util.List;

import org.gots.action.ActionOnSeed;
import org.gots.action.BaseAction;
import org.gots.action.GotsActionManager;
import org.gots.action.provider.local.LocalActionProvider;
import org.gots.action.provider.local.LocalActionSeedProvider;
import org.gots.action.provider.nuxeo.NuxeoActionProvider;
import org.gots.action.provider.nuxeo.NuxeoActionSeedProvider;
import org.gots.authentication.GotsSyncAdapter;
import org.gots.bean.BaseAllotmentInterface;
import org.gots.broadcast.BroadCastMessages;
import org.gots.seed.GrowingSeed;

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
        getContext().sendBroadcast(new Intent(BroadCastMessages.PROGRESS_UPDATE));
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
                for (GrowingSeed seedInterface : growingSeedManager.getGrowingSeedsByAllotment(allotmentInterface, true)) {
                    synchronizeActionSeed(seedInterface,
                            localActionSeedProvider.getActionsToDoBySeed(seedInterface, true),
                            nuxeoActionSeedProvider.getActionsToDoBySeed(seedInterface, true));
                }
            }
        getContext().sendBroadcast(new Intent(BroadCastMessages.PROGRESS_FINISHED));
    }

    private ArrayList<BaseAction> synchronizeActions(List<BaseAction> localActions, ArrayList<BaseAction> remoteActions2) {
        ArrayList<BaseAction> myActions = new ArrayList<BaseAction>();
        // Synchronize remote action with local gardens
        for (BaseAction remoteAction : remoteActions2) {
            boolean found = false;
            for (BaseAction localAction : localActions) {
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

        // for (BaseActionInterface localAction : localActions) {
        // if (localAction.getUUID() == null) { // local only without
        // // UUID => create
        // // remote
        // // myActions.add(createNuxeoGarden(localAction));
        // } else {
        // boolean found = false;
        // for (BaseActionInterface remoteAction : remoteActions2) {
        // if (remoteAction.getUUID() != null && remoteAction.getUUID().equals(localAction.getUUID())) {
        // found = true;
        // break;
        // }
        // }
        // if (!found) { // local only with UUID -> delete local
        // // super.removeGarden(localAction);
        // }
        // }
        // }
        return myActions;
    }

    protected List<ActionOnSeed> synchronizeActionSeed(GrowingSeed seed, List<ActionOnSeed> myLocalActions,
            List<ActionOnSeed> remoteActions) {
        List<ActionOnSeed> myActions = new ArrayList<ActionOnSeed>();
        // Synchronize remote actions with local gardens
        for (ActionOnSeed remoteAction : remoteActions) {
            boolean found = false;
            for (ActionOnSeed localAction : myLocalActions) {
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

        for (ActionOnSeed localAction : myLocalActions) {
            if (localAction.getUUID() == null) { 
                BaseAction newAction = GotsActionManager.getInstance().initIfNew(getContext()).getActionByName(
                        localAction.getName());
                newAction.setDuration(localAction.getDuration());
                myActions.add(nuxeoActionSeedProvider.insertNuxeoAction(seed, newAction));
            } else {
                boolean found = false;
                for (ActionOnSeed remoteAction : remoteActions) {
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
