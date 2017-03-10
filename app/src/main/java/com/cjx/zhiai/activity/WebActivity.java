package com.cjx.zhiai.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseActivity;

/**
 * Created by cjx on 2016/8/25.
 * 网页界面
 */
public class WebActivity extends BaseActivity {
    WebView wv;
    ProgressBar pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        setToolBar(true, null, -1);
        setToolbarTitle(getIntent().getStringExtra("title"));

        pb = (ProgressBar) findViewById(R.id.web_progressbar);

        String url = getIntent().getStringExtra("url");
        if (!url.toLowerCase().startsWith("http://") && !url.toLowerCase().startsWith("https://")) {
            url = "http://" + url;
        }
        wv = (WebView) findViewById(R.id.webview);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            wv.removeJavascriptInterface("searchBoxJavaBredge_");
            wv.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        wv.setVerticalScrollBarEnabled(false);
        wv.setHorizontalScrollBarEnabled(false);
        wv.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int progress) {
                pb.setProgress(progress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }
        });

        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("sms:")) {
                    // 针对webview里的短信注册流程，需要在此单独处理sms协议
                    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                    sendIntent.putExtra("address", url.replace("sms:", ""));
                    sendIntent.setType("vnd.android-dir/mms-sms");
                    WebActivity.this.startActivity(sendIntent);
                    return true;
                }
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (!wv.getSettings().getLoadsImagesAutomatically()) {
                    wv.getSettings().setLoadsImagesAutomatically(true);
                }
                super.onPageFinished(view, url);
            }
        });
        WebSettings ws = wv.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setCacheMode(WebSettings.LOAD_NO_CACHE);
        ws.setUseWideViewPort(true);
        ws.setLoadWithOverviewMode(true);

        if (Build.VERSION.SDK_INT >= 19) {
            ws.setLoadsImagesAutomatically(true);
        } else {
            ws.setLoadsImagesAutomatically(false);
        }
        wv.loadUrl(url);
    }
}
