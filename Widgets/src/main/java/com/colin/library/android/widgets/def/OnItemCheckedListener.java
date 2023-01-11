package com.colin.library.android.widgets.def;

import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 作者： ColinLu
 * 时间： 2020-07-14 21:42
 * <p>
 * 描述： 点击选中复选框
 */
public interface OnItemCheckedListener {

    /**
     * When Item is clicked.
     *
     * @param view     item view.
     * @param position item position.
     */
    void onItemChecked(@NonNull CompoundButton view, int position, @Nullable Object object);
}