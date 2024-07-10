package com.colin.library.android.widgets.alpha;

import android.content.Context;
import android.view.View;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;

import com.colin.library.android.widgets.R;
import com.colin.library.android.widgets.Utils;

import java.lang.ref.WeakReference;

/**
 * 作者： ColinLu
 * 时间： 2022-05-03 11:20
 * <p>
 * 描述： View Alpha操作辅助类
 */
public final class AlphaViewHelper {
    private final WeakReference<View> mViewRef;

    /**
     * 设置是否要在 press 时改变透明度
     */
    private boolean mChangedWhenPress = true;

    /**
     * 设置是否要在 disabled 时改变透明度
     */
    private boolean mChangedWhenDisable = true;

    private final float mNormalAlpha = 1.0f;
    private final float mPressedAlpha;
    private final float mDisabledAlpha;

    public AlphaViewHelper(@NonNull View view) {
        final Context context = view.getContext();
        this.mViewRef = new WeakReference<>(view);
        this.mPressedAlpha = Utils.getAttrFloat(context, R.attr.alpha_pressed);
        this.mDisabledAlpha = Utils.getAttrFloat(context, R.attr.alpha_disabled);
    }

    public AlphaViewHelper(@NonNull View view, @FloatRange(from = 0.0F, to = 1.0F) float pressed,
            @FloatRange(from = 0.0F, to = 1.0F) float disabled) {
        this.mViewRef = new WeakReference<>(view);
        this.mPressedAlpha = pressed;
        this.mDisabledAlpha = disabled;
    }

    /**
     * 在 {@link View#setPressed(boolean)} 中调用，通知 helper 更新
     *
     * @param current the view to be handled, maybe not equal to target view
     * @param pressed {@link View#setPressed(boolean)} 中接收到的参数
     */
    public void onChangedWhenPressed(View current, boolean pressed) {
        final View view = mViewRef.get();
        if (view == null) return;
        if (current.isEnabled()) {
            view.setAlpha(mChangedWhenPress && pressed && current.isClickable() ? mPressedAlpha : mNormalAlpha);
        } else if (mChangedWhenDisable) view.setAlpha(mDisabledAlpha);

    }

    /**
     * 在 {@link View#setEnabled(boolean)} 中调用，通知 helper 更新
     *
     * @param current the view to be handled, maybe not  equal to target view
     * @param enabled {@link View#setEnabled(boolean)} 中接收到的参数
     */
    public void onChangedWhenEnabled(View current, boolean enabled) {
        final View view = mViewRef.get();
        if (view == null) return;
        float alphaForIsEnable;
        if (mChangedWhenDisable) alphaForIsEnable = enabled ? mNormalAlpha : mDisabledAlpha;
        else alphaForIsEnable = mNormalAlpha;
        if (current != view && view.isEnabled() != enabled) view.setEnabled(enabled);
        view.setAlpha(alphaForIsEnable);
    }

    /**
     * 设置是否要在 press 时改变透明度
     *
     * @param changedWhenPress 是否要在 press 时改变透明度
     */
    public void changedWhenPress(boolean changedWhenPress) {
        mChangedWhenPress = changedWhenPress;
    }

    /**
     * 设置是否要在 disabled 时改变透明度
     *
     * @param changedWhenDisable 是否要在 disabled 时改变透明度
     */
    public void changedWhenDisable(boolean changedWhenDisable) {
        mChangedWhenDisable = changedWhenDisable;
        final View view = mViewRef.get();
        if (view != null) onChangedWhenEnabled(view, view.isEnabled());
    }

}
