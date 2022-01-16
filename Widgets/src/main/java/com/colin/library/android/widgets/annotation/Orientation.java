package com.colin.library.android.widgets.annotation;

import static androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP_PREFIX;

import android.widget.LinearLayout;

import androidx.annotation.IntDef;
import androidx.annotation.RestrictTo;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 作者： ColinLu
 * 时间： 2021-03-05 22:53
 * <p>
 * 描述： 控件方向 横纵
 */
@RestrictTo(LIBRARY_GROUP_PREFIX)
@Retention(RetentionPolicy.SOURCE)
@IntDef({Orientation.HORIZONTAL, Orientation.VERTICAL})
public @interface Orientation {
    int HORIZONTAL = LinearLayout.HORIZONTAL;
    int VERTICAL = LinearLayout.VERTICAL;
}