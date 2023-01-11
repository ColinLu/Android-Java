package com.colin.library.android.widgets.progress;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;


import com.colin.library.android.widgets.R;
import com.colin.library.android.widgets.Utils;

import java.util.Locale;


/**
 * 作者： ColinLu
 * 时间： 2019-11-25 09:46
 * <p>
 * 描述： 显示进度百分比
 */
public class NumberProgressBar extends View {
    /**
     * For save and restore instance of progressbar.
     */
    private static final String INSTANCE_STATE = "saved_instance";
    private static final String INSTANCE_TEXT_COLOR = "text_color";
    private static final String INSTANCE_TEXT_SIZE = "text_size";
    private static final String INSTANCE_REACHED_BAR_HEIGHT = "reached_bar_height";
    private static final String INSTANCE_REACHED_BAR_COLOR = "reached_bar_color";
    private static final String INSTANCE_UNREACHED_BAR_HEIGHT = "unreached_bar_height";
    private static final String INSTANCE_UNREACHED_BAR_COLOR = "unreached_bar_color";
    private static final String INSTANCE_MAX = "max";
    private static final String INSTANCE_PROGRESS = "progress";
    private static final String INSTANCE_SUFFIX = "suffix";
    private static final String INSTANCE_PREFIX = "prefix";
    private static final String INSTANCE_TEXT_VISIBILITY = "text_visibility";

    private final Paint mReachedBarPaint;
    private final Paint mUnreachedBarPaint;
    private final Paint mTextPaint;
    private final RectF mUnreachedRectF = new RectF(0, 0, 0, 0);
    private final RectF mReachedRectF = new RectF(0, 0, 0, 0);

    private int mMaxProgress = 100;
    private int mCurrentProgress = 0;
    @ColorInt
    private int mTextColor;
    private float mTextSize;
    @ColorInt
    private int mReachedBarColor;
    private float mReachedBarHeight;
    @ColorInt
    private int mUnreachedBarColor;
    private float mUnreachedBarHeight;

    /*显示文字后缀*/
    private String mSuffix = "%";
    /*显示文字前缀*/
    private String mPrefix = "";
    /*绘制当前文字的宽度*/
    private float mDrawTextWidth;
    private float mDrawTextStart;
    private float mDrawTextEnd;

    private String mCurrentDrawText;
    private OnFormatListener mOnFormatListener;


    /**
     * The progress text offset.
     */
    private float mOffset;

    /**
     * Determine if need to draw unreached area.
     */
    private boolean mDrawUnreachedBar = true;

    private boolean mDrawReachedBar = true;

    private boolean mShowText = true;


    public NumberProgressBar(Context context) {
        this(context, null);
    }

    public NumberProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumberProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTextColor = ContextCompat.getColor(context, R.color.colorAccent);
        mReachedBarColor = ContextCompat.getColor(context, R.color.colorAccent);
        mUnreachedBarColor = ContextCompat.getColor(context, R.color.colorPrimary);

        final TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.NumberProgressBar, defStyleAttr, 0);
        mShowText = typedArray.getBoolean(R.styleable.NumberProgressBar_numberShowText, mShowText);
        mTextColor = typedArray.getColor(R.styleable.NumberProgressBar_numberTextColor, mTextColor);
        mTextSize = typedArray.getDimension(R.styleable.NumberProgressBar_numberTextSize, Utils.sp2px(context, 10));
        mOffset = typedArray.getDimension(R.styleable.NumberProgressBar_numberTextOffset, Utils.dp2px(context, 3.0F));

        mReachedBarColor = typedArray.getColor(R.styleable.NumberProgressBar_numberReachedColor, mReachedBarColor);
        mReachedBarHeight = typedArray.getDimension(R.styleable.NumberProgressBar_numberReachedHeight, Utils.dp2px(context, 1.5F));
        mUnreachedBarColor = typedArray.getColor(R.styleable.NumberProgressBar_numberUnreachedColor, mUnreachedBarColor);
        mUnreachedBarHeight = typedArray.getDimension(R.styleable.NumberProgressBar_numberUnreachedHeight, Utils.dp2px(context, 1.0F));

        mCurrentProgress = typedArray.getInteger(R.styleable.NumberProgressBar_numberCurrent, mCurrentProgress);
        mMaxProgress = typedArray.getInteger(R.styleable.NumberProgressBar_numberMax, mMaxProgress);

        typedArray.recycle();


        mReachedBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mReachedBarPaint.setColor(mReachedBarColor);

        mUnreachedBarPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mUnreachedBarPaint.setColor(mUnreachedBarColor);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
    }

    @Override
    protected int getSuggestedMinimumWidth() {
        return (int) mTextSize;
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        return Math.max((int) mTextSize, Math.max((int) mReachedBarHeight, (int) mUnreachedBarHeight));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec, true), measure(heightMeasureSpec, false));
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (mShowText) calculateRectFWithText(getProgress());
        else calculateRectFNoText(getProgress());
        if (mDrawReachedBar) canvas.drawRect(mReachedRectF, mReachedBarPaint);
        if (mDrawUnreachedBar) canvas.drawRect(mUnreachedRectF, mUnreachedBarPaint);
        if (mShowText) canvas.drawText(mCurrentDrawText, mDrawTextStart, mDrawTextEnd, mTextPaint);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putInt(INSTANCE_TEXT_COLOR, getTextColor());
        bundle.putFloat(INSTANCE_TEXT_SIZE, getProgressTextSize());
        bundle.putFloat(INSTANCE_REACHED_BAR_HEIGHT, getReachedBarHeight());
        bundle.putFloat(INSTANCE_UNREACHED_BAR_HEIGHT, getUnreachedBarHeight());
        bundle.putInt(INSTANCE_REACHED_BAR_COLOR, getReachedBarColor());
        bundle.putInt(INSTANCE_UNREACHED_BAR_COLOR, getUnreachedBarColor());
        bundle.putInt(INSTANCE_MAX, getMax());
        bundle.putInt(INSTANCE_PROGRESS, getProgress());
        bundle.putString(INSTANCE_SUFFIX, getSuffix());
        bundle.putString(INSTANCE_PREFIX, getPrefix());
        bundle.putBoolean(INSTANCE_TEXT_VISIBILITY, isShowText());
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;
            mTextColor = bundle.getInt(INSTANCE_TEXT_COLOR);
            mTextSize = bundle.getFloat(INSTANCE_TEXT_SIZE);
            mReachedBarHeight = bundle.getFloat(INSTANCE_REACHED_BAR_HEIGHT);
            mUnreachedBarHeight = bundle.getFloat(INSTANCE_UNREACHED_BAR_HEIGHT);
            mReachedBarColor = bundle.getInt(INSTANCE_REACHED_BAR_COLOR);
            mUnreachedBarColor = bundle.getInt(INSTANCE_UNREACHED_BAR_COLOR);
            mTextPaint.setColor(mTextColor);
            mTextPaint.setTextSize(mTextSize);
            mReachedBarPaint.setColor(mReachedBarColor);
            mUnreachedBarPaint.setColor(mUnreachedBarColor);
            setMax(bundle.getInt(INSTANCE_MAX));
            setProgress(bundle.getInt(INSTANCE_PROGRESS));
            setSuffix(bundle.getString(INSTANCE_SUFFIX, getSuffix()));
            setPrefix(bundle.getString(INSTANCE_PREFIX, getPrefix()));
            setShowText(bundle.getBoolean(INSTANCE_PREFIX, isShowText()));
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
        } else super.onRestoreInstanceState(state);
    }

    private int measure(int measureSpec, boolean isWidth) {
        final int mode = MeasureSpec.getMode(measureSpec);
        final int size = MeasureSpec.getSize(measureSpec);
        final int padding = isWidth ? getPaddingLeft() + getPaddingRight() : getPaddingTop() + getPaddingBottom();
        int result;
        if (mode == MeasureSpec.EXACTLY) result = size;
        else {
            result = isWidth ? getSuggestedMinimumWidth() : getSuggestedMinimumHeight();
            result += padding;
            if (mode == MeasureSpec.AT_MOST) {
                if (isWidth) result = Math.max(result, size);
                else result = Math.min(result, size);
            }
        }
        return result;
    }


    private void calculateRectFWithText(final int progress) {
        mCurrentDrawText = formatText(progress);
        mDrawTextWidth = mTextPaint.measureText(mCurrentDrawText);

        if (progress == 0) {
            mDrawReachedBar = false;
            mDrawTextStart = getPaddingLeft();
        } else {
            mDrawReachedBar = true;
            mReachedRectF.left = getPaddingLeft();
            mReachedRectF.top = getHeight() / 2.0f - mReachedBarHeight / 2.0f;
            mReachedRectF.right = (getWidth() - getPaddingLeft() - getPaddingRight()) / (getMax() * 1.0f) * progress - mOffset + getPaddingLeft();
            mReachedRectF.bottom = getHeight() / 2.0f + mReachedBarHeight / 2.0f;
            mDrawTextStart = (mReachedRectF.right + mOffset);
        }

        mDrawTextEnd = (int) ((getHeight() / 2.0f) - ((mTextPaint.descent() + mTextPaint.ascent()) / 2.0f));

        if ((mDrawTextStart + mDrawTextWidth) >= getWidth() - getPaddingRight()) {
            mDrawTextStart = getWidth() - getPaddingRight() - mDrawTextWidth;
            mReachedRectF.right = mDrawTextStart - mOffset;
        }

        float unreachedBarStart = mDrawTextStart + mDrawTextWidth + mOffset;
        if (unreachedBarStart >= getWidth() - getPaddingRight()) mDrawUnreachedBar = false;
        else {
            mDrawUnreachedBar = true;
            mUnreachedRectF.left = unreachedBarStart;
            mUnreachedRectF.right = getWidth() - getPaddingRight();
            mUnreachedRectF.top = getHeight() / 2.0f + -mUnreachedBarHeight / 2.0f;
            mUnreachedRectF.bottom = getHeight() / 2.0f + mUnreachedBarHeight / 2.0f;
        }
    }

    @NonNull
    private String formatText(final int progress) {
        String formatData = mOnFormatListener == null ? null : mOnFormatListener.format(this, getMax(), progress);
        if (formatData == null) {
            String data = String.format(Locale.US, "%d", progress * 100 / getMax());
            formatData = mPrefix + data + mSuffix;
        }
        return formatData;
    }

    private void calculateRectFNoText(final int progress) {
        mReachedRectF.left = getPaddingLeft();
        mReachedRectF.top = getHeight() / 2.0f - mReachedBarHeight / 2.0f;
        mReachedRectF.right = (getWidth() - getPaddingLeft() - getPaddingRight()) / (getMax() * 1.0f) * progress + getPaddingLeft();
        mReachedRectF.bottom = getHeight() / 2.0f + mReachedBarHeight / 2.0f;

        mUnreachedRectF.left = mReachedRectF.right;
        mUnreachedRectF.right = getWidth() - getPaddingRight();
        mUnreachedRectF.top = getHeight() / 2.0f + -mUnreachedBarHeight / 2.0f;
        mUnreachedRectF.bottom = getHeight() / 2.0f + mUnreachedBarHeight / 2.0f;
    }


    ///////////////////////////////////////////////////////////////////////////
    // 对外公开 方法
    ///////////////////////////////////////////////////////////////////////////
    public void setProgressTextSize(float textSize) {
        if (this.mTextSize == textSize) return;
        this.mTextSize = textSize;
        mTextPaint.setTextSize(mTextSize);
        postInvalidate();
    }

    public void setProgressTextColor(@ColorInt int color) {
        if (this.mTextColor == color) return;
        this.mTextColor = color;
        mTextPaint.setColor(mTextColor);
        postInvalidate();
    }

    public void setUnreachedBarColor(@ColorInt int color) {
        if (this.mUnreachedBarColor == color) return;
        this.mUnreachedBarColor = color;
        mUnreachedBarPaint.setColor(color);
        postInvalidate();
    }

    public void setReachedBarColor(@ColorInt int color) {
        if (this.mReachedBarColor == color) return;
        this.mReachedBarColor = color;
        mReachedBarPaint.setColor(color);
        postInvalidate();
    }

    public void setReachedBarHeight(float height) {
        if (this.mReachedBarHeight == height) return;
        mReachedBarHeight = height;
        postInvalidate();
    }

    public void setUnreachedBarHeight(float height) {
        if (this.mUnreachedBarHeight == height) return;
        mUnreachedBarHeight = height;
        postInvalidate();
    }

    public void setMax(int maxProgress) {
        if (this.mMaxProgress == maxProgress || maxProgress <= 0) return;
        this.mMaxProgress = maxProgress;
        postInvalidate();
    }

    public void setPrefix(@Nullable String prefix) {
        if (TextUtils.equals(this.mPrefix, prefix)) return;
        if (null == prefix) this.mPrefix = "";
        this.mPrefix = prefix;
        postInvalidate();
    }

    public void setSuffix(@Nullable String suffix) {
        if (TextUtils.equals(this.mSuffix, suffix)) return;
        if (null == suffix) this.mSuffix = "";
        this.mSuffix = suffix;
        postInvalidate();
    }

    public void setProgressBy(int progress) {
        setProgress(getProgress() + progress);
    }

    public void setProgress(int progress) {
        if (this.mCurrentProgress == progress) return;
        if (progress < 0) this.mCurrentProgress = 0;
        else this.mCurrentProgress = Math.min(progress, getMax());
        postInvalidate();
    }


    public void setOnFormatListener(OnFormatListener onFormatListener) {
        mOnFormatListener = onFormatListener;
    }

    public boolean isShowText() {
        return mShowText;
    }

    public void setShowText(boolean showText) {
        if (this.mShowText == showText) return;
        mShowText = showText;
        invalidate();
    }


    public int getTextColor() {
        return mTextColor;
    }

    public float getProgressTextSize() {
        return mTextSize;
    }

    public int getUnreachedBarColor() {
        return mUnreachedBarColor;
    }

    public int getReachedBarColor() {
        return mReachedBarColor;
    }

    public int getProgress() {
        return mCurrentProgress;
    }

    public int getMax() {
        return mMaxProgress;
    }

    public float getReachedBarHeight() {
        return mReachedBarHeight;
    }

    public float getUnreachedBarHeight() {
        return mUnreachedBarHeight;
    }

    public String getPrefix() {
        return mPrefix;
    }

    public String getSuffix() {
        return mSuffix;
    }


    public interface OnFormatListener {
        @Nullable
        String format(@NonNull View view, int max, int progress);
    }
}
