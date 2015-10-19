package org.gots.inapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.vending.billing.util.IabHelper;
import com.android.vending.billing.util.IabResult;
import com.android.vending.billing.util.Inventory;
import com.android.vending.billing.util.Purchase;
import com.android.vending.billing.util.SkuDetails;

import org.gots.R;
import org.gots.context.GotsContext;

import java.util.ArrayList;
import java.util.List;

public class GotsBillingDialog extends DialogFragment {

    protected static final String SKU_PREMIUM = GotsPurchaseItem.SKU_PREMIUM;

//    protected String SKU_FEATURE = null;


    protected static final int BUY_REQUEST_CODE = 12345;

    protected static final String TAG = "GotsBillingDialog";

    private Purchase purchase;

    private IabHelper buyHelper;

    View v;

    private LinearLayout horizontalScrollView;
    private List<HolderSku> mSkus = new ArrayList<>();
    private OnPurchaseFinished onPurchasedFinishedListener;


    public void addSKUFeature(HolderSku sku) {
        mSkus.add(sku);
    }

    @Override
    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);

    }

    boolean billingServiceAvailable = false;

    protected GotsContext getGotsContext() {
        return GotsContext.get(getActivity());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.purchase, container, false);
        getDialog().setTitle(getResources().getString(R.string.inapp_purchase_title));
        horizontalScrollView = (LinearLayout) v.findViewById(R.id.layoutPurchaseItems);

        String PUBKEY = getGotsContext().getServerConfig().getPlayStorePubKey();
        buyHelper = new IabHelper(getActivity(), PUBKEY);

        buyHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess())
                    return;
                billingServiceAvailable = true;
                update();
            }
        });

        return v;
    }

    private void update() {
        ArrayList<String> moreSkus = new ArrayList<String>();
        moreSkus.add(SKU_PREMIUM);
        for (HolderSku mSku : mSkus)
            moreSkus.add(mSku.getSku());

        buyHelper.queryInventoryAsync(true, moreSkus, new IabHelper.QueryInventoryFinishedListener() {
            @Override
            public void onQueryInventoryFinished(IabResult result, Inventory inv) {
                if (result.isSuccess() && getDialog() != null) {
                    horizontalScrollView.removeAllViews();
                    /*
                     * Display feature purchase information
                     */
                    for (HolderSku mSku : mSkus) {
                        SkuDetails detailsFeature = inv.getSkuDetails(mSku.getSku());
                        purchase = inv.getPurchase(mSku.getSku());
                        if (purchase != null && purchase.getSku().equals(GotsPurchaseItem.SKU_TEST_PURCHASE))
                            consumePurchase(purchase);
                        addPurchaseItem(detailsFeature, inv.hasPurchase(mSku.getSku()) || inv.hasPurchase(SKU_PREMIUM));
                    }
                     /*
                     * Display premium purchase information
                     */
                    SkuDetails details = inv.getSkuDetails(SKU_PREMIUM);
                    purchase = inv.getPurchase(SKU_PREMIUM);
                    addPurchaseItem(details, inv.hasPurchase(SKU_PREMIUM));

                } else {
                    Log.w(TAG, "Error getting inventory!");
                }
            }
        });
    }

    public void consumePurchase(Purchase purchase) {
        if (buyHelper != null)
            buyHelper.flagEndAsync();
        buyHelper.consumeAsync(purchase, new IabHelper.OnConsumeFinishedListener() {
            @Override
            public void onConsumeFinished(Purchase purchase, IabResult result) {
                Log.d(TAG, "consume finished: " + result.getMessage());
            }
        });
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
            = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
//            if (result.isFailure()) {
//                Log.d(TAG, "Error purchasing: " + result);
//                return;
//            }

            if (result.isSuccess()) {
                update();
                if (onPurchasedFinishedListener != null)
                    onPurchasedFinishedListener.onPurchaseSucceed(purchase);
                Log.d(TAG, "Success purchasing: " + result);

            } else {
                if (onPurchasedFinishedListener != null)
                    onPurchasedFinishedListener.onPurchaseFailed(purchase);
                Log.d(TAG, "Error purchasing: " + result);

            }
        }
    };


    private void addPurchaseItem(final SkuDetails detailsFeature, boolean hasPurchase) {
        PurchaseItemLayout purchaseItemLayout = new PurchaseItemLayout(getActivity());
        purchaseItemLayout.setPurchasePrice(hasPurchase ? "" : detailsFeature.getPrice());
        String title;
        if (detailsFeature.getTitle().indexOf("(") != -1) {
            title = detailsFeature.getTitle().substring(0, detailsFeature.getTitle().indexOf("("));
        } else
            title = detailsFeature.getTitle();
        purchaseItemLayout.setPurchaseDescription(detailsFeature.getDescription());
        int icon = getActivity().getResources().getIdentifier("org.gots:drawable/" + detailsFeature.getSku().replace(".", "_"), null, null);
        purchaseItemLayout.setPurchaseIcon(icon);
        purchaseItemLayout.setPurchaseState(hasPurchase);
        purchaseItemLayout.setPurchaseTitle(title);
        if (!hasPurchase) {
            purchaseItemLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (buyHelper != null)
                        buyHelper.flagEndAsync();
                    buyHelper.launchPurchaseFlow(getActivity(), detailsFeature.getSku(), BUY_REQUEST_CODE, mPurchaseFinishedListener);
                }
            });
        }
        horizontalScrollView.addView(purchaseItemLayout);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!buyHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.i(TAG, "onActivityResult handled by IABUtil.");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            buyHelper.dispose();
        } catch (Exception e) {
            Log.e(TAG, "buyHelper.dispose()");
        }
    }

    public void setOnPurchasedFinishedListener(OnPurchaseFinished onPurchasedFinishedListener) {
        this.onPurchasedFinishedListener = onPurchasedFinishedListener;
    }
}
