package com.colin.library.android.media.choose;


import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

/**
 * 作者： ColinLu
 * 时间： 2019-12-14 21:45
 * <p>
 * 描述： 选择动作
 */
public interface IChoose<Single, Multiple> {
    /*单选*/
    @NonNull
    Single single();

    /*多选*/
    @NonNull
    Multiple multiple(@IntRange(from = 1) int limit);


}