package com.colin.library.android.widgets;

import android.graphics.Outline;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.ViewOutlineProvider;

import androidx.annotation.RequiresApi;

/**
 * 作者： ColinLu
 * 时间： 2020-11-05 16:12
 * <p>
 * 描述： 控件圆角切割
 */
public class RoundOutlineProvider extends ViewOutlineProvider {
    private final float mRadius;

    public RoundOutlineProvider(float radius) {
        this.mRadius = radius;
    }

    @Override
    public void getOutline(View view, Outline outline) {
        // 绘制区域
        final Rect selfRect = new Rect(0, 0, view.getWidth(), view.getHeight());
        outline.setRoundRect(selfRect, mRadius);
    }

}