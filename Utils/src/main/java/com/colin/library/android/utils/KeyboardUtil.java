package com.colin.library.android.utils;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;


/**
 * 作者： ColinLu
 * 时间： 2018-12-06 14:49
 * <p>
 * 描述： 键盘工具类
 */
public final class KeyboardUtil {
    private static int sDecorViewInvisibleHeightPre;
    private static ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener;
    private static Action sAction;
    private static int sContentViewInvisibleHeightPre5497;
    private static int sDecorViewDelta = 0;

    /**
     * 强制性弹出键盘
     *
     * @param activity
     */
    public static void showSoftInput(@Nullable Activity activity) {
        if (null == activity) return;
        View view = activity.getCurrentFocus();
        if (null == view) view = new View(activity);
        showSoftInput(view);
    }

    /**
     * 强制性弹出键盘
     *
     * @param view
     */
    public static void showSoftInput(@Nullable final View view) {
        if (null == view || null == view.getContext()) return;
        final InputMethodManager manager = AppUtil.getInputMethodManager();
        if (null == manager) return;
        ViewUtil.setFocusable(view, true);
        view.requestFocus();
        manager.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    /**
     * 强制性 隐藏键盘
     *
     * @param activity
     */
    public static void hideSoftInput(@Nullable final Activity activity) {
        if (null == activity) return;
        View view = activity.getCurrentFocus();
        if (null == view) view = new View(activity);
        hideSoftInput(view);
    }

    public static void hideSoftInput(@NonNull View view) {
        if (null == view.getContext()) return;
        InputMethodManager manager = AppUtil.getInputMethodManager();
        if (null == manager) return;
        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    private static int getDecorViewInvisibleHeight(final Activity activity) {
        final View decorView = activity.getWindow().getDecorView();
        if (decorView == null) return sDecorViewInvisibleHeightPre;
        final Rect outRect = new Rect();
        decorView.getWindowVisibleDisplayFrame(outRect);
        int delta = Math.abs(decorView.getBottom() - outRect.bottom);
        if (delta <= ResourceUtil.getNavBarHeight()) {
            sDecorViewDelta = delta;
            return 0;
        }
        return delta - sDecorViewDelta;
    }

    /**
     * 注册监听 Activity 弹出消失变化
     *
     * @param activity
     * @param action
     */
    public static void registerSoftInputChangedListener(final Activity activity, final Action action) {
        final int flags = activity.getWindow().getAttributes().flags;
        if ((flags & WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS) != 0) {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        final FrameLayout contentView = activity.findViewById(android.R.id.content);
        sDecorViewInvisibleHeightPre = getDecorViewInvisibleHeight(activity);
        sAction = action;
        onGlobalLayoutListener = () -> {
            if (sAction != null) {
                int height = getDecorViewInvisibleHeight(activity);
                if (sDecorViewInvisibleHeightPre != height) {
                    sAction.keyboard(height);
                    sDecorViewInvisibleHeightPre = height;
                }
            }
        };
        contentView.getViewTreeObserver().addOnGlobalLayoutListener(onGlobalLayoutListener);
    }

    /**
     * 解绑
     *
     * @param activity
     */
    public static void unregisterSoftInputChangedListener(final Activity activity) {
        final View contentView = activity.findViewById(android.R.id.content);
        contentView.getViewTreeObserver().removeOnGlobalLayoutListener(onGlobalLayoutListener);
        sAction = null;
        onGlobalLayoutListener = null;
    }


    ///////////////////////////////////////////////////////////////////////////
    // interface
    ///////////////////////////////////////////////////////////////////////////

    public interface Action {
        void keyboard(@Px int height);
    }
}
