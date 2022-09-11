package com.colin.library.android.widgets.transform;

import android.view.View;

import androidx.annotation.NonNull;


public class AccordionTransformer extends BaseTransformer {

    @Override
    protected void onTransform(@NonNull View view, float position) {
        view.setPivotX(position < 0 ? 0 : view.getWidth());
        view.setScaleX(position < 0 ? 1f + position : 1f - position);
    }

}
