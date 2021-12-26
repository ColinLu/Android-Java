package com.colin.android.demo.java.widgets.def;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.colin.android.demo.java.utils.ToastUtil;

/**
 * 作者： ColinLu
 * 时间： 2021-12-17 20:56
 * <p>
 * 描述： TODO
 */
public class SwitchLayout extends FrameLayout {
    private static final float SCALE_MIN = 0.95f;
    private static final float SCALE_MAX = 1.0f;
    private static final float ALPHA_MIN = 0.0f;
    private static final float ALPHA_MAX = 1.0f;
    private static final long DURATION = 120L;
    private static final long OFFSET = 110L;
    private static final float PIVOT = 0.5F;
    private int mMode;
    private final SparseArray<ImageView> mArray = new SparseArray<>(5);

    public SwitchLayout(@NonNull Context context) {
        this(context, null, 0);
    }

    public SwitchLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwitchLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void switchView(int mode) {
        if (mMode == mode) {
            return;
        }
        this.mMode = mode;
        final ImageView imageView = getDriveModeView(getContext(), mode, null);
        bringChildToFront(imageView);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public void switchView(int mode, boolean anim, String tag) {
        if (mMode == mode) {
            return;
        }
        final ImageView exit = mArray.get(mMode);
        final ImageView enter = getDriveModeView(getContext(), mode, tag);
        this.mMode = mode;
        if (exit == null || !anim) {
            bringChildToFront(enter);
        } else {
            switchViewByAnim(exit, enter);
        }
    }

    private void switchViewByAnim(final View exit, final View enter) {
//        final AnimationSet exitAnim = (AnimationSet) AnimationUtils.loadAnimation(getContext(), R.anim.switch_anim_exit);
//        final AnimationSet enterAnim = (AnimationSet) AnimationUtils.loadAnimation(getContext(), R.anim.switch_anim_enter);
//        exit.startAnimation(exitAnim);
//        enter.startAnimation(enterAnim);
        final Animation exitAnim = getExit();
        final Animation enterAnim = getEnter();
        exit.startAnimation(exitAnim);
        enter.startAnimation(enterAnim);
        enterAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                bringChildToFront(enter);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    private ImageView getDriveModeView(@NonNull Context context, int mode, String tag) {
        ImageView imageView = mArray.get(mode);
        if (imageView == null) {
            imageView = createImageView(context, mode, tag);
            mArray.put(mode, imageView);
            addView(imageView, 0);
        }
        return imageView;
    }

    private ImageView createImageView(@NonNull Context context, int mode, String tag) {
        final ImageView imageView = new ImageView(context);
        final LayoutParams layoutParams = generateDefaultLayoutParams();
        imageView.setId(View.generateViewId());
        imageView.setLayoutParams(layoutParams);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageResource(mode);
        imageView.setOnClickListener(v -> ToastUtil.show(tag));
        return imageView;
    }

    private Animation getExit() {
        final AnimationSet set = new AnimationSet(false);
        set.addAnimation(getScale(SCALE_MAX, SCALE_MIN, new AccelerateInterpolator()));
        set.addAnimation(getAlpha(ALPHA_MAX, ALPHA_MIN, new LinearInterpolator()));
        set.addAnimation(getTranslation());
        set.setFillAfter(true);
        set.setFillBefore(false);
        set.setRepeatMode(Animation.RESTART);
        return set;
    }

    private Animation getEnter() {
        final AnimationSet set = new AnimationSet(false);
        set.addAnimation(getScale(SCALE_MIN, SCALE_MAX, new DecelerateInterpolator()));
        set.addAnimation(getAlpha(ALPHA_MIN, ALPHA_MAX, new LinearInterpolator()));
        set.setFillAfter(true);
        set.setFillBefore(false);
        set.setStartOffset(OFFSET);
        set.setRepeatMode(Animation.RESTART);
        return set;
    }

    private Animation getScale(float from, float to, Interpolator interpolator) {
        final ScaleAnimation animation = new ScaleAnimation(from, to, from, to,
                Animation.RELATIVE_TO_SELF, PIVOT, Animation.RELATIVE_TO_SELF, PIVOT);
        animation.setDuration(DURATION);
        animation.setInterpolator(interpolator);
        return animation;
    }

    private Animation getAlpha(float from, float to, Interpolator interpolator) {
        final AlphaAnimation animation = new AlphaAnimation(from, to);
        animation.setDuration(DURATION);
        animation.setInterpolator(interpolator);
        return animation;
    }

    private Animation getTranslation() {
        final TranslateAnimation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
        animation.setDuration(DURATION);
        animation.setInterpolator(new AccelerateInterpolator());
        return animation;
    }
}
