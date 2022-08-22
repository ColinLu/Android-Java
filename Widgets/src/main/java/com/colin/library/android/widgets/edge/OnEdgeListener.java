package com.colin.library.android.widgets.edge;

import androidx.annotation.NonNull;

import com.colin.library.android.widgets.annotation.Direction;

/**
 * 作者： ColinLu
 * 时间： 2021-03-06 16:00
 * <p>
 * 描述： 触发回调
 */
public interface OnEdgeListener {
    default void start(@NonNull Edge edge) {
    }

    default void offset(@Direction int direction, int offset) {
    }

    default void finish(@NonNull Edge edge) {
    }
}
