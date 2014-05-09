package org.gots.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

import org.gots.R;
import org.gots.authentication.GoogleAuthentication;
import org.gots.authentication.GotsSocialAuthentication;
import org.gots.authentication.NuxeoAuthentication;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.UserRecoverableAuthException;

//import android.util.Base64;

public class FirstLaunchActivity extends AbstractActivity {
    private String TAG = "FirstLaunchActivity";

    private Menu optionsMenu;

    private View progressView;

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

        View connect = (View) findViewById(R.id.buttonConnect);
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
                    public void onClick(DialogInterface dialog, final int item) {

                        new AsyncTask<String, Integer, String>() {

                            protected void onPreExecute() {
                                setRefreshActionButtonState(true);
                                findViewById(R.id.textViewError).setVisibility(View.GONE);

                            };

                            @Override
                            protected String doInBackground(String... params) {
                                GotsSocialAuthentication authentication = new GoogleAuthentication(
                                        getApplicationContext());
                                String googleToken = null;
                                String nuxeoToken = null;
                                try {
                                    googleToken = authentication.getToken(params[0]);
                                    if (googleToken != null) {
                                        NuxeoAuthentication nuxeoAuthentication = new NuxeoAuthentication(
                                                getApplicationContext());
                                        nuxeoToken = nuxeoAuthentication.request_oauth2_token(googleToken);
                                    }
                                } catch (UserRecoverableAuthException e) {
                                    startActivityForResult(e.getIntent(), 0);
                                } catch (IOException e) {
                                    Log.e(TAG, e.getMessage(), e);
                                } catch (GoogleAuthException e) {
                                    Log.e(TAG, e.getMessage(), e);
                                }
                                return nuxeoToken;
                            }

                            @Override
                            protected void onPostExecute(String resultToken) {
                                if (resultToken != null) {
                                    gotsPrefs.setNuxeoLogin(usableAccounts.get(item).name);
                                    gotsPrefs.setToken(resultToken);
                                    gotsPrefs.setConnectedToServer(true);
                                    Toast.makeText(
                                            getApplicationContext(),
                                            getResources().getString(R.string.login_connect_description).replace(
                                                    "_ACCOUNT_", gotsPrefs.getNuxeoLogin()), Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(FirstLaunchActivity.this, DashboardActivity.class);
                                    startActivity(intent);
                                    finish();

                                } else {
                                    // Toast.makeText(getApplicationContext(),
                                    // "Error requesting GoogleAuthUtil.getToken",
                                    // Toast.LENGTH_SHORT).show();
                                    findViewById(R.id.textViewError).setVisibility(View.VISIBLE);
                                }
                                setRefreshActionButtonState(false);
                                super.onPostExecute(resultToken);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        this.optionsMenu = menu;
        inflater.inflate(R.menu.menu_firstlaunch, menu);
        MenuItem itemRefresh = (MenuItem) menu.findItem(R.id.menuRefresh);
        itemRefresh.setVisible(false);
        // refreshConnectionState();
        return super.onCreateOptionsMenu(menu);
    }

    public void setRefreshActionButtonState(final boolean refreshing) {

        if (optionsMenu != null) {
            final MenuItem refreshItem = optionsMenu.findItem(R.id.menuRefresh);
            Animation mCycleFadeAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate);
            if (progressView == null)
                progressView = getLayoutInflater().inflate(R.layout.actionbar_indeterminate_progress, null);

            if (refreshItem != null) {
                if (refreshing) {
                    refreshItem.setActionView(progressView);
                    progressView.startAnimation(mCycleFadeAnimation);
                    refreshItem.setVisible(true);
                } else {
                    progressView.clearAnimation();
                    refreshItem.setActionView(null);
                    refreshItem.setVisible(false);
                }
            }
        }
    }
}
