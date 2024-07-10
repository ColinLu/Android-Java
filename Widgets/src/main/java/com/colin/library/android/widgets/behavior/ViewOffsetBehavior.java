package com.colin.library.android.widgets.behavior;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

/**
 * 作者： ColinLu
 * 时间： 2022-12-31 18:52
 * <p>
 * 描述： Behavior 偏移
 */
public class ViewOffsetBehavior<V extends View> extends CoordinatorLayout.Behavior<V> {
    private ViewOffsetHelper mViewOffsetHelper;

    private int tempTopBottomOffset = 0;
    private int tempLeftRightOffset = 0;

    public ViewOffsetBehavior() {
    }

    public ViewOffsetBehavior(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onLayoutChild(@NonNull CoordinatorLayout parent, @NonNull V child, int layoutDirection) {
        // First let lay the child out
        layoutChild(parent, child, layoutDirection);
        if (mViewOffsetHelper == null) mViewOffsetHelper = new ViewOffsetHelper(child);
        mViewOffsetHelper.onViewLayout();

        if (tempTopBottomOffset != 0) {
            mViewOffsetHelper.setTopAndBottomOffset(tempTopBottomOffset);
            tempTopBottomOffset = 0;
        }
        if (tempLeftRightOffset != 0) {
            mViewOffsetHelper.setLeftAndRightOffset(tempLeftRightOffset);
            tempLeftRightOffset = 0;
        }

        return true;
    }

    protected void layoutChild(CoordinatorLayout parent, V child, int layoutDirection) {
        // Let the parent lay it out by default
        parent.onLayoutChild(child, layoutDirection);
    }

    public boolean setTopAndBottomOffset(int offset) {
        if (mViewOffsetHelper != null) return mViewOffsetHelper.setTopAndBottomOffset(offset);
        else tempTopBottomOffset = offset;
        return false;
    }

    public boolean setLeftAndRightOffset(int offset) {
        if (mViewOffsetHelper != null) return mViewOffsetHelper.setLeftAndRightOffset(offset);
        else tempLeftRightOffset = offset;
        return false;
    }

    public int getTopAndBottomOffset() {
        return mViewOffsetHelper == null ? 0 : mViewOffsetHelper.getTopAndBottomOffset();
    }

    public int getLeftAndRightOffset() {
        return mViewOffsetHelper == null ? 0 : mViewOffsetHelper.getLeftAndRightOffset();
    }

    public void setVerticalOffsetEnabled(boolean verticalOffsetEnabled) {
        if (mViewOffsetHelper != null) mViewOffsetHelper.setVerticalOffsetEnabled(verticalOffsetEnabled);
    }

    public int getLayoutTop() {
        return mViewOffsetHelper == null ? 0 : mViewOffsetHelper.getLayoutTop();
    }

    public int getLayoutLeft() {
        return mViewOffsetHelper == null ? 0 : mViewOffsetHelper.getLayoutLeft();
    }

    public boolean isVerticalOffsetEnabled() {
        return mViewOffsetHelper != null && mViewOffsetHelper.isVerticalOffsetEnabled();
    }

    public void setHorizontalOffsetEnabled(boolean horizontalOffsetEnabled) {
        if (mViewOffsetHelper != null) {
            mViewOffsetHelper.setHorizontalOffsetEnabled(horizontalOffsetEnabled);
        }
    }

    public boolean isHorizontalOffsetEnabled() {
        return mViewOffsetHelper != null && mViewOffsetHelper.isHorizontalOffsetEnabled();
    }
}
