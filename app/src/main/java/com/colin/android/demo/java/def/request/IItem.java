package com.colin.android.demo.java.def.request;

import androidx.annotation.NonNull;

import java.util.List;

/**
 * 作者： ColinLu
 * 时间： 2022-03-09 22:32
 * <p>
 * 描述： TODO
 */
public interface IItem {
    @NonNull
    String getName();

    @NonNull
    List<ICategory> getList();
}
