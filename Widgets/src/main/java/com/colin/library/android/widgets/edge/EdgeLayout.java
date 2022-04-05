package com.colin.library.android.widgets.edge;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.OverScroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.core.view.NestedScrollingParent3;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.colin.library.android.widgets.Constants;
import com.colin.library.android.widgets.R;
import com.colin.library.android.widgets.annotation.Direction;


public class EdgeLayout extends FrameLayout implements NestedScrollingParent3 {
    public static final Interpolator INTERPOLATOR = t -> {
        t -= 1.0f;
        return t * t * t * t * t + 1.0f;
    };
    public static final int DEFAULT_MIN_SCROLL_DURATION = 300;

    private static final int STATE_IDLE = 0;
    private static final int STATE_PULLING = 1;
    private static final int STATE_SETTLING_TO_TRIGGER_OFFSET = 2;
    private static final int STATE_TRIGGERING = 3;
    private static final int STATE_SETTLING_TO_INIT_OFFSET = 4;
    private static final int STATE_SETTLING_DELIVER = 5;
    private static final int STATE_SETTLING_FLING = 6;

    private final int[] mNestedScrollingV2ConsumedCompat = new int[2];

    private int mEnabledEdges;
    private View mTargetView;
    private Edge mEdgeLeft = null;
    private Edge mEdgeTop = null;
    private Edge mEdgeRight = null;
    private Edge mEdgeBottom = null;
    private OnEdgeListener mOnEdgeListener;
    private ViewOffsetHelper mViewOffsetHelper;
    private StopTargetViewFlingImpl mStopTargetViewFlingImpl = DefaultStopTargetViewFlingImpl.getInstance();
    private Runnable mStopTargetFlingRunnable = null;
    private float mNestedPreFlingVelocityScaleDown = 10;
    private int mMinScrollDuration = DEFAULT_MIN_SCROLL_DURATION;
    private int mState = STATE_IDLE;

    private final OverScroller mScroller;
    private final NestedScrollingParentHelper mNestedScrollingParentHelper;

    public EdgeLayout(@NonNull Context context) {
        this(context, null, R.attr.EdgeLayoutStyle);
    }

    public EdgeLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.EdgeLayoutStyle);
    }

    public EdgeLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.EdgeLayout, defStyleAttr, 0);
        mEnabledEdges = array.getInt(R.styleable.EdgeLayout_direction, Direction.LEFT | Direction.TOP | Direction.RIGHT | Direction.BOTTOM);
        array.recycle();
        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        mScroller = new OverScroller(context, INTERPOLATOR);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int w = r - l;
        final int h = b - t;
        if (mTargetView != null) {
            mTargetView.layout(0, 0, w, h);
            mViewOffsetHelper.onViewLayout();
        }
        if (mEdgeLeft != null) mEdgeLeft.onLayout(w, h);
        if (mEdgeTop != null) mEdgeTop.onLayout(w, h);
        if (mEdgeRight != null) mEdgeRight.onLayout(w, h);
        if (mEdgeBottom != null) mEdgeBottom.onLayout(w, h);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        final int count = getChildCount();
        if (count == 0) return;
        boolean isTargetSet = false;
        int edgesSet = 0;
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            final LayoutParams lp = (LayoutParams) child.getLayoutParams();
            if (lp.mTarget) {
                if (isTargetSet) throw new RuntimeException("target view more than one");
                isTargetSet = true;
                setTargetView(child);
            } else {
                if ((edgesSet & lp.mDirection) != 0)
                    throw new RuntimeException("same direction edge view more than one");
                edgesSet |= lp.mDirection;
                setActionView(child, lp);
            }
        }
    }

    @Override
    public void computeScroll() {
        if (!mScroller.computeScrollOffset()) return;
        if (!mScroller.isFinished()) {
            updateOffset(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidateOnAnimation();
            return;
        }
        if (mState == STATE_SETTLING_TO_INIT_OFFSET) {
            mState = STATE_IDLE;
            return;
        }
        if (mState == STATE_TRIGGERING) return;

        if (mState == STATE_SETTLING_FLING) {
            checkScrollToTargetOffset();
            return;
        }
        if (mState == STATE_SETTLING_TO_TRIGGER_OFFSET) {
            final int finalX = mScroller.getFinalX();
            final int finalY = mScroller.getFinalY();
            edgeStart(getDirectionEdge(Direction.LEFT), finalX, finalY);
            edgeStart(getDirectionEdge(Direction.TOP), finalX, finalY);
            edgeStart(getDirectionEdge(Direction.RIGHT), finalX, finalY);
            edgeStart(getDirectionEdge(Direction.BOTTOM), finalX, finalY);
            updateOffset(mScroller.getCurrX(), mScroller.getCurrY());
        }
    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes, int type) {
        return mTargetView == target
                && (axes == ViewCompat.SCROLL_AXIS_HORIZONTAL && (isDirectionEnabled(Direction.LEFT) || isEdgeEnabled(Direction.RIGHT))) ||
                (axes == ViewCompat.SCROLL_AXIS_VERTICAL && (isEdgeEnabled(Direction.TOP) || isEdgeEnabled(Direction.BOTTOM)));
    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int nestedScrollAxes) {
        return onStartNestedScroll(child, target, nestedScrollAxes, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes, @ViewCompat.NestedScrollType int type) {
        if (type == ViewCompat.TYPE_TOUCH) {
            removeStopTargetFlingRunnable();
            mScroller.abortAnimation();
            mState = STATE_PULLING;
        }
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes);
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes) {
        onNestedScrollAccepted(child, target, axes, ViewCompat.TYPE_TOUCH);
    }


    @Override
    public void onNestedPreScroll(@NonNull final View target, @Px int dx, @Px int dy, @NonNull int[] consumed, int type) {
        final int originDx = dx, originDy = dy;
        dy = checkEdgeTopScrollDown(dy, consumed, type);
        dy = checkEdgeBottomScrollDown(dy, consumed, type);
        dy = checkEdgeTopScrollUp(dy, consumed, type);
        dy = checkEdgeBottomScrollUp(dy, consumed, type);

        dx = checkEdgeLeftScrollRight(dx, consumed, type);
        dx = checkEdgeRightScrollRight(dx, consumed, type);
        dx = checkEdgeLeftScrollLeft(dx, consumed, type);
        dx = checkEdgeRightScrollLeft(dx, consumed, type);

        if (originDx == dx && originDy == dy && mState == STATE_SETTLING_DELIVER) {
            checkStopTargetFling(target, dx, dy, type);
        }
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed) {
        onNestedPreScroll(target, dx, dy, consumed, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @ViewCompat.NestedScrollType int type, @NonNull int[] consumed) {
        final int originDxUnconsumed = dxUnconsumed, originDyUnconsumed = dyUnconsumed;
        dyUnconsumed = checkEdgeTopScrollDown(dyUnconsumed, consumed, type);
        dyUnconsumed = checkEdgeBottomScrollDown(dyUnconsumed, consumed, type);
        dyUnconsumed = checkEdgeTopScrollUp(dyUnconsumed, consumed, type);
        dyUnconsumed = checkEdgeBottomScrollUp(dyUnconsumed, consumed, type);

        dxUnconsumed = checkEdgeLeftScrollRight(dxUnconsumed, consumed, type);
        dxUnconsumed = checkEdgeRightScrollRight(dxUnconsumed, consumed, type);
        dxUnconsumed = checkEdgeLeftScrollLeft(dxUnconsumed, consumed, type);
        dxUnconsumed = checkEdgeRightScrollLeft(dxUnconsumed, consumed, type);
        if (dyUnconsumed == originDyUnconsumed && dxUnconsumed == originDxUnconsumed && mState == STATE_SETTLING_DELIVER) {
            checkStopTargetFling(target, dxUnconsumed, dyUnconsumed, type);
        }
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, mNestedScrollingV2ConsumedCompat);
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY) {
        final int hOffset = mViewOffsetHelper.getLeftAndRightOffset();
        final int vOffset = mViewOffsetHelper.getTopAndBottomOffset();

        // if the targetView is RecyclerView and we set OnFlingListener for RecyclerView.
        // then the targetView can not deliver fling consume to NestedScrollParent
        // so we intercept the fling if the target view can not consume the fling.
        if (mEdgeLeft != null && isEdgeEnabled(Direction.LEFT)) {
            if (velocityX < 0 && !mTargetView.canScrollHorizontally(-1)) {
                mState = STATE_SETTLING_FLING;
                velocityX /= mNestedPreFlingVelocityScaleDown;
                int maxX = mEdgeLeft.isEdgeOver() ? Integer.MAX_VALUE : mEdgeLeft.getTargetOffset();
                mScroller.fling(hOffset, vOffset, (int) -velocityX, 0, 0, maxX, vOffset, vOffset);
                postInvalidateOnAnimation();
                return true;
            } else if (velocityX > 0 && hOffset > 0) {
                mState = STATE_SETTLING_TO_INIT_OFFSET;
                mScroller.startScroll(hOffset, vOffset, -hOffset, 0, scrollDuration(mEdgeLeft, hOffset));
                postInvalidateOnAnimation();
                return true;
            }
        }
        if (mEdgeRight != null && isEdgeEnabled(Direction.RIGHT)) {
            if (velocityX > 0 && !mTargetView.canScrollHorizontally(1)) {
                mState = STATE_SETTLING_FLING;
                velocityX /= mNestedPreFlingVelocityScaleDown;
                int minX = mEdgeRight.isEdgeOver() ? Integer.MIN_VALUE : -mEdgeRight.getTargetOffset();
                mScroller.fling(hOffset, vOffset, (int) -velocityX, 0, minX, 0, vOffset, vOffset);
                postInvalidateOnAnimation();
                return true;
            } else if (velocityX < 0 && hOffset < 0) {
                mState = STATE_SETTLING_TO_INIT_OFFSET;
                mScroller.startScroll(hOffset, vOffset, -hOffset, 0, scrollDuration(mEdgeRight, hOffset));
                postInvalidateOnAnimation();
                return true;
            }
        }

        if (mEdgeTop != null && isEdgeEnabled(Direction.TOP)) {
            if (velocityY < 0 && !mTargetView.canScrollVertically(-1)) {
                mState = STATE_SETTLING_FLING;
                velocityY /= mNestedPreFlingVelocityScaleDown;
                int maxY = mEdgeTop.isEdgeOver() ? Integer.MAX_VALUE : mEdgeTop.getTargetOffset();
                mScroller.fling(hOffset, vOffset, 0, (int) -velocityY, hOffset, hOffset, 0, maxY);
                postInvalidateOnAnimation();
                return true;
            } else if (velocityY > 0 && vOffset > 0) {
                mState = STATE_SETTLING_TO_INIT_OFFSET;
                mScroller.startScroll(hOffset, vOffset, 0, -vOffset, scrollDuration(mEdgeTop, vOffset));
                postInvalidateOnAnimation();
                return true;
            }
        }


        if (mEdgeBottom != null && isEdgeEnabled(Direction.BOTTOM)) {
            if (velocityY > 0 && !mTargetView.canScrollVertically(1)) {
                mState = STATE_SETTLING_FLING;
                velocityY /= mNestedPreFlingVelocityScaleDown;
                int minY = mEdgeBottom.isEdgeOver() ? Integer.MIN_VALUE : -mEdgeBottom.getTargetOffset();
                mScroller.fling(hOffset, vOffset, 0, (int) -velocityY, hOffset, hOffset, minY, 0);
                postInvalidateOnAnimation();
                return true;
            } else if (velocityY < 0 && vOffset < 0) {
                mState = STATE_SETTLING_TO_INIT_OFFSET;
                mScroller.startScroll(hOffset, vOffset, 0, -vOffset, scrollDuration(mEdgeBottom, vOffset));
                postInvalidateOnAnimation();
                return true;
            }
        }
        mState = STATE_SETTLING_DELIVER;
        return super.onNestedPreFling(target, velocityX, velocityY);
    }

    @Override
    public void onStopNestedScroll(@NonNull View target, @ViewCompat.NestedScrollType int type) {
        if (mState == STATE_PULLING) {
            checkScrollToTargetOffset();
        } else if (mState == STATE_SETTLING_DELIVER && type != ViewCompat.TYPE_TOUCH) {
            removeStopTargetFlingRunnable();
            checkScrollToTargetOffset();
        }
    }

    private void updateOffset(int offsetX, int offsetY) {
        setHorOffsetToTargetOffsetHelper(offsetX);
        setVerOffsetToTargetOffsetHelper(offsetY);
    }

    public void setStopTargetViewFlingImpl(@NonNull StopTargetViewFlingImpl stopTargetViewFlingImpl) {
        mStopTargetViewFlingImpl = stopTargetViewFlingImpl;
    }

    public void setMinScrollDuration(int minScrollDuration) {
        mMinScrollDuration = minScrollDuration;
    }

    public void setTargetView(@NonNull View view) {
        if (view.getParent() != this) {
            throw new RuntimeException("Target already exists other parent view.");
        }
        if (view.getParent() == null) {
            final int match = ViewGroup.LayoutParams.MATCH_PARENT;
            final LayoutParams lp = new LayoutParams(match, match);
            addView(view, lp);
        }
        innerSetTargetView(view);
    }

    private void innerSetTargetView(@NonNull View view) {
        mTargetView = view;
        mViewOffsetHelper = new ViewOffsetHelper(view);
    }

    public void setActionView(@NonNull View view, @NonNull LayoutParams params) {
        final Edge.Builder builder = new Edge.Builder(view, params.mDirection)
                .setEdgeOver(params.mEdgeOver)
                .setFlingFromTarget(params.mFlingFromTarget)
                .setScrollOffset(params.mScrollOffset)
                .setScrollTouchUp(params.mScrollTouchUp)
                .setStartOffset(params.mStartOffset)
                .setTargetOffset(params.mTargetOffset)
                .setEdgeRate(params.mScrollSpeed)
                .setScrollFling(params.mScrollSpeed)
                .setScrollSpeed(params.mScrollSpeed);
        view.setLayoutParams(params);
        setActionView(builder);
    }

    public void setActionView(@NonNull Edge.Builder builder) {
        if (builder.getView().getParent() != this) {
            throw new RuntimeException("Action view already exists other parent view.");
        }
        if (builder.getView().getParent() == null) {
            ViewGroup.LayoutParams lp = builder.getView().getLayoutParams();
            if (lp == null) {
                lp = new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            }
            addView(builder.getView(), lp);
        }
        if (builder.getDirection() == Direction.LEFT) {
            mEdgeLeft = builder.build();
        } else if (builder.getDirection() == Direction.TOP) {
            mEdgeTop = builder.build();
        } else if (builder.getDirection() == Direction.RIGHT) {
            mEdgeRight = builder.build();
        } else if (builder.getDirection() == Direction.BOTTOM) {
            mEdgeBottom = builder.build();
        }
    }

    public void setEdgeListener(OnEdgeListener edgeListener) {
        this.mOnEdgeListener = edgeListener;
    }

    public Edge getDirectionEdge(@Direction int direction) {
        return isDirectionEnabled(direction) ? getEdge(direction) : null;
    }

    public void setEnabledEdges(@Direction int direction) {
        mEnabledEdges = direction;
    }

    @Deprecated
    public boolean isEdgeEnabled(@Direction int direction) {
        return (mEnabledEdges & direction) == direction && getEdge(direction) != null;
    }


    public boolean isDirectionEnabled(@Direction int direction) {
        return (mEnabledEdges & direction) == direction;
    }

    @Nullable
    private Edge getEdge(@Direction int direction) {
        if (direction == Direction.LEFT) {
            return mEdgeLeft;
        } else if (direction == Direction.TOP) {
            return mEdgeTop;
        } else if (direction == Direction.RIGHT) {
            return mEdgeRight;
        } else if (direction == Direction.BOTTOM) {
            return mEdgeBottom;
        }
        return null;
    }


    public void setNestedPreFlingVelocityScaleDown(float nestedPreFlingVelocityScaleDown) {
        this.mNestedPreFlingVelocityScaleDown = nestedPreFlingVelocityScaleDown;
    }

    private int scrollDuration(@NonNull Edge edge, @Px int offset) {
        return Math.max(mMinScrollDuration, Math.abs(edge.getScrollOffset(offset)));
    }

    private void edgeStart(@Nullable Edge edge, int finalX, int finalY) {
        if (edge != null && edge.getScrollFinal(finalX, finalY)) {
            edgeStart(edge, STATE_TRIGGERING);
        }
    }

    private void edgeStart(@NonNull Edge edge, int state) {
        if (edge.isRunning()) return;
        this.mState = state;
        edge.setRunning(true);
        if (mOnEdgeListener != null) mOnEdgeListener.start(edge);
        if (edge.getView() instanceof EdgeWatcher) ((EdgeWatcher) edge.getView()).start();
    }

    public void edgeFinish() {
        if (mEdgeLeft != null && mEdgeLeft.isRunning()) edgeFinish(mEdgeLeft, true);
        if (mEdgeTop != null && mEdgeTop.isRunning()) edgeFinish(mEdgeTop, true);
        if (mEdgeRight != null && mEdgeRight.isRunning()) edgeFinish(mEdgeRight, true);
        if (mEdgeBottom != null && mEdgeBottom.isRunning()) edgeFinish(mEdgeBottom, true);
    }

    public void edgeFinish(@NonNull Edge edge) {
        edgeFinish(edge, true);
    }

    public void edgeFinish(@NonNull Edge edge, boolean animate) {
        edge.setRunning(false);
        if (edge.getView() instanceof EdgeWatcher) ((EdgeWatcher) edge.getView()).finish();
        if (mState == STATE_PULLING) return;
        if (!animate) {
            mState = STATE_IDLE;
            setVerOffsetToTargetOffsetHelper(0);
            setHorOffsetToTargetOffsetHelper(0);
            return;
        }
        mState = STATE_SETTLING_TO_INIT_OFFSET;
        @Direction final int direction = edge.getDirection();
        final int vOffset = mViewOffsetHelper.getTopAndBottomOffset();
        final int hOffset = mViewOffsetHelper.getLeftAndRightOffset();
        if (direction == Direction.TOP && vOffset > 0) {
            mScroller.startScroll(hOffset, vOffset, 0, -vOffset, scrollDuration(edge, vOffset));
            postInvalidateOnAnimation();
        } else if (direction == Direction.BOTTOM && vOffset < 0) {
            mScroller.startScroll(hOffset, vOffset, 0, -vOffset, scrollDuration(edge, vOffset));
            postInvalidateOnAnimation();
        } else if (direction == Direction.LEFT && hOffset > 0) {
            mScroller.startScroll(hOffset, vOffset, -hOffset, 0, scrollDuration(edge, hOffset));
            postInvalidateOnAnimation();
        } else if (direction == Direction.RIGHT && hOffset < 0) {
            mScroller.startScroll(hOffset, vOffset, -hOffset, 0, scrollDuration(edge, hOffset));
            postInvalidateOnAnimation();
        }
    }


    private void checkScrollToTargetOffset() {
        if (mTargetView == null) return;
        mScroller.abortAnimation();
        final int hOffset = mViewOffsetHelper.getLeftAndRightOffset();
        final int vOffset = mViewOffsetHelper.getTopAndBottomOffset();
        int hTarget = 0, vTarget = 0;
        if (mEdgeLeft != null && isDirectionEnabled(Direction.LEFT) && hOffset > 0) {
            int targetOffset = mEdgeLeft.getTargetOffset();
            if (hOffset == targetOffset) {
                edgeStart(mEdgeLeft, STATE_TRIGGERING);
                return;
            }
            if (hOffset > targetOffset) {
                if (!mEdgeLeft.isScrollTouchUp()) {
                    edgeStart(mEdgeLeft, STATE_TRIGGERING);
                    return;
                }
                if (!mEdgeLeft.isScrollOffset()) {
                    edgeStart(mEdgeLeft, STATE_TRIGGERING);
                } else {
                    mState = STATE_SETTLING_TO_TRIGGER_OFFSET;
                }
                hTarget = targetOffset;
            } else {
                mState = STATE_SETTLING_TO_INIT_OFFSET;
            }
            int dx = hTarget - hOffset;
            mScroller.startScroll(hOffset, vOffset, dx, 0, scrollDuration(mEdgeLeft, dx));
            postInvalidateOnAnimation();
            return;
        }

        if (mEdgeRight != null && isDirectionEnabled(Direction.RIGHT) && hOffset < 0) {
            int targetOffset = mEdgeRight.getTargetOffset();
            if (hOffset == -targetOffset) {
                edgeStart(mEdgeRight, STATE_TRIGGERING);
                return;
            }
            if (hOffset < -targetOffset) {
                if (!mEdgeRight.isScrollTouchUp()) {
                    edgeStart(mEdgeRight, STATE_TRIGGERING);
                    return;
                }

                if (!mEdgeRight.isScrollOffset()) {
                    edgeStart(mEdgeRight, STATE_TRIGGERING);
                } else {
                    mState = STATE_SETTLING_TO_TRIGGER_OFFSET;
                }
                hTarget = -targetOffset;
            } else {
                mState = STATE_SETTLING_TO_INIT_OFFSET;
            }
            int dx = hTarget - hOffset;
            mScroller.startScroll(hOffset, vOffset, dx, 0, scrollDuration(mEdgeRight, dx));
            postInvalidateOnAnimation();
            return;
        }

        if (mEdgeTop != null && isDirectionEnabled(Direction.TOP) && vOffset > 0) {
            mState = STATE_SETTLING_TO_INIT_OFFSET;
            int targetOffset = mEdgeTop.getTargetOffset();
            if (vOffset == targetOffset) {
                edgeStart(mEdgeTop, STATE_TRIGGERING);
                return;
            }
            if (vOffset > targetOffset) {
                if (!mEdgeTop.isScrollTouchUp()) {
                    edgeStart(mEdgeTop, STATE_TRIGGERING);
                    return;
                }

                if (!mEdgeTop.isScrollOffset()) {
                    edgeStart(mEdgeTop, STATE_TRIGGERING);
                } else {
                    mState = STATE_SETTLING_TO_TRIGGER_OFFSET;
                }
                vTarget = targetOffset;
            } else {
                mState = STATE_SETTLING_TO_INIT_OFFSET;
            }
            int dy = vTarget - vOffset;
            mScroller.startScroll(hOffset, vOffset, hOffset, dy, scrollDuration(mEdgeTop, dy));
            postInvalidateOnAnimation();
            return;
        }

        if (mEdgeBottom != null && isDirectionEnabled(Direction.BOTTOM) && vOffset < 0) {
            int targetOffset = mEdgeBottom.getTargetOffset();
            if (vOffset == -targetOffset) {
                edgeStart(mEdgeBottom, STATE_TRIGGERING);
                return;
            }
            if (vOffset < -targetOffset) {
                if (!mEdgeBottom.isScrollTouchUp()) {
                    edgeStart(mEdgeBottom, STATE_TRIGGERING);
                    return;
                }

                if (!mEdgeBottom.isScrollOffset()) {
                    edgeStart(mEdgeBottom, STATE_TRIGGERING);
                } else {
                    mState = STATE_SETTLING_TO_TRIGGER_OFFSET;
                }
                vTarget = -targetOffset;
            } else {
                mState = STATE_SETTLING_TO_INIT_OFFSET;
            }
            int dy = vTarget - vOffset;
            mScroller.startScroll(hOffset, vOffset, hOffset, dy, scrollDuration(mEdgeBottom, dy));
            postInvalidateOnAnimation();
            return;
        }

        mState = STATE_IDLE;
    }


    private void removeStopTargetFlingRunnable() {
        if (mStopTargetFlingRunnable != null) {
            removeCallbacks(mStopTargetFlingRunnable);
            mStopTargetFlingRunnable = null;
        }
    }

    private void checkStopTargetFling(@NonNull final View targetView, int dx, int dy, int type) {
        if (mStopTargetFlingRunnable != null || type == ViewCompat.TYPE_TOUCH) return;
        if ((dy < 0 && !mTargetView.canScrollVertically(-1)) ||
                (dy > 0 && !mTargetView.canScrollVertically(1)) ||
                (dx < 0 && !mTargetView.canScrollHorizontally(-1)) ||
                (dx > 0 && !mTargetView.canScrollHorizontally(1))) {
            mStopTargetFlingRunnable = () -> {
                mStopTargetViewFlingImpl.stopFling(targetView);
                mStopTargetFlingRunnable = null;
                checkScrollToTargetOffset();
            };
            post(mStopTargetFlingRunnable);
        }
    }

    private void setHorOffsetToTargetOffsetHelper(int hOffset) {
        mViewOffsetHelper.setLeftAndRightOffset(hOffset);
        onTargetViewLeftAndRightOffsetChanged(hOffset);
        if (mEdgeLeft != null) {
            mEdgeLeft.onTargetMoved(hOffset);
            if (mEdgeLeft.getView() instanceof EdgeWatcher) {
                ((EdgeWatcher) mEdgeLeft.getView()).offset(mEdgeLeft, hOffset);
            }

        }
        if (mEdgeRight != null) {
            mEdgeRight.onTargetMoved(-hOffset);
            if (mEdgeRight.getView() instanceof EdgeWatcher) {
                ((EdgeWatcher) mEdgeRight.getView()).offset(mEdgeRight, -hOffset);
            }
        }
    }

    private void setVerOffsetToTargetOffsetHelper(int vOffset) {
        mViewOffsetHelper.setTopAndBottomOffset(vOffset);
        onTargetViewTopAndBottomOffsetChanged(vOffset);
        if (mEdgeTop != null) {
            mEdgeTop.onTargetMoved(vOffset);
            if (mEdgeTop.getView() instanceof EdgeWatcher) {
                ((EdgeWatcher) mEdgeTop.getView()).offset(mEdgeTop, vOffset);
            }

        }
        if (mEdgeBottom != null) {
            mEdgeBottom.onTargetMoved(-vOffset);
            if (mEdgeBottom.getView() instanceof EdgeWatcher) {
                ((EdgeWatcher) mEdgeBottom.getView()).offset(mEdgeBottom, -vOffset);
            }
        }
    }

    protected void onTargetViewTopAndBottomOffsetChanged(int vOffset) {

    }

    protected void onTargetViewLeftAndRightOffsetChanged(int hOffset) {

    }

    private int checkEdgeTopScrollDown(int dy, int[] consumed, @ViewCompat.NestedScrollType int type) {
        int vOffset = mViewOffsetHelper.getTopAndBottomOffset();
        if (dy > 0 && isEdgeEnabled(Direction.TOP) && vOffset > 0) {
            float pullRate = type == ViewCompat.TYPE_TOUCH ? mEdgeTop.getEdgeRate() : 1f;
            int ry = (int) (dy * pullRate);
            if (ry == 0) {
                return dy;
            }
            if (vOffset >= ry) {
                consumed[1] += dy;
                vOffset -= ry;
                dy = 0;
            } else {
                int yConsumed = (int) (vOffset / pullRate);
                consumed[1] += yConsumed;
                dy -= yConsumed;
                vOffset = 0;
            }
            setVerOffsetToTargetOffsetHelper(vOffset);
        }
        return dy;
    }

    private int checkEdgeTopScrollUp(int dy, int[] consumed, @ViewCompat.NestedScrollType int type) {
        if (dy < 0 && isEdgeEnabled(Direction.TOP) && !mTargetView.canScrollVertically(-1) &&
                (type == ViewCompat.TYPE_TOUCH || mEdgeTop.isFlingFromTarget())) {
            int vOffset = mViewOffsetHelper.getTopAndBottomOffset();
            float pullRate = type == ViewCompat.TYPE_TOUCH ? mEdgeTop.getEdgeRate() : mEdgeTop.getFlingRate(vOffset);
            int ry = (int) (dy * pullRate);
            if (ry == 0) {
                return dy;
            }
            if (mEdgeTop.isEdgeOver() || -ry <= mEdgeTop.getTargetOffset() - vOffset) {
                vOffset -= ry;
                consumed[1] += dy;
                dy = 0;
            } else {
                int yConsumed = (int) ((vOffset - mEdgeTop.getTargetOffset()) / pullRate);
                consumed[1] += yConsumed;
                dy -= yConsumed;
                vOffset = mEdgeBottom.getTargetOffset();
            }
            setVerOffsetToTargetOffsetHelper(vOffset);
        }
        return dy;
    }

    private int checkEdgeBottomScrollDown(int dy, int[] consumed, @ViewCompat.NestedScrollType int type) {
        if (dy > 0 && isEdgeEnabled(Direction.BOTTOM) && !mTargetView.canScrollVertically(1) &&
                (type == ViewCompat.TYPE_TOUCH || mEdgeBottom.isFlingFromTarget())) {
            int vOffset = mViewOffsetHelper.getTopAndBottomOffset();
            float pullRate = type == ViewCompat.TYPE_TOUCH ? mEdgeBottom.getEdgeRate() : mEdgeBottom.getFlingRate(-vOffset);
            int ry = (int) (dy * pullRate);
            if (ry == 0) {
                return dy;
            }
            if (mEdgeBottom.isEdgeOver() || vOffset - ry >= -mEdgeBottom.getTargetOffset()) {
                vOffset -= ry;
                consumed[1] += dy;
                dy = 0;
            } else {
                int yConsumed = (int) ((-mEdgeBottom.getTargetOffset() - vOffset) / pullRate);
                consumed[1] += yConsumed;
                dy -= yConsumed;
                vOffset = -mEdgeBottom.getTargetOffset();
            }
            setVerOffsetToTargetOffsetHelper(vOffset);
        }
        return dy;
    }

    private int checkEdgeBottomScrollUp(int dy, int[] consumed, @ViewCompat.NestedScrollType int type) {
        int vOffset = mViewOffsetHelper.getTopAndBottomOffset();
        if (dy < 0 && isEdgeEnabled(Direction.BOTTOM) && vOffset < 0) {
            float pullRate = type == ViewCompat.TYPE_TOUCH ? mEdgeBottom.getEdgeRate() : 1f;
            int ry = (int) (dy * pullRate);
            if (ry == 0) {
                return dy;
            }
            if (vOffset <= ry) {
                consumed[1] += dy;
                vOffset -= ry;
                dy = 0;
            } else {
                int yConsumed = (int) (vOffset / pullRate);
                consumed[1] += yConsumed;
                dy -= yConsumed;
                vOffset = 0;
            }
            setVerOffsetToTargetOffsetHelper(vOffset);
        }
        return dy;
    }

    private int checkEdgeLeftScrollRight(int dx, int[] consumed, @ViewCompat.NestedScrollType int type) {
        int hOffset = mViewOffsetHelper.getLeftAndRightOffset();
        if (dx > 0 && isEdgeEnabled(Direction.LEFT) && hOffset > 0) {
            float pullRate = type == ViewCompat.TYPE_TOUCH ? mEdgeLeft.getEdgeRate() : 1f;
            int rx = (int) (dx * pullRate);
            if (rx == 0) {
                return dx;
            }
            if (hOffset >= rx) {
                consumed[0] += dx;
                hOffset -= rx;
                dx = 0;
            } else {
                int xConsumed = (int) (hOffset / pullRate);
                consumed[0] += xConsumed;
                dx -= xConsumed;
                hOffset = 0;
            }
            setHorOffsetToTargetOffsetHelper(hOffset);
        }
        return dx;
    }

    private int checkEdgeLeftScrollLeft(int dx, int[] consumed, @ViewCompat.NestedScrollType int type) {
        int hOffset = mViewOffsetHelper.getLeftAndRightOffset();
        if (dx < 0 && isEdgeEnabled(Direction.LEFT) && !mTargetView.canScrollHorizontally(-1) &&
                (type == ViewCompat.TYPE_TOUCH || mEdgeLeft.isFlingFromTarget())) {
            float pullRate = type == ViewCompat.TYPE_TOUCH ? mEdgeLeft.getEdgeRate() : mEdgeLeft.getFlingRate(hOffset);
            int rx = (int) (dx * pullRate);
            if (rx == 0) {
                return dx;
            }
            if (mEdgeLeft.isEdgeOver() || -rx <= mEdgeLeft.getTargetOffset() - hOffset) {
                hOffset -= rx;
                consumed[0] += dx;
                dx = 0;
            } else {
                int xConsumed = (int) ((hOffset - mEdgeLeft.getTargetOffset()) / pullRate);
                consumed[0] += xConsumed;
                dx -= xConsumed;
                hOffset = mEdgeLeft.getTargetOffset();
            }
            setHorOffsetToTargetOffsetHelper(hOffset);
        }
        return dx;
    }

    private int checkEdgeRightScrollRight(int dx, int[] consumed, @ViewCompat.NestedScrollType int type) {
        if (dx > 0 && isEdgeEnabled(Direction.RIGHT) && !mTargetView.canScrollHorizontally(1) &&
                (type == ViewCompat.TYPE_TOUCH || mEdgeRight.isFlingFromTarget())) {
            int hOffset = mViewOffsetHelper.getLeftAndRightOffset();
            float pullRate = type == ViewCompat.TYPE_TOUCH ? mEdgeRight.getEdgeRate() : mEdgeRight.getFlingRate(-hOffset);
            int rx = (int) (dx * pullRate);
            if (rx == 0) {
                return dx;
            }
            if (mEdgeRight.isEdgeOver() || hOffset - rx >= -mEdgeRight.getTargetOffset()) {
                hOffset -= rx;
                consumed[0] += dx;
                dx = 0;
            } else {
                int xConsumed = (int) ((-mEdgeRight.getTargetOffset() - hOffset) / pullRate);
                consumed[0] += xConsumed;
                dx -= xConsumed;
                hOffset = -mEdgeRight.getTargetOffset();
            }
            setHorOffsetToTargetOffsetHelper(hOffset);
        }
        return dx;
    }

    private int checkEdgeRightScrollLeft(int dx, int[] consumed, @ViewCompat.NestedScrollType int type) {
        int hOffset = mViewOffsetHelper.getLeftAndRightOffset();
        if (dx < 0 && isEdgeEnabled(Direction.RIGHT) && hOffset < 0) {
            float pullRate = type == ViewCompat.TYPE_TOUCH ? mEdgeRight.getEdgeRate() : 1f;
            int rx = (int) (dx * pullRate);
            if (rx == 0) {
                return dx;
            }
            if (hOffset <= dx) {
                consumed[0] += dx;
                hOffset -= rx;
                dx = 0;
            } else {
                int xConsumed = (int) (hOffset / pullRate);
                consumed[0] += xConsumed;
                dx -= xConsumed;
                hOffset = 0;
            }
            setHorOffsetToTargetOffsetHelper(hOffset);
        }
        return dx;
    }

    @Override
    protected FrameLayout.LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        return new LayoutParams(lp);
    }

    @Override
    public FrameLayout.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected FrameLayout.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams && super.checkLayoutParams(p);
    }

    public static class LayoutParams extends FrameLayout.LayoutParams {
        public boolean mTarget = false;
        public boolean mEdgeOver;
        public boolean mFlingFromTarget = true;
        public boolean mScrollOffset = false;
        public boolean mScrollTouchUp = true;
        @Direction
        public int mDirection;
        public int mStartOffset;
        public int mTargetOffset = ViewGroup.LayoutParams.WRAP_CONTENT;
        public float mEdgeRate = Edge.EDGE_RATE_DEFAULT;
        public float mScrollFling = Constants.SCROLL_FLING_DEFAULT;
        public float mScrollSpeed = Constants.SCROLL_SPEED_DEFAULT;


        public LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);
            final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.EdgeLayout_Layout);
            mTarget = array.getBoolean(R.styleable.EdgeLayout_Layout_target, mTarget);
            if (!mTarget) {
                mDirection = array.getInteger(R.styleable.EdgeLayout_Layout_direction, Direction.TOP);
                mTargetOffset = array.getLayoutDimension(R.styleable.EdgeLayout_Layout_edgeTargetOffset, mTargetOffset);
                mStartOffset = array.getDimensionPixelSize(R.styleable.EdgeLayout_Layout_edgeStartOffset, mStartOffset);
                mEdgeOver = array.getBoolean(R.styleable.EdgeLayout_Layout_edgeOver, mEdgeOver);
                mEdgeRate = array.getFloat(R.styleable.EdgeLayout_Layout_edgeRate, mEdgeRate);
                mScrollFling = array.getFloat(R.styleable.EdgeLayout_Layout_edgeFlingFraction, mScrollFling);
                mScrollSpeed = array.getFloat(R.styleable.EdgeLayout_Layout_edgeScrollSpeed, mScrollSpeed);
                mFlingFromTarget = array.getBoolean(R.styleable.EdgeLayout_Layout_edgeFlingFromTarget, mFlingFromTarget);
                mScrollOffset = array.getBoolean(R.styleable.EdgeLayout_Layout_edgeScrollOffset, mScrollOffset);
                mScrollTouchUp = array.getBoolean(R.styleable.EdgeLayout_Layout_edgeScrollTouchUp, mScrollTouchUp);
            }
            array.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams params) {
            super(params);
        }

        public LayoutParams(MarginLayoutParams params) {
            super(params);
        }
    }


    public static class DefaultStopTargetViewFlingImpl implements StopTargetViewFlingImpl {

        private static DefaultStopTargetViewFlingImpl sInstance;

        public static DefaultStopTargetViewFlingImpl getInstance() {
            if (sInstance == null) {
                sInstance = new DefaultStopTargetViewFlingImpl();
            }
            return sInstance;
        }

        private DefaultStopTargetViewFlingImpl() {

        }

        @Override
        public void stopFling(View view) {
            if (view instanceof RecyclerView) {
                ((RecyclerView) view).stopScroll();
            }
        }
    }

    public interface OnEdgeListener {
        default void start(@NonNull Edge edge) {
        }

        default void offset(@Direction int direction, int offset) {
        }

        default void finish(@NonNull Edge edge) {
        }
    }


    public interface StopTargetViewFlingImpl {
        void stopFling(View view);
    }
}
