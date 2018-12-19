package com.jeffinbao.colorfulnotes.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.jeffinbao.colorfulnotes.R;
import com.jeffinbao.colorfulnotes.utils.OSUtil;

/**
 * Author: baojianfeng
 * Date: 2018-12-19
 */
public class PrivatePolicyActivity extends BaseActivity {
    private final String URL = "https://jeffinbao.github.io/2018/12/19/20181219-privacy_policy/";
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBarToolbar().setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_private_policy;
    }

    @Override
    protected void initValues() {

    }

    @Override
    protected void initViews() {
        webView = (WebView) findViewById(R.id.wv_private_policy);
        initializeWebView(webView);
        webView.loadUrl(URL);
    }

    /**
     * initialize webview, including web settings and set webview client
     * @param webView webView
     */
    private void initializeWebView(WebView webView) {
        WebSettings ws = webView.getSettings();

        ws.setDomStorageEnabled(true);
        ws.setAppCacheEnabled(true);
        String cachePath = getApplicationContext().getDir("cache", Context.MODE_PRIVATE).getPath();
        ws.setAppCachePath(cachePath);

        // change cache mode according to network availability
        if (OSUtil.isNetworkAvailable()) {
            ws.setCacheMode(WebSettings.LOAD_DEFAULT);
        } else {
            ws.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        }
    }
}
