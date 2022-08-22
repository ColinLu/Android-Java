package com.colin.library.android.widgets.span;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextPaint;
import android.text.style.TypefaceSpan;

import androidx.annotation.Nullable;

/**
 * 作者： ColinLu
 * 时间： 2022-05-03 07:39
 * <p>
 * 描述： TODO
 */
public class CustomTypefaceSpan extends TypefaceSpan {

    /* http://stackoverflow.com/questions/6612316/how-set-spannable-object-font-with-custom-font#answer-10741161 */

    public static final Parcelable.Creator<CustomTypefaceSpan> CREATOR = new Parcelable.Creator<CustomTypefaceSpan>() {
        @Override
        public CustomTypefaceSpan createFromParcel(Parcel source) {
            return null;
        }

        @Override
        public CustomTypefaceSpan[] newArray(int size) {
            return new CustomTypefaceSpan[size];
        }
    };
    @Nullable
    private final Typeface newType;

    /**
     * @param family Typeface 字体的字体名
     * @param type   该字体的 Typeface 对象
     */
    public CustomTypefaceSpan(String family, @Nullable Typeface type) {
        super(family);
        newType = type;
    }

    private static void applyCustomTypeFace(Paint paint, @Nullable Typeface tf) {
        if (tf == null) return;
        int oldStyle;
        Typeface old = paint.getTypeface();
        if (old == null) oldStyle = Typeface.NORMAL;
        else oldStyle = old.getStyle();
        int fake = oldStyle & ~tf.getStyle();
        if ((fake & Typeface.BOLD) != 0) paint.setFakeBoldText(true);
        if ((fake & Typeface.ITALIC) != 0) paint.setTextSkewX(-0.25f);
        paint.setTypeface(tf);
    }

    @Override
    public void updateDrawState(TextPaint paint) {
        applyCustomTypeFace(paint, newType);
    }

    @Override
    public void updateMeasureState(TextPaint paint) {
        applyCustomTypeFace(paint, newType);
    }
}