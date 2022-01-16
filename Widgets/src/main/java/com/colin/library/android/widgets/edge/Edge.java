package com.colin.library.android.widgets.edge;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Px;

import com.colin.library.android.widgets.Constants;
import com.colin.library.android.widgets.annotation.Direction;


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

    public boolean isEdgeOver() {
        return mEdgeOver;
    }

    public float getScrollFling() {
        return mScrollFling;
    }

    public float getScrollSpeed() {
        return mScrollSpeed;
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

    public float getFlingRate(@Px int offset) {
        return Math.min(getEdgeRate(), Math.max(getEdgeRate() - (offset - getTargetOffset()) * getScrollFling(), 0));
    }

    public void onLayout(final int width, final int height, final int left, final int top, final int right, final int bottom) {
        if (mDirection == Direction.LEFT) {
            int vw = mView.getMeasuredWidth(), vh = mView.getMeasuredHeight(), vc = (height - vh) >> 1;
            mView.layout(-vw, vc, 0, vc + vh);
            mViewOffsetHelper.onViewLayout();
        }
        if (mDirection == Direction.TOP) {
            int vw = mView.getMeasuredWidth(), vh = mView.getMeasuredHeight(), vc = (width - vw) >> 1;
            mView.layout(vc, -vh, vc + vw, 0);
            mViewOffsetHelper.onViewLayout();
        }

        if (mDirection == Direction.RIGHT) {
            int vw = mView.getMeasuredWidth(), vh = mView.getMeasuredHeight(), vc = (height - vh) >> 1;
            mView.layout(width, vc, width + vw, vc + vh);
            mViewOffsetHelper.onViewLayout();
        }

        if (mDirection == Direction.BOTTOM) {
            int vw = mView.getMeasuredWidth(), vh = mView.getMeasuredHeight(), vc = (width - vw) >> 1;
            mView.layout(vc, height, vc + vw, height + vh);
            mViewOffsetHelper.onViewLayout();
        }
    }

    public void onTargetMoved(int targetOffset) {
        updateOffset(getOffsetCalculator().calculator(this, targetOffset));
    }

    public void updateOffset(int offset) {
        if (mDirection == Direction.LEFT) mViewOffsetHelper.setLeftAndRightOffset(offset);
        else if (mDirection == Direction.TOP) mViewOffsetHelper.setTopAndBottomOffset(offset);
        else if (mDirection == Direction.RIGHT) mViewOffsetHelper.setLeftAndRightOffset(-offset);
        else if (mDirection == Direction.BOTTOM) mViewOffsetHelper.setTopAndBottomOffset(-offset);
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
            mView = view;
            mDirection = direction;
        }

        public Builder setStartOffset(int startOffset) {
            mStartOffset = startOffset;
            return this;
        }

        public Builder setTargetOffset(int targetOffset) {
            mTargetOffset = targetOffset;
            return this;
        }

        public Builder setEdgeRate(float rate) {
            mEdgeRate = rate;
            return this;
        }

        public Builder setScrollFling(float scrollFling) {
            mScrollFling = scrollFling;
            return this;
        }

        public Builder setScrollSpeed(float scrollSpeed) {
            mScrollSpeed = scrollSpeed;
            return this;
        }

        public Builder setEdgeOver(boolean edgeOver) {
            mEdgeOver = edgeOver;
            return this;
        }

        public Builder setFlingFromTarget(boolean flingFromTarget) {
            mFlingFromTarget = flingFromTarget;
            return this;
        }

        public Builder setScrollOffset(boolean scrollOffset) {
            mScrollOffset = scrollOffset;
            return this;
        }

        public Builder setScrollTouchUp(boolean scrollTouchUp) {
            mScrollTouchUp = scrollTouchUp;
            return this;
        }

        public Builder setOffsetCalculator(IEdgeOffsetCalculator offsetCalculator) {
            mOffsetCalculator = offsetCalculator;
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
