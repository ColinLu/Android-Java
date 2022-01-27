package com.colin.library.android.widgets.text;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.util.AttributeSet;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.appcompat.widget.AppCompatTextView;

import com.colin.library.android.widgets.R;
import com.colin.library.android.widgets.annotation.Orientation;

/**
 * 作者： ColinLu
 * 时间： 2022-01-26 22:23
 * <p>
 * 描述： 字体颜色渐变
 */
public class GradientTextView extends AppCompatTextView {
    private static final float[] POSITIONS = new float[]{0.0F, 1.0F};
    @Orientation
    private int mOrientation = Orientation.HORIZONTAL;
    @ColorInt
    private int mTextStartColor;
    @ColorInt
    private int mTextEndColor;
    @ColorInt
    private int mTextColor;
    private boolean mShowGradient = false;
    private LinearGradient mLinearGradient;


    public GradientTextView(@NonNull Context context) {
        this(context, null, Resources.ID_NULL);
    }

    public GradientTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, Resources.ID_NULL);
    }

    public GradientTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.GradientTextView, defStyleAttr, Resources.ID_NULL);
        mShowGradient = array.getBoolean(R.styleable.GradientTextView_showGradient, mShowGradient);
        mTextStartColor = array.getColor(R.styleable.GradientTextView_textStartColor, Color.TRANSPARENT);
        mTextEndColor = array.getColor(R.styleable.GradientTextView_textEndColor, Color.TRANSPARENT);
        mTextColor = array.getColor(R.styleable.TextAppearance_android_textColor, Color.WHITE);
        mOrientation = array.getInteger(R.styleable.GradientTextView_android_orientation, mOrientation);
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mOrientation == Orientation.VERTICAL && h != oldh) {
            mLinearGradient = getVerticalGradient(h, mTextStartColor, mTextEndColor);
        } else if (mOrientation == Orientation.HORIZONTAL && w != oldw) {
            mLinearGradient = getHorizontalGradient(w, mTextStartColor, mTextEndColor);
        }
        if (mShowGradient && mLinearGradient != null) {
            getPaint().setShader(mLinearGradient);
        } else {
            getPaint().setShader(null);
        }
    }

    public void setOrientation(int orientation) {
        if (mOrientation == orientation) {
            return;
        }
        setTextColor(orientation, mTextStartColor, mTextEndColor);

    }

    public void setTextColor(@ColorInt int startColor, @ColorInt int endColor) {
        setTextColor(mOrientation, startColor, endColor);
    }

    public void setTextColor(@Orientation int orientation, @ColorInt int startColor, @ColorInt int endColor) {
        if (startColor == endColor) {
            setTextColor(startColor);
            return;
        }
        if (mShowGradient && mOrientation == orientation && startColor == mTextStartColor && endColor == mTextEndColor) {
            return;
        }
        this.mOrientation = orientation;
        this.mTextStartColor = startColor;
        this.mTextEndColor = endColor;
        this.mShowGradient = true;
        this.mLinearGradient = getGradient(orientation, startColor, endColor);
        getPaint().setShader(mLinearGradient);
        invalidate();
    }


    @Override
    public void setTextColor(int textColor) {
        if (!mShowGradient && mTextColor == textColor) {
            return;
        }
        this.mTextColor = textColor;
        this.mShowGradient = false;
        getPaint().setShader(null);
        getPaint().setColor(textColor);
        invalidate();
    }

    @NonNull
    private LinearGradient getGradient(int orientation, int startColor, int endColor) {
        if (orientation == Orientation.VERTICAL) {
            return getVerticalGradient(getHeight(), startColor, endColor);
        } else {
            return getHorizontalGradient(getWidth(), startColor, endColor);
        }
    }

    @NonNull
    private LinearGradient getVerticalGradient(@Px int height, @ColorInt int startColor, @ColorInt int endColor) {
        return new LinearGradient(0, 0, 0, height, new int[]{startColor, endColor}, POSITIONS, Shader.TileMode.CLAMP);
    }

    @NonNull
    private LinearGradient getHorizontalGradient(@Px int width, @ColorInt int startColor, @ColorInt int endColor) {
        return new LinearGradient(0, 0, width, 0, new int[]{startColor, endColor}, POSITIONS, Shader.TileMode.CLAMP);
    }
}
