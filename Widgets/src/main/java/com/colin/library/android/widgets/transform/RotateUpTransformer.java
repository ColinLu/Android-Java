package com.colin.library.android.widgets.transform;


import android.view.View;

import androidx.annotation.NonNull;


public class RotateUpTransformer extends BaseTransformer {

    private static final float ROT_MOD = -15f;

    @Override
    protected void onTransform(@NonNull View view, float position) {
        final float width = view.getWidth();
        final float rotation = ROT_MOD * position;

        view.setPivotX(width * 0.5f);
        view.setPivotY(0f);
        view.setTranslationX(0f);
        view.setRotation(rotation);
    }

    @Override
    protected boolean isPagingEnabled() {
        return true;
    }

}
