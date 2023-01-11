package com.colin.library.android.widgets.web;

import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
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
import android.webkit.WebStorage;
import android.webkit.WebView;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.WorkerThread;

/**
 * 作者： ColinLu
 * 时间： 2023-01-01 16:16
 * <p>
 * 描述： WebChromeClient WebViewClient 接口回调
 */
public interface IWebCallback {
    //////////////////////////////////////////////WebViewClient开始////////////////////////////////////////////////////
    /*在点击请求的是连接是才会调用，重写此方法返回true表明点击网页里面的链接还是在当前的webView里跳转，不跳到浏览器里边  不使用POST“方法”请求。*/
    @Deprecated
    default boolean shouldOverrideUrlLoading(@NonNull WebView view, @Nullable String url) {
        return false;
    }

    default boolean shouldOverrideUrlLoading(@NonNull WebView view, @NonNull WebResourceRequest url) {
        return false;
    }

    /*网页加载开始 网页logo 可能为空*/
    default void onPageStarted(@NonNull WebView view, @Nullable String url, @Nullable Bitmap favicon) {
    }

    /*网页加载结束*/
    default void onPageFinished(@NonNull WebView view, @Nullable String url) {
    }

    /*在加载页面资源是会调用，每一个资源（比如图片）的加载都会调用一次*/
    default void onLoadResource(@NonNull WebView view, @Nullable String url) {
    }

    default void onPageCommitVisible(@NonNull WebView view, @Nullable String url) {
    }

    @Deprecated
    @Nullable
    default WebResourceResponse shouldInterceptRequest(@NonNull WebView view, @Nullable String url) {
        return null;
    }

    @Nullable
    default WebResourceResponse shouldInterceptRequest(@NonNull WebView view, @NonNull WebResourceRequest request) {
        return shouldInterceptRequest(view, request.getUrl().toString());
    }

    @Deprecated
    default boolean onTooManyRedirects(@NonNull WebView view, @NonNull Message cancelMsg, @NonNull Message continueMsg) {
        return false;
    }

    /*加载错误的时候会回调，在其中可做错误处理，比如再请求加载一次，或者提示404的错误页面*/
    @Deprecated
    default boolean onReceivedError(@NonNull WebView view, int errorCode, String description, String failingUrl) {
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    default boolean onReceivedError(@NonNull WebView view, @NonNull WebResourceRequest request, @NonNull WebResourceError error) {
        return false;
    }

    /*通知主机应用程序在加载资源时从服务器接收到HTTP错误。HTTP错误的状态代码>＝400。此回调将调用任何资源（IFRAME、图像等），而不只是针对主页*/
    @WorkerThread
    default void onReceivedHttpError(@NonNull WebView view, @NonNull WebResourceRequest webResourceRequest, WebResourceResponse webResourceResponse) {
    }

    /*设置是否重发数据，post的请求的时候，默认不重新发送*/
    default boolean onFormResubmission(@NonNull WebView view, @NonNull Message dontResend, @NonNull Message resend) {
        return false;
    }

    /*通知主机程序更新访问的链接*/
    default void doUpdateVisitedHistory(@NonNull WebView view, String url, boolean isReload) {
    }

    /**
     * HTTPS协议是通过SSL来通信的，所以当使用HTTPS通信的网址（以https://开头的网站）出现错误时,就会回调的该方法
     * 重写 此方法 一定要注释掉 super.onReceivedSslError(view, handler, error);
     */
    default boolean onReceivedSslError(@NonNull WebView view, @NonNull SslErrorHandler sslErrorHandler, SslError sslError) {
        return false;
    }

    default boolean onReceivedClientCertRequest(@NonNull WebView view, @NonNull ClientCertRequest clientCertRequest) {
        return false;
    }

    default boolean onReceivedHttpAuthRequest(@NonNull WebView view, @NonNull HttpAuthHandler handler, String host, String realm) {
        return false;
    }

    /**
     * 给主机应用程序一个同步处理key event的机会，例如菜单快捷键事件需要以这种方式过滤。
     * 如果返回true，WebView 将不处理key event 。
     * 如果返回false，WebView将始终处理键事件，
     * 因此视图链中的超级节点都不会看到key event。默认行为返回false
     */
    default boolean shouldOverrideKeyEvent(@NonNull WebView view, @NonNull KeyEvent keyEvent) {
        return false;
    }

    /*回调该方法，处理未被WebView处理的事件*/
    default boolean onUnhandledKeyEvent(@NonNull WebView view, @NonNull KeyEvent keyEvent) {
        return false;
    }

    /*WebView比例尺改变*/
    default void onScaleChanged(@NonNull WebView view, float oldScale, float newScale) {
    }

    default void onReceivedLoginRequest(@NonNull WebView view, @Nullable String realm, @Nullable String account, @Nullable String args) {
    }

    default boolean onRenderProcessGone(@NonNull WebView view, RenderProcessGoneDetail detail) {
        return false;
    }

    default boolean onSafeBrowsingHit(@NonNull WebView view, @NonNull WebResourceRequest request, int threatType, @NonNull SafeBrowsingResponse callback) {
        return false;
    }
    //////////////////////////////////////////////WebViewClient结束////////////////////////////////////////////////////


    //////////////////////////////////////////////WebChromeClient开始////////////////////////////////////////////////////
    /*通知主程序当前加载页面进度*/
    default void onProgressChanged(@NonNull WebView view, @IntRange(from = 0, to = 100) int newProgress) {
    }

    /*通知主程序document title变化*/
    default void onReceivedTitle(@NonNull WebView view, @Nullable String title) {
    }

    /*通知主程序当前页面有新的favicon*/
    default void onReceivedIcon(@NonNull WebView view, @Nullable Bitmap icon) {
    }

    /*通知主程序的apple-touch-icon的url。apple-touch-icon用于给苹果设备生成桌面快捷方式*/
    default void onReceivedTouchIconUrl(@NonNull WebView view, String url, boolean precomposed) {
    }

    default void onShowCustomView(@NonNull View view, WebChromeClient.CustomViewCallback callback) {
    }

    default void onShowCustomView(@NonNull View view, int requestedOrientation, WebChromeClient.CustomViewCallback callback) {
    }

    /*通知主程序当前页面已经退出全屏模式。主程序需要隐藏自定义view。*/
    default void onHideCustomView() {
    }

    default boolean onCreateWindow(@NonNull WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
        return false;
    }

    /*关闭WebView*/
    default void onRequestFocus(@NonNull WebView view) {
    }

    default void onCloseWindow(@NonNull WebView window) {
    }

    /*这仨是接收js三种对话框事件的，返回true则app处理，否则网页webview自己处理*/
    default boolean onJsAlert(@NonNull WebView view, String url, String message, final JsResult result) {
        return false;
    }

    default boolean onJsConfirm(@NonNull WebView view, String url, String message, final JsResult result) {
        return false;
    }

    default boolean onJsPrompt(@NonNull WebView view, String url, String message, String defaultValue, final JsPromptResult result) {
        return false;
    }

    /**
     * 告诉客户端显示一个对话框以确认导航离开当前页面。这是预先卸载JavaScript事件的结果。
     * 客户端返回true，WebVIEW将假定客户端将处理确认对话框并调用适当的JSREST方法。
     * 客户端返回false，则true值将返回到JavaScript以接受当前页面的导航。
     * 默认行为是返回false。将JSRESULT设置为true将导航离开当前页面，false将取消导航
     */
    default boolean onJsBeforeUnload(@NonNull WebView view, String url, String message, JsResult result) {
        return false;
    }

    @Deprecated
    default boolean onExceededDatabaseQuota(String url, String databaseIdentifier, long quota,
                                            long estimatedDatabaseSize, long totalQuota, WebStorage.QuotaUpdater quotaUpdater) {
        // This default implementation passes the current quota back to WebCore.
        // WebCore will interpret this that new quota was declined.
        return false;
    }

    @Deprecated
    default boolean onReachedMaxAppCacheSize(long requiredStorage, long quota, WebStorage.QuotaUpdater quotaUpdater) {
        return false;
    }

    /*通知主机应用程序，来自指定来源的Web内容试图使用地理定位API，但目前没有为该源设置许可状态。宿主应用程序应该调用具有所需权限状态的指定回调。有关详细信息，请参阅GeolocationPermissions。*/
    default boolean onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
        return false;
    }

    /*如果上面那个权限取消会调用这个，因为没有权限，所以相关ui应该被隐藏*/
    default void onGeolocationPermissionsHidePrompt() {
    }

    default void onPermissionRequest(@NonNull PermissionRequest request) {
    }

    default void onPermissionRequestCanceled(@NonNull PermissionRequest request) {
    }

    @Deprecated
    default boolean onJsTimeout() {
        return false;
    }

    @Deprecated
    default boolean onConsoleMessage(String message, int lineNumber, String sourceID) {
        return false;
    }

    default boolean onConsoleMessage(@NonNull ConsoleMessage consoleMessage) {
        return false;
    }

    /*当不播放时，视频元素由'poster' 图像表示。可以使用HTML中的视频标签的poster 属性来指定要使用的图像。如果属性不存在，则将使用默认poster 。此方法允许ChromeClient 提供默认图像*/
    @Nullable
    default Bitmap getDefaultVideoPoster() {
        return null;
    }

    /*获取在全屏视频正在缓冲的同时显示的视图。宿主应用程序可以重写此方法，以提供包含旋转器或类似物的视图。*/
    @Nullable
    default View getVideoLoadingProgressView() {
        return null;
    }

    default void getVisitedHistory(ValueCallback<String[]> callback) {
    }

    default boolean onShowFileChooser(@NonNull WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
        return false;
    }

}
