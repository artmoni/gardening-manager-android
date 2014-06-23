package org.gots.sensor;

import org.gots.R;
import org.gots.authentication.ParrotAuthentication;
import org.gots.inapp.GotsBillingDialog;
import org.gots.inapp.GotsPurchaseItem;
import org.gots.ui.AbstractDialogFragment;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class SensorLoginDialogFragment extends AbstractDialogFragment {

    private static final String TAG = "SensorLoginFragment";

    private TextView loginTextView;

    private TextView passwordTextView;

    private Button buttonLogin;

    private Button buttonBuy;

    public static String EVENT_AUTHENTICATE = "sensor.authenticate.success";

    public SensorLoginDialogFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sensor_login, container, false);

        loginTextView = (TextView) view.findViewById(R.id.idParrotLogin);
        passwordTextView = (TextView) view.findViewById(R.id.idParrotPassword);
        buttonLogin = (Button) view.findViewById(R.id.idButtonParrotLogin);

        buttonLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        buttonBuy = (Button) view.findViewById(R.id.idButtonCreateGarden);

        buttonBuy.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.parrotshopping.com/fr/p_parrot_listing.aspx?f=3943"));
                startActivity(browserIntent);
            }
        });

        return view;
    }

    protected void login() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                ParrotAuthentication parrotAuthentication = ParrotAuthentication.getInstance(getActivity());
                String token = parrotAuthentication.getToken(loginTextView.getText().toString(),
                        passwordTextView.getText().toString());
                if (token != null && !"".equals(token)) {
                    Log.i(TAG, token);
                    getActivity().sendBroadcast(new Intent(EVENT_AUTHENTICATE));
                    dismiss();
                }
                return null;
            }

        }.execute();

    }

    @Override
    public void onResume() {
        super.onResume();
    }

}
