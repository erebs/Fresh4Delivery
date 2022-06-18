package com.seclob.fresh4delivery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class WebActivity extends AppCompatActivity {

    WebView webview;
    String url="",tittle="";
    TextView ActionTtitle;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        getSupportActionBar().hide();

        webview = findViewById(R.id.webView);
        ActionTtitle = findViewById(R.id.toolbar_name);
        sharedPreferences = getSharedPreferences("NISC",MODE_PRIVATE);

        try {
            Intent intent = getIntent();
            url = intent.getStringExtra("url");
            tittle = intent.getStringExtra("title");
        } catch (Exception e) {
            Log.e("getIntent Error", e + "");
        }
        ActionTtitle.setText(tittle.toLowerCase(Locale.ROOT));
        LinearLayout Loader = findViewById(R.id.progress_horizontal);

        WebSettings set1 = webview.getSettings();
        set1.setJavaScriptEnabled(true);
        set1.setBuiltInZoomControls(false);
        set1.setDomStorageEnabled(true);
        webview.loadUrl(url);
        webview.setBackgroundColor(0x00000000);
        webview.setVerticalScrollBarEnabled(false);
        webview.getSettings().setAppCachePath("/data/data/" + getPackageName() + "/cache");
        webview.getSettings().setAppCacheEnabled(true);
        webview.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webview.getSettings().setDomStorageEnabled(true);
        webview.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);
        webview.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webview.addJavascriptInterface(new WebAppInterface(this), "AndroidInterface");
        webview.setWebViewClient(new WebClient());
        webview.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);

                if (newProgress >= 100)
                    Loader.setVisibility(View.GONE);
                else
                    Loader.setVisibility(View.VISIBLE);

            }
        });

    }

    public void GoBackBtn(View view)
    {
        super.onBackPressed();
    }

    class WebClient extends WebViewClient {

        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if (url.startsWith("tel:") || url.startsWith("mailto:"))
            {
                OpenBrowser(url);
            }
            else
                view.loadUrl(url);
            return true;
        }

    }


    void OpenBrowser(String Url)
    {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(Url));
        startActivity(i);
    }

    public class WebAppInterface {
        Context mContext;

        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public String getUserId() {
            return sharedPreferences.getString("id","");
        }

        @JavascriptInterface
        public String getUserPhone() {
            return sharedPreferences.getString("phone","");
        }

        @JavascriptInterface
        public String getUserName() {
            return sharedPreferences.getString("name","");
        }

        @JavascriptInterface
        public String getUserEmail() {
            return sharedPreferences.getString("email","");
        }

        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
        }
    }

    public void closeActivty(View view)
    {
        finish();
    }
}