package com.colin.library.android.widgets.transform;

import android.view.View;

import androidx.annotation.NonNull;


public class StackTransformer extends BaseTransformer {

    @Override
    protected void onTransform(@NonNull View view, float position) {
        view.setTranslationX(position < 0 ? 0f : -view.getWidth() * position);
    }

}
