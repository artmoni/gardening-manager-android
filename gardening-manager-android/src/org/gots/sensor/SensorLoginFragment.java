package org.gots.sensor;

import java.util.List;

import org.gots.R;
import org.gots.authentication.ParrotAuthentication;
import org.gots.sensor.parrot.ParrotLocation;
import org.gots.sensor.parrot.ParrotSensorProvider;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class SensorLoginFragment extends Fragment {

    private static final String TAG = "SensorLoginFragment";

    private TextView loginTextView;

    private TextView passwordTextView;

    private Button buttonLogin;

    public SensorLoginFragment() {
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
