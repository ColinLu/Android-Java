package com.colin.library.android.widgets.span;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;

import com.colin.library.android.utils.ResourceUtil;
import com.colin.library.android.widgets.R;

/**
 * 作者： ColinLu
 * 时间： 2022-05-03 00:51
 * <p>
 * 描述：  可 Touch 的 Span，在 {@link #setPressed(boolean)} 后根据是否 pressed 来触发不同的UI状态
 * 提供设置 span 的文字颜色和背景颜色的功能, 在构造时传入
 */
public abstract class TouchableSpan extends ClickableSpan implements ITouchableSpan {
    private boolean mIsPressed;
    @ColorInt
    private final int mNormalBackgroundColor;
    @ColorInt
    private final int mPressedBackgroundColor;
    @ColorInt
    private int mNormalTextColor;
    @ColorInt
    private int mPressedTextColor;
    private boolean mIsNeedUnderline = false;

    public abstract void onClickSpan(@NonNull View view);


    public TouchableSpan(@ColorInt int normalTextColor,
                         @ColorInt int pressedTextColor,
                         @ColorInt int normalBackgroundColor,
                         @ColorInt int pressedBackgroundColor) {
        mNormalTextColor = normalTextColor;
        mPressedTextColor = pressedTextColor;
        mNormalBackgroundColor = normalBackgroundColor;
        mPressedBackgroundColor = pressedBackgroundColor;
    }

    @Deprecated
    public TouchableSpan(@NonNull TextView view) {
        mNormalTextColor = getAttrColor(view, R.attr.span_text_normal_color);
        mPressedTextColor = getAttrColor(view, R.attr.span_text_pressed_color);
        mNormalBackgroundColor = getAttrColor(view, R.attr.span_bg_normal_color);
        mPressedBackgroundColor = getAttrColor(view, R.attr.span_bg_pressed_color);
    }

    @Override
    public final void onClick(@NonNull View view) {
        if (ViewCompat.isAttachedToWindow(view)) onClickSpan(view);
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(mIsPressed ? mPressedTextColor : mNormalTextColor);
        ds.bgColor = mIsPressed ? mPressedBackgroundColor : mNormalBackgroundColor;
        ds.setUnderlineText(mIsNeedUnderline);
    }

    public int getNormalBackgroundColor() {
        return mNormalBackgroundColor;
    }

    public void setNormalTextColor(int normalTextColor) {
        mNormalTextColor = normalTextColor;
    }

    public void setPressedTextColor(int pressedTextColor) {
        mPressedTextColor = pressedTextColor;
    }

    public int getNormalTextColor() {
        return mNormalTextColor;
    }

    public int getPressedBackgroundColor() {
        return mPressedBackgroundColor;
    }

    public int getPressedTextColor() {
        return mPressedTextColor;
    }

    public void setPressed(boolean isSelected) {
        mIsPressed = isSelected;
    }

    public boolean isPressed() {
        return mIsPressed;
    }

    public void setIsNeedUnderline(boolean isNeedUnderline) {
        mIsNeedUnderline = isNeedUnderline;
    }

    public boolean isNeedUnderline() {
        return mIsNeedUnderline;
    }


    @ColorInt
    private int getAttrColor(TextView view, int attr) {
        return ResourceUtil.getAttrColor(view.getContext().getTheme(), attr);
    }
}
