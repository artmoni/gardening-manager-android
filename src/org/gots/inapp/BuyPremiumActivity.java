package org.gots.inapp;

import java.util.ArrayList;

import org.gots.R;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragment;
import com.android.vending.billing.util.IabHelper;
import com.android.vending.billing.util.IabResult;
import com.android.vending.billing.util.Inventory;
import com.android.vending.billing.util.Purchase;
import com.android.vending.billing.util.SkuDetails;

public class BuyPremiumActivity extends SherlockFragment {

    protected static final String SKU = "android.test.purchased";

    private static final String PUBKEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtAFVYGad4FaKIZ9A0W2JfMh+B1PQMU+tal9B0XYbEJdZy6UCwqoH42/YLDn0GTjKA+ozAZJtaQqoU/ew95tYKEYszj067HfVehpRtKxLlySFMnqdai0SuGyl5EI4QQovsw3wFU1ihELWBaCg2CcTJqk1jXcWaxsqPPPWty5tAcMwQDWZ0cw6uw8QddztiKlw5IB1XTWdhZTuPL/RcR0Ns+lbEB2kdosozekXr+dRqZ4+PKyHn+j8/407hb76gqn9CmrGhOsJ3E7aOVRCZWZ9nf6aJfFYJP5JY/QHsa+9OsiSj8QXS2vic3ay+MazF09bteN7Wnb15Y9CBK/sM2RAqQIDAQAB";

    protected static final int BUY_REQUEST_CODE = 12345;

    private Button butUpdate;

    private Button butBuy;

    private Button butConsume;

    private Purchase purchase;

    private IabHelper buyHelper;

    @Override
    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_main, container, false);
        butUpdate = (Button) v.findViewById(R.id.button_update);

        butBuy = (Button) v.findViewById(R.id.button_buy);
        butBuy.setEnabled(false);

        butConsume = (Button) v.findViewById(R.id.button_consume);
        butConsume.setEnabled(false);

        buyHelper = new IabHelper(getActivity(), PUBKEY);

        butUpdate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });

        butConsume.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                buyHelper.consumeAsync(purchase, new IabHelper.OnConsumeFinishedListener() {
                    @Override
                    public void onConsumeFinished(Purchase purchase, IabResult result) {
                        if (result.isSuccess()) {
                            Toast.makeText(getActivity(), "Purchase consumed!", Toast.LENGTH_SHORT).show();

                            try {
                                // Small HACK: Give the system some time to realize the consume... without the sleep
                                // here,
                                // you have to press "Update" to see that the item can be bought again...
                                Thread.sleep(1000);
                                update();
                            } catch (Exception e) {
                                // ignored
                            }

                        } else {
                            Toast.makeText(getActivity(), "Error consuming: " + result.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        buyHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
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
                                }
                            }
                        });
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
                if (result.isSuccess()) {
                    SkuDetails details = inv.getSkuDetails(SKU);
                    String price = details.getPrice();

                    TextView tvPrice = (TextView) getView().findViewById(R.id.textview_price);
                    tvPrice.setText(price);

                    purchase = inv.getPurchase(SKU);

                    if (purchase != null) {
                        butBuy.setEnabled(false);
                        butConsume.setEnabled(true);
                    } else {
                        butBuy.setEnabled(true);
                        butConsume.setEnabled(false);
                    }

                    Toast.makeText(getActivity(), "Successful got inventory!", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getActivity(), "Error getting inventory!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
