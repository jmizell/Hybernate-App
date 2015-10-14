package net.ottercove.hybernate;

import android.content.Context;
import android.content.Intent;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class WebViewJSInterface {
    Context mContext;

    /** Instantiate the interface and set the context */
    WebViewJSInterface(Context c) {
        mContext = c;
    }

    /** Show a toast from the web page */
    @JavascriptInterface
    public void showToast(String toast) {
        Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
    }

    @JavascriptInterface
    public String isRooted() {
        if(RootStuff.rootGiven()) {
            return mContext.getString(R.string.warning_root_text_true);
        } else {
            return mContext.getString(R.string.warning_root_text_false);
        }
    }

    @JavascriptInterface
    public String rootGiven() {
        if(RootStuff.rootGiven()) {
            return mContext.getString(R.string.warning_root_given_text_true);
        } else {
            return mContext.getString(R.string.warning_root_given_text_false);
        }
    }

    @JavascriptInterface
    public String pmPresent() {
        if(RootStuff.findBinary("pm")) {
            return mContext.getString(R.string.warning_pm_text_true);
        } else {
            return mContext.getString(R.string.warning_pm_text_false);
        }
    }

    @JavascriptInterface
    public void playDemo() {
        Intent intent = new Intent(mContext, VideoDemoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }
}
