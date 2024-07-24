package com.colin.android.demo.java.ui.web;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Message;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.utils.BitmapUtil;
import com.colin.library.android.widgets.web.IWebClient;

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-07-16
 * <p>
 * Des   :自定义WebChromeClient
 */
public class DemoWebChromeClient extends WebChromeClient {
    private final IWebClient mWebClient;

    public DemoWebChromeClient(@Nullable IWebClient webClient) {
        this.mWebClient = webClient;
    }

    @Override
    public void onProgressChanged(@NonNull WebView view, int newProgress) {
        if (mWebClient != null) mWebClient.onProgressChanged(view, newProgress);
        else super.onProgressChanged(view, newProgress);
    }

    @Override
    public void onReceivedTitle(@NonNull WebView view, @Nullable String title) {
        if (mWebClient != null) mWebClient.onReceivedTitle(view, title);
        else super.onReceivedTitle(view, title);
    }

    @Override
    public void onReceivedIcon(@NonNull WebView view, @Nullable Bitmap icon) {
        if (mWebClient != null) mWebClient.onReceivedIcon(view, icon);
        else super.onReceivedIcon(view, icon);
    }

    @Override
    public void onReceivedTouchIconUrl(@NonNull WebView view, String url, boolean precomposed) {
        if (mWebClient != null) mWebClient.onReceivedTouchIconUrl(view, url, precomposed);
        else super.onReceivedTouchIconUrl(view, url, precomposed);
    }

    @Override
    public void onShowCustomView(@NonNull View view, CustomViewCallback callback) {
        if (mWebClient != null) mWebClient.onShowCustomView(view, callback);
        else super.onShowCustomView(view, callback);
    }


    @Override
    public void onHideCustomView() {
        if (mWebClient != null) mWebClient.onHideCustomView();
        else super.onHideCustomView();
    }

    @Override
    public boolean onCreateWindow(@NonNull WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
        return (mWebClient != null && mWebClient.onCreateWindow(view, isDialog, isUserGesture, resultMsg)) || super.onCreateWindow(view, isDialog, isUserGesture, resultMsg);
    }

    @Override
    public void onRequestFocus(@NonNull WebView view) {
        if (mWebClient != null) mWebClient.onRequestFocus(view);
        else super.onRequestFocus(view);
    }

    @Override
    public void onCloseWindow(@NonNull WebView window) {
        if (mWebClient != null) mWebClient.onCloseWindow(window);
        else super.onCloseWindow(window);
    }

    @Override
    public boolean onJsAlert(@NonNull WebView view, String url, String message, JsResult result) {
        return (mWebClient != null && mWebClient.onJsAlert(view, url, message, result)) || super.onJsAlert(view, url, message, result);
    }

    @Override
    public boolean onJsConfirm(@NonNull WebView view, String url, String message, JsResult result) {
        return (mWebClient != null && mWebClient.onJsConfirm(view, url, message, result)) || super.onJsConfirm(view, url, message, result);
    }

    @Override
    public boolean onJsPrompt(@NonNull WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        return (mWebClient != null && mWebClient.onJsPrompt(view, url, message, defaultValue, result)) || super.onJsPrompt(view, url, message, defaultValue, result);
    }

    @Override
    public boolean onJsBeforeUnload(@NonNull WebView view, String url, String message, JsResult result) {
        return (mWebClient != null && mWebClient.onJsBeforeUnload(view, url, message, result)) || super.onJsBeforeUnload(view, url, message, result);
    }

    @Override
    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
        if (mWebClient != null) mWebClient.onGeolocationPermissionsShowPrompt(origin, callback);
        else super.onGeolocationPermissionsShowPrompt(origin, callback);
    }

    @Override
    public void onGeolocationPermissionsHidePrompt() {
        if (mWebClient != null) mWebClient.onGeolocationPermissionsHidePrompt();
        else super.onGeolocationPermissionsHidePrompt();
    }

    @Override
    public void onPermissionRequest(@NonNull PermissionRequest request) {
        if (mWebClient != null) mWebClient.onPermissionRequest(request);
        else super.onPermissionRequest(request);
    }

    @Override
    public void onPermissionRequestCanceled(@NonNull PermissionRequest request) {
        if (mWebClient != null) mWebClient.onPermissionRequestCanceled(request);
        else super.onPermissionRequestCanceled(request);
    }

    @Override
    public boolean onConsoleMessage(@NonNull ConsoleMessage consoleMessage) {
        return (mWebClient != null && mWebClient.onConsoleMessage(consoleMessage)) || super.onConsoleMessage(consoleMessage);

    }

    @Nullable
    @Override
    public Bitmap getDefaultVideoPoster() {
        final Bitmap bitmap = mWebClient == null ? null : mWebClient.getDefaultVideoPoster();
        return BitmapUtil.isEmpty(bitmap) ? super.getDefaultVideoPoster() : bitmap;
    }

    @Nullable
    @Override
    public View getVideoLoadingProgressView() {
        final View view = mWebClient == null ? null : mWebClient.getVideoLoadingProgressView();
        return view == null ? super.getVideoLoadingProgressView() : view;
    }

    /**
     * Obtains a list of all visited history items, used for link coloring
     */
    @Override
    public void getVisitedHistory(@NonNull ValueCallback<String[]> callback) {
        if (mWebClient != null) mWebClient.getVisitedHistory(callback);
        else super.getVisitedHistory(callback);
    }

    @Override
    public boolean onShowFileChooser(@NonNull WebView view, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        return (mWebClient != null && mWebClient.onShowFileChooser(view, filePathCallback, fileChooserParams)) || super.onShowFileChooser(view, filePathCallback, fileChooserParams);

    }
}
