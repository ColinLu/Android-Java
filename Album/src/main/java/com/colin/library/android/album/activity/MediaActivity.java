package com.colin.library.android.album.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.colin.library.android.base.BaseActivity;

import java.util.List;

/**
 * 作者： ColinLu
 * 时间： 2023-01-05 00:12
 * <p>
 * 描述： TODO
 */
public class MediaActivity extends BaseActivity {
    public static Action<List<MediaFile>> sResultMultiple;      //多选操作返回
    public static Action<MediaFile> sResultSingle;              //单选操作返回
    public static Action<String> sCancel;                       //取消提示语句

    public static Filter<Long> sFilterSize;                     //筛选条件 大小
    public static Filter<Long> sFilterDuration;                 //筛选条件 时长（视频）
    public static Filter<String> sFilterMime;                   //筛选条件 多媒体类型类型

    @Override
    public int layoutRes() {
        return 0;
    }

    @Override
    public void initView(@Nullable Bundle bundle) {

    }

    @Override
    public void initData(@Nullable Bundle bundle) {

    }

    @Override
    public void loadData(boolean refresh) {

    }
}
