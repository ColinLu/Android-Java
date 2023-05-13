package com.colin.library.android.widgets.edge;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.content.ContextCompat;

import com.colin.library.android.utils.LogUtil;
import com.colin.library.android.utils.WidgetUtil;
import com.colin.library.android.widgets.R;
import com.colin.library.android.widgets.Utils;

import java.util.Locale;


public class LoadRefreshView extends AppCompatImageView implements EdgeWatcher {
    private static final int MAX_ALPHA = 255;
    private static final float TRIM_RATE = 0.85f;
    private static final float TRIM_OFFSET = 0.4f;
    private static final float SCALE_DEFAULT = 0.8f;
    private static final int CIRCLE_DIAMETER = 40;
    private static final int CIRCLE_DIAMETER_LARGE = 56;
    private final ProgressDrawable mProgress;
    private final DisplayMetrics mDisplayMetrics;
    private int mCircleDiameter;
    private int mRadius;

    public LoadRefreshView(Context context) {
        this(context, null, R.attr.LoadRefreshViewStyle);
    }

    public LoadRefreshView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.LoadRefreshViewStyle);
    }

    public LoadRefreshView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);
        mDisplayMetrics = context.getResources().getDisplayMetrics();
        mCircleDiameter = (int) (CIRCLE_DIAMETER * mDisplayMetrics.density);
        mProgress = new ProgressDrawable(context);
        if (attrs != null) {
            final TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.LoadRefreshView, defStyleAttr, 0);
            mProgress.setStyle(array.getInteger(R.styleable.LoadRefreshView_progressStyle, ProgressDrawable.DEFAULT));
            mProgress.setColorSchemeColors(
                    array.getColor(R.styleable.LoadRefreshView_progressColor, ContextCompat.getColor(context, R.color.colorAccent)));
            mProgress.setAlpha(array.getInt(R.styleable.LoadRefreshView_progressAlpha, MAX_ALPHA));
            mProgress.setArrowScale(array.getFloat(R.styleable.LoadRefreshView_progressScale, SCALE_DEFAULT));
            setRadius(array.getDimensionPixelSize(R.styleable.LoadRefreshView_radius, mRadius));
            array.recycle();
        } else {
            mProgress.setStyle(ProgressDrawable.DEFAULT);
            mProgress.setColorSchemeColors(ContextCompat.getColor(context, R.color.colorAccent));
            mProgress.setAlpha(MAX_ALPHA);
            mProgress.setArrowScale(SCALE_DEFAULT);
            setRadius(mRadius);
        }
        setImageDrawable(mProgress);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mCircleDiameter, mCircleDiameter);
    }

    @Override
    public void offset(@NonNull Edge edge, int offset) {
        if (mProgress.isRunning()) return;
        final int targetOffset = edge.getOffsetTarget();
        final float end = TRIM_RATE * Math.min(targetOffset, offset) / targetOffset;
        final float rotate = TRIM_OFFSET * offset / targetOffset;
        LogUtil.d(String.format(Locale.US, "targetOffset:%d  end:%f rotate:%f", targetOffset, end, rotate));
        mProgress.setArrowEnabled(true);
        mProgress.setStartEndTrim(0, end);
        mProgress.setProgressRotation(rotate);
    }

    @Override
    public void start() {
        mProgress.start();
    }

    @Override
    public void finish() {
        mProgress.stop();
    }

    @Override
    protected void onDetachedFromWindow() {
        mProgress.stop();
        super.onDetachedFromWindow();
    }

    public void setColorRes(@ColorRes int... resIds) {
        final Context context = getContext();
        int[] colorRes = new int[resIds.length];
        for (int i = 0; i < resIds.length; i++) {
            colorRes[i] = ContextCompat.getColor(context, resIds[i]);
        }
        setColor(colorRes);
    }

    public void setColor(@ColorInt int... colors) {
        mProgress.setColorSchemeColors(colors);
    }

    /**
     * One of DEFAULT, or LARGE.
     */
    public void setSize(@ProgressDrawable.ProgressStyle int style) {
        if (style != ProgressDrawable.LARGE && style != ProgressDrawable.DEFAULT) return;
        if (style == ProgressDrawable.LARGE) {
            mCircleDiameter = (int) (CIRCLE_DIAMETER_LARGE * mDisplayMetrics.density);
        } else mCircleDiameter = (int) (CIRCLE_DIAMETER * mDisplayMetrics.density);

        setImageDrawable(null);
        mProgress.setStyle(style);
        setImageDrawable(mProgress);
    }

    public void setRadius(int radius) {
        if (mRadius != radius) {
            this.mRadius = radius;
            WidgetUtil.setRadius(this, radius);
        }
    }


}
