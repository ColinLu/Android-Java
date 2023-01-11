package com.colin.library.android.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.Dimension;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;

import com.colin.library.android.utils.LogUtil;
import com.colin.library.android.widgets.web.AppWebChromeClient;
import com.colin.library.android.widgets.web.AppWebViewClient;
import com.colin.library.android.widgets.web.IWebCallback;

/**
 * 作者： ColinLu
 * 时间： 2021-08-22 16:45
 * <p>
 * 描述： Widgets辅助类
 */
public final class Utils {

    private static final float DENSITY_OFFSET = 0.5F;
    private static TypedValue sTmpValue;

    /*资源 颜色*/
    @ColorInt
    public static int getAttrColor(@NonNull Context context, @AttrRes int attrRes) {
        return getAttrColor(context.getTheme(), attrRes);
    }

    /*资源 颜色*/
    @ColorInt
    public static int getAttrColor(@NonNull Resources.Theme theme, @AttrRes int attrRes) {
        if (null == sTmpValue) sTmpValue = new TypedValue();
        if (!theme.resolveAttribute(attrRes, sTmpValue, true)) return Color.TRANSPARENT;
        if (sTmpValue.type == TypedValue.TYPE_ATTRIBUTE) return getAttrColor(theme, sTmpValue.data);
        return sTmpValue.data;
    }

    /*资源*/
    @Nullable
    public static Drawable getAttrDrawable(@NonNull Context context, @NonNull Resources.Theme theme, @AttrRes int attrRes) {
        if (attrRes == Resources.ID_NULL) return null;
        if (sTmpValue == null) sTmpValue = new TypedValue();

        if (!theme.resolveAttribute(attrRes, sTmpValue, true)) return null;
        if (sTmpValue.type >= TypedValue.TYPE_FIRST_COLOR_INT && sTmpValue.type <= TypedValue.TYPE_LAST_COLOR_INT) {
            return new ColorDrawable(sTmpValue.data);
        }
        if (sTmpValue.type == TypedValue.TYPE_ATTRIBUTE) {
            return getAttrDrawable(context, theme, sTmpValue.data);
        }

        if (sTmpValue.resourceId != Resources.ID_NULL) {
            return getVectorDrawable(context, sTmpValue.resourceId);
        }
        return null;
    }

    @Nullable
    public static Drawable getAttrDrawable(Context context, TypedArray typedArray, int index) {
        final TypedValue value = typedArray.peekValue(index);
        if (value != null && value.type != TypedValue.TYPE_ATTRIBUTE && value.resourceId != Resources.ID_NULL) {
            return getVectorDrawable(context, value.resourceId);
        }
        return null;
    }

    @Nullable
    public static Drawable getVectorDrawable(@NonNull final Context context, @DrawableRes final int res) {
        return AppCompatResources.getDrawable(context, res);
    }


    /*屏幕高度*/
    public static int getWindowHeight(@NonNull final Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /*屏幕宽度*/
    public static int getWindowWidth(@NonNull final Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /*将px值转换为sp值，保证文字大小不变*/
    public static float px2sp(@NonNull final Context context, final float px) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return px / fontScale + DENSITY_OFFSET;
    }

    /*将px值转换为sp值，保证文字大小不变*/
    public static int px2sp(@NonNull final Context context, final int px) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (px / fontScale + DENSITY_OFFSET);
    }

    /*将px值转换为sp值，保证文字大小不变*/
    public static float sp2px(@NonNull final Context context, final float sp) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return sp * fontScale + DENSITY_OFFSET;
    }

    /*将px值转换为sp值，保证文字大小不变*/
    public static int sp2px(@NonNull final Context context, final int sp) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * fontScale + DENSITY_OFFSET);
    }

    /*根据手机的分辨率从 dp 的单位 转成为 px(像素)*/
    public static float dp2px(@NonNull final Context context, final float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return dp * scale + DENSITY_OFFSET;
    }

    /*根据手机的分辨率从 dp 的单位 转成为 px(像素)*/
    public static int dp2px(@NonNull final Context context, final int dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) ((dp * scale) + DENSITY_OFFSET);
    }

    public static float dpToPx(@NonNull Context context, @Dimension(unit = Dimension.DP) int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

    /*根据手机的分辨率从 dp 的单位 转成为 px(像素)*/
    public static float px2dp(@NonNull final Context context, final float px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return px / scale + DENSITY_OFFSET;
    }

    /*根据手机的分辨率从 dp 的单位 转成为 px(像素)*/
    public static int px2dp(@NonNull final Context context, final int px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + DENSITY_OFFSET);
    }


    /*资源ID 转 颜色*/
    @ColorInt
    public static int getColor(@Nullable final Context context, @ColorRes final int res) {
        if (null == context || res == Resources.ID_NULL) return Color.TRANSPARENT;
        return ContextCompat.getColor(context, res);
    }

    /*资源ID 转 Drawable*/
    @Nullable
    public static Drawable getDrawable(@Nullable final Context context, @DrawableRes final int res) {
        if (null == context || res == Resources.ID_NULL) return null;
        return ContextCompat.getDrawable(context, res);
    }

    /*自定义控件 onMeasure()*/
    @Px
    public static int getSize(final int measureSpec, @Px final int def) {
        final int mode = View.MeasureSpec.getMode(measureSpec);
        switch (mode) {
            case View.MeasureSpec.EXACTLY:          //具体数值，或者 MATCH_PARENT
                return View.MeasureSpec.getSize(mode);
            case View.MeasureSpec.AT_MOST:          //WRAP_CONTENT
            case View.MeasureSpec.UNSPECIFIED:
            default:
                return def;
        }
    }

    /*控件圆角*/
    public static void setRadius(@Nullable final View view, @Px final float radius) {
        if (view != null) {
            view.setClipToOutline(true);
            view.setOutlineProvider(new RoundOutlineProvider(radius));
        }
    }

    /*控件是否可点击*/
    public static void setClickable(@Nullable final View view, final boolean clickable) {
        if (null == view || null == view.getContext()) return;
        view.setClickable(clickable);
        view.setEnabled(clickable);
    }

    /*控件设置焦点 可触摸 可点击*/
    public static void setFocusable(@Nullable final View view, final boolean focusable) {
        if (null == view || null == view.getContext()) return;
        view.setFocusable(focusable);
        view.setFocusableInTouchMode(focusable);
    }


    /*显示/隐藏密码*/
    public static void password(@Nullable final EditText editText, final boolean show) {
        if (null == editText || null == editText.getContext()) return;
        if (show) editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        else editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        final Editable text = editText.getText();
        final int selectPosition = null == text ? 0 : text.toString().trim().length();
        if (selectPosition > 0) editText.setSelection(selectPosition);
    }

    /*可输入可编辑*/
    public static void setEditable(@Nullable final EditText editText, final boolean editable) {
        if (null == editText) return;
        editText.setFocusable(editable);
        editText.setFocusableInTouchMode(editable);
        editText.setEnabled(editable);
        if (editable) editText.requestFocus();
    }

    public static void init(@Nullable final WebView view,@Nullable String url) {
        init(view, url, null);
    }

    public static void init(@Nullable final WebView view, @Nullable String url, @Nullable IWebCallback callback) {
        if (null == view || null == view.getContext()) return;
        view.setWebChromeClient(new AppWebChromeClient(callback));
        view.setWebViewClient(new AppWebViewClient(callback));
        initSetting(view, false);
        view.loadUrl(url);
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
        // 设置可以访问文件 localStorage
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheMaxSize(1024 * 1024 * 8);
        webSettings.setAppCachePath(view.getContext().getDir("appcache", 0).getPath());

        webSettings.setAllowFileAccess(true);
        webSettings.setAppCacheEnabled(true);
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
            rect.set((int) (rectF.left + 0.5f), (int) (rectF.top + 0.5f),
                    (int) (rectF.right + 0.5f), (int) (rectF.bottom + 0.5f));
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
