package com.colin.library.android.base.def;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;

/**
 * 作者： ColinLu
 * 时间： 2021-12-20 20:21
 * <p>
 * 描述： 界面实现定义
 */
public interface IBase {

    @LayoutRes
    default int layoutRes() {
        return Resources.ID_NULL;
    }

    void initView(@Nullable Bundle bundle);


    /*Previous interface data*/
    void initData(@Nullable Bundle bundle);


    /*local sqlite net*/
    void loadData(boolean refresh);


}
