package com.colin.library.android.widgets.transform;

import android.view.View;

import androidx.annotation.NonNull;

public class RotateDownTransformer extends BaseTransformer {

    private static final float ROT_MOD = -15f;

    @Override
    protected void onTransform(@NonNull View view, float position) {
        final float width = view.getWidth();
        final float height = view.getHeight();
        final float rotation = ROT_MOD * position * -1.25f;

        view.setPivotX(width * 0.5f);
        view.setPivotY(height);
        view.setRotation(rotation);
    }

    @Override
    protected boolean isPagingEnabled() {
        return true;
    }

}
