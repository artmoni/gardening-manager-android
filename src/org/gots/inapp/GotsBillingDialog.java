package org.gots.inapp;

import java.util.ArrayList;

import org.gots.R;
import org.gots.preferences.GotsPreferences;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.android.vending.billing.util.IabHelper;
import com.android.vending.billing.util.IabResult;
import com.android.vending.billing.util.Inventory;
import com.android.vending.billing.util.Purchase;
import com.android.vending.billing.util.SkuDetails;

public class GotsBillingDialog extends SherlockDialogFragment {

    protected static final String SKU = GotsPurchaseItem.SKU_PREMIUM;

    // protected static final String SKU = GotsPurchaseItem.SKU_TEST_PURCHASE;

    protected static final int BUY_REQUEST_CODE = 12345;

    protected static final String TAG = "GotsBillingDialog";

    private Button butBuy;

    private Purchase purchase;

    private IabHelper buyHelper;

    View v;

    private TextView textDescription;

    @Override
    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.purchase, container, false);

        textDescription = (TextView) v.findViewById(R.id.idPurchaseDescription);

        butBuy = (Button) v.findViewById(R.id.button_buy);

        String PUBKEY = GotsPreferences.getInstance().initIfNew(getActivity()).getPlayStorePubKey();
        buyHelper = new IabHelper(getActivity(), PUBKEY);

        buyHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess())
                    return;
                update();
            }
        });

        butBuy.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                buyHelper.launchPurchaseFlow(getActivity(), SKU, BUY_REQUEST_CODE,
                        new IabHelper.OnIabPurchaseFinishedListener() {
                            @Override
                            public void onIabPurchaseFinished(IabResult result, Purchase info) {
                                if (result.isSuccess()) {
                                    Toast.makeText(getActivity(), "Thanks for buying!", Toast.LENGTH_SHORT).show();
                                    update();

//                                    Transaction myTrans = new Transaction.Builder("0_123456", // (String) Transaction
//                                                                                              // Id, should be unique.
//                                            (long) (0.1 * 1000000)) // (long) Order total (in micros)
//                                    .setStoreName("In-App Store") // (String) Affiliation
//                                    .setTotalTax((long) (0.17 * 1000000)) // (long) Total tax (in micros)
//                                    .setShippingCost(0) // (long) Total shipping cost (in micros)
//                                    .build();

                                }
                            }
                        });
                getDialog().dismiss();
            }
        });
        return v;
    }

    private void update() {
        ArrayList<String> moreSkus = new ArrayList<String>();
        moreSkus.add(SKU);
        buyHelper.queryInventoryAsync(true, moreSkus, new IabHelper.QueryInventoryFinishedListener() {
            @Override
            public void onQueryInventoryFinished(IabResult result, Inventory inv) {
                if (result.isSuccess() && getDialog() != null) {
                    SkuDetails details = inv.getSkuDetails(SKU);
                    String price = details.getPrice();

                    String title = details.getTitle();
                    getDialog().setTitle(title);

                    String description = details.getDescription();
                    textDescription.setText(description);

                    Button tvPrice = (Button) v.findViewById(R.id.button_buy);
                    tvPrice.setText(tvPrice.getText() + " " + price);

                    purchase = inv.getPurchase(SKU);

                    if (purchase != null) {
                        butBuy.setEnabled(false);
                    } else {
                        butBuy.setEnabled(true);
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
        buyHelper.dispose();
    }
}
