package com.colin.library.android.media;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 作者： ColinLu
 * 时间： 2020-05-22 09:50
 * <p>
 * 描述： 多选
 */
public interface IMultiple<Returner, Selected> {
    /*设置已选中多媒体数据*/
    @NonNull
    Returner selected(@Nullable Selected list);
}
