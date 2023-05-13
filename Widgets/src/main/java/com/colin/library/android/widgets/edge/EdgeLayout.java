package com.colin.library.android.widgets.edge;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.OverScroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.core.view.NestedScrollingChild3;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.NestedScrollingParent3;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ViewCompat;

import com.colin.library.android.utils.LogUtil;
import com.colin.library.android.utils.StringUtil;
import com.colin.library.android.widgets.R;
import com.colin.library.android.widgets.annotation.Direction;
import com.colin.library.android.widgets.annotation.ScrollState;
import com.colin.library.android.widgets.def.Constants;
import com.colin.library.android.widgets.interpolator.Interpolators;

import java.util.Locale;

/**
 * 作者： ColinLu
 * 时间： 2023-02-01 21:34
 * <p>
 * 描述： https://blog.csdn.net/jb_home/article/details/113757780
 * 在NestedScrollingChild滚动过程中，它和NestedScrollingParent会一直"保持通讯"，比如：
 * 当Child滚动之前，会通知Parent:"我要开始滚动啦，你看你要不要做点什么"。
 * 当Child在滚动的时候，也会每次通知Parent:"我这次消费了xxx，你看你还要做什么"。
 * 当Child滚动完成，Parent也会收到通知："我滚动完成了"。
 * 当然了，除了手指触摸滚动的，还有惯性滚动，但原理和流程是一样的
 * 因为child是产生滑动的造势者，所以它的api都是以直接的动词开头，而parent的滑动响应是child通知parent的，所以都是以监听on开头：
 * parent ----> onXXXX()
 * child -----> verbXXXX()
 */
public class EdgeLayout extends FrameLayout implements IEdgeLayout, NestedScrollingParent3, NestedScrollingChild3 {
    public static final Interpolator INTERPOLATOR = Interpolators.QUNITIC;

    private float mNestedPreFlingVelocityScaleDown = 10;
    private int mScrollState = ScrollState.SCROLL_STATE_IDLE;
    private int mLastScrollerY;
    private float mInterceptDownY;

    private final int[] mTotalUnconsumed = new int[2];
    private boolean mNestedScrollInProgress = false;
    private final int[] mParentScrollConsumed = new int[2];
    private final int[] mNestedScrollConsumed = new int[2];
    private final int[] mParentOffsetInWindow = new int[2];
    private int mTotalUnconsumedY;
    private int mTotalDragDistanceY;
    private boolean mScrollEdgeFling;//mReturningToStart
    private boolean mNestScrollEdge;
    private boolean mActionDragged;
    private int mActivePointerId = Constants.INVALID;

    private EdgeHelper mEdgeHelper;
    private final OverScroller mScroller;
    private final int mTouchSlop;
    private NestedScrollingParentHelper mNestedScrollingParentHelper;
    private NestedScrollingChildHelper mNestedScrollingChildHelper;

    public EdgeLayout(@NonNull Context context) {
        this(context, null, R.attr.EdgeLayoutStyle);
    }

    public EdgeLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.EdgeLayoutStyle);
    }

    public EdgeLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        setChildrenDrawingOrderEnabled(true);
        setNestedScrollingEnabled(true);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mScroller = new OverScroller(context, INTERPOLATOR);
        if (attrs != null) {
            final TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.EdgeLayout, defStyleAttr, 0);
            setDirectionEnabled(array.getInt(R.styleable.EdgeLayout_direction, Direction.TOP | Direction.BOTTOM));
            array.recycle();
        }

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            LayoutParams params = (LayoutParams) view.getLayoutParams();
            if (params.mTarget) setTargetView(view);
            else setEdgeView(view, params);
        }
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (getChildCount() == 0) return;
        getEdgeHelper().onLayout(this, l, t, r, b);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public FrameLayout.LayoutParams generateLayoutParams(@NonNull AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected FrameLayout.LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        return new LayoutParams(lp);
    }

    @Override
    protected FrameLayout.LayoutParams generateDefaultLayoutParams() {
        return new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams params) {
        return params instanceof LayoutParams;
    }

    ///////////////////////////////////////////////////////////////////////////
    // edge layout
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public void setDirectionEnabled(@Direction final int enabled) {
        getEdgeHelper().setDirectionEnabled(enabled);
    }

    @Override
    public void start(@NonNull Edge edge) {
        getEdgeHelper().start(edge.getDirection());
    }

    @Override
    public void offset(@Direction int direction, @Px int offset) {
        getEdgeHelper().offset(direction, offset);
    }

    @Override
    public void finish(@NonNull Edge edge) {
        getEdgeHelper().finish(edge.getDirection());
    }

    ///////////////////////////////////////////////////////////////////////////
    // parent scroll
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes, int type) {
        LogUtil.log(String.format(Locale.US, "parent:onStartNestedScroll:type %d,axes:%d ", type, axes));
        return isEnabled() && getEdgeHelper().getTargetView() == target && mEdgeHelper.isNestedScrollEnabled(axes);
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes, int type) {
        LogUtil.log(String.format(Locale.US, "parent:onNestedScrollAccepted:type %d,axes:%d ", type, axes));
        // Reset the counter of how much leftover scroll needs to be consumed.
        getParentHelper().onNestedScrollAccepted(child, target, axes, type);
        // Dispatch up to the nested parent
        startNestedScroll(axes, type);
        mTotalUnconsumed[0] = 0;
        mTotalUnconsumed[1] = 0;
        mNestedScrollInProgress = true;
    }


    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        LogUtil.log(
                String.format(Locale.US, "parent:onNestedPreScroll:type %d,dx:%d,dy:%d consumed:%s", type, dx, dy, StringUtil.toString(consumed)));
        if (type != ViewCompat.TYPE_TOUCH) return;
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        LogUtil.log(String.format(Locale.US, "parent:onNestedScroll:type %d,dxConsumed:%d,dyConsumed:%d,dxUnconsumed:%d ,dyUnconsumed:%d", type,
                dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed));
        onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, mParentScrollConsumed);
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type,
            @NonNull int[] consumed) {
        LogUtil.log(String.format(Locale.US, "parent:onNestedScroll:type %d,dxConsumed:%d,dyConsumed:%d ,dxUnconsumed:%d,dyUnconsumed:%d,consumed:%s",
                type, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, StringUtil.toString(consumed)));
        if (type != ViewCompat.TYPE_TOUCH) return;
        dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, mParentOffsetInWindow, type, consumed);
    }

    @Override
    public void onStopNestedScroll(@NonNull View target, int type) {
        LogUtil.log(String.format(Locale.US, "parent:onStopNestedScroll:type %d,", type));
        getParentHelper().onStopNestedScroll(target, type);
    }

    ///////////////////////////////////////////////////////////////////////////
    // parent scroll
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // child scroll
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public boolean startNestedScroll(int axes, int type) {
        LogUtil.log(String.format(Locale.US, "child:startNestedScroll:type %d,axes:%d ", type, axes));
        return getChildHelper().startNestedScroll(axes, type);
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @Nullable int[] offsetInWindow,
            int type) {
        LogUtil.log(String.format(Locale.US,
                "child:dispatchNestedScroll:type %d,dxConsumed:%d,dyConsumed:%d,dxUnconsumed:%d,dyUnconsumed,%d,offsetInWindow:%s ,consumed:%s ",
                type, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, StringUtil.toString(offsetInWindow)));
        return getChildHelper().dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type);
    }

    @Override
    public void dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @Nullable int[] offsetInWindow, int type,
            @NonNull int[] consumed) {
        LogUtil.log(String.format(Locale.US,
                "child:dispatchNestedScroll:type %d,dxConsumed:%d,dyConsumed:%d,dxUnconsumed:%d,dyUnconsumed,%d,offsetInWindow:%s ,consumed:%s ",
                type, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, StringUtil.toString(offsetInWindow), StringUtil.toString(consumed)));
        getChildHelper().dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type, consumed);
    }


    @Override
    public void stopNestedScroll(int type) {
        LogUtil.log(String.format(Locale.US, "child:stopNestedScroll:type %d", type));
        getChildHelper().stopNestedScroll(type);
    }

    @Override
    public boolean hasNestedScrollingParent(int type) {
        final boolean result = getChildHelper().hasNestedScrollingParent(type);
        LogUtil.log(String.format(Locale.US, "child:hasNestedScrollingParent:type %d result:%s", type, result));
        return result;
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, @Nullable int[] consumed, @Nullable int[] offsetInWindow, int type) {
        final boolean result = getChildHelper().dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type);
        LogUtil.log(
                String.format(Locale.US, "child:dispatchNestedPreScroll:type %d,dx:%d,dy:%d,offsetInWindow:%s ,consumed:%s ,result:%s", type, dx, dy,
                        StringUtil.toString(offsetInWindow), StringUtil.toString(consumed), result));
        return result;
    }


    ///////////////////////////////////////////////////////////////////////////
    // child scroll
    ///////////////////////////////////////////////////////////////////////////


    ///////////////////////////////////////////////////////////////////////////
    // 辅助类
    ///////////////////////////////////////////////////////////////////////////
    @NonNull
    private EdgeHelper getEdgeHelper() {
        if (mEdgeHelper == null) mEdgeHelper = new EdgeHelper(this);
        return mEdgeHelper;
    }

    @NonNull
    private NestedScrollingParentHelper getParentHelper() {
        if (mNestedScrollingParentHelper == null) mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        return mNestedScrollingParentHelper;
    }

    @NonNull
    private NestedScrollingChildHelper getChildHelper() {
        if (mNestedScrollingChildHelper == null) mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);
        return mNestedScrollingChildHelper;
    }

    ///////////////////////////////////////////////////////////////////////////
    //
    ///////////////////////////////////////////////////////////////////////////
    public void setTargetView(@NonNull View view) {
        if (view.getParent() != this) {
            throw new RuntimeException("Target already exists other parent view.");
        }
        if (view.getParent() == null) {
            addView(view, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        getEdgeHelper().setTargetView(view);
    }

    public void setEdgeView(@NonNull View view, @NonNull LayoutParams params) {
        final Edge.Builder builder = new Edge.Builder(view).setEdgeOver(params.mEdgeOver).setEdgeRate(params.mEdgeRate).setScrollTouchUp(
                params.mScrollTouchUp).setFlingFromTarget(params.mFlingFromTarget).setScrollFling(params.mScrollFling).setOffsetInit(
                params.mOffsetInit).setOffsetTarget(params.mOffsetTarget).setScrollOffset(params.mScrollOffset).setScrollSpeed(params.mScrollSpeed);
        view.setLayoutParams(params);
        setEdgeView(params.mDirection, builder);
    }

    public void setEdgeView(@Direction int direction, @NonNull Edge.Builder builder) {
        if (builder.getView().getParent() != this) {
            throw new RuntimeException("Edge already exists other parent view.");
        }
        if (builder.getView().getParent() == null) {
            ViewGroup.LayoutParams lp = builder.getView().getLayoutParams();
            if (lp == null) lp = new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            addView(builder.getView(), lp);
        }
        mEdgeHelper.setEdgeView(direction, builder.build(direction));
    }


    public static class LayoutParams extends FrameLayout.LayoutParams {
        public boolean mTarget = false;
        public boolean mEdgeOver;
        public boolean mFlingFromTarget = true;
        public boolean mScrollOffset = false;
        public boolean mScrollTouchUp = true;
        @Direction
        public int mDirection;
        public int mOffsetInit;
        public int mOffsetTarget = ViewGroup.LayoutParams.WRAP_CONTENT;
        public float mEdgeRate = Edge.EDGE_RATE_DEFAULT;
        public float mScrollFling = Constants.SCROLL_FLING_DEFAULT;
        public float mScrollSpeed = Constants.SCROLL_SPEED_DEFAULT;


        public LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);
            final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.EdgeLayout_Layout);
            mTarget = array.getBoolean(R.styleable.EdgeLayout_Layout_target, mTarget);
            if (!mTarget) {
                mDirection = array.getInteger(R.styleable.EdgeLayout_Layout_direction, Direction.TOP);
                mOffsetInit = array.getDimensionPixelSize(R.styleable.EdgeLayout_Layout_edgeOffsetInit, mOffsetInit);
                mOffsetTarget = array.getLayoutDimension(R.styleable.EdgeLayout_Layout_edgeOffsetTarget, mOffsetTarget);
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


}
