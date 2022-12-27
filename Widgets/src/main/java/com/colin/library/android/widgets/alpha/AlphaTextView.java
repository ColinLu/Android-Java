package com.colin.library.android.widgets.alpha;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.widgets.span.SpanTextView;

/**
 * 作者： ColinLu
 * 时间： 2022-05-03 01:05
 * <p>
 * 描述： 透明效果
 */
public class AlphaTextView extends SpanTextView implements IAlphaView {
    private AlphaViewHelper mAlphaViewHelper;

    public AlphaTextView(@NonNull Context context) {
        this(context, null, Resources.ID_NULL);
    }

    public AlphaTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, Resources.ID_NULL);
    }

    public AlphaTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
