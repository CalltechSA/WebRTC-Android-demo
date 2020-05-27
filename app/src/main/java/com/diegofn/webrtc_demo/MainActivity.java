package com.diegofn.webrtc_demo;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.media.AudioManager;
import android.net.http.SslError;
import android.os.Bundle;

import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.PermissionRequest;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private PermissionRequest mPermissionRequest;
    private AudioManager audioManager;
    private static final String DESKTOP_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/81.0.4044.138 Safari/537.36";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (0 != (getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE)) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

/*
        audioManager = (AudioManager)getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_NORMAL);
        audioManager.setSpeakerphoneOn(false);
*/

        webView = findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient());

        webView.loadUrl("https://clicktocall.teleperformance.co/cticontroldespegar/");

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setUserAgentString(DESKTOP_USER_AGENT);


       /* webView.setWebViewClient(
                new SSLTolerentWebViewClient()
        );*/
//        webView.loadUrl("https://clicktocall.teleperformance.co/cticontroldespegar/");

        webView.setWebChromeClient(new WebChromeClient() {

            // This method is called when the web content is requesting permission to access some
            // resources.
            @Override
            public void onPermissionRequest(PermissionRequest request) {
                mPermissionRequest = request;
                final String[] requestedResources = request.getResources();
                for (String r : requestedResources) {
                    if (r.equals(PermissionRequest.RESOURCE_AUDIO_CAPTURE)) {
                        if(request.getOrigin().toString().equals("https://clicktocall.teleperformance.co/")) {
                            request.grant(request.getResources());

                        } else {
                            request.deny();
                        }
                    }
                }
            }
        });

    }

     //https://stackoverflow.com/questions/48702140/webview-https-handshake-failed
    /*class SSLTolerentWebViewClient extends WebViewClient {
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed(); // Ignore SSL certificate errors
        }
    }*/

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        /**
         * When the application falls into the background we want to stop the media stream
         * such that the camera is free to use by other apps.
         */
        webView.evaluateJavascript("if(window.localStream){window.localStream.stop();}", null);
    }
}
