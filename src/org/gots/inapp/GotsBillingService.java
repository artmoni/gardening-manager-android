package org.gots.inapp;

import java.util.ArrayList;

import org.gots.seed.service.GotsService;

import android.content.Intent;
import android.widget.Toast;

import com.android.vending.billing.util.IabHelper;
import com.android.vending.billing.util.IabResult;
import com.android.vending.billing.util.Inventory;

public class GotsBillingService extends GotsService {
    private IabHelper buyHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        buyHelper = new IabHelper(getApplicationContext(), gotsPrefs.getPlayStorePubKey());

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final ArrayList<String> moreSkus = new ArrayList<String>();
        moreSkus.add(GotsPurchaseItem.SKU_PREMIUM);
        buyHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                // update();
                Toast.makeText(getApplicationContext(), "Set up finished!", Toast.LENGTH_SHORT).show();

                buyHelper.queryInventoryAsync(true, moreSkus, new IabHelper.QueryInventoryFinishedListener() {
                    @Override
                    public void onQueryInventoryFinished(IabResult result, Inventory inv) {
                        if (result.isSuccess()) {

                            boolean isPremium = inv.hasPurchase(GotsPurchaseItem.SKU_PREMIUM);
                            gotsPrefs.setPremium(isPremium);
                            Toast.makeText(getApplicationContext(), "Successful got inventory!", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(getApplicationContext(), "Error getting inventory!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        buyHelper.dispose();
    }
}
