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
import org.gots.broadcast.BroadCastMessages;
import org.gots.help.HelpUriBuilder;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class WebHelpActivity extends AbstractActivity {

    public static final String URL = "org.gots.doc.classsimplename";

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);

        ActionBar bar = getSupportActionBar();
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setTitle("Documentation");

        String helpClass = getIntent().getExtras().getString(URL);
        mWebView = (WebView) findViewById(R.id.webViewHelp);
        mWebView.setWebViewClient(new WebHelpClient());

        // mWebView.getSettings().setJavaScriptEnabled(true);

        mWebView.loadUrl(Uri.parse(HelpUriBuilder.getUri(helpClass)).toString());

        Button close = (Button) findViewById(R.id.buttonClose);
        close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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
            sendBroadcast(new Intent(BroadCastMessages.PROGRESS_FINISHED));
        }
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            sendBroadcast(new Intent(BroadCastMessages.PROGRESS_UPDATE));
            super.onPageStarted(view, url, favicon);
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onRefresh(String AUTHORITY) {
        mWebView.reload();
    }
}
