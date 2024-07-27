package com.colin.library.android.widgets.transform;

import android.view.View;

import androidx.annotation.NonNull;

public class DefaultTransformer extends BaseTransformer {

    @Override
    protected void onTransform(@NonNull View view, float position) {
    }

    @Override
    public boolean isPagingEnabled() {
        return true;
    }

}
