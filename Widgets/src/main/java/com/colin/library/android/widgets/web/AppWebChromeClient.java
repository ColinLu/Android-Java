package com.colin.library.android.widgets.web;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Message;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

/**
 * 作者： ColinLu
 * 时间： 2023-01-01 16:15
 * <p>
 * 描述： WebChromeClient主要辅助WebView处理JavaScript的对话框、网站图片、网站title、加载进度等比如
 */
public class AppWebChromeClient extends WebChromeClient {
    protected final IWebCallback mCallback;

    public AppWebChromeClient() {
        this(null);
    }

    public AppWebChromeClient(IWebCallback callback) {
        mCallback = callback;
    }

    // 设置网页加载的进度条
    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        if (null != mCallback) mCallback.onProgressChanged(view, newProgress);
        else super.onProgressChanged(view, newProgress);
    }

    // 设置程序的Title
    @Override
    public void onReceivedTitle(WebView view, String title) {
        if (null != mCallback) mCallback.onReceivedTitle(view, title);
        else super.onReceivedTitle(view, title);
    }

    @Override
    public void onReceivedIcon(WebView view, Bitmap icon) {
        if (null != mCallback) mCallback.onReceivedIcon(view, icon);
        else super.onReceivedIcon(view, icon);
    }

    /**
     * 通知加载当前网页的主机应用程序，获取手机触屏的图标
     *
     * @param view
     * @param url
     * @param precomposed
     */
    @Override
    public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
        if (null != mCallback) mCallback.onReceivedTouchIconUrl(view, url, precomposed);
    }

    // 通知应用当前页进入了全屏模式，此时应用必须显示一个包含网页内容的自定义View
    public void onShowCustomView(View view, WebChromeClient.CustomViewCallback callback) {
        if (null != mCallback) mCallback.onShowCustomView(view, callback);
        else super.onShowCustomView(view, callback);
    }

    @Override
    public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
        if (null != mCallback) mCallback.onShowCustomView(view, requestedOrientation, callback);
        else super.onShowCustomView(view, requestedOrientation, callback);
    }

    // 通知应用当前页退出了全屏模式，此时应用必须隐藏之前显示的自定义View
    public void onHideCustomView() {
        if (null != mCallback) mCallback.onHideCustomView();
    }

    @Override
    public boolean onCreateWindow(WebView webView, boolean isDialog, boolean isUserGesture, Message message) {
        if (mCallback != null && mCallback.onCreateWindow(webView, isDialog, isUserGesture, message))
            return true;
        return super.onCreateWindow(webView, isDialog, isUserGesture, message);
    }

    @Override
    public void onRequestFocus(WebView view) {
        if (mCallback != null) mCallback.onRequestFocus(view);
        else super.onRequestFocus(view);
    }

    /**
     * 通知主机程序关闭WebView同时需要的时候从系统视图中移除，此时，内核停止加载
     *
     * @param window
     */
    @Override
    public void onCloseWindow(WebView window) {
        if (null != mCallback) mCallback.onCloseWindow(window);
        else super.onCloseWindow(window);
    }

    // 告诉客户端显示一个JS弹窗，如果客户端返回true，WebView将会接受客户端处理弹窗；反之，返回false，程序继续执行
    @Override
    public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
        result.confirm();//这里必须调用，否则页面会阻塞造成假死
        if (null != mCallback) return mCallback.onJsAlert(view, url, message, result);
        return false;
    }

    // 告诉客户端向用户显示确认对话框，如果客户端返回true，同样，WebView将会接受客户端处理对话框和回调JsResult方法；反之，返回false，程序继续执行，默认返回false
    @Override
    public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
        if (null != mCallback) return mCallback.onJsConfirm(view, url, message, result);
        return super.onJsConfirm(view, url, message, result);
    }

    // 告诉客户端向用户显示警告弹窗，如果客户端返回true，同样，WebView将会接受客户端处理警告弹窗和回调JsPromptResult方法；反之，返回false，程序继续执行，默认返回false
    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, final JsPromptResult result) {
        if (null != mCallback)
            return mCallback.onJsPrompt(view, url, message, defaultValue, result);
        return super.onJsPrompt(view, url, message, defaultValue, result);
    }

    @Override
    public boolean onJsBeforeUnload(WebView webView, String url, String message, JsResult jsResult) {
        if (mCallback != null && mCallback.onJsBeforeUnload(webView, url, message, jsResult))
            return true;
        return super.onJsBeforeUnload(webView, url, message, jsResult);
    }

    /*提醒主机程序来自特殊数据源的网页内容想要使用定位API，当前设置没有允许状态。主机程序应该以期待的权限调用指定的回调，详细信息可以参考GeolocationPermissions*/
    @Override
    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
        if (mCallback != null) mCallback.onGeolocationPermissionsShowPrompt(origin, callback);
        else super.onGeolocationPermissionsShowPrompt(origin, callback);
    }

    //提醒主机程序请求地理位置权限，先前的调用onGeolocationPermissionsShowPrompt和onGeolocationPermissionsShowPrompt被取消，所有相关的UI因此被隐藏。
    @Override
    public void onGeolocationPermissionsHidePrompt() {
        if (null != mCallback) mCallback.onGeolocationPermissionsHidePrompt();
        else super.onGeolocationPermissionsHidePrompt();
    }

    @Override
    public void onPermissionRequest(PermissionRequest request) {
        if (mCallback != null) mCallback.onPermissionRequest(request);
        else super.onPermissionRequest(request);
    }

    @Override
    public void onPermissionRequestCanceled(PermissionRequest request) {
        if (mCallback != null) mCallback.onPermissionRequestCanceled(request);
        else super.onPermissionRequestCanceled(request);
    }


    @Override
    public void onConsoleMessage(String message, int lineNumber, String sourceID) {
        if (mCallback != null) mCallback.onConsoleMessage(message, lineNumber, sourceID);
        else super.onConsoleMessage(message, lineNumber, sourceID);
    }

    @Override
    public Bitmap getDefaultVideoPoster() {
        if (mCallback != null) return mCallback.getDefaultVideoPoster();
        return null;
    }

    @Override
    public View getVideoLoadingProgressView() {
        if (null != mCallback) return mCallback.getVideoLoadingProgressView();
        return super.getVideoLoadingProgressView();
    }

    @Override
    public void getVisitedHistory(ValueCallback<String[]> callback) {
        if (mCallback != null) mCallback.getVisitedHistory(callback);
        else super.getVisitedHistory(callback);
    }

    //android 5.0+
    @Override
    public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
        if (null != mCallback && mCallback.onShowFileChooser(webView, filePathCallback, fileChooserParams))
            return true;
        return super.onShowFileChooser(webView, filePathCallback, fileChooserParams);
    }

}
