package com.colin.library.android.widgets.web;

import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ClientCertRequest;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.HttpAuthHandler;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.SafeBrowsingResponse;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-07-16
 * <p>
 * Des   :WebView Client 回调
 */
public interface IWebClient {
    ///////////////////////////////////////////////////////////////////////////
    // WebViewClient
    ///////////////////////////////////////////////////////////////////////////
    default boolean shouldOverrideUrlLoading(@NonNull WebView view, @NonNull WebResourceRequest request) {
        return false;
    }

    default void onPageStarted(@NonNull WebView view, String url, Bitmap favicon) {
    }

    default void onPageFinished(@NonNull WebView view, String url) {

    }

    default void onLoadResource(@NonNull WebView view, String url) {
    }

    default void onPageCommitVisible(@NonNull WebView view, String url) {
    }

    @Nullable
    default WebResourceResponse shouldInterceptRequest(@NonNull WebView view, @NonNull WebResourceRequest request) {
        return null;
    }

    default void onReceivedError(@NonNull WebView view, @NonNull WebResourceRequest request, WebResourceError error) {

    }

    default void onReceivedHttpError(@NonNull WebView view, @NonNull WebResourceRequest request, @NonNull WebResourceResponse error) {

    }

    default void onReceivedSslError(@NonNull WebView view, @NonNull SslErrorHandler handler, @NonNull SslError error) {
        handler.cancel();
    }

    default void onFormResubmission(@NonNull WebView view, @NonNull Message dontResend, Message resend) {
        dontResend.sendToTarget();
    }

    default void doUpdateVisitedHistory(@NonNull WebView view, String url, boolean isReload) {
    }

    default void onReceivedClientCertRequest(@NonNull WebView view, @NonNull ClientCertRequest request) {
        request.cancel();
    }

    default void onReceivedHttpAuthRequest(@NonNull WebView view, @NonNull HttpAuthHandler handler, String host, String realm) {
        handler.cancel();
    }

    default boolean shouldOverrideKeyEvent(@NonNull WebView view, @NonNull KeyEvent event) {
        return false;
    }

    /*默认交给系统处理吧*/
    default boolean onUnhandledKeyEvent(@NonNull WebView view, @NonNull KeyEvent event) {
        return false;
    }

    default void onScaleChanged(@NonNull WebView view, float oldScale, float newScale) {

    }

    default void onReceivedLoginRequest(@NonNull WebView view, String realm, @Nullable String account, String args) {
    }

    default boolean onRenderProcessGone(@NonNull WebView view, @NonNull RenderProcessGoneDetail detail) {
        return false;
    }

    default void onSafeBrowsingHit(@NonNull WebView view, @NonNull WebResourceRequest request, int threatType, @NonNull SafeBrowsingResponse callback) {
        callback.showInterstitial(true);
    }

    ///////////////////////////////////////////////////////////////////////////
    // WebChromeClient
    ///////////////////////////////////////////////////////////////////////////
    default void onProgressChanged(@NonNull WebView view, int newProgress) {
    }

    default void onReceivedTitle(@NonNull WebView view, @Nullable String title) {
    }

    default void onReceivedIcon(@NonNull WebView view, @Nullable Bitmap icon) {
    }

    default void onReceivedTouchIconUrl(@NonNull WebView view, String url, boolean precomposed) {
    }

    default void onShowCustomView(@NonNull View view, WebChromeClient.CustomViewCallback callback) {
    }


    default void onHideCustomView() {
    }

    default boolean onCreateWindow(@NonNull WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
        return false;
    }

    default void onRequestFocus(@NonNull WebView view) {
    }

    default void onCloseWindow(@NonNull WebView window) {
    }

    default boolean onJsAlert(@NonNull WebView view, String url, String message, JsResult result) {
        return false;
    }

    default boolean onJsConfirm(@NonNull WebView view, String url, String message, JsResult result) {
        return false;
    }

    default boolean onJsPrompt(@NonNull WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        return false;
    }

    default boolean onJsBeforeUnload(@NonNull WebView view, String url, String message, JsResult result) {
        return false;
    }

    default void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
    }

    default void onGeolocationPermissionsHidePrompt() {
    }

    default void onPermissionRequest(@NonNull PermissionRequest request) {
        request.deny();
    }

    default void onPermissionRequestCanceled(@NonNull PermissionRequest request) {
    }

    default boolean onConsoleMessage(@NonNull ConsoleMessage consoleMessage) {
        return false;
    }

    @Nullable
    default Bitmap getDefaultVideoPoster() {
        return null;
    }

    @Nullable
    default View getVideoLoadingProgressView() {
        return null;
    }

    default void getVisitedHistory(@NonNull ValueCallback<String[]> callback) {
    }

    default boolean onShowFileChooser(@NonNull WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
        return false;
    }

}
