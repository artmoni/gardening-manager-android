package org.gots.inapp;

import java.util.ArrayList;

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
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.util.IabHelper;
import com.android.vending.billing.util.IabResult;
import com.android.vending.billing.util.Inventory;
import com.android.vending.billing.util.Purchase;
import com.android.vending.billing.util.SkuDetails;

public class GotsBillingDialog extends DialogFragment {

    protected static final String SKU_PREMIUM = GotsPurchaseItem.SKU_PREMIUM;

    protected String SKU_FEATURE = null;

    // protected static final String SKU = GotsPurchaseItem.SKU_TEST_PURCHASE;

    protected static final int BUY_REQUEST_CODE = 12345;

    protected static final String TAG = "GotsBillingDialog";

    private Button butBuyPremium;

    private Button butBuyFeature;

    private Purchase purchase;

    private IabHelper buyHelper;

    View v;

    private TextView textDescription;

    private TextView textDescriptionFeature;

    private TextView textTitlePremium;

    private TextView textTitleFeature;

    public GotsBillingDialog() {
    }

    public GotsBillingDialog(String featureSKU) {
        SKU_FEATURE = featureSKU;
    }

    @Override
    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);

    }

    // @Override
    // public Dialog onCreateDialog(Bundle savedInstanceState) {
    // return new AlertDialog.Builder(getActivity()).setIcon(R.drawable.logo_premium).setTitle(
    // getResources().getString(R.string.inapp_purchase_title))
    //
    // .create();
    // }

    boolean billingServiceAvailable = false;

    protected GotsContext getGotsContext() {
        return GotsContext.get(getActivity());
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.purchase, container, false);
        textDescription = (TextView) v.findViewById(R.id.idPurchasePremiumDescription);
        textDescriptionFeature = (TextView) v.findViewById(R.id.idPurchaseFeatureDescription);
        textTitlePremium = (TextView) v.findViewById(R.id.idPurchasePremiumTitle);
        textTitleFeature = (TextView) v.findViewById(R.id.idPurchaseFeatureTitle);

        butBuyPremium = (Button) v.findViewById(R.id.idPurchasePremiumButton);
        butBuyFeature = (Button) v.findViewById(R.id.idPurchaseFeatureButton);

        getDialog().setTitle(getResources().getString(R.string.inapp_purchase_title));
        // getDialog().requestWindowFeature(Window.FEATURE_LEFT_ICON);
        // // getDialog().getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.launcher);
        // getDialog().setFeatureDrawable(Window.FEATURE_LEFT_ICON, getResources().getDrawable(R.drawable.launcher));

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

        butBuyPremium.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (billingServiceAvailable)
                    buyHelper.launchPurchaseFlow(getActivity(), SKU_PREMIUM, BUY_REQUEST_CODE,
                            new IabHelper.OnIabPurchaseFinishedListener() {
                                @Override
                                public void onIabPurchaseFinished(IabResult result, Purchase info) {
                                    if (result.isSuccess()) {
                                        Toast.makeText(getActivity(), "Thanks for buying!", Toast.LENGTH_SHORT).show();
                                        update();

                                        // Transaction myTrans = new Transaction.Builder("0_123456", // (String)
                                        // Transaction
                                        // // Id, should be unique.
                                        // (long) (0.1 * 1000000)) // (long) Order total (in micros)
                                        // .setStoreName("In-App Store") // (String) Affiliation
                                        // .setTotalTax((long) (0.17 * 1000000)) // (long) Total tax (in micros)
                                        // .setShippingCost(0) // (long) Total shipping cost (in micros)
                                        // .build();

                                    }
                                }
                            });
                getDialog().dismiss();
            }
        });

        butBuyFeature.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buyHelper != null)
                    buyHelper.flagEndAsync();
                buyHelper.launchPurchaseFlow(getActivity(), SKU_FEATURE, BUY_REQUEST_CODE,
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

        if (SKU_FEATURE == null)
            v.findViewById(R.id.idPurchaseFeature).setVisibility(View.GONE);
        return v;
    }

    private void update() {
        ArrayList<String> moreSkus = new ArrayList<String>();
        moreSkus.add(SKU_PREMIUM);
        moreSkus.add(SKU_FEATURE);

        buyHelper.queryInventoryAsync(true, moreSkus, new IabHelper.QueryInventoryFinishedListener() {
            @Override
            public void onQueryInventoryFinished(IabResult result, Inventory inv) {
                if (result.isSuccess() && getDialog() != null) {
                    /*
                     * Display premium purchase information
                     */
                    SkuDetails details = inv.getSkuDetails(SKU_PREMIUM);
                    String price = details.getPrice();

                    String title = details.getTitle();
                    textTitlePremium.setText(title);

                    String description = details.getDescription();
                    textDescription.setText(description);

                    butBuyPremium.setText(butBuyPremium.getText() + " " + price);
                    butBuyPremium.setTextAppearance(getActivity(), R.style.buttonRed);

                    purchase = inv.getPurchase(SKU_PREMIUM);
                    /*
                     * Display feature purchase information
                     */
                    if (SKU_FEATURE != null) {
                        SkuDetails detailsFeature = inv.getSkuDetails(SKU_FEATURE);
                        String priceFeature = detailsFeature.getPrice();

                        String titleFeature = detailsFeature.getTitle();
                        textTitleFeature.setText(titleFeature);

                        String descriptionFeature = detailsFeature.getDescription();
                        textDescriptionFeature.setText(descriptionFeature);

                        butBuyFeature.setText(butBuyFeature.getText() + " " + priceFeature);
                        butBuyFeature.setTextAppearance(getActivity(), R.style.buttonRed);
                        purchase = inv.getPurchase(SKU_FEATURE);
                    }
                    if (purchase != null) {
                        butBuyPremium.setEnabled(false);
                    } else {
                        butBuyPremium.setEnabled(true);
                    }

                } else {
                    Log.w(TAG, "Error getting inventory!");
                }
            }
        });
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
