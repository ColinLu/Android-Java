package com.colin.library.android.widgets.edge;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.core.view.ViewCompat;

import com.colin.library.android.utils.LogUtil;
import com.colin.library.android.widgets.annotation.Direction;
import com.colin.library.android.widgets.behavior.ViewOffsetHelper;

import java.lang.ref.WeakReference;

/**
 * 作者： ColinLu
 * 时间： 2023-01-27 15:58
 * <p>
 * 描述： TODO
 */
public final class EdgeHelper {
    @Nullable
    private final WeakReference<View> mViewRef;
    @Nullable
    private View mTargetView;
    private Edge mEdgeLeft;
    @Nullable
    private Edge mEdgeTop;
    @Nullable
    private Edge mEdgeRight;
    @Nullable
    private Edge mEdgeBottom;
    @Direction
    private int mDirectionEnabled;
    private long mEdgeScrollDuration = 500L;
    private int mEdgeLayoutLeft;
    private int mEdgeLayoutTop;
    private int mEdgeLayoutRight;
    private int mEdgeLayoutBottom;

    private ViewOffsetHelper mTargetViewOffsetHelper;
    private OnEdgeListener mOnEdgeListener;

    public EdgeHelper(@NonNull View view) {
        this.mViewRef = new WeakReference<>(view);
    }


    public void setTargetView(@NonNull View view) {
        this.mTargetView = view;
        this.mTargetViewOffsetHelper = new ViewOffsetHelper(view);
    }

    @Nullable
    public View getTargetView() {
        return mTargetView;
    }

    public void setEdgeView(@Direction final int direction, @NonNull Edge edge) {
        switch (direction) {
            case Direction.LEFT:
                mEdgeLeft = edge;
                break;
            case Direction.TOP:
                mEdgeTop = edge;
                break;
            case Direction.RIGHT:
                mEdgeRight = edge;
                break;
            case Direction.BOTTOM:
                mEdgeBottom = edge;
                break;
            default:
                break;
        }
    }

    public void onLayout(@NonNull ViewGroup layout, @Px int left, @Px int top, @Px int right, @Px int bottom) {
        final int width = layout.getMeasuredWidth();
        final int height = layout.getMeasuredHeight();
        final int childLeft = layout.getPaddingLeft();
        final int childTop = layout.getPaddingTop();
        final int childWidth = width - childLeft - layout.getPaddingRight();
        final int childHeight = height - childTop - layout.getPaddingBottom();
        if (mTargetView != null) {
            mTargetView.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
        }
        if (mEdgeLeft != null) mEdgeLeft.layout(width, height);
        if (mEdgeTop != null) mEdgeTop.layout(width, height);
        if (mEdgeRight != null) mEdgeRight.layout(width, height);
        if (mEdgeBottom != null) mEdgeBottom.layout(width, height);
    }

    @Nullable
    public Edge getEdge(@Direction int direction) {
        switch (direction) {
            case Direction.LEFT:
                return mEdgeLeft;
            case Direction.TOP:
                return mEdgeTop;
            case Direction.RIGHT:
                return mEdgeRight;
            case Direction.BOTTOM:
                return mEdgeBottom;
            default:
                return null;
        }
    }

    @Nullable
    public Edge getEnabledEdge(@Direction int direction) {
        return isDirectionEnabled(direction) ? getEdge(direction) : null;
    }

    public void onMeasure(int width, int height) {
        measure(getEdge(Direction.LEFT), width, height);
        measure(getEdge(Direction.TOP), width, height);
        measure(getEdge(Direction.RIGHT), width, height);
        measure(getEdge(Direction.BOTTOM), width, height);
    }

    private void measure(@Nullable Edge edge, final int width, final int height) {
        if (edge != null) edge.measure(width, height);
    }


    public void setDirectionEnabled(@Direction final int enabled) {
        this.mDirectionEnabled = enabled;
    }

    @Direction
    public int getDirectionEnabled() {
        return mDirectionEnabled;
    }

    public boolean isDirectionEnabled(@Direction final int direction) {
        return (mDirectionEnabled & direction) == direction;
    }


    public void setEdgeListener(@Nullable OnEdgeListener edgeListener) {
        this.mOnEdgeListener = edgeListener;
    }

    public void scroll(int offsetX, int offsetY) {
        if (offsetX >= 0) scroll(getEnabledEdge(Direction.LEFT), offsetX);
        if (offsetY >= 0) scroll(getEnabledEdge(Direction.TOP), offsetY);
        if (offsetX <= 0) scroll(getEnabledEdge(Direction.RIGHT), offsetX);
        if (offsetY <= 0) scroll(getEnabledEdge(Direction.BOTTOM), offsetY);
    }

    private void scroll(@Nullable Edge edge, int offset) {
        if (edge != null) edge.updateOffset(offset);
    }


    public void startNestedScroll(int axes, int type) {

    }

    public void stopNestedScroll(int type) {

    }

    public boolean isRunning() {
        return isLeftRunning() || isTopRunning() || isRightRunning() || isBottomRunning();
    }

    public boolean isLeftRunning() {
        final Edge edge = getEnabledEdge(Direction.LEFT);
        return edge != null && edge.isRunning();
    }

    public boolean isTopRunning() {
        final Edge edge = getEnabledEdge(Direction.TOP);
        return edge != null && edge.isRunning();
    }

    public boolean isRightRunning() {
        final Edge edge = getEnabledEdge(Direction.RIGHT);
        return edge != null && edge.isRunning();
    }

    public boolean isBottomRunning() {
        final Edge edge = getEnabledEdge(Direction.BOTTOM);
        return edge != null && edge.isRunning();
    }


    public void start(@Direction int direction) {
        final Edge edge = getEnabledEdge(direction);
        if (edge == null || edge.isRunning()) return;
        LogUtil.i(edge.toString());
        edge.setRunning(true);
        if (mOnEdgeListener != null) mOnEdgeListener.start(edge);

    }

    public void offset(@Direction int direction, @Px int px) {

    }

    public void finish() {
        finish(getEnabledEdge(Direction.LEFT), true);
        finish(getEnabledEdge(Direction.TOP), true);
        finish(getEnabledEdge(Direction.RIGHT), true);
        finish(getEnabledEdge(Direction.BOTTOM), true);
    }

    public void finish(@Direction int direction) {
        finish(getEnabledEdge(direction), true);
    }

    public void finish(@Nullable Edge edge, boolean animate) {
        if (edge == null || !edge.isRunning()) return;
        LogUtil.i(edge.toString());
        if (!animate) {
            edge.setRunning(false);
            edge.updateOffset(0);
            if (mOnEdgeListener != null) mOnEdgeListener.finish(edge);
        } else {

        }
    }


    private void scrollTo(int offsetX, int offsetY, int dx, int dy, int duration) {

    }

    public void setScrollDuration(long duration) {
        this.mEdgeScrollDuration = duration;
    }


    public boolean isNestedScrollEnabled(@ViewCompat.ScrollAxis int axes) {
        return isNestedScrollVertical(axes) || isNestedScrollHorizontal(axes);
    }

    private boolean isNestedScrollHorizontal(@ViewCompat.ScrollAxis int axes) {
        return (axes & ViewCompat.SCROLL_AXIS_HORIZONTAL) != 0 && (isDirectionEnabled(Direction.LEFT) || isDirectionEnabled(Direction.RIGHT));
    }

    private boolean isNestedScrollVertical(@ViewCompat.ScrollAxis int axes) {
        return (axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0 && (isDirectionEnabled(Direction.TOP) || isDirectionEnabled(Direction.BOTTOM));
    }

    private boolean isVertical(@Direction int direction) {
        return direction == Direction.TOP || direction == Direction.BOTTOM;
    }

    public void setOffset(@Px int offsetX, @Px int offsetY) {
        if (offsetX >= 0) setOffset(getEnabledEdge(Direction.LEFT), offsetX);
        if (offsetY >= 0) setOffset(getEnabledEdge(Direction.TOP), offsetY);
        if (offsetX <= 0) setOffset(getEnabledEdge(Direction.RIGHT), offsetX);
        if (offsetY <= 0) setOffset(getEnabledEdge(Direction.BOTTOM), offsetY);
    }

    public void setOffset(@Nullable Edge edge, @Px int offset) {
        if (edge == null) return;
        LogUtil.log("setOffset:direction:%d,offset:%d", edge.getDirection(), offset);
        edge.setOffset(offset);
    }

    public void interceptTouchEventActionDown(MotionEvent ev) {

        updateOffset(getEnabledEdge(Direction.TOP));
    }

    private void updateOffset(@Nullable Edge edge) {
        if (edge == null) return;
        switch (edge.getDirection()) {
            case Direction.LEFT:
                break;
            case Direction.TOP:
                ViewCompat.offsetTopAndBottom(edge.getView(), 0);
                break;
            default:
                break;
        }
    }

    public void actionStart(@ViewCompat.ScrollAxis int axes) {
        if ((axes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0) {
            LogUtil.log("垂直方向滑动：actionStart");
        } else if ((axes & ViewCompat.SCROLL_AXIS_HORIZONTAL) != 0) {
            LogUtil.log("水平方向滑动：actionStart");
        }
    }


}
