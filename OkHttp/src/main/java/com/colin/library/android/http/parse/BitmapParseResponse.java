package com.colin.library.android.http.parse;

import android.graphics.Bitmap;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.utils.BitmapUtil;

import java.io.IOException;

import okhttp3.Response;

/**
 * 作者： ColinLu
 * 时间： 2020-11-30 14:01
 * <p>
 * 描述： 解析图片
 */
public class BitmapParseResponse implements IParseResponse<Bitmap> {
    private final int mMaxWidth;
    private final int mMaxHeight;
    private final Bitmap.Config mConfig;
    private final ImageView.ScaleType mScaleType;

    public BitmapParseResponse() {
        this(Bitmap.Config.ARGB_8888, ImageView.ScaleType.FIT_CENTER, 0, 0);
    }

    public BitmapParseResponse(int width, int height) {
        this(Bitmap.Config.ARGB_8888, ImageView.ScaleType.FIT_CENTER, width, height);
    }

    public BitmapParseResponse(@NonNull final Bitmap.Config config, int width, int height) {
        this(config, ImageView.ScaleType.FIT_CENTER, width, height);
    }

    public BitmapParseResponse(@NonNull final ImageView.ScaleType scaleType, int width, int height) {
        this(Bitmap.Config.ARGB_8888, scaleType, width, height);
    }

    public BitmapParseResponse(@NonNull final Bitmap.Config config, @NonNull final ImageView.ScaleType scaleType, final int width, final int height) {
        this.mConfig = config;
        this.mScaleType = scaleType;
        this.mMaxWidth = width;
        this.mMaxHeight = height;
    }

    @Nullable
    @Override
    public Bitmap parse(@NonNull Response response) throws IOException {
        return BitmapUtil.getBitmap(response.body().bytes(), mConfig, mScaleType, mMaxWidth, mMaxHeight);
    }
}
