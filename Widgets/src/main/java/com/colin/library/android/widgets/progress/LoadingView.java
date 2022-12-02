package com.colin.library.android.widgets.progress;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.core.content.ContextCompat;

import com.colin.library.android.widgets.R;
import com.colin.library.android.widgets.Utils;


public class LoadingView extends View {
    private int mSize;
    private int mColor;
    private int mAnimateValue;
    private final Paint mPaint;
    private ValueAnimator mAnimator;
    private static final int LINE_COUNT = 12;
    private static final int DEGREE_PER_LINE = 360 / LINE_COUNT;

    public LoadingView(@NonNull Context context) {
        this(context, null, Resources.ID_NULL);
    }

    public LoadingView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, Resources.ID_NULL);
    }

    public LoadingView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mSize = context.getResources().getDimensionPixelSize(R.dimen.load_size_default);
        mColor = ContextCompat.getColor(context, R.color.colorAccent);
        if (attrs != null) {
            final TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.LoadingView, defStyleAttr, Resources.ID_NULL);
            mSize = array.getDimensionPixelSize(R.styleable.LoadingView_loadSize, mSize);
            mColor = array.getColor(R.styleable.LoadingView_loadColor, mColor);
            array.recycle();
        }
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setColor(mColor);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(Utils.getSize(widthMeasureSpec, mSize), Utils.getSize(heightMeasureSpec, mSize));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final int count = canvas.saveLayer(0, 0, getWidth(), getHeight(), null, Canvas.ALL_SAVE_FLAG);
        drawLoading(canvas, mAnimateValue * DEGREE_PER_LINE);
        canvas.restoreToCount(count);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        start();
    }

    @Override
    protected void onDetachedFromWindow() {
        stop();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility == VISIBLE) start();
        else stop();
    }


    public LoadingView setColor(@ColorInt int color) {
        if (mColor != color) {
            mColor = color;
            mPaint.setColor(color);
            invalidate();
        }
        return this;
    }

    public LoadingView setSize(@Px int size) {
        if (mSize != size) {
            mSize = size;
            requestLayout();
        }
        return this;
    }


    public void start() {
        if (mAnimator == null) {
            mAnimator = ValueAnimator.ofInt(0, LINE_COUNT - 1);
            mAnimator.addUpdateListener(mUpdateListener);
            mAnimator.setDuration(600);
            mAnimator.setRepeatMode(ValueAnimator.RESTART);
            mAnimator.setRepeatCount(ValueAnimator.INFINITE);
            mAnimator.setInterpolator(new LinearInterpolator());
            mAnimator.start();
        } else if (!mAnimator.isStarted()) mAnimator.start();
    }

    public void stop() {
        if (mAnimator != null) {
            mAnimator.removeUpdateListener(mUpdateListener);
            mAnimator.removeAllUpdateListeners();
            mAnimator.cancel();
            mAnimator = null;
        }
    }


    private void drawLoading(Canvas canvas, int rotateDegrees) {
        final int width = mSize / 12, height = mSize / 6, halfSize = mSize >> 1;
        final int radius = width >> 1;
        mPaint.setStrokeWidth(width);
        canvas.rotate(rotateDegrees, halfSize, halfSize);
        canvas.translate(halfSize, halfSize);
        for (int i = 0; i < LINE_COUNT; i++) {
            canvas.rotate(DEGREE_PER_LINE);
            mPaint.setAlpha((int) (255f * (i + 1) / LINE_COUNT));
            canvas.translate(0, -halfSize + radius);
            canvas.drawLine(0, 0, 0, height, mPaint);
            canvas.translate(0, halfSize - radius);
        }
    }

    private final ValueAnimator.AnimatorUpdateListener mUpdateListener = animation -> {
        mAnimateValue = (int) animation.getAnimatedValue();
        invalidate();
    };

}
