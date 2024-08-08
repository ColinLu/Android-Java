package com.colin.library.android.utils;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 作者： ColinLu
 * 时间： 2020-10-27 21:36
 * <p>
 * 描述： View 工具类
 */
public final class ViewUtil {
    private ViewUtil() {
        throw new UnsupportedOperationException("don't instantiate");
    }

    public static void init(@Nullable final RecyclerView view, @Nullable final RecyclerView.Adapter<?> adapter) {
        if (view == null || view.getContext() == null) return;
        init(view, new LinearLayoutManager(view.getContext()), adapter, null, false);
    }

    public static void init(@Nullable final RecyclerView view, @Nullable final RecyclerView.Adapter<?> adapter, final int grid, final boolean scroll) {
        if (null == view || null == adapter) return;
        if (grid > 1) {
            init(view, new GridLayoutManager(view.getContext(), grid), adapter, null, scroll);
        } else {
            init(view, new LinearLayoutManager(view.getContext()), adapter, null, scroll);
        }
    }

    public static void init(@Nullable final RecyclerView view, @Nullable final RecyclerView.LayoutManager manager, @Nullable final RecyclerView.Adapter<?> adapter, @Nullable final RecyclerView.ItemAnimator animator, final boolean scroll) {
        if (null == view || null == manager || null == adapter) return;
        if (manager instanceof GridLayoutManager) {
            ((GridLayoutManager) manager).setRecycleChildrenOnDetach(true);
        } else if (manager instanceof LinearLayoutManager) {
            ((LinearLayoutManager) manager).setRecycleChildrenOnDetach(true);
        }
        view.setLayoutManager(manager);
        view.setItemViewCacheSize(10);
        view.setAdapter(adapter);
        view.setItemAnimator(animator);
        view.setFocusable(false);
        view.setNestedScrollingEnabled(!scroll);
    }

    /**
     * WebView 效果参数设置
     *
     * @param view
     */
    @SuppressLint("SetJavaScriptEnabled")
    public static void initSetting(@NonNull WebView view, boolean zoom) {
        // 设置支持JavaScript脚本
        WebSettings webSettings = view.getSettings();
        webSettings.setDefaultTextEncodingName("UTF-8");
        webSettings.setJavaScriptEnabled(true);// 设置可以运行JS脚本
        // 设置可以支持缩放
        webSettings.setSupportZoom(zoom);
        // 设置出现缩放工具
        webSettings.setBuiltInZoomControls(zoom);
        // 设置默认缩放方式尺寸是far
        webSettings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
        webSettings.setDefaultFontSize(20);
        // 允许js弹出窗口
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        //允许混合加载
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setSavePassword(false);
        if (zoom) {
            webSettings.setUseWideViewPort(true);// 打开页面时， 自适应屏幕
            webSettings.setLoadWithOverviewMode(true);// 打开页面时， 自适应屏幕
        }
        // webSettings.setTextZoom(120);//Sets the text zoom of the page in
        // percent. The default is 100.
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        view.setVerticalScrollBarEnabled(false);
        view.setHorizontalScrollBarEnabled(false);
        // 判断系统版本是不是5.0或之上, Android 5.0允许内容http https混合
        //让系统不屏蔽混合内容
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        WebView.setWebContentsDebuggingEnabled(true);
        view.setBackgroundColor(Color.parseColor("#00000000"));
        // 禁止即在网页顶出现一个空白，又自动回去。
        view.setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    /**
     * WebView 效果参数设置
     *
     * @param view WebView
     */
    @SuppressLint("SetJavaScriptEnabled")
    public static void init(@NonNull WebView view, WebViewClient webClient, WebChromeClient chromeClient) {
        WebSettings settings = view.getSettings();
        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        settings.setDomStorageEnabled(true);
        settings.setAllowContentAccess(true);
        settings.setAllowFileAccess(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false); //隐藏原生的缩放控件
        settings.setSupportMultipleWindows(false);
        settings.setDatabaseEnabled(true);
        settings.setGeolocationEnabled(true);
        view.setWebViewClient(webClient);
        view.setWebChromeClient(chromeClient);
    }

    /*自定义控件 onMeasure()*/
    @Px
    public static int getSize(int measureSpec, @Px final int def) {
        final int mode = View.MeasureSpec.getMode(measureSpec);
        switch (mode) {
            //具体数值，或者 MATCH_PARENT
            case View.MeasureSpec.EXACTLY:
                return View.MeasureSpec.getSize(mode);
            //WRAP_CONTENT
            case View.MeasureSpec.AT_MOST:
            case View.MeasureSpec.UNSPECIFIED:
            default:
                return def;
        }
    }

    /*控件设置焦点 可触摸 可点击*/
    public static void setFocusable(@Nullable final View view, final boolean focusable) {
        if (null == view) return;
        view.setFocusable(focusable);
        view.setFocusableInTouchMode(focusable);
    }


    /*控件是否可点击*/
    public static void setClickable(@Nullable View view, boolean clickable) {
        if (null == view || null == view.getContext()) return;
        view.setClickable(clickable);
        view.setEnabled(clickable);
    }

    public static void setEditable(@Nullable final EditText editText, final boolean editable) {
        if (null == editText) return;
        editText.setFocusable(editable);
        editText.setFocusableInTouchMode(editable);
        editText.setEnabled(editable);
    }


    /*webview 销毁*/
    public static void destroy(@Nullable final WebView view) {
        if (null == view) return;
        try {
            view.setVisibility(View.GONE);// 把destroy()延后
            view.loadUrl("about:blank");
            ViewGroup parent = (ViewGroup) view.getParent();
            parent.removeView(view);
            view.clearCache(true);
            view.clearMatches();
            view.clearFormData();
            view.clearSslPreferences();
            view.clearHistory();
            view.destroy();
        } catch (Exception e) {
            LogUtil.log(e);
        }
    }

}
