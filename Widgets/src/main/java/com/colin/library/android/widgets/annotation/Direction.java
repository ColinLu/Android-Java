package com.colin.library.android.widgets.annotation;

import static androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX;

import androidx.annotation.IntDef;
import androidx.annotation.RestrictTo;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 作者： ColinLu
 * 时间： 2021-03-05 22:55
 * <p>
 * 描述： 控件方向 上下左右
 */
@RestrictTo(LIBRARY_GROUP_PREFIX)
@Retention(RetentionPolicy.SOURCE)
@IntDef({Direction.NONE, Direction.LEFT, Direction.TOP, Direction.RIGHT, Direction.BOTTOM,
        Direction.HORIZONTAL, Direction.VERTICAL, Direction.ALL})
public @interface Direction {
    int NONE = 1;
    int LEFT = 2;
    int TOP = 4;
    int RIGHT = 8;
    int BOTTOM = 16;
    int HORIZONTAL = LEFT | RIGHT;
    int VERTICAL = TOP | BOTTOM;
    int ALL = HORIZONTAL | VERTICAL;
}