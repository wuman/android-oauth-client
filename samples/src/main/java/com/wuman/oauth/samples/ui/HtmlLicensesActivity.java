package com.wuman.oauth.samples.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewStub;
import android.view.Window;
import android.webkit.WebView;

import com.wuman.oauth.samples.R;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

public class HtmlLicensesActivity extends FragmentActivity implements View.OnClickListener {

    private BottomButtonBar mButtonBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.licenses);
        requestWindowFeature(Window.FEATURE_LEFT_ICON);

        setContentView(R.layout.html_dialog_layout);
        WebView wv = (WebView) findViewById(R.id.wv);

        String content = getContentFromResource(this, R.raw.licenses)
                .replaceAll("<app_name>", getString(R.string.app_name_long));

        try {
            wv.loadDataWithBaseURL("file:///android_asset/",
                    content, null, "utf-8", null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();

        setFeatureDrawableResource(Window.FEATURE_LEFT_ICON,
                R.drawable.ic_launcher);

        mButtonBar = (BottomButtonBar) ((ViewStub) findViewById(R.id.bottom_button_bar_stub))
                .inflate();
        mButtonBar.setVisibility(View.GONE);
        mButtonBar.setBackgroundResource(android.R.drawable.bottom_bar);
        mButtonBar.setOnClickListener(this, R.id.btn_left, R.id.btn_middle,
                R.id.btn_right);
        onSetupButtonBar(mButtonBar);
    }

    protected void onSetupButtonBar(BottomButtonBar buttonBar) {
        buttonBar.setVisibility(View.GONE, R.id.btn_left, R.id.btn_right);
        buttonBar.setText(android.R.string.ok, R.id.btn_middle);
        buttonBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_middle: {
                finish();
                break;
            }
        }
    }

    private static String getContentFromResource(Context context, int resId) {
        InputStream is = null;
        try {
            is = context.getResources().openRawResource(resId);
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            return new String(buffer);
        } catch (IOException e) {
            return "";
        } finally {
            closeSilently(is);
        }
    }

    private static void closeSilently(Closeable c) {
        if (c == null) {
            return;
        }
        try {
            c.close();
        } catch (Throwable t) {
            // do nothing
        }
    }

}
