package com.colin.library.android.widgets.edit;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.ColorInt;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.graphics.drawable.DrawableCompat;

import com.colin.library.android.widgets.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 作者：  ColinLu
 * 邮件：  945919945@qq.com
 * 时间：  2019-06-07 14:31
 * <p>
 * 描述：  格式化输入文字控件
 * 1：默认文本内容 4个一个空格
 * 2：手机号码 344
 * 3：身份证号码 33444
 * 4：银行卡号 44444
 * 5：自带删除按钮 获取焦点 显示
 * 6：支持emoji表情包
 * <p>
 * ps：密码不支持格式化
 */
public class FormatEditText extends AppCompatEditText implements View.OnFocusChangeListener {
    private static final String INSTANCE_STATE = "saved_instance";
    private static final String INSTANCE_FORMAT_TYPE = "format_type";
    private static final String INSTANCE_FORMAT_SEPARATOR = "format_separator";
    private static final String INSTANCE_SUPPORT_EMOJI = "support_emoji";

    public static final int FORMAT_TYPE_NORMAL = 0;
    public static final int FORMAT_TYPE_MOBILE = 1;
    public static final int FORMAT_TYPE_BANK = 2;
    public static final int FORMAT_TYPE_ID = 3;


    @IntDef({FORMAT_TYPE_NORMAL, FORMAT_TYPE_MOBILE, FORMAT_TYPE_BANK, FORMAT_TYPE_ID})
    @Retention(RetentionPolicy.SOURCE)
    public @interface FormatType {
    }

    private int mFormatType = FORMAT_TYPE_NORMAL;           //类型
    private String mFormatSeparator = null;                 //分隔符
    private int mFormatInterval = 4;
    private Drawable mDeleteDrawable;                       //删除图标
    @ColorInt
    private int mDeleteColor = Color.TRANSPARENT;
    private boolean mSupportEmoji = false;                  //是否支持Emoji表情

    private int start, count, before;
    private int mInputCountLength = 0;

    private boolean hasFocus;

    private final FormatTextChangeListener mFormatTextChangeListener = new FormatTextChangeListener();


    public FormatEditText(Context context) {
        this(context, null, android.R.attr.editTextStyle);
    }

    public FormatEditText(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public FormatEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData(context, attrs, defStyleAttr);
    }


    /**
     * 布局文件处理  开始逻辑处理
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    private void initData(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FormatEditText, defStyleAttr, defStyleAttr);
        final int formatType = typedArray.getInteger(R.styleable.FormatEditText_formatType, mFormatType);
        final String formatSeparator = typedArray.getString(R.styleable.FormatEditText_formatSeparator);
        mDeleteDrawable = typedArray.getDrawable(R.styleable.FormatEditText_formatDelete);
        mDeleteColor = typedArray.getColor(R.styleable.FormatEditText_formatDeleteColor, mDeleteColor);
        mSupportEmoji = typedArray.getBoolean(R.styleable.FormatEditText_formatSupportEmoji, mSupportEmoji);
        mFormatInterval = typedArray.getInteger(R.styleable.FormatEditText_formatInterval, mFormatInterval);
        typedArray.recycle();

        if (null != mDeleteDrawable) {
            mDeleteDrawable.setBounds(0, 0, mDeleteDrawable.getIntrinsicWidth(), mDeleteDrawable.getIntrinsicHeight());
        }
        this.addTextChangedListener(mFormatTextChangeListener);
        setOnFocusChangeListener(this);

        setFormat(formatType, formatSeparator);
    }

    ///////////////////////////////////////////////////////////////////////////
    //  重写父类方法
    ///////////////////////////////////////////////////////////////////////////

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    /*修改输入类型*/
    @Override
    public void setInputType(int type) {
        if (this.getInputType() == type) return;
        super.setInputType(type);
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        this.hasFocus = hasFocus;
        setDeleteDrawableVisible();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        setDeleteDrawableVisible();
    }


    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) return super.onTouchEvent(event);

        if (event.getAction() == MotionEvent.ACTION_UP) performClick();

        touchDeleteDrawable(event);
        return super.onTouchEvent(event);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        final Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putInt(INSTANCE_FORMAT_TYPE, getFormatType());
        bundle.putString(INSTANCE_FORMAT_SEPARATOR, getFormatSeparator());
        bundle.putBoolean(INSTANCE_SUPPORT_EMOJI, isSupportEmoji());
        return bundle;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            final Bundle bundle = (Bundle) state;
            final int formatType = bundle.getInt(INSTANCE_FORMAT_TYPE, getFormatType());
            final String separator = bundle.getString(INSTANCE_FORMAT_SEPARATOR, getFormatSeparator());
            setFormat(formatType, separator);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
        } else super.onRestoreInstanceState(state);
    }

    /**
     * 销毁
     */
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeTextChangedListener(mFormatTextChangeListener);
    }

    ///////////////////////////////////////////////////////////////////////////
    // 公开方法
    ///////////////////////////////////////////////////////////////////////////

    public FormatEditText setFormatType(int formatType) {
        mFormatType = formatType;
        return this;
    }

    public FormatEditText setFormatSeparator(String formatSeparator) {
        mFormatSeparator = formatSeparator;
        return this;
    }

    public void setFormatInterval(int formatInterval) {
        if (mFormatInterval == formatInterval) return;
        if (!shouldFormat()) {
            this.mFormatInterval = formatInterval;
            return;
        }
        Editable editable = getText();
        if (null == editable || editable.length() == 0) {
            this.mFormatInterval = formatInterval;
            return;
        }
        CharSequence input = deleteSeparator(editable, mFormatSeparator.length(), 0, 0);
        editable.clear();
        this.mFormatInterval = formatInterval;
        editable.append(addSeparator(input, 0));
        setSelection(editable.length());
    }

    /**
     * 设置格式化
     *
     * @param formatType
     * @param separator
     */
    public void setFormat(@FormatType int formatType, @Nullable String separator) {
        if (this.mFormatType == formatType && TextUtils.equals(mFormatSeparator, separator)) return;
        this.mFormatType = formatType;
        this.mFormatSeparator = separator;
        setRules();
        refreshFormatValue(getText());
    }

    public void setEmoji(boolean supportEmoji) {
        if (this.mSupportEmoji == supportEmoji) return;
        this.mSupportEmoji = supportEmoji;
        setFilters(getFormatFiler());
    }

    public void setHasFocus(boolean hasFocus) {
        if (this.hasFocus == hasFocus) return;
        this.hasFocus = hasFocus;
        setDeleteDrawableVisible();
    }

    /**
     * 公开 获取用户输入文字信息
     *
     * @return
     */
    public CharSequence getRealValue() {
        Editable editable = getText();
        if (!shouldFormat()) return editable;
        if (null == editable || editable.length() == 0) return editable;
        return deleteSeparator(editable, mFormatSeparator.length(), 0, 0);
    }

    public void setDeleteDrawableVisible() {
        if (null == mDeleteDrawable) return;
        DrawableCompat.setTint(mDeleteDrawable, mDeleteColor == Color.TRANSPARENT ? getCurrentHintTextColor() : mDeleteColor);
        Editable editable = getText();
        boolean visible = isEnabled() && hasFocus && null != editable && editable.length() > 0;
        setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], visible ? mDeleteDrawable : null, getCompoundDrawables()[3]);
    }

    public int getFormatType() {
        return mFormatType;
    }

    public String getFormatSeparator() {
        return mFormatSeparator;
    }

    public Drawable getDeleteDrawable() {
        return mDeleteDrawable;
    }

    public boolean isSupportEmoji() {
        return mSupportEmoji;
    }

    ///////////////////////////////////////////////////////////////////////////
    // 私有方法  内部处理逻辑
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 需要制定一些规则  如果输入银行卡之类的
     * 注意方法顺序
     */
    private void setRules() {
        if (mFormatType == FORMAT_TYPE_MOBILE) {
            mInputCountLength = 13;
            mSupportEmoji = false;
            setSingleLine();
            setInputType(InputType.TYPE_CLASS_PHONE);
            setDigits(mFormatSeparator + "0123456789");
            setFilters(getFormatFiler());
        }
        if (mFormatType == FORMAT_TYPE_ID) {
            mInputCountLength = 22;
            mSupportEmoji = false;
            setSingleLine();
            setInputType(InputType.TYPE_CLASS_TEXT);
            setDigits(mFormatSeparator + "0123456789Xx");
            setFilters(getFormatFiler());
        }
        if (mFormatType == FORMAT_TYPE_BANK) {
            mInputCountLength = 24;
            mSupportEmoji = false;
            setSingleLine();
            setInputType(InputType.TYPE_CLASS_NUMBER);
            setDigits(mFormatSeparator + "0123456789");
            setFilters(getFormatFiler());
        }
        if (mFormatType == FORMAT_TYPE_NORMAL) {
            mInputCountLength = Integer.MAX_VALUE;
            setInputType(getInputType());
            setFilters(getFormatFiler());
        }
    }

    private InputFilter[] getFormatFiler() {
        if (!mSupportEmoji && mInputCountLength > 0)
            return new InputFilter[]{new EMojiFilter(), new InputFilter.LengthFilter(mInputCountLength)};
        else if (mInputCountLength > 0)
            return new InputFilter[]{new InputFilter.LengthFilter(mInputCountLength)};
        else if (!mSupportEmoji)
            return new InputFilter[]{new EMojiFilter()};
        else return new InputFilter[0];
    }

    /* 非常重要:setKeyListener要在setInputType后面调用，否则无效。*/
    private void setDigits(String digits) {
        if (TextUtils.isEmpty(digits)) return;
        setKeyListener(DigitsKeyListener.getInstance(digits));
    }


    /**
     * 格式化字符串
     *
     * @param editable
     */
    private void refreshFormatValue(@Nullable Editable editable) {
        if (null == editable || editable.length() == 0) return;
        removeTextChangedListener(mFormatTextChangeListener);
        format(editable);
        addTextChangedListener(mFormatTextChangeListener);
    }

    /*格式化现有的字段 返回游标位置  before 删除  count 增加  */
    private synchronized void format(@NonNull final Editable editable) {
        if (!shouldFormat()) return;
        final int length = editable.length();
        final int separatorLength = mFormatSeparator.length();
        //从后面删除最后一位
        if (before > 0 && start == length) return;
        //从后面增加 增加数量 等于最小单位
        if (count == separatorLength && shouldFormat(length + 1)) {
            editable.append(mFormatSeparator);
            return;
        }
        //中间 删除 增加操作
        if (start + count < length && (count > 0 || before > 0)) {
            final CharSequence input = deleteSeparator(editable,
                    separatorLength,
                    start == 0 ? 0 : start - 1,
                    before > 0 ? before : -count);
            editable.clear();
            editable.append(addSeparator(input, start == 0 ? 0 : start - 1));
            return;
        }
        //粘贴复制 赋值
        if (count >= separatorLength) {
            CharSequence input = addSeparator(editable.toString(), start - 1);
            editable.clear();
            editable.append(input);
        }
    }

    /*删除格式化的字符 删除增加 中间开始 有偏移量*/
    private synchronized CharSequence deleteSeparator(@NonNull final Editable editable, final int separatorLength, final int start, int offset) {
        final int length = editable.length();
        StringBuilder stringBuilder = new StringBuilder(editable.subSequence(0, start));
        for (int i = start; i < length; i++) {
            CharSequence charSequence = editable.subSequence(i, i + 1);
            if (mFormatSeparator.contentEquals(charSequence) && shouldFormat(stringBuilder.length() + 1 + offset)) {
                offset += separatorLength;
                continue;
            }
            stringBuilder.append(charSequence);
        }
        return stringBuilder.toString();
    }

    private synchronized CharSequence addSeparator(@NonNull final CharSequence input, int start) {
        int length = input.length();
        start = start < 0 ? 0 : start;
        StringBuilder builder = new StringBuilder(input.subSequence(0, start));
        for (int i = start; i < length; i++) {
            builder.append(input.subSequence(i, i + 1));
            if (shouldFormat(builder.length() + 1)) builder.append(mFormatSeparator);
        }
        return builder;
    }

    private boolean shouldFormat() {
        return !isInputPassword() && mFormatSeparator != null && mFormatSeparator.length() > 0;
    }

    /**
     * 是否应该格式化
     *
     * @param length
     * @return
     */
    private boolean shouldFormat(final int length) {
        if (null == mFormatSeparator || mFormatSeparator.length() == 0) return false;
        if (mFormatType == FORMAT_TYPE_MOBILE)
            return length == 4 || length == 9;
        if (mFormatType == FORMAT_TYPE_ID)
            return length == 4 || length == 8 || length == 13 || length == 18;
        if (mFormatType == FORMAT_TYPE_BANK) return length % 5 == 0;
        if (mFormatInterval == 0) return false;
        return length % (mFormatInterval + 1) == 0;

    }


    /**
     * 判断当前输入类型是否是密码
     *
     * @return
     */
    private boolean isInputPassword() {
        int inputType = getInputType();
        return inputType == InputType.TYPE_NUMBER_VARIATION_PASSWORD
                || inputType == InputType.TYPE_TEXT_VARIATION_PASSWORD
                || inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                || inputType == InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD;
    }


    private static boolean isVisiblePasswordInputType(int inputType) {
        final int variation = inputType & (EditorInfo.TYPE_MASK_CLASS | EditorInfo.TYPE_MASK_VARIATION);
        return variation == (EditorInfo.TYPE_CLASS_TEXT | EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
    }

    /**
     * 监听格式化文字内容
     */
    private class FormatTextChangeListener implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            FormatEditText.this.start = start;
            FormatEditText.this.before = before;
            FormatEditText.this.count = count;
        }

        @Override
        public void afterTextChanged(Editable s) {
            refreshFormatValue(s);
            setDeleteDrawableVisible();
        }
    }

    private void touchDeleteDrawable(MotionEvent event) {
        if (null == mDeleteDrawable || !hasFocus) return;
        //不是抬起
        if (event.getAction() != MotionEvent.ACTION_UP) return;

        Rect rect = mDeleteDrawable.getBounds();
        int rectW = rect.width();
        int rectH = rect.height();
        int top = (getMeasuredHeight() - rectH) >> 1;
        int right = getMeasuredWidth() - getPaddingRight();
        boolean isAreaX = event.getX() <= right && event.getX() >= right - rectW;
        boolean isAreaY = event.getY() >= top && event.getY() <= (top + rectH);
        if (isAreaX && isAreaY) {
            setError(null);
            setText("");
        }
    }


    /**
     * 输入特殊符号 EMoji
     */
    private class EMojiFilter implements InputFilter {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            for (int i = start; i < end; i++) {
                int type = Character.getType(source.charAt(i));
                if (type == Character.SURROGATE || type == Character.OTHER_SYMBOL) return "";
            }
            return null;
        }
    }


}
