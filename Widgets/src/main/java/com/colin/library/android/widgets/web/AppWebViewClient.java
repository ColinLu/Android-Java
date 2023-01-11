package com.colin.library.android.widgets.web;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
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
import androidx.annotation.RequiresApi;
import androidx.annotation.WorkerThread;

import com.colin.library.android.utils.ActivityUtil;
import com.colin.library.android.utils.StringUtil;
import com.colin.library.android.widgets.web.bridge.BridgeUtil;

/**
 * 作者： ColinLu
 * 时间： 2023-01-01 16:15
 * <p>
 * 描述： WebViewClient 这个类主要帮助WebView处理各种通知、请求时间的
 */
public class AppWebViewClient extends WebViewClient {

    protected final IWebCallback mCallback;
    protected boolean mWebLoadFinish;
    protected boolean mLoadJsBridge = true;

    public AppWebViewClient() {
        this(null);
    }

    public AppWebViewClient(@Nullable IWebCallback callback) {
        this.mCallback = callback;
    }

    public boolean isWebLoadFinish() {
        return mWebLoadFinish;
    }

    public AppWebViewClient setWebLoadFinish(boolean webLoadFinish) {
        this.mWebLoadFinish = webLoadFinish;
        return this;
    }

    public boolean isLoadJsBridge() {
        return mLoadJsBridge;
    }

    public AppWebViewClient setLoadJsBridge(boolean loadJsBridge) {
        mLoadJsBridge = loadJsBridge;
        return this;
    }

    /**
     * 重要方法
     *
     * @param view
     * @param url
     * @return
     */
    @Deprecated
    @Override
    public boolean shouldOverrideUrlLoading(@NonNull WebView view, @Nullable String url) {
        if (!ActivityUtil.isAlive(view.getContext()) || StringUtil.isEmpty(url))
            return super.shouldOverrideUrlLoading(view, url);
        //Js
        if (isLoadJsBridge() && BridgeUtil.loadJsBridge(view, url)) return true;
        //接口拦截
        if (null != mCallback && mCallback.shouldOverrideUrlLoading(view, url)) return true;
        return super.shouldOverrideUrlLoading(view, url);

    }


    // 拦截页面加载，返回true表示宿主app拦截并处理了该url，否则返回false由当前WebView处理
    // 此方法添加于API24，不处理POST请求，可拦截处理子frame的非http请求
    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public boolean shouldOverrideUrlLoading(@NonNull WebView view, @NonNull WebResourceRequest request) {
        if (mCallback != null && mCallback.shouldOverrideUrlLoading(view, request)) return true;
        return super.shouldOverrideUrlLoading(view, request);
    }

    /**
     * 网页开始  进度弹框开始显示
     *
     * @param view
     * @param url
     * @param favicon
     */
    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        if (null != mCallback) mCallback.onPageStarted(view, url, favicon);
        else super.onPageStarted(view, url, favicon);
        mWebLoadFinish = false;
    }

    /**
     * 网页结束  进度弹框关闭
     *
     * @param view
     * @param url
     */
    @Override
    public void onPageFinished(@NonNull WebView view, String url) {
        mWebLoadFinish = true;
        //设置网页在加载的时候暂时不加载图片
        //webView.getSettings().setBlockNetworkImage(false);
        //页面finish后再发起图片加载
        if (!view.getSettings().getLoadsImagesAutomatically()) {
            view.getSettings().setLoadsImagesAutomatically(true);
        }
        if (mCallback != null) mCallback.onPageFinished(view, url);
        else super.onPageFinished(view, url);
        if (isLoadJsBridge()) BridgeUtil.insertJsBridge(view, url);
    }

    /**
     * 在加载页面资源时会调用，每一个资源（比如图片）的加载都会调用一次
     *
     * @param view
     * @param url
     */
    @Override
    public void onLoadResource(@NonNull WebView view, String url) {
        if (null != mCallback) mCallback.onLoadResource(view, url);
        else super.onLoadResource(view, url);
    }

    @Override
    public void onPageCommitVisible(@NonNull WebView view, String url) {
        if (mCallback != null) mCallback.onPageCommitVisible(view, url);
        else super.onPageCommitVisible(view, url);
    }

    @Deprecated
    @Nullable
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
        final WebResourceResponse response = null == mCallback ? null : mCallback.shouldInterceptRequest(view, url);
        return response != null ? response : super.shouldInterceptRequest(view, url);
    }

    @Nullable
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        final WebResourceResponse response = mCallback == null ? null : mCallback.shouldInterceptRequest(view, request);
        return response != null ? response : super.shouldInterceptRequest(view, request);
    }

    @Override
    public void onTooManyRedirects(WebView view, Message cancelMsg, Message continueMsg) {
        if (mCallback == null || !mCallback.onTooManyRedirects(view, cancelMsg, continueMsg)) {
            super.onTooManyRedirects(view, cancelMsg, continueMsg);
        }
    }

    /**
     * 没有网络连接
     * 连接超时
     * 找不到页面www.barryzhang.com
     * <p>
     * request.isForMainFrame()或者 request.getUrl().toString() .equals(getUrl()) 判定
     *
     * @param view
     * @param errorCode
     * @param description
     * @param failingUrl
     */
    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        if (null == mCallback || !mCallback.onReceivedError(view, errorCode, description, failingUrl)) {
            super.onReceivedError(view, errorCode, description, failingUrl);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
        if (mCallback == null || !mCallback.onReceivedError(view, request, error)) {
            super.onReceivedError(view, request, error);
        }
    }

    /**
     * 非UI线程中执行
     * <p>
     * WebView在请求加载一个页面的同时，还会发送一个请求图标文件的请求。
     * 比如我们采用WebView去加载一个页面：
     * webView.loadUrl("http://192.168.5.40:9006/sso_web/html/H5/doctor/aboutUs.html");
     * 同时还会发送一个请求图标文件的请求
     * http://192.168.5.40:9006/favicon.ico
     * onReceivedHttpError这个方法主要用于响应服务器返回的Http错误(状态码大于等于400)，这个回调将被调用任何资源（IFRAME，图像等），而不仅仅是主页面。
     * 所以就会出现主页面虽然加载成功，但由于网站没有favicon.ico文件导致返回404错误。
     *
     * @param view
     * @param webResourceRequest
     * @param webResourceResponse
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @WorkerThread
    @Override
    public void onReceivedHttpError(WebView view, WebResourceRequest webResourceRequest, WebResourceResponse webResourceResponse) {
        if (mCallback != null) {
            mCallback.onReceivedHttpError(view, webResourceRequest, webResourceResponse);
        } else super.onReceivedHttpError(view, webResourceRequest, webResourceResponse);
    }

    /**
     * 设置是否重发数据，post的请求的时候，默认不重新发送
     *
     * @param view
     * @param dontResend
     * @param resend
     */
    @Override
    public void onFormResubmission(WebView view, Message dontResend, Message resend) {
        if (mCallback == null || !mCallback.onFormResubmission(view, dontResend, resend)) {
            super.onFormResubmission(view, dontResend, resend);
        }
    }

    /**
     * 通知主机程序更新访问的链接
     *
     * @param view
     * @param url
     * @param isReload
     */
    @Override
    public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
        if (null != mCallback) mCallback.doUpdateVisitedHistory(view, url, isReload);
        else super.doUpdateVisitedHistory(view, url, isReload);
    }

    /**
     * HTTPS协议是通过SSL来通信的，所以当使用HTTPS通信的网址（以https://开头的网站）出现错误时,就会回调的该方法
     * <p>
     * 注意
     * 1 onReceivedSslError回调，不一定会回调onReceivedError的方法。
     * 2 加载默认的onReceivedSslError的会出现的白屏。
     * 3 可以忽略这个错误继续加载的
     * <p>
     * SslErrorHandler.proceed()表示忽略错误继续加载，默认实现
     * SslErrorHandler.cancel()表示取消加载
     *
     * @param view
     * @param handler
     * @param error
     */
    @Override
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
        if (mCallback == null || !mCallback.onReceivedSslError(view, handler, error)) {
            super.onReceivedSslError(view, handler, error);
        }
    }

    @Override
    public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
        if (mCallback == null || !mCallback.onReceivedClientCertRequest(view, request)) {
            super.onReceivedClientCertRequest(view, request);
        }
    }

    @Override
    public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
        if (mCallback == null || !mCallback.onReceivedHttpAuthRequest(view, handler, host, realm)) {
            super.onReceivedHttpAuthRequest(view, handler, host, realm);
        }
    }

    /**
     * 屏蔽一个按键的操作，
     * 返回false，则不屏蔽，交给webview处理，true反之
     *
     * @param webView
     * @param keyEvent
     * @return
     */
    @Override
    public boolean shouldOverrideKeyEvent(WebView webView, KeyEvent keyEvent) {
        if (mCallback != null && mCallback.shouldOverrideKeyEvent(webView, keyEvent)) {
            return true;
        }
        return super.shouldOverrideKeyEvent(webView, keyEvent);
    }

    @Override
    public void onUnhandledKeyEvent(WebView webView, KeyEvent keyEvent) {
        if (mCallback == null || !mCallback.onUnhandledKeyEvent(webView, keyEvent)) {
            super.onUnhandledKeyEvent(webView, keyEvent);
        }
    }

    /**
     * webView的发生缩放改变的时候调用
     *
     * @param view
     * @param oldScale
     * @param newScale
     */
    @Override
    public void onScaleChanged(WebView view, float oldScale, float newScale) {
        if (mCallback != null) mCallback.onScaleChanged(view, oldScale, newScale);
        else super.onScaleChanged(view, oldScale, newScale);
    }


    /**
     * 自动登录请求的回调方法
     *
     * @param view
     * @param realm
     * @param account
     * @param args
     */
    @Override
    public void onReceivedLoginRequest(WebView view, String realm, String account, String args) {
        if (null != mCallback) mCallback.onReceivedLoginRequest(view, realm, account, args);
        else super.onReceivedLoginRequest(view, realm, account, args);
    }

    @Override
    public boolean onRenderProcessGone(WebView view, RenderProcessGoneDetail detail) {
        if (mCallback != null && mCallback.onRenderProcessGone(view, detail)) return true;
        return super.onRenderProcessGone(view, detail);
    }

    @Override
    public void onSafeBrowsingHit(WebView view, WebResourceRequest request, int threatType, SafeBrowsingResponse callback) {
        if (mCallback == null || !mCallback.onSafeBrowsingHit(view, request, threatType, callback)) {
            super.onSafeBrowsingHit(view, request, threatType, callback);
        }
    }
}
