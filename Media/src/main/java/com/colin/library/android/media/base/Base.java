package com.colin.library.android.media.base;

import android.net.Uri;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.media.MediaHelper;
import com.colin.library.android.media.def.Action;
import com.colin.library.android.media.def.Constants;
import com.colin.library.android.media.def.Facing;
import com.colin.library.android.media.def.Filter;
import com.colin.library.android.media.def.MediaFile;
import com.colin.library.android.media.def.MediaType;

import java.util.List;

public abstract class Base<Returner, Result, Cancel> implements IBase<Returner, Result, Cancel> {
    @MediaType
    protected final int mMediaType;             //多媒体类型
    @IntRange(from = 1)
    protected final int mLimitCount;            //显示获取多媒体数量 0标示不限制
    @Nullable
    protected CharSequence mTitle;              //界面显示标题
    @Nullable
    protected Uri mUri;                         //返回地址
    @Facing
    protected int mFacing;                      //前置 or 后置
    @Nullable
    protected Filter<Long> mFilterSize;         //筛选文件大小
    @Nullable
    protected Filter<String> mFilterMimeType;   //筛选多媒体类型
    @Nullable
    protected Filter<Long> mFilterDuration;     //筛选视频、音频时长
    @Nullable
    protected Action<Result> mResult;           //选中之后回调
    @Nullable
    protected Action<Cancel> mCancel;           //取消、异常之后回调
    @Nullable
    protected List<MediaFile> mList;            //已选中数量
    protected boolean mDisplayCamera;           //列表是否显示camera
    protected boolean mDisplayInvalid;          //列表是否显示无效多媒体文件
    protected boolean mNeedCrop;                //是否剪切
    protected boolean mMultiple;            //列表开启多选模式
    @IntRange(from = 2, to = 5)
    protected int mColumn = MediaHelper.getInstance().getMediaConfig().getColumn();//列表显示列数
    protected int mLimitQuality = Constants.DEFAULT_LIMIT_QUALITY;      //视频 质量
    protected long mLimitSize = Constants.DEFAULT_LIMIT_SIZE;           //文件 大小
    protected long mLimitDuration = Constants.DEFAULT_LIMIT_DURATION;   //视频、音频时长

    public Base(@MediaType int mediaType, @IntRange(from = 1) int limit) {
        this(mediaType, limit, false);
    }

    public Base(@MediaType int mediaType, @IntRange(from = 1) int limit, boolean multiple) {
        this.mMediaType = mediaType;
        this.mLimitCount = limit;
        this.mMultiple = multiple;
    }

    public Base(@MediaType int mediaType, @NonNull Uri uri, @IntRange(from = 1) int limit) {
        this.mMediaType = mediaType;
        this.mUri = uri;
        this.mLimitCount = limit;
    }

    /*返回选中的结果*/
    @NonNull
    public final Returner title(@Nullable String title) {
        this.mTitle = title;
        return (Returner) this;
    }

    /*返回选中的结果*/
    @NonNull
    public final Returner result(@Nullable Action<Result> result) {
        this.mResult = result;
        return (Returner) this;
    }

    /*取消操作，返回提示语句*/
    @NonNull
    public final Returner cancel(@Nullable Action<Cancel> cancel) {
        this.mCancel = cancel;
        return (Returner) this;
    }


}
