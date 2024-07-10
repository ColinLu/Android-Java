package com.colin.library.android.widgets.bar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import com.colin.library.android.utils.NumberUtil;
import com.colin.library.android.utils.ResourceUtil;

/**
 * 作者： ColinLu
 * 时间： 2022-12-31 21:40
 * <p>
 * 描述： 快速滚动条
 */
public class DragScrollBar extends View {

    private final int[] STATE_PRESSED = new int[]{android.R.attr.state_pressed};
    private final int[] STATE_NORMAL = new int[]{};
    private final Runnable mDelayInvalidateRunnable = this::invalidate;
    private final int mAdjustDistanceProtection = ResourceUtil.dp2px(20);
    private final int mAdjustMaxDistanceOnce = ResourceUtil.dp2px(4);
    private Drawable mDragDrawable;
    private long mStartTransitionTime = 0;
    private int mKeepShownTime = 800;
    private int mTransitionDuration = 100;
    private int mDrawableDrawTop = -1;
    private float mCurrentAlpha = 0f;
    private float mPercent = 0f;
    private float mDragInnerTop = 0;
    private boolean mAdjustDistanceWithAnimation = true;
    private boolean enableFadeInAndOut = true;
    private boolean mIsInDragging = false;
    private Callback mCallback;

    public DragScrollBar(@NonNull Context context) {
        this(context, (AttributeSet) null);
    }

    public DragScrollBar(@NonNull Context context, @NonNull Drawable dragDrawable) {
        this(context, (AttributeSet) null);
        this.mDragDrawable = dragDrawable.mutate();
    }

    public DragScrollBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setCallback(@Nullable Callback callback) {
        mCallback = callback;
    }

    public void setAdjustDistanceWithAnimation(boolean adjustDistanceWithAnimation) {
        this.mAdjustDistanceWithAnimation = adjustDistanceWithAnimation;
    }

    public void setKeepShownTime(int keepShownTime) {
        this.mKeepShownTime = keepShownTime;
    }

    public void setTransitionDuration(int transitionDuration) {
        this.mTransitionDuration = transitionDuration;
    }

    public void setEnableFadeInAndOut(boolean enableFadeInAndOut) {
        this.enableFadeInAndOut = enableFadeInAndOut;
    }

    public boolean isEnableFadeInAndOut() {
        return enableFadeInAndOut;
    }

    public void setDragDrawable(Drawable dragDrawable) {
        mDragDrawable = dragDrawable.mutate();
        invalidate();
    }

    public void setPercent(float percent) {
        if (!mIsInDragging) setPercentInternal(percent);
    }

    private void setPercentInternal(float percent) {
        mPercent = percent;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final Drawable drawable = mDragDrawable;
        if (drawable == null) super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        else super.onMeasure(MeasureSpec.makeMeasureSpec(drawable.getIntrinsicWidth(), MeasureSpec.EXACTLY), heightMeasureSpec);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        final Drawable drawable = mDragDrawable;
        if (drawable == null) return super.onTouchEvent(event);
        final int action = event.getActionMasked();
        final float x = event.getX();
        final float y = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mIsInDragging = false;
                if (mCurrentAlpha > 0 && x > getWidth() - drawable.getIntrinsicWidth() && y >= mDrawableDrawTop && y <= mDrawableDrawTop + drawable.getIntrinsicHeight()) {
                    mDragInnerTop = y - mDrawableDrawTop;
                    getParent().requestDisallowInterceptTouchEvent(true);
                    mIsInDragging = true;
                    if (mCallback != null) {
                        mCallback.onDragStarted();
                        mDragDrawable.setState(STATE_PRESSED);
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mIsInDragging) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    onDragging(drawable, y);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mIsInDragging) {
                    mIsInDragging = false;
                    onDragging(drawable, y);
                    if (mCallback != null) {
                        mCallback.onDragEnd();
                        mDragDrawable.setState(STATE_NORMAL);
                    }
                }
                break;
            default:
                break;
        }
        return mIsInDragging;
    }

    private void onDragging(Drawable drawable, float currentY) {
        float percent = (currentY - getScrollBarTopMargin() - mDragInnerTop) / (getHeight() - getScrollBarBottomMargin() - getScrollBarTopMargin() - drawable.getIntrinsicHeight());
        percent = NumberUtil.constrain(percent, 0f, 1f);
        if (mCallback != null) mCallback.onDragToPercent(percent);
        setPercentInternal(percent);
    }

    public void awakenScrollBar() {
        if (mDragDrawable == null) {
            mDragDrawable = ContextCompat.getDrawable(getContext(), android.R.drawable.status_bar_item_app_background);
        }
        final long current = System.currentTimeMillis();
        if (current - mStartTransitionTime > mTransitionDuration) {
            mStartTransitionTime = current - mTransitionDuration;
        }
        ViewCompat.postInvalidateOnAnimation(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Drawable drawable = mDragDrawable;
        if (drawable == null) return;
        int drawableWidth = drawable.getIntrinsicWidth();
        int drawableHeight = drawable.getIntrinsicHeight();
        if (drawableWidth <= 0 || drawableHeight <= 0) return;

        int needInvalidate = -1;
        if (enableFadeInAndOut) {
            long timeAfterStartShow = System.currentTimeMillis() - mStartTransitionTime;
            long timeAfterEndShow;
            if (timeAfterStartShow < mTransitionDuration) {
                // in show animation
                mCurrentAlpha = timeAfterStartShow * 1f / mTransitionDuration;
                needInvalidate = 0;
            } else if (timeAfterStartShow - mTransitionDuration < mKeepShownTime) {
                // keep show
                mCurrentAlpha = 1f;
                needInvalidate = (int) (mKeepShownTime - (timeAfterStartShow - mTransitionDuration));
            } else if ((timeAfterEndShow = timeAfterStartShow - mTransitionDuration - mKeepShownTime) < mTransitionDuration) {
                // in hide animation
                mCurrentAlpha = 1 - timeAfterEndShow * 1f / mTransitionDuration;
                needInvalidate = 0;
            } else mCurrentAlpha = 0f;
            if (mCurrentAlpha <= 0f) return;
        } else {
            mCurrentAlpha = 1f;
        }
        drawable.setAlpha((int) (mCurrentAlpha * 255));

        int totalHeight = getHeight() - getScrollBarTopMargin() - getScrollBarBottomMargin();
        int totalWidth = getWidth();
        int top = getScrollBarTopMargin() + (int) ((totalHeight - drawableHeight) * mPercent);
        int left = totalWidth - drawableWidth;
        if (!mIsInDragging && mDrawableDrawTop > 0 && mAdjustDistanceWithAnimation) {
            int moveDistance = top - mDrawableDrawTop;
            if (moveDistance > mAdjustMaxDistanceOnce && moveDistance < mAdjustDistanceProtection) {
                top = mDrawableDrawTop + mAdjustMaxDistanceOnce;
                needInvalidate = 0;
            } else if (moveDistance < -mAdjustMaxDistanceOnce && moveDistance > -mAdjustDistanceProtection) {
                top = mDrawableDrawTop - mAdjustMaxDistanceOnce;
                needInvalidate = 0;
            }
        }
        drawable.setBounds(0, 0, drawableWidth, drawableHeight);
        canvas.save();
        canvas.translate(left, top);
        drawable.draw(canvas);
        canvas.restore();
        mDrawableDrawTop = top;

        if (needInvalidate == 0) invalidate();
        else if (needInvalidate > 0) ViewCompat.postOnAnimationDelayed(this, mDelayInvalidateRunnable, needInvalidate);
    }

    protected int getScrollBarTopMargin() {
        return 0;
    }

    protected int getScrollBarBottomMargin() {
        return 0;
    }

    public interface Callback {
        void onDragStarted();

        void onDragToPercent(float percent);

        void onDragEnd();
    }
}
