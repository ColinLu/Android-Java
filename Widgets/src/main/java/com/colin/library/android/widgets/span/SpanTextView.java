package com.colin.library.android.widgets.span;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.Spannable;
import android.text.method.MovementMethod;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

import androidx.annotation.AttrRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

/**
 * 作者： ColinLu
 * 时间： 2022-05-02 21:55
 * <p>
 * 修复了 {@link TextView} 与 {@link android.text.style.ClickableSpan} 一起使用时，
 * 点击 {@link android.text.style.ClickableSpan} 也会触发 {@link TextView} 的事件的问题。
 * </p>
 * <p>
 * 同时通过 {@link #setNeedForceEventToParent(boolean)} 控制该 TextView 的点击事件能否传递给其 Parent，
 * 修复了 {@link TextView} 默认情况下如果添加了 {@link android.text.style.ClickableSpan} 之后就无法把点击事件传递给 {@link TextView} 的 Parent 的问题。
 * </p>
 * <p>
 * 注意: 使用该 {@link TextView} 时, 用 {@link TouchableSpan} 代替 {@link android.text.style.ClickableSpan},
 * 且同时可以使用 {@link TouchableSpan} 达到修改 span 的文字颜色和背景色的目的。
 * </p>
 * <p>
 * 注意: 使用该 {@link TextView} 时, 需调用 {@link #setMovementMethodDefault()} 方法设置默认的 {@link TouchSpanMovementMethod},
 * TextView 会在 {@link #onTouchEvent(MotionEvent)} 时将事件传递给 {@link TouchSpanMovementMethod},
 * 然后传递给 {@link TouchableSpan}, 实现点击态的变化和点击事件的响应。
 * </p>
 */


public class SpanTextView extends AppCompatTextView implements ITouchSpan {
    /**
     * 记录当前 Touch 事件对应的点是不是点在了 span 上面
     */
    private boolean mTouchSpan;

    /**
     * 记录每次真正传入的press，每次更改mTouchSpanHint，需要再调用一次setPressed，确保press状态正确
     */
    private boolean mPressed = false;
    /**
     * TextView是否应该消耗事件
     */
    private boolean mNeedForceEventToParent = false;

    public SpanTextView(@NonNull Context context) {
        this(context, null, Resources.ID_NULL);
    }

    public SpanTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, Resources.ID_NULL);
    }

    public SpanTextView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setHighlightColor(Color.TRANSPARENT);
    }


    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (!(getText() instanceof Spannable) || !(getMovementMethod() instanceof TouchSpanMovementMethod)) {
            mTouchSpan = false;
            return super.onTouchEvent(event);
        }
        // 调用super.onTouchEvent,会走到TouchSpanMovementMethod
        // 会走到TouchSpanMovementMethod#onTouchEvent会修改mTouchSpan
        mTouchSpan = true;
        return mNeedForceEventToParent || super.onTouchEvent(event);
    }

    @Override
    public boolean performClick() {
        return !mTouchSpan && !mNeedForceEventToParent && super.performClick();
    }

    @Override
    public boolean performLongClick() {
        return !mTouchSpan && !mNeedForceEventToParent && super.performLongClick();
    }

    @Override
    public void setPressed(boolean pressed) {
        this.mPressed = pressed;
        if (!mTouchSpan) super.setPressed(pressed);
    }

    @Override
    public void setTouchSpan(boolean touch) {
        if (mTouchSpan != touch) {
            mTouchSpan = touch;
            setPressed(mPressed);
        }
    }

    public void setNeedForceEventToParent(boolean needForceEventToParent) {
        mNeedForceEventToParent = needForceEventToParent;
        setFocusable(!needForceEventToParent);
        setClickable(!needForceEventToParent);
        setLongClickable(!needForceEventToParent);
    }

    /**
     * 使用者主动调用
     */
    public void setMovementMethodDefault() {
        setMovementMethodCompat(TouchSpanMovementMethod.getInstance());
    }

    public void setMovementMethodCompat(MovementMethod movement) {
        setMovementMethod(movement);
        if (mNeedForceEventToParent) setNeedForceEventToParent(true);
    }
}
