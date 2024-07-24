package com.colin.library.android.widgets.transform;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;


public class ScaleInTransformer implements ViewPager2.PageTransformer {

    private static final float DEFAULT_CENTER = 0.5f;
    public static final float DEFAULT_MIN_SCALE = 0.85f;
    public static final float MAX_SCALE = 0.999f;
    private float mMinScale = DEFAULT_MIN_SCALE;

    public ScaleInTransformer(float minScale) {
        mMinScale = minScale;
    }

    @Override
    public void transformPage(@NonNull View view, float position) {
        final int w = view.getWidth();
        final int h = view.getHeight();
        view.setPivotY(h >> 1);
        view.setPivotX(w >> 1);
        if (position < -1) {
            // This page is way off-screen to the left.
            view.setScaleX(mMinScale);
            view.setScaleY(mMinScale);
            view.setPivotX(w);
        } else if (position <= 1) {
            // Modify the default slide transition to shrink the page as well
            if (position < 0) {
                float scaleFactor = (1 + position) * (1 - mMinScale) + mMinScale;
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);
                view.setPivotX(w * (DEFAULT_CENTER + (DEFAULT_CENTER * -position)));
            } else {
                float scaleFactor = (1 - position) * (1 - mMinScale) + mMinScale;
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);
                view.setPivotX(w * ((1 - position) * DEFAULT_CENTER));
            }
        } else {
            view.setPivotX(0);
            view.setScaleX(mMinScale);
            view.setScaleY(mMinScale);
        }
    }
}
