package com.colin.library.android.widgets.image;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewOutlineProvider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.appcompat.widget.AppCompatImageView;

import com.colin.library.android.widgets.R;
import com.colin.library.android.widgets.RoundOutlineProvider;

/**
 * 作者： ColinLu
 * 时间： 2022-01-30 07:27
 * <p>
 * 描述： 圆角
 */
public class CircleImageView extends AppCompatImageView {
    private float mRadius;
    private Path mPath;
    private RectF mRect;
    private ViewOutlineProvider mOutlineProvider;

    public CircleImageView(@NonNull Context context) {
        this(context, null, 0);
    }

    public CircleImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView, defStyleAttr, 0);
        mRadius = array.getDimension(R.styleable.CircleImageView_android_radius, 0F);
        array.recycle();
        if (isOutline()) {
            if (mOutlineProvider == null) mOutlineProvider = getViewOutlineProvider(mRadius);
        } else {
            if (mPath == null) mPath = new Path();
            if (mRect == null) mRect = new RectF();
        }
    }

    public void setRadius(@Px float radius) {
        if (mRadius == radius) return;
        this.mRadius = radius;
        if (radius > 0) {
            if (isOutline()) {
                setOutlineProvider(mOutlineProvider);
                setClipToOutline(true);
            } else {
                mRect.set(0, 0, getWidth(), getHeight());
                mPath.reset();
                mPath.addRoundRect(mRect, radius, radius, Path.Direction.CW);
            }
        } else if (isOutline()) setClipToOutline(false);
        invalidateOutline();
    }

    @Override
    public void draw(Canvas canvas) {
        if (!isOutline()) {
            canvas.save();
            canvas.clipPath(mPath);
            super.draw(canvas);
            canvas.restore();
        } else super.draw(canvas);
    }

    private ViewOutlineProvider getViewOutlineProvider(float radius) {
        return new RoundOutlineProvider(radius);
    }

    private boolean isOutline() {
        return Build.VERSION_CODES.LOLLIPOP > Build.VERSION.SDK_INT;
    }
}
