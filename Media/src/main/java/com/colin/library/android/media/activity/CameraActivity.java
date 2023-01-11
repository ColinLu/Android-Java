package com.colin.library.android.media.activity;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.colin.library.android.base.BaseActivity;
import com.colin.library.android.media.R;
import com.colin.library.android.media.adapter.MediaAdapter;
import com.colin.library.android.media.def.Action;
import com.colin.library.android.media.def.MediaFile;
import com.colin.library.android.media.def.MediaType;

public class CameraActivity extends BaseActivity {
    public static Action<MediaFile> sResult;                //成功 返回文件路径
    public static Action<String> sCancel;                   //取消 返回原因
    private String mTitle;                                  //显示标题
    @MediaType
    private int mMediaType;
    private Uri mUri;                                       //多媒体文件输出路径
    private String mFilePath;                               //多媒体文件输出路径
    private int mLimitQuality = 1;                          //视频    限制质量 0 or 1
    private long mLimitDuration = Long.MAX_VALUE;           //视频    限制时间 1 - 0x7fffffffffffffffL
    private long mLimitSize = Long.MAX_VALUE;               //视频    限制大小 1 - 0x7fffffffffffffffL
    private boolean mCrop = false;
    private boolean mBefore = false;
    private boolean toPermissionSetting = false;



    @Override
    public int layoutRes() {
        return R.layout.activity_camera;
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