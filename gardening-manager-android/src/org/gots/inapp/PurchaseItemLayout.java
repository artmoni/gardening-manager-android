package org.gots.inapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.gots.R;

/**
 * Created by sfleury on 22/07/15.
 */
public class PurchaseItemLayout extends RelativeLayout {

    Context mContext;

    public PurchaseItemLayout(Context context) {
        super(context);
        mContext = context;
        setupView();
    }

    private void setupView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.purchase_item, this);
    }

    public void setPurchaseIcon(int ressource) {
        ImageView v = (ImageView) findViewById(R.id.imageViewPurchaseItem);
        v.setImageResource(ressource);
    }

    public void setPurchaseTitle(String title) {
        TextView titleView = (TextView) findViewById(R.id.textViewPurchaseItem);
        titleView.setTypeface(Typefaces.get(mContext, "cartoons.ttf"));

        titleView.setText(title);
    }

    public void setPurchasePrice(String price) {
        TextView priceButton = (TextView) findViewById(R.id.buttonPrice);
        priceButton.setText(price);
    }

    public void setPurchaseDescription(String description) {
        TextView titleView = (TextView) findViewById(R.id.textViewPurchaseDescription);
        titleView.setText(description);
    }

    public void setPurchaseState(boolean isPurchase) {
        ImageView stateView = (ImageView) findViewById(R.id.imageViewPurchaseState);
        if (isPurchase)
            stateView.setVisibility(VISIBLE);
        else
            stateView.setVisibility(GONE);
    }
}
