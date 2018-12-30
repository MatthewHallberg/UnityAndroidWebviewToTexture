package com.example.matthew.webViewPlugin;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;


import java.io.File;
import android.os.Environment;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;


import java.io.ByteArrayOutputStream;

public class ImageHelper {
    private WebView webview;
    private WebBridge webBridge;
    private ReadData readData;
    Bitmap bitmap;
    Canvas canvas;
    ByteArrayOutputStream stream;

    public ImageHelper(Activity activity, WebBridge bridge) {
        webBridge = bridge;
        readData = new ReadData(new byte[]{});
        //WebView.enableSlowWholeDocumentDraw();

        webview = new WebView(activity);
        int width = 3000; // was 3000
        int height = 2000; // was 2000

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, height);
        layoutParams.width = width;
        layoutParams.height = height;
        webview.setLayoutParams(layoutParams);
        webview.measure(0, 0);
        webview.layout(0, 0, width, height);
        WebSettings webSettings = webview.getSettings();
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setSafeBrowsingEnabled(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setDomStorageEnabled(true);

        bitmap = Bitmap.createBitmap(webview.getWidth(), webview.getHeight(), Bitmap.Config.ARGB_8888);
        Log.e("yo","WIDTH: " + webview.getWidth() + " HEIGHT: " + webview.getHeight());
        canvas = new Canvas(bitmap);
        stream = new ByteArrayOutputStream();
    }

    public void loadWeb(String url) {

        boolean isRedirected;

        webview.setWebViewClient(new WebViewClient() {

            private boolean isRedirected;

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                isRedirected = false;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                isRedirected = true;
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                Log.e("yo", "redirected = " + isRedirected);
                if (!isRedirected) {
                    renderBitmap();
                }
            }
        });
        webview.loadUrl(url);
    }

    public void renderBitmap() {
        webview.draw(canvas);
        bitmap.compress(Bitmap.CompressFormat.PNG,1, stream);
        readData.Buffer = stream.toByteArray();
        Log.e("yo:", "Bytes from android: " + stream.size());
        webBridge.UnityBitmapCallback.onFrameUpdate(readData);
        stream.reset();
        //save to file for testing
//        String filename = "pippo.png";
//        File sd = Environment.getExternalStorageDirectory();
//        File dest = new File(sd, filename);
//        try {
//            FileOutputStream out = new FileOutputStream(dest,false);
//            bitmap.compress(Bitmap.CompressFormat.PNG, 50, out);
//        } catch (FileNotFoundException e){
//            Log.e("yo",  "error: " + e);
//        }
    }
}
