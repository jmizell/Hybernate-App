package net.ottercove.hybernate;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.webkit.WebView;
import android.widget.ListView;


public class MainActivity extends ActionBarActivity {
    int e = Integer.MAX_VALUE;
    public ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkRoot();

        WebView wv = new WebView(getApplicationContext());
        wv.getSettings().setJavaScriptEnabled(true);
        wv.addJavascriptInterface(new WebViewJSInterface(this), "hybernate");
        wv.loadUrl("file:///android_asset/welcome.html");
        setContentView(wv);
    }

    private void checkRoot() {
        if(CheckRootActivity.check()) {
            return;
        } else {
            CheckRootActivity.displayRootStatus(this);
            finish();
        }
    }
}
