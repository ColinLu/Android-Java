package com.colin.library.android.widgets.transform;

import android.view.View;

import androidx.annotation.NonNull;

public class CubeOutTransformer extends BaseTransformer {

    @Override
    protected void onTransform(@NonNull View view, float position) {
        view.setPivotX(position < 0f ? view.getWidth() : 0f);
        view.setPivotY(view.getHeight() * 0.5f);
        view.setRotationY(90f * position);
    }

    @Override
    public boolean isPagingEnabled() {
        return true;
    }

}
