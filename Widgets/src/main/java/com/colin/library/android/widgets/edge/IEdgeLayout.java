package com.colin.library.android.widgets.edge;

import com.colin.library.android.widgets.annotation.Direction;

/**
 * 作者： ColinLu
 * 时间： 2023-05-13 23:04
 * <p>
 * 描述： TODO
 */
public interface IEdgeLayout extends OnEdgeListener {
    void setDirectionEnabled(@Direction final int enabled);
}
