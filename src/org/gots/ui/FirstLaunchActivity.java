package org.gots.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.gots.R;
import org.gots.authentication.GoogleAuthentication;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.UserRecoverableAuthException;

//import android.util.Base64;

public class FirstLaunchActivity extends AbstractActivity {
    private String TAG = "FirstLaunchActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_launch);

        ActionBar bar = getSupportActionBar();
        // bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle(R.string.app_name);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Button buttonCreateProfile = (Button) findViewById(R.id.buttonCreate);
        buttonCreateProfile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(FirstLaunchActivity.this, ProfileCreationActivity.class);
                startActivityForResult(intent, 1);

            }

        });

        Button connect = (Button) findViewById(R.id.buttonConnect);
        // connect.setVisibility(View.GONE);
        connect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Intent intent = new Intent(FirstLaunchActivity.this, LoginActivity.class);
                // startActivityForResult(intent, 2);
                launchGoogle();
            }

        });

    }

    void launchGoogle() {
        AccountManager manager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        Account[] accounts = manager.getAccounts();
        final List<Account> usableAccounts = new ArrayList<Account>();
        List<String> items = new ArrayList<String>();
        for (Account account : accounts) {
            if ("com.google".equals(account.type)) {
                usableAccounts.add(account);
                items.add(String.format("%s (%s)", account.name, account.type));
            }
        }

        new AlertDialog.Builder(this).setTitle("Action").setItems(items.toArray(new String[items.size()]),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {

                        new AsyncTask<String, Integer, String>() {
                            @Override
                            protected String doInBackground(String... params) {

                                GoogleAuthentication authentication = new GoogleAuthentication(getApplicationContext());
                                String token = null;
                                try {
                                    token = authentication.getToken(params[0]);
                                } catch (UserRecoverableAuthException e) {
                                    startActivityForResult(e.getIntent(), 0);
                                } catch (IOException e) {
                                    Log.e(TAG, e.getMessage(), e);
                                } catch (GoogleAuthException e) {
                                    Log.e(TAG, e.getMessage(), e);
                                }
                                return token;
                            }

                            @Override
                            protected void onPostExecute(String result) {
                                if (result != null){
                                    Toast.makeText(FirstLaunchActivity.this, result, Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                                else
                                    Toast.makeText(FirstLaunchActivity.this,
                                            "Login failed", Toast.LENGTH_SHORT).show();

                                super.onPostExecute(result);
                            }
                        }.execute(usableAccounts.get(item).name);
                    }

                }).show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if (requestCode == 2) {
        // Intent intent = new Intent(FirstLaunchActivity.this, DashboardActivity.class);
        // startActivity(intent);
        // }
        if (gotsPrefs.isConnectedToServer() || gotsPrefs.getCurrentGardenId() > -1)
            finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

        default:
            return super.onOptionsItemSelected(item);
        }
    }

}
