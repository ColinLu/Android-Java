package com.colin.library.android.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.utils.LogUtil;

/**
 * 作者： ColinLu
 * 时间： 2021-08-22 16:45
 * <p>
 * 描述： Widgets辅助类
 */
public final class Utils {
    private static TypedValue sTmpValue;

    public static float getAttrFloat(@NonNull Context context, int attr) {
        return getAttrFloat(context.getTheme(), attr);
    }

    public static float getAttrFloat(@NonNull Resources.Theme theme, int attr) {
        if (sTmpValue == null) sTmpValue = new TypedValue();
        if (!theme.resolveAttribute(attr, sTmpValue, true)) return 0F;
        return sTmpValue.getFloat();
    }

    public static int getAttrColor(@NonNull Context context, int attr) {
        return getAttrColor(context.getTheme(), attr);
    }

    public static int getAttrColor(@NonNull Resources.Theme theme, int attr) {
        if (sTmpValue == null) sTmpValue = new TypedValue();
        if (!theme.resolveAttribute(attr, sTmpValue, true)) return 0;
        if (sTmpValue.type == TypedValue.TYPE_ATTRIBUTE) return getAttrColor(theme, sTmpValue.data);
        return sTmpValue.data;
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

    /*webview 销毁*/
    public static void destroy(@Nullable final WebView view) {
        if (null == view) return;
        view.setVisibility(View.GONE);// 把destroy()延后
        final long timeout = ViewConfiguration.getZoomControlsTimeout();
        try {
            view.postDelayed(() -> {
                view.clearCache(true);
                view.clearMatches();
                view.clearFormData();
                view.clearSslPreferences();
                view.clearHistory();
                view.destroy();
            }, timeout);
        } catch (Exception e) {
            LogUtil.log(e);
        }
    }

    /**
     * Retrieve the transformed bounding rect of an arbitrary descendant view.
     * This does not need to be a direct child.
     *
     * @param descendant descendant view to reference
     * @param out        rect to set to the bounds of the descendant view
     */
    public static void getDescendantRect(ViewGroup parent, View descendant, Rect out) {
        out.set(0, 0, descendant.getWidth(), descendant.getHeight());
        ViewGroupHelper.offsetDescendantRect(parent, descendant, out);
    }

    private static class ViewGroupHelper {
        private static final ThreadLocal<Matrix> MATRIX = new ThreadLocal<>();
        private static final ThreadLocal<RectF> RECT_F = new ThreadLocal<>();

        public static void offsetDescendantRect(ViewGroup group, View child, Rect rect) {
            Matrix m = MATRIX.get();
            if (m == null) {
                m = new Matrix();
                MATRIX.set(m);
            } else m.reset();
            m.preTranslate(-group.getScrollX(), -group.getScrollY());
            offsetDescendantMatrix(group, child, m);

            RectF rectF = RECT_F.get();
            if (rectF == null) {
                rectF = new RectF();
                RECT_F.set(rectF);
            }
            rectF.set(rect);
            m.mapRect(rectF);
            rect.set((int) (rectF.left + 0.5f), (int) (rectF.top + 0.5f), (int) (rectF.right + 0.5f), (int) (rectF.bottom + 0.5f));
        }

        static void offsetDescendantMatrix(ViewParent target, View view, Matrix m) {
            final ViewParent parent = view.getParent();
            if (parent instanceof View && parent != target) {
                final View vp = (View) parent;
                offsetDescendantMatrix(target, vp, m);
                m.preTranslate(-vp.getScrollX(), -vp.getScrollY());
            }

            m.preTranslate(view.getLeft(), view.getTop());
            if (!view.getMatrix().isIdentity()) m.preConcat(view.getMatrix());

        }
    }


}
