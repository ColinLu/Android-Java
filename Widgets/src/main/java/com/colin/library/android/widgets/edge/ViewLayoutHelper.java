package com.colin.library.android.widgets.edge;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;

import java.lang.ref.WeakReference;

/**
 * 作者： ColinLu
 * 时间： 2023-02-01 20:29
 * <p>
 * 描述： TODO
 */
public final class ViewLayoutHelper {
    private final WeakReference<View> mViewRef;
    @Px
    private int mLayoutTop, mLayoutLeft;
    @Px
    private int mOffsetTop, mOffsetLeft;

    public ViewLayoutHelper(@NonNull View view) {
        this.mViewRef = new WeakReference<>(view);
    }

    public void onLayout() {
        onLayout(true);
    }

    private void onLayout(boolean invalidate) {
    }

    private void invalidate(@Nullable View view, int offsetX, int offsetY) {
        if (view == null) return;
        this.mLayoutTop = view.getTop();
        this.mLayoutLeft = view.getLeft();
    }

}
