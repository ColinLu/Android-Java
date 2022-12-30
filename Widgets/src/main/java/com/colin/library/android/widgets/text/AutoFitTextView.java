package com.colin.library.android.widgets.text;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

import com.colin.library.android.utils.ResourceUtil;
import com.colin.library.android.widgets.R;

/**
 * 作者： ColinLu
 * 时间： 2022-05-02 21:28
 * <p>
 * 描述： 自动适配字体 size
 */
public class AutoFitTextView extends AppCompatTextView {
    private Paint mTestPaint;
    private float mMinSize;
    private float mMaxSize;

    public AutoFitTextView(@NonNull Context context) {
        this(context, null, Resources.ID_NULL);
    }

    public AutoFitTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, Resources.ID_NULL);
    }

    public AutoFitTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTestPaint = new Paint();
        mTestPaint.set(this.getPaint());
        final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.AutoFitTextView, defStyleAttr, 0);
        final float density = ResourceUtil.getDensity();
        mMinSize = array.getDimensionPixelSize(R.styleable.AutoFitTextView_minSize, Math.round(14 * density));
        mMaxSize = array.getDimensionPixelSize(R.styleable.AutoFitTextView_maxSize, Math.round(18 * density));
        array.recycle();
        //max size defaults to the initially specified text size unless it is too small
    }


    /* Re size the font so the specified text fits in the text box
     * assuming the text box is the specified width.
     */
    private void refitText(String text, int textWidth) {
        if (textWidth <= 0) return;
        final int targetWidth = textWidth - this.getPaddingLeft() - this.getPaddingRight();
        final float threshold = 0.5f; // How close we have to be
        float hi = mMaxSize, lo = mMinSize, size;
        mTestPaint.set(this.getPaint());
        mTestPaint.setTextSize(mMaxSize);
        if (mTestPaint.measureText(text) <= targetWidth) lo = mMaxSize;
        else {
            mTestPaint.setTextSize(mMinSize);
            if (mTestPaint.measureText(text) < targetWidth) {
                while ((hi - lo) > threshold) {
                    size = (hi + lo) / 2;
                    mTestPaint.setTextSize(size);
                    if (mTestPaint.measureText(text) >= targetWidth) hi = size; // too big
                    else lo = size; // too small
                }
            }
        }
        // Use lo so that we undershoot rather than overshoot
        this.setTextSize(TypedValue.COMPLEX_UNIT_PX, lo);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        final int height = getMeasuredHeight();
        refitText(this.getText().toString(), parentWidth);
        this.setMeasuredDimension(parentWidth, height);
    }

    @Override
    protected void onTextChanged(final CharSequence text, final int start, final int before, final int after) {
        refitText(text.toString(), this.getWidth());
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (w != oldw) refitText(this.getText().toString(), w);
    }
}
