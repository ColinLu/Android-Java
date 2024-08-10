package com.colin.library.android.map.search;

import androidx.annotation.NonNull;

import com.colin.library.android.map.def.Status;

import java.util.List;

/**
 * Author:ColinLu
 * E-mail:945919945@qq.com
 * Date  :2024-08-09
 * <p>
 * Des   :TODO
 */
public interface OnSearchListener {
    void change(@Status int status, @NonNull List<Result> list);
}
