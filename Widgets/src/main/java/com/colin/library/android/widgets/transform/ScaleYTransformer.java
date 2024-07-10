package com.colin.library.android.widgets.transform;

import android.view.View;

import androidx.annotation.NonNull;


public class ScaleYTransformer extends BaseTransformer {
    private static final float MIN_SCALE = 0.9F;

    @Override
    protected void onTransform(@NonNull View page, float position) {
        if (position < -1) page.setScaleY(MIN_SCALE);
        else if (position <= 1) page.setScaleY(Math.max(MIN_SCALE, 1 - Math.abs(position)));
        else page.setScaleY(MIN_SCALE);

    }

}
