package org.gots.inapp;

import java.util.ArrayList;
import java.util.List;

import org.gots.R;
import org.gots.context.GotsContext;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.util.IabHelper;
import com.android.vending.billing.util.IabResult;
import com.android.vending.billing.util.Inventory;
import com.android.vending.billing.util.Purchase;
import com.android.vending.billing.util.SkuDetails;

public class GotsBillingDialog extends DialogFragment {

    protected static final String SKU_PREMIUM = GotsPurchaseItem.SKU_PREMIUM;

//    protected String SKU_FEATURE = null;


    protected static final int BUY_REQUEST_CODE = 12345;

    protected static final String TAG = "GotsBillingDialog";

    private Purchase purchase;

    private IabHelper buyHelper;

    View v;

    private LinearLayout horizontalScrollView;
    private List<String> mSkus = new ArrayList<>();

    public GotsBillingDialog() {
    }

    public GotsBillingDialog(String featureSKU) {
        addSKUFeature(featureSKU);
    }

    public void addSKUFeature(String featureSKU) {
        mSkus.add(featureSKU);
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
        for (String mSku : mSkus)
            moreSkus.add(mSku);

        buyHelper.queryInventoryAsync(true, moreSkus, new IabHelper.QueryInventoryFinishedListener() {
            @Override
            public void onQueryInventoryFinished(IabResult result, Inventory inv) {
                if (result.isSuccess() && getDialog() != null) {
                    /*
                     * Display feature purchase information
                     */
                    for (String mSku : mSkus) {
                        SkuDetails detailsFeature = inv.getSkuDetails(mSku);
                        String priceFeature = detailsFeature.getPrice();

                        purchase = inv.getPurchase(mSku);

                        addPurchaseItem(detailsFeature);
                    }
                     /*
                     * Display premium purchase information
                     */
                    SkuDetails details = inv.getSkuDetails(SKU_PREMIUM);
                    purchase = inv.getPurchase(SKU_PREMIUM);
                    addPurchaseItem(details);

                } else {
                    Log.w(TAG, "Error getting inventory!");
                }
            }
        });
    }

    private void addPurchaseItem(final SkuDetails detailsFeature) {
        PurchaseItemLayout purchaseItemLayout = new PurchaseItemLayout(getActivity());
        purchaseItemLayout.setPurchasePrice(detailsFeature.getPrice());
        purchaseItemLayout.setPurchaseTitle(detailsFeature.getTitle().substring(0, detailsFeature.getTitle().indexOf('(')));
        purchaseItemLayout.setPurchaseDescription(detailsFeature.getDescription());
        int icon = getActivity().getResources().getIdentifier("org.gots:drawable/" + detailsFeature.getSku().replace(".", "_"), null, null);
        purchaseItemLayout.setPurchaseIcon(icon);
        purchaseItemLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buyHelper != null)
                    buyHelper.flagEndAsync();
                buyHelper.launchPurchaseFlow(getActivity(), detailsFeature.getSku(), BUY_REQUEST_CODE,
                        new IabHelper.OnIabPurchaseFinishedListener() {
                            @Override
                            public void onIabPurchaseFinished(IabResult result, Purchase info) {
                                if (result.isSuccess()) {
                                    Toast.makeText(getActivity(), "Thanks for buying!", Toast.LENGTH_SHORT).show();
                                    update();
                                }
                            }
                        });
                getDialog().dismiss();
            }
        });
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
}
