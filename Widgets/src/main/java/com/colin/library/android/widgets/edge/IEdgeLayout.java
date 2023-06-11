package com.colin.library.android.widgets.edge;

import androidx.annotation.Px;

import com.colin.library.android.widgets.annotation.Direction;

/**
 * 作者： ColinLu
 * 时间： 2023-05-13 23:04
 * <p>
 * 描述： TODO
 */
public interface IEdgeLayout {
    void setDirectionEnabled(@Direction final int enabled);

    boolean isRunning();

    void start();

    void offset(@Px int offsetX, @Px int offsetY);

    void finish();


}
