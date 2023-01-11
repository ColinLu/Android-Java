package com.colin.library.android.widgets.def;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 作者： ColinLu
 * 时间： 2021-12-14 23:52
 * <p>
 * 描述： 点击Item
 */
public interface OnItemLongClickListener {
    boolean item(@NonNull View view, int position, @Nullable Object object);
}
