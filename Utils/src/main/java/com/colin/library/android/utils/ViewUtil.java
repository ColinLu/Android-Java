package com.colin.library.android.utils;

import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
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
        init(view, adapter, 0, false);
    }

    public static void init(@Nullable final RecyclerView view, @Nullable final RecyclerView.Adapter<?> adapter, final int grid, final boolean scroll) {
        if (null == view || null == adapter) return;
        if (grid > 1) {
            init(view, new GridLayoutManager(view.getContext(), grid), adapter, null, scroll);
        } else {
            init(view, new LinearLayoutManager(view.getContext()), adapter, null, scroll);
        }
    }

    public static void init(@Nullable final RecyclerView view,
                            @Nullable final RecyclerView.LayoutManager manager,
                            @Nullable final RecyclerView.Adapter<?> adapter,
                            @Nullable final RecyclerView.ItemAnimator animator,
                            final boolean scroll) {
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


}
