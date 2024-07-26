package com.colin.library.android.widgets.image;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.ViewOutlineProvider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.appcompat.widget.AppCompatImageView;

import com.colin.library.android.widgets.R;
import com.colin.library.android.widgets.provider.AppViewOutlineProvider;

/**
 * 作者： ColinLu
 * 时间： 2022-01-30 07:27
 * <p>
 * 描述： 圆角
 */
public class CircleImageView extends AppCompatImageView {
    private float mRadius;

    public CircleImageView(@NonNull Context context) {
        this(context, null, Resources.ID_NULL);
    }

    public CircleImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, Resources.ID_NULL);
    }

    public CircleImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView, defStyleAttr, 0);
        final float radius = array.getDimension(R.styleable.CircleImageView_android_radius, 0F);
        array.recycle();
        setRadius(radius);
    }

    public void setRadius(@Px float radius) {
        if (mRadius != radius) {
            mRadius = radius;
            setOutlineProvider(new AppViewOutlineProvider(radius));
            setClipToOutline(true);
        }
    }


    private ViewOutlineProvider getViewOutlineProvider(float radius) {
        return new AppViewOutlineProvider(radius);
    }
}
