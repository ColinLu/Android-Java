package com.colin.library.android.media.def;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 作者： ColinLu
 * 时间： 2023-01-04 21:56
 * <p>
 * 描述： TODO
 */
@IntDef({Facing.FONT, Facing.BACK})
@Retention(RetentionPolicy.SOURCE)
public @interface Facing {
    int FONT = 2;
    int BACK = 1;
}
