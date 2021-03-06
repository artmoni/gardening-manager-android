/**
 * ****************************************************************************
 * Copyright (c) 2012 sfleury.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * <p/>
 * Contributors:
 * sfleury - initial API and implementation
 * ****************************************************************************
 */
package org.gots.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.gots.R;
import org.gots.broadcast.BroadCastMessages;
import org.gots.help.HelpUriBuilder;

public class WebHelpActivity extends BaseGotsActivity {

    public static final String URL_CLASSNAME = "org.gots.doc.classsimplename";
    public static final String URL_EXTERNAL = "org.gots.doc.externalurl";

    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help);
        setTitleBar("Documentation");


        mWebView = (WebView) findViewById(R.id.webViewHelp);
        mWebView.setWebViewClient(new WebHelpClient());
        mWebView.getSettings().setJavaScriptEnabled(true);

        if (getIntent().getExtras().getString(URL_CLASSNAME) != null) {
            String helpClass = getIntent().getExtras().getString(URL_CLASSNAME);
            mWebView.loadUrl(Uri.parse(HelpUriBuilder.getUri(this, helpClass)).toString());
        } else {
            String url = getIntent().getExtras().getString(URL_EXTERNAL);
            mWebView.loadUrl(url);
        }
        // Button close = (Button) findViewById(R.id.buttonClose);
        // close.setOnClickListener(new View.OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // finish();
        // }
        // });

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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected boolean requireFloatingButton() {
        return false;
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

}
