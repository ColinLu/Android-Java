package com.colin.library.android.widgets.edge;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Px;
import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;

import com.colin.library.android.widgets.annotation.Direction;
import com.colin.library.android.widgets.annotation.Orientation;

/**
 * Utility helper for moving a {@link View} around using
 * {@link View#offsetLeftAndRight(int)} and
 * {@link View#offsetTopAndBottom(int)}.
 * <p>
 * Also the setting of absolute offsets (similar to translationX/Y), rather than additive
 * offsets.
 */
public final class ViewOffsetHelper {
    private final View mView;
    private int mLayoutTop;
    private int mLayoutLeft;
    private int mOffsetTop;
    private int mOffsetLeft;

    private boolean mVerticalOffsetEnabled = true;
    private boolean mHorizontalOffsetEnabled = true;

    public ViewOffsetHelper(@NonNull View view) {
        mView = view;
    }

    public void onViewLayout() {
        onViewLayout(true);
    }

    public void onViewLayout(boolean applyOffset) {
        mLayoutTop = mView.getTop();
        mLayoutLeft = mView.getLeft();
        if (applyOffset) applyOffsets();
    }


    public boolean setDirection(@Direction int direction, @Px int offset) {
        switch (direction) {
            case Direction.LEFT:
                return setLeftAndRightOffset(offset);
            case Direction.TOP:
                return setTopAndBottomOffset(offset);
            case Direction.RIGHT:
                return setLeftAndRightOffset(-offset);
            case Direction.BOTTOM:
                return setTopAndBottomOffset(-offset);
            default:
                return false;
        }
    }

    public boolean setOrientationOffset(@Orientation int orientation, @Px int offset) {
        if (orientation == Orientation.HORIZONTAL) return setLeftAndRightOffset(offset);
        return setTopAndBottomOffset(offset);
    }

    public boolean setOffset(@Px int leftOffset, @Px int topOffset) {
        if (!mHorizontalOffsetEnabled && !mVerticalOffsetEnabled) return false;
        if (mHorizontalOffsetEnabled && mVerticalOffsetEnabled) {
            if (mOffsetLeft != leftOffset || mOffsetTop != topOffset) {
                mOffsetLeft = leftOffset;
                mOffsetTop = topOffset;
                applyOffsets();
                return true;
            }
            return false;
        } else if (mHorizontalOffsetEnabled) return setLeftAndRightOffset(leftOffset);
        else return setTopAndBottomOffset(topOffset);
    }

    /***
     * @param offset the offset in px.
     * @return true if the offset has changed
     */
    public boolean setTopAndBottomOffset(@Px int offset) {
        if (mVerticalOffsetEnabled && mOffsetTop != offset) {
            mOffsetTop = offset;
            applyOffsets();
            return true;
        }
        return false;
    }


    /***
     * @param offset the offset in px.
     * @return true if the offset has changed
     */
    public boolean setLeftAndRightOffset(@Px int offset) {
        if (mHorizontalOffsetEnabled && mOffsetLeft != offset) {
            mOffsetLeft = offset;
            applyOffsets();
            return true;
        }
        return false;
    }

    public int getTopAndBottomOffset() {
        return mOffsetTop;
    }

    public int getLeftAndRightOffset() {
        return mOffsetLeft;
    }

    public int getOffset(@Orientation int orientation) {
        return orientation == Orientation.HORIZONTAL ? mOffsetLeft : mOffsetTop;
    }

    public int getLayoutTop() {
        return mLayoutTop;
    }

    public int getLayoutLeft() {
        return mLayoutLeft;
    }

    public void setHorizontalOffsetEnabled(boolean enabled) {
        mHorizontalOffsetEnabled = enabled;
    }

    public boolean isHorizontalOffsetEnabled() {
        return mHorizontalOffsetEnabled;
    }

    public void setVerticalOffsetEnabled(boolean enabled) {
        mVerticalOffsetEnabled = enabled;
    }

    public boolean isVerticalOffsetEnabled() {
        return mVerticalOffsetEnabled;
    }

    public void applyOffsets() {
        ViewCompat.offsetTopAndBottom(mView, mOffsetTop - (mView.getTop() - mLayoutTop));
        ViewCompat.offsetLeftAndRight(mView, mOffsetLeft - (mView.getLeft() - mLayoutLeft));
    }
}