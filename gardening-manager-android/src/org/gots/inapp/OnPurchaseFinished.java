package org.gots.inapp;

import com.android.vending.billing.util.Purchase;

/**
 * Created by sfleury on 24/07/15.
 */
public interface OnPurchaseFinished {
    void onPurchaseSucceed(Purchase purchase);

    void onPurchaseFailed(Purchase purchase);
}
