package com.colin.library.android.widgets.edge;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Px;
import androidx.core.view.ViewCompat;

import com.colin.library.android.widgets.Constants;
import com.colin.library.android.widgets.annotation.Direction;
import com.colin.library.android.widgets.edge.offset.AlwaysFollowOffsetCalculator;
import com.colin.library.android.widgets.edge.offset.IEdgeOffsetCalculator;


/**
 * 作者： ColinLu
 * 时间： 2021-03-06 12:06
 * <p>
 * 描述： 控件边缘
 */
public final class Edge {
    public static final float EDGE_RATE_DEFAULT = 0.45f;
    @NonNull
    private final View mView;
    @Direction
    private final int mDirection;
    private final int mStartOffset;
    private final int mTargetOffset;
    private final float mEdgeRate;
    private final float mScrollFling;
    private final float mScrollSpeed;
    private final boolean mEdgeOver;
    private final boolean mFlingFromTarget;
    private final boolean mScrollOffset;
    private final boolean mScrollTouchUp;
    private final IEdgeOffsetCalculator mOffsetCalculator;
    private final ViewOffsetHelper mViewOffsetHelper;
    private volatile boolean mRunning = false;


    public Edge(@NonNull Builder builder) {
        mView = builder.getView();
        mDirection = builder.getDirection();
        mEdgeOver = builder.isEdgeOver();
        mStartOffset = builder.getStartOffset();
        mTargetOffset = builder.getTargetOffset();
        mEdgeRate = builder.getEdgeRate();
        mScrollFling = builder.getScrollFling();
        mScrollSpeed = builder.getScrollSpeed();
        mFlingFromTarget = builder.isFlingFromTarget();
        mScrollOffset = builder.isScrollOffset();
        mScrollTouchUp = builder.isScrollTouchUp();
        mOffsetCalculator = builder.getOffsetCalculator();
        mViewOffsetHelper = new ViewOffsetHelper(builder.getView());
        updateOffset(mStartOffset);
    }

    public Edge setRunning(boolean running) {
        mRunning = running;
        return this;
    }

    @NonNull
    public View getView() {
        return mView;
    }

    @NonNull
    public int getDirection() {
        return mDirection;
    }

    @Px
    public int getEdgeSize() {
        return mDirection == Direction.TOP || mDirection == Direction.BOTTOM ? mView.getHeight() : mView.getWidth();
    }

    public int getStartOffset() {
        return mStartOffset;
    }

    public float getEdgeRate() {
        return mEdgeRate;
    }

    public float getEdgeRate(@ViewCompat.NestedScrollType int type) {
        return type == ViewCompat.TYPE_TOUCH ? mEdgeRate : 1.0F;
    }

    public boolean isEdgeOver() {
        return mEdgeOver;
    }

    public float getScrollFling() {
        return mScrollFling;
    }

    public float getScrollSpeed() {
        return mScrollSpeed;
    }

    public boolean isFlingFromTarget(@ViewCompat.NestedScrollType int type) {
        return type == ViewCompat.TYPE_TOUCH || mFlingFromTarget;
    }

    public boolean isFlingFromTarget() {
        return mFlingFromTarget;
    }

    public boolean isScrollOffset() {
        return mScrollOffset;
    }

    public boolean isScrollTouchUp() {
        return mScrollTouchUp;
    }

    public IEdgeOffsetCalculator getOffsetCalculator() {
        return mOffsetCalculator;
    }

    public boolean isRunning() {
        return mRunning;
    }


    public int getTargetOffset() {
        if (mTargetOffset != ViewGroup.LayoutParams.WRAP_CONTENT) return mTargetOffset;
        return getEdgeSize() - getStartOffset() * 2;
    }

    public float getFlingRate(@ViewCompat.NestedScrollType int type, @Px int offset) {
        if (type == ViewCompat.TYPE_TOUCH) return mEdgeRate;
        return Math.min(mEdgeRate, Math.max(mEdgeRate - (offset - getTargetOffset()) * getScrollFling(), 0));
    }

    public float getFlingRate(@Px int offset) {
        return Math.min(getEdgeRate(), Math.max(getEdgeRate() - (offset - getTargetOffset()) * getScrollFling(), 0));
    }

    public int getScrollOffset(int offset) {
        return (int) (mScrollSpeed * offset);
    }

    public void onLayout(final int width, final int height) {
        final int vw = mView.getMeasuredWidth(), vh = mView.getMeasuredHeight();
        int vc = 0;
        switch (mDirection) {
            case Direction.LEFT:
                vc = (height - vh) >> 1;
                mView.layout(-vw, vc, 0, vc + vh);
                mViewOffsetHelper.onViewLayout();
                break;
            case Direction.TOP:
                vc = (width - vw) >> 1;
                mView.layout(vc, -vh, vc + vw, 0);
                mViewOffsetHelper.onViewLayout();
                break;
            case Direction.RIGHT:
                vc = (height - vh) >> 1;
                mView.layout(width, vc, width + vw, vc + vh);
                mViewOffsetHelper.onViewLayout();
                break;
            case Direction.BOTTOM:
                vc = (width - vw) >> 1;
                mView.layout(vc, height, vc + vw, height + vh);
                mViewOffsetHelper.onViewLayout();
                break;
            default:
                break;
        }

    }

    public void scrollToTarget(int offset) {
        if (mDirection == Direction.LEFT || mDirection == Direction.TOP) {
            updateOffset(getOffsetCalculator().calculator(this, offset));
        } else {
            updateOffset(getOffsetCalculator().calculator(this, -offset));
        }
    }

    public void onTargetMoved(int targetOffset) {
        updateOffset(getOffsetCalculator().calculator(this, targetOffset));
    }

    public void updateOffset(int offset) {
        mViewOffsetHelper.setDirection(mDirection, offset);
        if (mView instanceof EdgeWatcher) ((EdgeWatcher) mView).offset(mView, offset);
    }

    public ViewOffsetHelper getViewOffsetHelper() {
        return mViewOffsetHelper;
    }

    public boolean isScrollFinal(int finalX, int finalY) {
        switch (mDirection) {
            case Direction.LEFT:
                return finalX == mTargetOffset;
            case Direction.TOP:
                return finalY == mTargetOffset;
            case Direction.RIGHT:
                return finalX == -mTargetOffset;
            case Direction.BOTTOM:
                return finalY == -mTargetOffset;
            default:
                return false;
        }
    }

    public int getTargetOffsetMax() {
        if (mDirection == Direction.RIGHT || mDirection == Direction.BOTTOM) return 0;
        else return mEdgeOver ? Integer.MAX_VALUE : mTargetOffset;
    }

    public int getTargetOffsetMin() {
        if (mDirection == Direction.LEFT || mDirection == Direction.TOP) return 0;
        else return mEdgeOver ? Integer.MIN_VALUE : -mTargetOffset;
    }

    public void scrollToTargetOffset(int offset) {

    }

    public static class Builder {
        @NonNull
        private final View mView;
        @Direction
        private final int mDirection;
        private int mStartOffset;
        private int mTargetOffset = ViewGroup.LayoutParams.WRAP_CONTENT;
        private float mEdgeRate = EDGE_RATE_DEFAULT;
        private float mScrollFling = Constants.SCROLL_FLING_DEFAULT;
        private float mScrollSpeed = Constants.SCROLL_SPEED_DEFAULT;
        private boolean mEdgeOver;
        private boolean mFlingFromTarget = true;
        private boolean mScrollOffset = false;
        private boolean mScrollTouchUp = true;
        private IEdgeOffsetCalculator mOffsetCalculator;

        public Builder(@NonNull View view, @Direction int direction) {
            this.mView = view;
            this.mDirection = direction;
        }

        public Builder setStartOffset(@Px int startOffset) {
            this.mStartOffset = startOffset;
            return this;
        }

        public Builder setTargetOffset(@Px int targetOffset) {
            this.mTargetOffset = targetOffset;
            return this;
        }

        public Builder setEdgeRate(float rate) {
            this.mEdgeRate = rate;
            return this;
        }

        public Builder setScrollFling(float scrollFling) {
            this.mScrollFling = scrollFling;
            return this;
        }

        public Builder setScrollSpeed(float scrollSpeed) {
            this.mScrollSpeed = scrollSpeed;
            return this;
        }

        public Builder setEdgeOver(boolean edgeOver) {
            this.mEdgeOver = edgeOver;
            return this;
        }

        public Builder setFlingFromTarget(boolean flingFromTarget) {
            this.mFlingFromTarget = flingFromTarget;
            return this;
        }

        public Builder setScrollOffset(boolean scrollOffset) {
            this.mScrollOffset = scrollOffset;
            return this;
        }

        public Builder setScrollTouchUp(boolean scrollTouchUp) {
            this.mScrollTouchUp = scrollTouchUp;
            return this;
        }

        public Builder setOffsetCalculator(@NonNull IEdgeOffsetCalculator offsetCalculator) {
            this.mOffsetCalculator = offsetCalculator;
            return this;
        }

        @NonNull
        public View getView() {
            return mView;
        }

        @Direction
        public int getDirection() {
            return mDirection;
        }

        public int getStartOffset() {
            return mStartOffset;
        }

        public int getTargetOffset() {
            return mTargetOffset;
        }

        public float getEdgeRate() {
            return mEdgeRate;
        }

        public float getScrollFling() {
            return mScrollFling;
        }

        public float getScrollSpeed() {
            return mScrollSpeed;
        }

        public boolean isEdgeOver() {
            return mEdgeOver;
        }

        public boolean isFlingFromTarget() {
            return mFlingFromTarget;
        }

        public boolean isScrollOffset() {
            return mScrollOffset;
        }

        public boolean isScrollTouchUp() {
            return mScrollTouchUp;
        }

        @NonNull
        public IEdgeOffsetCalculator getOffsetCalculator() {
            return null == mOffsetCalculator ? new AlwaysFollowOffsetCalculator() : mOffsetCalculator;
        }

        public Edge build() {
            return new Edge(this);
        }
    }

}
