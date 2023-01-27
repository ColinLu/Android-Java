package com.colin.library.android.widgets.edge;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import com.colin.library.android.utils.LogUtil;
import com.colin.library.android.widgets.Constants;
import com.colin.library.android.widgets.annotation.Direction;
import com.colin.library.android.widgets.annotation.Orientation;
import com.colin.library.android.widgets.annotation.ScrollState;

import java.lang.ref.WeakReference;

/**
 * 作者： ColinLu
 * 时间： 2023-01-27 15:58
 * <p>
 * 描述： TODO
 */
public final class EdgeHelper {
    @Nullable
    private final WeakReference<EdgeLayout> mEdgeLayoutRef;
    @Nullable
    private WeakReference<Edge> nEdgeLeftRef;
    @Nullable
    private WeakReference<Edge> nEdgeTopRef;
    @Nullable
    private WeakReference<Edge> nEdgeRightRef;
    @Nullable
    private WeakReference<Edge> nEdgeBottomRef;
    @Direction
    private int mDirectionEnabled;
    private long mEdgeScrollDuration = 500L;
    private OnEdgeListener mOnEdgeListener;

    public EdgeHelper(@NonNull EdgeLayout layout) {
        this.mEdgeLayoutRef = new WeakReference<>(layout);
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

    public void build(@Direction final int direction, @NonNull Edge.Builder builder) {
        switch (direction) {
            case Direction.LEFT:
                nEdgeLeftRef = new WeakReference<>(builder.build());
                break;
            case Direction.TOP:
                nEdgeTopRef = new WeakReference<>(builder.build());
                break;
            case Direction.RIGHT:
                nEdgeRightRef = new WeakReference<>(builder.build());
                break;
            case Direction.BOTTOM:
                nEdgeBottomRef = new WeakReference<>(builder.build());
                break;
            default:
                break;
        }
    }

    @Nullable
    public Edge getEdge(@Direction int direction) {
        switch (direction) {
            case Direction.LEFT:
                return nEdgeLeftRef == null ? null : nEdgeLeftRef.get();
            case Direction.TOP:
                return nEdgeTopRef == null ? null : nEdgeTopRef.get();
            case Direction.RIGHT:
                return nEdgeRightRef == null ? null : nEdgeRightRef.get();
            case Direction.BOTTOM:
                return nEdgeBottomRef == null ? null : nEdgeBottomRef.get();
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

    public void onLayout(final int width, final int height) {
        layout(getEdge(Direction.LEFT), width, height);
        layout(getEdge(Direction.TOP), width, height);
        layout(getEdge(Direction.RIGHT), width, height);
        layout(getEdge(Direction.BOTTOM), width, height);
    }


    public void layout(@Nullable final Edge edge, final int width, final int height) {
        if (edge != null) edge.layout(width, height);
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


    private boolean isStartNestedScrollHorizontal(@ViewCompat.ScrollAxis int axes) {
        return axes == ViewCompat.SCROLL_AXIS_HORIZONTAL && (isDirectionEnabled(Direction.LEFT) || isDirectionEnabled(Direction.RIGHT));
    }

    private boolean isStartNestedScrollVertical(@ViewCompat.ScrollAxis int axes) {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL && (isDirectionEnabled(Direction.TOP) || isDirectionEnabled(Direction.BOTTOM));
    }

    public boolean isHorizontalEnabled() {
        return isDirectionEnabled(Direction.LEFT) || isDirectionEnabled(Direction.RIGHT);
    }

    public boolean isVerticalEnabled() {
        return isDirectionEnabled(Direction.TOP) || isDirectionEnabled(Direction.BOTTOM);
    }
}
