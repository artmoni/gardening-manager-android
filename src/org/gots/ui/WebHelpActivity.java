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
import org.gots.help.HelpUriBuilder;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.MenuItem;
import com.google.zxing.integration.android.IntentIntegrator;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class WebHelpActivity extends AbstractActivity {
    private ProgressDialog pd;

    private String baseHelpURL = "http://www.gardening-manager.com";

    public static final String URL = "org.gots.doc.classsimplename";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);

        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle("Documentation");

        String helpClass = getIntent().getExtras().getString(URL);
        WebView mWebView = (WebView) findViewById(R.id.webViewHelp);
        mWebView.setWebViewClient(new WebHelpClient());

        mWebView.getSettings().setJavaScriptEnabled(true);

        mWebView.loadUrl(Uri.parse(HelpUriBuilder.getUri(helpClass)).toString());

        Button close = (Button) findViewById(R.id.buttonClose);
        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        pd = ProgressDialog.show(this, "", getResources().getString(R.string.help_loading), true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch (item.getItemId()) {

        case android.R.id.home:
            finish();
            return true;

        default:
            return super.onOptionsItemSelected(item);
        }
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
