package com.colin.library.android.widgets.interpolator;

import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import androidx.interpolator.view.animation.FastOutLinearInInterpolator;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;

/**
 * 作者： ColinLu
 * 时间： 2022-12-31 21:20
 * <p>
 * 描述： TODO
 */
public class Interpolators {
    public static final Interpolator LINEAR = new LinearInterpolator();
    public static final Interpolator FAST_OUT_SLOW_IN= new FastOutSlowInInterpolator();
    public static final Interpolator FAST_OUT_LINEAR_IN = new FastOutLinearInInterpolator();
    public static final Interpolator LINEAR_OUT_SLOW_IN = new LinearOutSlowInInterpolator();
    public static final Interpolator DECELERATE = new DecelerateInterpolator();
    public static final Interpolator ACCELERATE = new AccelerateInterpolator();
    public static final Interpolator ACCELERATE_DECELERATE = new AccelerateDecelerateInterpolator();
    public static final Interpolator QUNITIC = t -> {
        t -= 1.0f;
        return t * t * t * t * t + 1.0f;
    };

}
