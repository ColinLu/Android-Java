package com.colin.library.android.widgets.square;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;


public class SquareCardView extends CardView {

    public SquareCardView(@NonNull Context context) {
        this(context, null, Resources.ID_NULL);
    }

    public SquareCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, Resources.ID_NULL);
    }

    public SquareCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            super.onMeasure(widthMeasureSpec, widthMeasureSpec);
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            super.onMeasure(heightMeasureSpec, heightMeasureSpec);
        } else {
            throw new AssertionError("This should not be the case.");
        }
    }
}