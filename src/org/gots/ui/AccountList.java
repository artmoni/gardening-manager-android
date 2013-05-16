package org.gots.ui;

import java.io.IOException;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.gots.preferences.GotsPreferences;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorDescription;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class AccountList extends ListActivity {
	protected AccountManager accountManager;
	protected Intent intent;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		accountManager = AccountManager.get(getApplicationContext());

//		Account[] accounts = accountManager.getAccounts();
		 Account[] accounts = accountManager.getAccountsByType("com.google");
		 
		for (int i = 0; i < accounts.length; i++) {
			Log.i("account", ">" + accounts[i]);
		}

		AuthenticatorDescription[] types = accountManager.getAuthenticatorTypes(); //
		for (AuthenticatorDescription type : types) {
			Log.d("account types", type.type);
		}

		this.setListAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, accounts));
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Account account = (Account) getListView().getItemAtPosition(position);
		Log.d("account.type", "" + account.type);
		String type;
		if ("com.google".equals(account.type)) {
			type = "Manage your tasks";
		} else {
			type = account.type;
		}
		
		accountManager.getAuthToken(account, // Account retrieved using
												// getAccountsByType()
				type, // Auth scope
				null, // Authenticator-specific options
				this, new OnTokenAcquired(), // Callback called when a token is
												// successfully acquired
				null); // Callback called if an error occurs

		// Intent intent = new Intent(this, ApplicationInfo.class);
		// intent.putExtra("account", account);
		// startActivity(intent);
	}

	private class OnTokenAcquired implements AccountManagerCallback<Bundle> {
		private String token;

		// private ConsumerManager manager;

		@Override
		public void run(AccountManagerFuture<Bundle> result) {
			// Get the result of the operation from the AccountManagerFuture.
			Bundle bundle;
			try {
				bundle = result.getResult();

				Log.d("KEY_ACCOUNT_NAME", "" + bundle.getString(AccountManager.KEY_ACCOUNT_NAME));
				Log.d("KEY_ACCOUNT_TYPE", "" + bundle.getString(AccountManager.KEY_ACCOUNT_TYPE));
				Log.d("KEY_AUTHTOKEN", "" + bundle.getString(AccountManager.KEY_AUTHTOKEN));

				// KEY_ACCOUNT_NAME - the name of the account you supplied
				// KEY_ACCOUNT_TYPE - the type of the account
				// KEY_AUTHTOKEN - the auth token you wanted
				// if (bundle.get(AccountManager.KEY_AUTHTOKEN) == null) {
				// accountManager.invalidateAuthToken(null, null);
				// }
				Intent intent = (Intent) bundle.get(AccountManager.KEY_INTENT);
				if (intent != null) {
					// User input required
					startActivity(intent);
				} else {

					CharSequence text = new String("Token: " + bundle.getString(AccountManager.KEY_AUTHTOKEN));
					Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
				}

				openid_connect(bundle.getString(AccountManager.KEY_AUTHTOKEN));
				GotsPreferences.getInstance(getApplicationContext()).setToken(bundle.getString(AccountManager.KEY_AUTHTOKEN));
				// accountManager.invalidateAuthToken(bundle.getString(AccountManager.KEY_ACCOUNT_TYPE),
				// bundle.getString(AccountManager.KEY_AUTHTOKEN));

			} catch (Exception e) {
				e.printStackTrace();
			}

			// The token is a named value in the bundle. The name of the value
			// is stored in the constant AccountManager.KEY_AUTHTOKEN.

		}

		private void openid_connect(String token) {
			// manager = new ConsumerManager();

		}

		// --- placing the authentication request ---
		public String authRequest(String userSuppliedString, HttpRequest httpReq, HttpResponse httpResp)
				throws IOException {
			// try {
			// // configure the return_to URL where your application will
			// // receive
			// // the authentication responses from the OpenID provider
			// String returnToUrl =
			// "http://srv2.gardening-manager.com:8090/nuxeo/nxstartup.faces?provider%3DGoogleOpenIDConnect";
			//
			//
			// // --- Forward proxy setup (only if needed) ---
			// // ProxyProperties proxyProps = new ProxyProperties();
			// // proxyProps.setProxyName("proxy.example.com");
			// // proxyProps.setProxyPort(8080);
			// // HttpClientFactory.setProxyProperties(proxyProps);
			//
			// // perform discovery on the user-supplied identifier
			// List discoveries = manager.discover(userSuppliedString);
			//
			// // attempt to associate with the OpenID provider
			// // and retrieve one service endpoint for authentication
			// DiscoveryInformation discovered = manager.associate(discoveries);
			//
			// // store the discovery information in the user's session
			// // httpReq.getSession().setAttribute("openid-disc", discovered);
			//
			// // obtain a AuthRequest message to be sent to the OpenID
			// // provider
			// AuthRequest authReq = manager.authenticate(discovered,
			// returnToUrl);
			//
			// // Attribute Exchange example: fetching the 'email' attribute
			// FetchRequest fetch = FetchRequest.createFetchRequest();
			// fetch.addAttribute("email",
			// // attribute alias
			// "http://schema.openid.net/contact/email", // type URI
			// true); // required
			//
			// // attach the extension to the authentication request
			// authReq.addExtension(fetch);
			//
			// if (!discovered.isVersion2()) {
			// // Option 1: GET HTTP-redirect to the OpenID Provider
			// // endpoint
			// // The only method supported in OpenID 1.x
			// // redirect-URL usually limited ~2048 bytes
			// httpResp.sendRedirect(authReq.getDestinationUrl(true));
			// HttpParams params = cliauthReqent.getParams();
			// HttpClientParams.setRedirecting(params, false);
			// return null;
			// } else {
			// // Option 2: HTML FORM Redirection (Allows payloads >2048
			// // bytes)
			//
			// RequestDispatcher dispatcher =
			// getServletContext().getRequestDispatcher("formredirection.jsp");
			// httpReq.setAttribute("parameterMap", authReq.getParameterMap());
			// httpReq.setAttribute("destinationUrl",
			// authReq.getDestinationUrl(false));
			// dispatcher.forward(httpReq, httpResp);
			// }
			// } catch (OpenIDException e) {
			// // present error to the user
			// }

			return null;
		}

	}
}