package com.colin.library.android.widgets.alpha;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 作者： ColinLu
 * 时间： 2023-01-01 00:17
 * <p>
 * 描述： 在 pressed 和 disabled 时改变 View 的透明度
 */
public class AlphaFrameLayout extends FrameLayout implements IAlphaView {
    private AlphaViewHelper mAlphaViewHelper;

    public AlphaFrameLayout(@NonNull Context context) {
        this(context, null, Resources.ID_NULL);
    }

    public AlphaFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, Resources.ID_NULL);
    }

    public AlphaFrameLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @NonNull
    public AlphaViewHelper getAlphaViewHelper() {
        if (mAlphaViewHelper == null) mAlphaViewHelper = new AlphaViewHelper(this);
        return mAlphaViewHelper;
    }

    @Override
    public void setPressed(boolean pressed) {
        super.setPressed(pressed);
        getAlphaViewHelper().onChangedWhenPressed(this, pressed);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        getAlphaViewHelper().onChangedWhenEnabled(this, enabled);
    }

    @Override
    public void changedWhenPress(boolean press) {
        getAlphaViewHelper().changedWhenPress(press);
    }

    @Override
    public void changedWhenDisable(boolean disable) {
        getAlphaViewHelper().changedWhenDisable(disable);
    }
}
