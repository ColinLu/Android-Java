package com.colin.library.android.base.def;

import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;

/**
 * 作者： ColinLu
 * 时间： 2021-12-20 20:21
 * <p>
 * 描述： 界面实现定义
 */
public interface IInitView {

    @LayoutRes
    int layoutRes();

    void initView(@Nullable Bundle bundle);

    /*Previous interface data*/
    void initData(@Nullable Bundle bundle);

    /*local sqlite net*/
    void loadData(boolean refresh);

}
