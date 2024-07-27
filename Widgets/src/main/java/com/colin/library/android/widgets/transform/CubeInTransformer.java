package com.colin.library.android.widgets.transform;

import android.view.View;

import androidx.annotation.NonNull;

public class CubeInTransformer extends BaseTransformer {

    @Override
    protected void onTransform(@NonNull View view, float position) {
        // Rotate the fragment_preview on the left or right edge
        view.setPivotX(position > 0 ? 0 : view.getWidth());
        view.setPivotY(0);
        view.setRotationY(-90f * position);
    }

    @Override
    public boolean isPagingEnabled() {
        return true;
    }

}
