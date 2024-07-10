package com.colin.library.android.widgets.transform;

import android.view.View;

import androidx.annotation.NonNull;

public class ScaleInOutTransformer extends BaseTransformer {

    @Override
    protected void onTransform(@NonNull View view, float position) {
        view.setPivotX(position < 0 ? 0 : view.getWidth());
        view.setPivotY(view.getHeight() / 2f);
        float scale = position < 0 ? 1f + position : 1f - position;
        view.setScaleX(scale);
        view.setScaleY(scale);
    }

}
