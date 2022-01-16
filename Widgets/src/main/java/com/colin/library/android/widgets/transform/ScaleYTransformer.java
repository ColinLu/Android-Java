package com.colin.library.android.widgets.transform;

import android.view.View;

import androidx.annotation.NonNull;

/**
 * Created by zhouwei on 17/5/26.
 */

public class ScaleYTransformer extends BaseTransformer {
    private static final float MIN_SCALE = 0.9F;

    @Override
    protected void onTransform(@NonNull View page, float position) {
        if(position < -1){
            page.setScaleY(MIN_SCALE);
        }else if(position<= 1){
            float scale = Math.max(MIN_SCALE,1 - Math.abs(position));
            page.setScaleY(scale);
            /*page.setScaleX(scale);

            if(position<0){
                page.setTranslationX(width * (1 - scale) /2);
            }else{
                page.setTranslationX(-width * (1 - scale) /2);
            }*/

        }else{
            page.setScaleY(MIN_SCALE);
        }
    }

}
