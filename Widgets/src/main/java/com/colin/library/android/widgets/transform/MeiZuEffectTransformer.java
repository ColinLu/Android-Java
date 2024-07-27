package com.colin.library.android.widgets.transform;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

public class MeiZuEffectTransformer extends BaseTransformer {
    private float reduceX = 0.0f;
    private float itemWidth = 0;
    private float offsetPosition = 0f;
    private int mCoverWidth;
    private float mScaleMax = 1.0f;
    private float mScaleMin = 0.8f;

    @Override
    protected void onTransform(@NonNull View view, float position) {

    }

    @Override
    public void transformPage(@NonNull View view, float position) {
        ViewGroup parent = (ViewGroup) view.getParent();
        if (offsetPosition == 0f) {
            float paddingLeft = parent.getPaddingLeft();
            float paddingRight = parent.getPaddingRight();
            float width = parent.getMeasuredWidth();
            offsetPosition = paddingLeft / (width - paddingLeft - paddingRight);
        }
        float currentPos = position - offsetPosition;
        if (itemWidth == 0) {
            itemWidth = view.getWidth();
            //由于左右边的缩小而减小的x的大小的一半
            reduceX = (2.0f - mScaleMax - mScaleMin) * itemWidth / 2.0f;
        }
        if (currentPos <= -1.0f) {
            view.setTranslationX(reduceX + mCoverWidth);
            view.setScaleX(mScaleMin);
            view.setScaleY(mScaleMin);
        } else if (currentPos <= 1.0) {
            float scale = (mScaleMax - mScaleMin) * Math.abs(1.0f - Math.abs(currentPos));
            float translationX = currentPos * -reduceX;
            if (currentPos <= -0.5) {//两个view中间的临界，这时两个view在同一层，左侧View需要往X轴正方向移动覆盖的值()
                view.setTranslationX(translationX + mCoverWidth * Math.abs(Math.abs(currentPos) - 0.5f) / 0.5f);
            } else if (currentPos <= 0.0f) {
                view.setTranslationX(translationX);
            } else if (currentPos >= 0.5) {//两个view中间的临界，这时两个view在同一层
                view.setTranslationX(translationX - mCoverWidth * Math.abs(Math.abs(currentPos) - 0.5f) / 0.5f);
            } else {
                view.setTranslationX(translationX);
            }
            view.setScaleX(scale + mScaleMin);
            view.setScaleY(scale + mScaleMin);
        } else {
            view.setScaleX(mScaleMin);
            view.setScaleY(mScaleMin);
            view.setTranslationX(-reduceX - mCoverWidth);
        }

    }
}
