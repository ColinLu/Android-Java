package com.colin.library.android.provider;

import android.graphics.Outline;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewOutlineProvider;

/**
 * 作者： ColinLu
 * 时间： 2023-04-04 23:34
 * <p>
 * 描述： View 圆角效果
 */
public class AppViewOutlineProvider extends ViewOutlineProvider {
    private final float mRadius;

    public AppViewOutlineProvider(float radius) {
        this.mRadius = radius;
    }

    @Override
    public void getOutline(View view, Outline outline) {
        // 绘制区域
        final Rect selfRect = new Rect(0, 0, view.getWidth(), view.getHeight());
        outline.setRoundRect(selfRect, mRadius);
    }

}