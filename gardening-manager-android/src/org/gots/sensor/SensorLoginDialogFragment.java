package org.gots.sensor;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.gots.R;
import org.gots.authentication.provider.parrot.ParrotAuthentication;
import org.gots.ui.fragment.AbstractDialogFragment;

public class SensorLoginDialogFragment extends AbstractDialogFragment {

    private static final String TAG = "SensorLoginFragment";
    private final SensorLoginEvent mCallback;

    private TextView loginTextView;

    private TextView passwordTextView;

    private Button buttonLogin;

    private Button buttonBuy;

//    public static String EVENT_AUTHENTICATE = "sensor.authenticate.success";

    public SensorLoginDialogFragment(SensorLoginEvent callback) {
        mCallback = callback;
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
        new AsyncTask<Void, Void, String>() {
            String login;

            @Override
            protected void onPreExecute() {
                login = loginTextView.getText().toString();
                super.onPreExecute();
            }

            @Override
            protected String doInBackground(Void... params) {
                ParrotAuthentication parrotAuthentication = ParrotAuthentication.getInstance(getActivity());
                String token = parrotAuthentication.getToken(login, passwordTextView.getText().toString());

                return token;
            }

            @Override
            protected void onPostExecute(String token) {
                if (token != null && !"".equals(token)) {
//                    Log.i(TAG, token);
                    if (mCallback != null)
                        mCallback.onSensorLoginSuccess();
                    gotsPrefs.setParrotLogin(login);
                    gotsPrefs.setParrotToken(token);
                } else if (mCallback != null) {
                    mCallback.onSensorLoginFailed();
                }
                dismiss();
                super.onPostExecute(token);
            }
        }.execute();

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public interface SensorLoginEvent {
        public void onSensorLoginSuccess();

        public void onSensorLoginFailed();

    }

}
