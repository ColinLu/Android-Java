package com.colin.library.android.utils;

import android.content.Context;
import android.content.res.Configuration;
import android.text.Editable;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.colin.library.android.provider.AppViewOutlineProvider;

/**
 * 作者： ColinLu
 * 时间： 2020-10-27 21:36
 * <p>
 * 描述： Widget 工具类
 */
public final class WidgetUtil {
    private WidgetUtil() {
        throw new UnsupportedOperationException("don't instantiate");
    }

    /*屏幕高度*/
    public static int getWindowHeight(@NonNull final Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /*屏幕宽度*/
    @Nullable
    public static int getWindowWidth(@NonNull final Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
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

    /*屏幕方向*/
    @RecyclerView.Orientation
    public static int getOrientation() {
        return getOrientation(ResourceUtil.getResources().getConfiguration());
    }

    /*屏幕方向*/
    @RecyclerView.Orientation
    public static int getOrientation(@NonNull Context context) {
        return getOrientation(ResourceUtil.getResources(context).getConfiguration());
    }

    /*屏幕方向*/
    @RecyclerView.Orientation
    public static int getOrientation(@NonNull Configuration config) {
        if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) return LinearLayoutManager.HORIZONTAL;
        return LinearLayoutManager.VERTICAL;
    }

    public static boolean scrollToLeft(@Nullable View view) {
        return view != null && view.canScrollHorizontally(-1);
    }

    public static boolean scrollToTop(@Nullable View view) {
        return view != null && view.canScrollVertically(-1);
    }

    public static boolean scrollToRight(@Nullable View view) {
        return view != null && view.canScrollHorizontally(1);
    }

    public static boolean scrollToBottom(@Nullable View view) {
        return view != null && view.canScrollVertically(1);
    }

    /*控件圆角*/
    public static void setRadius(@Nullable View view, @Px final float radius) {
        if (view != null) {
            view.setClipToOutline(true);
            view.setOutlineProvider(new AppViewOutlineProvider(radius));
        }
    }

    /*控件是否可点击*/
    public static void setClickable(@Nullable View view, final boolean clickable) {
        if (null == view || null == view.getContext()) return;
        view.setClickable(clickable);
        view.setEnabled(clickable);
    }

    /*控件设置焦点 可触摸 可点击*/
    public static void setFocusable(@Nullable View view, final boolean focusable) {
        if (null == view || null == view.getContext()) return;
        view.setFocusable(focusable);
        view.setFocusableInTouchMode(focusable);
    }


    /*显示/隐藏密码*/
    public static void password(@Nullable EditText editText, final boolean show) {
        if (null == editText || null == editText.getContext()) return;
        if (show) editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        else editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        final Editable text = editText.getText();
        final int selectPosition = null == text ? 0 : text.toString().trim().length();
        if (selectPosition > 0) editText.setSelection(selectPosition);
    }

    /*可输入可编辑*/
    public static void setEditable(@Nullable EditText editText, final boolean editable) {
        if (null == editText) return;
        editText.setFocusable(editable);
        editText.setFocusableInTouchMode(editable);
        editText.setEnabled(editable);
        if (editable) editText.requestFocus();
    }

}
