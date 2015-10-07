package org.gots.ui.fragment;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.UserRecoverableAuthException;

import org.gots.R;
import org.gots.authentication.GotsSocialAuthentication;
import org.gots.authentication.provider.google.GoogleAuthentication;
import org.gots.authentication.provider.nuxeo.NuxeoAuthentication;
import org.gots.broadcast.BroadCastMessages;
import org.gots.context.GotsContextProvider;
import org.gots.preferences.GotsPreferences;
import org.gots.provider.ActionsContentProvider;
import org.gots.provider.GardenContentProvider;
import org.gots.provider.SeedsContentProvider;
import org.gots.provider.WeatherContentProvider;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoginFragment extends BaseGotsFragment {
    protected static final String TAG = "LoginActivity";

    protected int AUTHTOKEN_CODE_RESULT = 1;

    Account selectedAccount = null;

    private View v;
    private OnLoginEventListener mCallBack;
    private GotsContextProvider gotsContextProvider;
    private ListView listView;

    public interface OnLoginEventListener {
        public void onAuthenticationSucceed(Account account);

        void onAuthenticationFailed(String string);
    }

    @Override
    public void onAttach(Activity activity) {
        try {
            gotsContextProvider = (GotsContextProvider) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement GotsContextProvider");
        }
        try {
            mCallBack = (OnLoginEventListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnLoginEventListener");
        }
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.login, container, false);
//        if (go.isConnectedToServer()) {
//            getDialog().setTitle(R.string.login_connect_state);
//            buildLayoutConnected();
//        } else {
//            getDialog().setTitle(R.string.login_disconnect_state);
        listView = (ListView) v.findViewById(R.id.listViewAccount);
//        }
        buildListAccount("com.google");

        return v;
    }


//    public List<String> getAccounts(String account_type) {
//        AccountManager manager = (AccountManager) getActivity().getSystemService(FragmentActivity.ACCOUNT_SERVICE);
//        Account[] accounts = manager.getAccounts();
//        List<String> accountString = new ArrayList<String>();
//        for (int i = 0; i < accounts.length; i++) {
//            if (accounts[i].type.equals(account_type))
//                accountString.add(accounts[i].name);
//        }
//
//        return accountString;
//    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (AUTHTOKEN_CODE_RESULT == requestCode) {
            if (resultCode == Activity.RESULT_OK) {
                requestOAuth2Token(selectedAccount);
//                mCallBack.onAuthenticationSucceed(selectedAccount);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void requestOAuth2Token(final Account account) {
        new AsyncTask<String, Integer, String>() {

            @Override
            protected String doInBackground(String... params) {

                // GotsSocialAuthentication authentication = new GoogleAuthentication(getActivity());
                GotsSocialAuthentication authentication = new GoogleAuthentication(getActivity());

                String authToken = null;
                String nuxeoToken = null;
                String accountName = params[0];
                try {
                    authToken = authentication.getToken(accountName);
                    if (authToken != null) {
                        NuxeoAuthentication nuxeoAuthentication = new NuxeoAuthentication(getActivity());
                        nuxeoToken = nuxeoAuthentication.request_oauth2_token(authToken);
                    }
                } catch (UserRecoverableAuthException e) {
                    startActivityForResult(e.getIntent(), AUTHTOKEN_CODE_RESULT);
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
                    GotsPreferences gotsPrefs = gotsContextProvider.getGotsContext().getServerConfig();
                    gotsPrefs.setNuxeoLogin(account.name);
                    gotsPrefs.setToken(resultToken);
                    gotsPrefs.setConnectedToServer(true);
                    if (isAdded())
                        Toast.makeText(
                                getActivity(),
                                getResources().getString(R.string.login_connect_description).replace("_ACCOUNT_",
                                        gotsPrefs.getNuxeoLogin()), Toast.LENGTH_LONG).show();
                    // onResume();
                    Account newAccount = gotsPrefs.getUserAccount();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
                    bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);

                    ContentResolver.setSyncAutomatically(newAccount, SeedsContentProvider.AUTHORITY, true);
                    ContentResolver.requestSync(newAccount, SeedsContentProvider.AUTHORITY, bundle);
                    ContentResolver.setSyncAutomatically(newAccount, GardenContentProvider.AUTHORITY, true);
                    ContentResolver.requestSync(newAccount, GardenContentProvider.AUTHORITY, bundle);
                    ContentResolver.setSyncAutomatically(newAccount, ActionsContentProvider.AUTHORITY, true);
                    ContentResolver.requestSync(newAccount, ActionsContentProvider.AUTHORITY, bundle);
                    ContentResolver.setSyncAutomatically(newAccount, WeatherContentProvider.AUTHORITY, true);
                    ContentResolver.requestSync(newAccount, WeatherContentProvider.AUTHORITY, bundle);
                    mCallBack.onAuthenticationSucceed(account);

                    getActivity().sendBroadcast(new Intent(BroadCastMessages.AUTHENTIFICATION_END));

                } else {
                    if (isAdded())
                        mCallBack.onAuthenticationFailed("Please check your internet connection or try later");
//                        Toast.makeText(getActivity(), "Please check your internet connection or try later",
//                                Toast.LENGTH_SHORT).show();
                }
                super.onPostExecute(resultToken);
            }
        }.execute(account.name);
    }

    void buildListAccount(String accountType) {
        AccountManager manager = (AccountManager) getActivity().getSystemService(FragmentActivity.ACCOUNT_SERVICE);
        Account[] accounts = manager.getAccounts();
        final List<Account> usableAccounts = new ArrayList<Account>();
        List<String> items = new ArrayList<String>();
        for (Account account : accounts) {
            if (account.type.equals(accountType)) {
                usableAccounts.add(account);
                items.add(account.name);
            }
        }
//        if (usableAccounts.size() > 1) {
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);
        arrayAdapter.addAll(items);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getActivity().sendBroadcast(new Intent(BroadCastMessages.AUTHENTIFICATION_BEGIN));
                selectedAccount = usableAccounts.get(position);
                requestOAuth2Token(selectedAccount);
                GoogleAnalyticsTracker.getInstance().trackEvent("Authentication", "Login", "Request " +selectedAccount.type, 0);
            }
        });


//        new AlertDialog.Builder(getActivity()).setTitle("Action").setItems(items.toArray(new String[items.size()]),
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int item) {
//                        getActivity().sendBroadcast(new Intent(BroadCastMessages.AUTHENTIFICATION_BEGIN));
//                        selectedAccount = usableAccounts.get(item);
//                        requestOAuth2Token(selectedAccount);
//                    }
//
//                }).show();
//        }else if (usableAccounts.size() == 1 && usableAccounts.get(0) != null) {
//            selectedAccount = usableAccounts.get(0);
//            requestOAuth2Token(usableAccounts.get(0));
//        }

    }


    @Override
    public void update() {

    }

    @Override
    protected boolean requireAsyncDataRetrieval() {
        return false;
    }
}
