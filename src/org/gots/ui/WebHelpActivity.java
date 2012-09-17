/*******************************************************************************
 * Copyright (c) 2012 sfleury.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     sfleury - initial API and implementation
 ******************************************************************************/
package org.gots.ui;

import org.gots.R;
import org.gots.ads.GotsAdvertisement;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;

public class WebHelpActivity extends Activity {
	private ProgressDialog pd;
	private String baseHelpURL = "http://www.gardening-manager.com";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);

		String page = getIntent().getExtras().getString("org.gots.help.page");
		
		WebView mWebView = (WebView) findViewById(R.id.webViewHelp);
		mWebView.setWebViewClient(new WebHelpClient());
		mWebView.getSettings().setJavaScriptEnabled(true);

		mWebView.loadUrl(baseHelpURL + "/" + page);
		// addContentView(mWebView, new LayoutParams(LayoutParams.FILL_PARENT,
		// LayoutParams.FILL_PARENT));

		Button close = (Button) findViewById(R.id.buttonClose);
		close.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

		

		pd = ProgressDialog.show(this, "", getResources().getString(R.string.help_loading), true);

	}

	private class WebHelpClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			pd.dismiss();
		}

	}
}
