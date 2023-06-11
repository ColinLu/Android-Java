package com.colin.library.android.widgets.edge;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Interpolator;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.OverScroller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
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
 * https://mp.weixin.qq.com/s?__biz=MzA5MzI3NjE2MA==&mid=2650263545&idx=1&sn=50ab0a558ff77be6779c86496d6dfc91&chksm=88633e96bf14b7808dfa6fee8501dae95ad4bf32eef0285f15b43491597e5f19bd738fb5b95d&scene=27
 * 在NestedScrollingChild滚动过程中，它和NestedScrollingParent会一直"保持通讯"，比如：
 * 当Child滚动之前，会通知Parent:"我要开始滚动啦，你看你要不要做点什么"。
 * 当Child在滚动的时候，也会每次通知Parent:"我这次消费了xxx，你看你还要做什么"。
 * 当Child滚动完成，Parent也会收到通知："我滚动完成了"。
 * 当然了，除了手指触摸滚动的，还有惯性滚动，但原理和流程是一样的
 * 因为child是产生滑动的造势者，所以它的api都是以直接的动词开头，而parent的滑动响应是child通知parent的，所以都是以监听on开头：
 * parent ----> onXXXX()
 * child -----> verbXXXX()
 */
public class EdgeLayout extends FrameLayout implements IEdgeLayout, NestedScrollingParent3 {
    private static final Interpolator INTERPOLATOR = Interpolators.QUNITIC;
    private static final int[] INIT = {0, 0};

    private float mNestedPreFlingVelocityScaleDown = 10;
    private int mScrollState = ScrollState.SCROLL_STATE_IDLE;
    private int mLastScrollerY;
    private float mInterceptDownY;

    private final int[] mEdgeUnconsumed = new int[2];
    private final int[] mParentScrollConsumed = new int[2];
    private final int[] mNestedScrollConsumed = new int[2];
    private final int[] mParentOffsetInWindow = new int[2];
    private boolean mScrollEdgeFling;//mReturningToStart
    private boolean mNestScrollEdge;
    private boolean mActionDragged;
    private int mActivePointerId = Constants.INVALID;

    private EdgeHelper mEdgeHelper;
    private final OverScroller mScroller;
    private NestedScrollingParentHelper mNestedScrollingParentHelper;
    private NestedScrollingChildHelper mNestedScrollingChildHelper;
    private final int mTouchSlop;
    private final int mMaximumVelocity;
    private final int mMinimumVelocity;
    private boolean mEnableLegacyRequestDisallowInterceptTouch;

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
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        mMinimumVelocity = configuration.getScaledMinimumFlingVelocity();

        mScroller = new OverScroller(context, INTERPOLATOR);
        if (attrs != null) {
            TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.EdgeLayout, defStyleAttr, 0);
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
        getEdgeHelper().onLayout(l, t, r, b);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public LayoutParams generateLayoutParams(@NonNull AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        return new LayoutParams(lp);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
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
    public boolean isRunning() {
        return getEdgeHelper().isRunning();
    }

    @Override
    public void start() {
        getEdgeHelper().start(Direction.LEFT);
        getEdgeHelper().start(Direction.TOP);
        getEdgeHelper().start(Direction.RIGHT);
        getEdgeHelper().start(Direction.BOTTOM);
    }

    @Override
    public void offset(@Px int offsetX, @Px int offsetY) {

    }

    @Override
    public void finish() {
        getEdgeHelper().finish(Direction.LEFT);
        getEdgeHelper().finish(Direction.TOP);
        getEdgeHelper().finish(Direction.RIGHT);
        getEdgeHelper().finish(Direction.BOTTOM);
    }

    @Override
    public void requestDisallowInterceptTouchEvent(boolean b) {
        // if this is a List < L or another view that doesn't support nested
        // scrolling, ignore this request so that the vertical scroll event
        // isn't stolen
        if ((android.os.Build.VERSION.SDK_INT < 21 && getEdgeHelper().getTargetView() instanceof AbsListView) || (getEdgeHelper().getTargetView() != null && !ViewCompat.isNestedScrollingEnabled(
                getEdgeHelper().getTargetView()))) {
            if (mEnableLegacyRequestDisallowInterceptTouch) {
                // Nope.
            } else {
                // Ignore here, but pass it up to our parent
                ViewParent parent = getParent();
                if (parent != null) {
                    parent.requestDisallowInterceptTouchEvent(b);
                }
            }
        } else {
            super.requestDisallowInterceptTouchEvent(b);
        }
    }
    ///////////////////////////////////////////////////////////////////////////
    // child scroll edge
    ///////////////////////////////////////////////////////////////////////////

//    /**
//     * @param enabled 开启或关闭嵌套滑动
//     */
//    @Override
//    public void setNestedScrollingEnabled(boolean enabled) {
//        getChildHelper().setNestedScrollingEnabled(enabled);
//    }
//
//    /**
//     * @return 返回是否开启嵌套滑动
//     */
//    @Override
//    public boolean isNestedScrollingEnabled() {
//        return getChildHelper().isNestedScrollingEnabled();
//    }
//
//    /**
//     * @return 返回是否有配合滑动NestedScrollingParent
//     */
//    @Override
//    public boolean hasNestedScrollingParent(int type) {
//        final boolean result = getChildHelper().hasNestedScrollingParent(type);
//        LogUtil.log(String.format(Locale.US, "child:hasNestedScrollingParent:type %d result:%s", type, result));
//        return result;
//    }
//
//    /**
//     * 沿着指定的方向开始滑动嵌套滑动
//     *
//     * @param axes 滑动方向
//     * @return 返回是否找到NestedScrollingParent配合滑动
//     */
//    @Override
//    public boolean startNestedScroll(int axes, int type) {
//        LogUtil.log(String.format(Locale.US, "child:startNestedScroll:type %d,axes:%d ", type, axes));
//        return getChildHelper().startNestedScroll(axes, type);
//    }
//
//    /**
//     * 在滑动之前，将滑动值分发给NestedScrollingParent
//     *
//     * @param dx             水平方向消费的距离
//     * @param dy             垂直方向消费的距离
//     * @param consumed       输出坐标数组，consumed[0]为NestedScrollingParent消耗的水平距离、
//     *                       consumed[1]为NestedScrollingParent消耗的垂直距离，此参数可空。
//     * @param offsetInWindow 同上dispatchNestedScroll
//     * @return 返回NestedScrollingParent是否消费部分或全部滑动值
//     */
//    @Override
//    public boolean dispatchNestedPreScroll(int dx, int dy, @Nullable int[] consumed, @Nullable int[] offsetInWindow, int type) {
//        final boolean result = getChildHelper().dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type);
//        LogUtil.log(
//                String.format(Locale.US, "child:dispatchNestedPreScroll:type %d,dx:%d,dy:%d,offsetInWindow:%s ,consumed:%s ,result:%s", type, dx, dy,
//                        StringUtil.toString(offsetInWindow), StringUtil.toString(consumed), result));
//        return result;
//    }
//
//    @Override
//    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @Nullable int[] offsetInWindow) {
//        return dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, ViewCompat.TYPE_TOUCH);
//    }
//
//    @Override
//    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @Nullable int[] offsetInWindow,
//            int type) {
//        LogUtil.log(String.format(Locale.US,
//                "child:dispatchNestedScroll:type %d,dxConsumed:%d,dyConsumed:%d,dxUnconsumed:%d,dyUnconsumed,%d,offsetInWindow:%s ,consumed:%s ",
//                type, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, StringUtil.toString(offsetInWindow)));
//        return getChildHelper().dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type);
//    }
//
//    /**
//     * 滑动完成后，将已经消费、剩余的滑动值分发给NestedScrollingParent
//     *
//     * @param dxConsumed     水平方向消费的距离
//     * @param dyConsumed     垂直方向消费的距离
//     * @param dxUnconsumed   水平方向剩余的距离
//     * @param dyUnconsumed   垂直方向剩余的距离
//     * @param offsetInWindow 含有View从此方法调用之前到调用完成后的屏幕坐标偏移量，
//     *                       可以使用这个偏移量来调整预期的输入坐标（即上面4个消费、剩余的距离）跟踪，此参数可空。
//     * @return 返回该事件是否被成功分发
//     */
//    @Override
//    public void dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, @Nullable int[] offsetInWindow, int type,
//            @NonNull int[] consumed) {
//        LogUtil.log(String.format(Locale.US,
//                "child:dispatchNestedScroll:type %d,dxConsumed:%d,dyConsumed:%d,dxUnconsumed:%d,dyUnconsumed,%d,offsetInWindow:%s ,consumed:%s ",
//                type, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, StringUtil.toString(offsetInWindow), StringUtil.toString(consumed)));
//        getChildHelper().dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow, type, consumed);
//    }
//
//
//    /**
//     * 在惯性滑动之前，将惯性滑动值分发给NestedScrollingParent
//     *
//     * @param velocityX 水平方向的速度
//     * @param velocityY 垂直方向的速度
//     * @return 返回NestedScrollingParent是否消费全部惯性滑动
//     */
//    @Override
//    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
//        return getChildHelper().dispatchNestedPreFling(velocityX, velocityY);
//    }
//
//    /**
//     * 将惯性滑动的速度和NestedScrollingChild自身是否需要消费此惯性滑动分发给NestedScrollingParent
//     *
//     * @param velocityX 水平方向的速度
//     * @param velocityY 垂直方向的速度
//     * @param consumed  NestedScrollingChild自身是否需要消费此惯性滑动
//     * @return 返回NestedScrollingParent是否消费全部惯性滑动
//     */
//    @Override
//    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
//        return getChildHelper().dispatchNestedFling(velocityX, velocityY, consumed);
//    }
//
//
//    /*停止嵌套滑动*/
//    @Override
//    public void stopNestedScroll(int type) {
//        LogUtil.log(String.format(Locale.US, "child:stopNestedScroll:type %d", type));
//        getChildHelper().stopNestedScroll(type);
//    }

    ///////////////////////////////////////////////////////////////////////////
    // child scroll
    ///////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////
    // parent scroll
    ///////////////////////////////////////////////////////////////////////////

    /**
     * @return 返回当前嵌套滑动的方向
     */
    @Override
    public int getNestedScrollAxes() {
        return getParentHelper().getNestedScrollAxes();
    }

    /**
     * 对NestedScrollingChild发起嵌套滑动作出应答
     *
     * @param child  布局中包含下面target的直接父View
     * @param target 发起嵌套滑动的NestedScrollingChild的View
     * @param axes   滑动方向
     * @return #{@value true 表示此Parent能够接收此次嵌套滑动事件 false 不接收此次嵌套滑动事件，后续方法都不会调用}
     */
    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes) {
        return onStartNestedScroll(child, target, axes, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes, int type) {
        LogUtil.log(String.format(Locale.US, "parent:onStartNestedScroll:type %d,axes:%d ", type, axes));
        return isEnabled() && getEdgeHelper().getTargetView() == target && getEdgeHelper().isNestedScrollEnabled(axes);
    }

    /**
     * NestedScrollingParent配合处理嵌套滑动回调此方法
     * 当 onStartNestedScroll() 方法返回 true 后，此方法会立刻调用,可在此方法做每次嵌套滑动的初始化工作
     *
     * @param child  布局中包含下面target的直接父View
     * @param target 发起嵌套滑动的NestedScrollingChild的View
     * @param axes   滑动方向
     */
    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes) {
        onNestedScrollAccepted(child, target, axes, ViewCompat.TYPE_TOUCH);
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes, int type) {
        LogUtil.log(String.format(Locale.US, "parent:onNestedScrollAccepted:type %d,axes:%d ", type, axes));
        // Reset the counter of how much leftover scroll needs to be consumed.
        getParentHelper().onNestedScrollAccepted(child, target, axes, type);
        mEdgeUnconsumed[0] = 0;
        mEdgeUnconsumed[1] = 0;
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed) {
        onNestedPreScroll(target, dx, dy, consumed, ViewCompat.TYPE_TOUCH);
    }

    /**
     * NestedScrollingChild滑动完之前将滑动值分发给NestedScrollingParent回调此方法
     *
     * @param target   发起嵌套滑动的NestedScrollingChild的View
     * @param dx       水平方向的距离
     * @param dy       水平方向的距离 dy>0->>向上；dy<0 ->>向下
     * @param consumed 返回NestedScrollingParent是否消费部分或全部滑动值
     */
    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        LogUtil.log(
                String.format(Locale.US, "parent:onNestedPreScroll:type %d,dx:%d,dy:%d consumed:%s", type, dx, dy, StringUtil.toString(consumed)));
        if (type != ViewCompat.TYPE_TOUCH || (dx == 0 && dy == 0)) return;
        //垂直向上
        if (Math.abs(dy) > Math.abs(dx) && dy > 0) {
            if (dy > mEdgeUnconsumed[1]) {
                consumed[1] = mEdgeUnconsumed[1];
                mEdgeUnconsumed[1] = 0;
            } else {
                mEdgeUnconsumed[1] -= dy;
                consumed[1] = dy;
            }
            startEdge(Direction.TOP, mEdgeUnconsumed[1]);
        }


    }

    private void startEdge(@Direction int direction, int offset) {

    }

    /**
     * NestedScrollingChild在惯性滑动之前,将惯性滑动的速度分发给NestedScrollingParent
     *
     * @param target    同上
     * @param velocityX 同上
     * @param velocityY 同上
     * @return 返回NestedScrollingParent是否消费全部惯性滑动
     */
    @Override
    public boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY) {
        return super.onNestedPreFling(target, velocityX, velocityY);
    }

    /**
     * NestedScrollingChild在惯性滑动之前，将惯性滑动的速度和NestedScrollingChild自身是否需要消费此惯性滑动分
     * 发给NestedScrollingParent回调此方法
     *
     * @param target    发起嵌套滑动的NestedScrollingChild的View
     * @param velocityX 水平方向的速度
     * @param velocityY 垂直方向的速度
     * @param consumed  NestedScrollingChild自身是否需要消费此惯性滑动
     * @return 返回NestedScrollingParent是否消费全部惯性滑动
     */
    @Override
    public boolean onNestedFling(@NonNull View target, float velocityX, float velocityY, boolean consumed) {
        return super.onNestedFling(target, velocityX, velocityY, consumed);
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, ViewCompat.TYPE_TOUCH, mParentScrollConsumed);
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, mParentScrollConsumed);
    }

    /**
     * NestedScrollingChild滑动完成后将滑动值分发给NestedScrollingParent回调此方法
     *
     * @param target       发起嵌套滑动的NestedScrollingChild的View
     * @param dxConsumed   水平方向消费的距离
     * @param dyConsumed   垂直方向消费的距离
     * @param dxUnconsumed 水平方向剩余的距离
     * @param dyUnconsumed 垂直方向剩余的距离
     */
    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type,
            @NonNull int[] consumed) {
        LogUtil.log(String.format(Locale.US, "parent:onNestedScroll:type %d,dxConsumed:%d,dyConsumed:%d ,dxUnconsumed:%d,dyUnconsumed:%d,consumed:%s",
                type, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, StringUtil.toString(consumed)));
        if (type != ViewCompat.TYPE_TOUCH) return;
    }

    /**
     * 嵌套滑动结束
     *
     * @param target 发起嵌套滑动的NestedScrollingChild的View
     */
    @Override
    public void onStopNestedScroll(@NonNull View target, int type) {
        LogUtil.log(String.format(Locale.US, "parent:onStopNestedScroll:type %d,", type));
        getParentHelper().onStopNestedScroll(target, type);
    }

    ///////////////////////////////////////////////////////////////////////////
    // parent scroll
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

    public void setEnableLegacyRequestDisallowInterceptTouch(boolean enable) {
        this.mEnableLegacyRequestDisallowInterceptTouch = enable;
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
