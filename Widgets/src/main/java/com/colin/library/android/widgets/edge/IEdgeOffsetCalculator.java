package com.colin.library.android.widgets.edge;

import androidx.annotation.NonNull;
import androidx.annotation.Px;

/**
 * 作者： ColinLu
 * 时间： 2021-03-05 23:19
 * <p>
 * 描述： 计算偏移量
 */
public interface IEdgeOffsetCalculator {
    int calculator(@NonNull Edge edge, @Px int offset);
}
