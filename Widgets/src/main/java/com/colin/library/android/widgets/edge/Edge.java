package com.colin.library.android.widgets.edge;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Px;
import androidx.core.view.ViewCompat;

import com.colin.library.android.widgets.behavior.ViewOffsetHelper;
import com.colin.library.android.widgets.def.Constants;
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
    @Direction
    private final int mDirection;
    @NonNull
    private final View mView;
    private final int mOffsetInit;      //初始位置
    private final int mOffsetTarget;    //触发Action的位置
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


    public Edge(@Direction int direction, @NonNull Builder builder) {
        mDirection = direction;
        mView = builder.getView();
        mEdgeOver = builder.isEdgeOver();
        mOffsetInit = builder.getOffsetInit();
        mOffsetTarget = builder.getOffsetTarget();
        mEdgeRate = builder.getEdgeRate();
        mScrollFling = builder.getScrollFling();
        mScrollSpeed = builder.getScrollSpeed();
        mFlingFromTarget = builder.isFlingFromTarget();
        mScrollOffset = builder.isScrollOffset();
        mScrollTouchUp = builder.isScrollTouchUp();
        mOffsetCalculator = builder.getOffsetCalculator();
        mViewOffsetHelper = new ViewOffsetHelper(builder.getView());
        updateOffset(mOffsetInit);
    }

    public void measure(final int width, final int height) {
        if (isVertical()) {
            final int widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
            final int heightSpec = View.MeasureSpec.makeMeasureSpec(mView.getHeight(), View.MeasureSpec.EXACTLY);
            mView.measure(widthSpec, heightSpec);
        } else {
            final int widthSpec = View.MeasureSpec.makeMeasureSpec(mView.getWidth(), View.MeasureSpec.EXACTLY);
            final int heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
            mView.measure(widthSpec, heightSpec);
        }
    }

    public void layout(final int width, final int height) {
        final int vw = mView.getMeasuredWidth(), vh = mView.getMeasuredHeight();
        int center = 0;
        switch (mDirection) {
            case Direction.LEFT:
                center = (height - vh) >> 1;
                mView.layout(-vw, center, 0, center + vh);
                getViewOffsetHelper().onViewLayout();
                break;
            case Direction.TOP:
                center = (width - vw) >> 1;
                mView.layout(center, -vh, center + vw, 0);
                getViewOffsetHelper().onViewLayout();
                break;
            case Direction.RIGHT:
                center = (height - vh) >> 1;
                mView.layout(width, center, width + vw, center + vh);
                getViewOffsetHelper().onViewLayout();
                break;
            case Direction.BOTTOM:
                center = (width - vw) >> 1;
                mView.layout(center, height, center + vw, height + vh);
                getViewOffsetHelper().onViewLayout();
                break;
            default:
                break;
        }

    }


    @NonNull
    public View getView() {
        return mView;
    }

    @Direction
    public int getDirection() {
        return mDirection;
    }

    @Px
    public int getEdgeSize() {
        return isVertical(mDirection) ? mView.getHeight() : mView.getWidth();
    }

    public int getOffsetInit() {
        return mOffsetInit;
    }

    public float getEdgeRate() {
        return mEdgeRate;
    }

    public float getEdgeRate(@ViewCompat.NestedScrollType int type) {
        return type == ViewCompat.TYPE_TOUCH ? mEdgeRate : 1.0F;
    }

    public float getEdgeRate(@ViewCompat.NestedScrollType int type, @Px int offset) {
        return type == ViewCompat.TYPE_TOUCH ? mEdgeRate : getFlingRate(offset);
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

    public int getOffsetTarget() {
        if (mOffsetTarget != ViewGroup.LayoutParams.WRAP_CONTENT) return mOffsetTarget;
        return getEdgeSize() - getOffsetInit() * 2;
    }

    public float getFlingRate(@ViewCompat.NestedScrollType int type, @Px int offset) {
        if (type == ViewCompat.TYPE_TOUCH) return mEdgeRate;
        return Math.min(mEdgeRate, Math.max(mEdgeRate - (offset - getOffsetTarget()) * getScrollFling(), 0));
    }

    public float getFlingRate(@Px int offset) {
        return Math.min(getEdgeRate(), Math.max(getEdgeRate() - (offset - getOffsetTarget()) * getScrollFling(), 0));
    }

    public int getScrollOffset(@Px int offset) {
        return (int) (mScrollSpeed * offset);
    }


    public void scrollToTarget(@Px int offset) {
        if (mDirection == Direction.LEFT || mDirection == Direction.TOP) {
            updateOffset(getOffsetCalculator().calculator(this, offset));
        } else {
            updateOffset(getOffsetCalculator().calculator(this, -offset));
        }
    }

    public void setRunning(boolean running) {
        mRunning = running;
        if (running && mView instanceof EdgeWatcher) ((EdgeWatcher) mView).start();
        if (!running && mView instanceof EdgeWatcher) ((EdgeWatcher) mView).finish();
    }
    public void setOffset(@Px int offset) {
        this.getViewOffsetHelper().setDirection(mDirection, getOffsetCalculator().calculator(this, offset));
        if (mView instanceof EdgeWatcher) ((EdgeWatcher) mView).offset(this, offset);
    }
    public void updateOffset(@Px int offset) {
        this.getViewOffsetHelper().setDirection(mDirection, getOffsetCalculator().calculator(this, offset));
        if (mView instanceof EdgeWatcher) ((EdgeWatcher) mView).offset(this, offset);
    }

    public ViewOffsetHelper getViewOffsetHelper() {
        return mViewOffsetHelper;
    }

    public boolean isScrollStart(int offsetX, int offsetY) {
        switch (mDirection) {
            case Direction.LEFT:
                return offsetX == getOffsetTarget();
            case Direction.TOP:
                return offsetY == getOffsetTarget();
            case Direction.RIGHT:
                return offsetX == -getOffsetTarget();
            case Direction.BOTTOM:
                return offsetY == -getOffsetTarget();
            default:
                return false;
        }
    }


    public int getTargetOffsetMax() {
        if (mDirection == Direction.RIGHT || mDirection == Direction.BOTTOM) return 0;
        else return mEdgeOver ? Integer.MAX_VALUE : getOffsetTarget();
    }

    public int getTargetOffsetMin() {
        if (mDirection == Direction.LEFT || mDirection == Direction.TOP) return 0;
        else return mEdgeOver ? Integer.MIN_VALUE : -getOffsetTarget();
    }


    public boolean isTargetOffset(int offset) {
        return getOffsetTarget() == Math.abs(offset);
    }

    private boolean isVertical() {
        return isVertical(mDirection);
    }

    public boolean isVertical(@Direction int direction) {
        return direction == Direction.TOP || direction == Direction.BOTTOM;
    }

    public static class Builder {
        @NonNull
        private final View mView;
        private int mOffsetInit;
        private int mOffsetTarget = ViewGroup.LayoutParams.WRAP_CONTENT;
        private float mEdgeRate = EDGE_RATE_DEFAULT;
        private float mScrollFling = Constants.SCROLL_FLING_DEFAULT;
        private float mScrollSpeed = Constants.SCROLL_SPEED_DEFAULT;
        private boolean mEdgeOver;
        private boolean mFlingFromTarget = true;
        private boolean mScrollOffset = false;
        private boolean mScrollTouchUp = true;
        private IEdgeOffsetCalculator mOffsetCalculator;

        public Builder(@NonNull View view) {
            this.mView = view;
        }

        public Builder setOffsetInit(@Px int offset) {
            this.mOffsetInit = offset;
            return this;
        }

        public Builder setOffsetTarget(@Px int offset) {
            this.mOffsetTarget = offset;
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

        public int getOffsetInit() {
            return mOffsetInit;
        }

        public int getOffsetTarget() {
            return mOffsetTarget;
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

        public Edge build(@Direction int direction) {
            return new Edge(direction, this);
        }
    }

    @Override
    public String toString() {
        return "Edge{" + "mView=" + mView + ", mDirection=" + mDirection + ", mOffsetInit=" + mOffsetInit + ", mOffsetTarget=" + mOffsetTarget + ", mEdgeRate=" + mEdgeRate + ", mScrollFling=" + mScrollFling + ", mScrollSpeed=" + mScrollSpeed + ", mEdgeOver=" + mEdgeOver + ", mFlingFromTarget=" + mFlingFromTarget + ", mScrollOffset=" + mScrollOffset + ", mScrollTouchUp=" + mScrollTouchUp + ", mOffsetCalculator=" + mOffsetCalculator + ", mViewOffsetHelper=" + mViewOffsetHelper + ", mRunning=" + mRunning + '}';
    }
}
