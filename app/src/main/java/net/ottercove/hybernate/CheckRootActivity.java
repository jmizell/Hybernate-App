package net.ottercove.hybernate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.webkit.WebView;

public class CheckRootActivity extends ActionBarActivity {

    public static boolean check() {
        if (RootStuff.isRooted() && RootStuff.findBinary("pm") && RootStuff.rootGiven()) {
            return true;
        } else {
            return false;
        }
    }

    public static void displayRootStatus(Context context) {
        Intent intent = new Intent(context, CheckRootActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warning);

        WebView wv = new WebView(getApplicationContext());
        wv.getSettings().setJavaScriptEnabled(true);
        wv.addJavascriptInterface(new WebViewJSInterface(this), "hybernate");
        wv.loadUrl("file:///android_asset/root_status.html");
        setContentView(wv);
    }
}