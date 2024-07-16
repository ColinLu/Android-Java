package com.colin.android.demo.java.ui.web;

import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Message;
import android.view.KeyEvent;
import android.webkit.ClientCertRequest;
import android.webkit.HttpAuthHandler;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.SafeBrowsingResponse;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.widgets.web.IWebClient;

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-07-16
 * <p>
 * Des   :自定义WebViewClient
 */
public class DemoWebViewClient extends WebViewClient {
    private final IWebClient mWebClient;

    public DemoWebViewClient(@Nullable IWebClient webClient) {
        this.mWebClient = webClient;
    }

    @Override
    public boolean shouldOverrideUrlLoading(@NonNull WebView view, @NonNull WebResourceRequest request) {
        return (mWebClient != null && mWebClient.shouldOverrideUrlLoading(view, request)) || super.shouldOverrideUrlLoading(view, request);
    }

    @Override
    public void onPageStarted(@NonNull WebView view, String url, Bitmap favicon) {
        if (mWebClient != null) mWebClient.onPageStarted(view, url, favicon);
        else super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(@NonNull WebView view, String url) {
        if (mWebClient != null) mWebClient.onPageFinished(view, url);
        else super.onPageFinished(view, url);
    }

    @Override
    public void onLoadResource(@NonNull WebView view, String url) {
        if (mWebClient != null) mWebClient.onLoadResource(view, url);
        else super.onLoadResource(view, url);
    }

    @Override
    public void onPageCommitVisible(@NonNull WebView view, String url) {
        if (mWebClient != null) mWebClient.onLoadResource(view, url);
        else super.onPageCommitVisible(view, url);
    }

    @Nullable
    @Override
    public WebResourceResponse shouldInterceptRequest(@NonNull WebView view, @NonNull WebResourceRequest request) {
        final WebResourceResponse response = mWebClient == null ? null : mWebClient.shouldInterceptRequest(view, request);
        return response == null ? super.shouldInterceptRequest(view, request) : response;
    }

    @Override
    public void onReceivedError(@NonNull WebView view, @NonNull WebResourceRequest request, WebResourceError error) {
        if (mWebClient != null) mWebClient.onReceivedError(view, request, error);
    }

    @Override
    public void onReceivedHttpError(@NonNull WebView view, @NonNull WebResourceRequest request, @NonNull WebResourceResponse errorResponse) {
        if (mWebClient != null) mWebClient.onReceivedHttpError(view, request, errorResponse);
        else super.onReceivedHttpError(view, request, errorResponse);

    }

    @Override
    public void onReceivedSslError(@NonNull WebView view, @NonNull SslErrorHandler handler, @NonNull SslError error) {
        if (mWebClient != null) mWebClient.onReceivedSslError(view, handler, error);
        else super.onReceivedSslError(view, handler, error);
    }

    @Override
    public void onFormResubmission(@NonNull WebView view, @NonNull Message dontResend, Message resend) {
        if (mWebClient != null) mWebClient.onFormResubmission(view, dontResend, resend);
        else super.onFormResubmission(view, dontResend, resend);
    }

    @Override
    public void doUpdateVisitedHistory(@NonNull WebView view, String url, boolean isReload) {
        if (mWebClient != null) mWebClient.doUpdateVisitedHistory(view, url, isReload);
        else super.doUpdateVisitedHistory(view, url, isReload);
    }

    @Override
    public void onReceivedClientCertRequest(@NonNull WebView view, @NonNull ClientCertRequest request) {
        if (mWebClient != null) mWebClient.onReceivedClientCertRequest(view, request);
        else super.onReceivedClientCertRequest(view, request);
    }

    @Override
    public void onReceivedHttpAuthRequest(@NonNull WebView view, @NonNull HttpAuthHandler handler, String host, String realm) {
        if (mWebClient != null) mWebClient.onReceivedHttpAuthRequest(view, handler, host, realm);
        else super.onReceivedHttpAuthRequest(view, handler, host, realm);
    }

    @Override
    public boolean shouldOverrideKeyEvent(@NonNull WebView view, @NonNull KeyEvent event) {
        return (mWebClient != null && mWebClient.shouldOverrideKeyEvent(view, event)) || super.shouldOverrideKeyEvent(view, event);
    }

    @Override
    public void onUnhandledKeyEvent(@NonNull WebView view, @NonNull KeyEvent event) {
        final boolean result = mWebClient != null && mWebClient.onUnhandledKeyEvent(view, event);
        if (!result) super.onUnhandledKeyEvent(view, event);
    }

    @Override
    public void onScaleChanged(@NonNull WebView view, float oldScale, float newScale) {
        if (mWebClient != null) mWebClient.onScaleChanged(view, oldScale, newScale);
        else super.onScaleChanged(view, oldScale, newScale);
    }

    @Override
    public void onReceivedLoginRequest(@NonNull WebView view, String realm, @Nullable String account, String args) {
        if (mWebClient != null) mWebClient.onReceivedLoginRequest(view, realm, account, args);
        else super.onReceivedLoginRequest(view, realm, account, args);
    }

    @Override
    public boolean onRenderProcessGone(@NonNull WebView view, @NonNull RenderProcessGoneDetail detail) {
        return (mWebClient != null && mWebClient.onRenderProcessGone(view, detail)) || super.onRenderProcessGone(view, detail);
    }

    @Override
    public void onSafeBrowsingHit(@NonNull WebView view, @NonNull WebResourceRequest request, int threatType, SafeBrowsingResponse callback) {
        if (mWebClient != null) mWebClient.onSafeBrowsingHit(view, request, threatType, callback);
        else super.onSafeBrowsingHit(view, request, threatType, callback);
    }
}
