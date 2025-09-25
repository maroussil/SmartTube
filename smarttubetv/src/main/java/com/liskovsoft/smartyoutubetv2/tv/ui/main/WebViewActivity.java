package com.liskovsoft.smartyoutubetv2.tv.ui.main;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.liskovsoft.smartyoutubetv2.tv.R;

public class WebViewActivity extends Activity {
    public static final String EXTRA_URL = "extra_url";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        WebView webView = findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        String url = getIntent().getStringExtra(EXTRA_URL);
        if (url != null) {
            webView.loadUrl(url);
        }
    }
}