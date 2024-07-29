package com.colin.library.android.http.parse;

import android.graphics.Bitmap;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.http.progress.IProgress;
import com.colin.library.android.utils.BitmapUtil;

import java.io.IOException;

import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 作者： ColinLu
 * 时间： 2023-04-22 13:58
 * <p>
 * 描述： Result->Bitmap
 */
public class ParseBitmap implements IParse<Bitmap> {
    private final int mMaxWidth;
    private final int mMaxHeight;
    private final Bitmap.Config mConfig;
    private final ImageView.ScaleType mScaleType;

    public ParseBitmap() {
        this(Bitmap.Config.ARGB_8888, ImageView.ScaleType.FIT_CENTER, 0, 0);
    }

    public ParseBitmap(int width, int height) {
        this(Bitmap.Config.ARGB_8888, ImageView.ScaleType.FIT_CENTER, width, height);
    }

    public ParseBitmap(@NonNull final Bitmap.Config config, int width, int height) {
        this(config, ImageView.ScaleType.FIT_CENTER, width, height);
    }

    public ParseBitmap(@NonNull final ImageView.ScaleType scaleType, int width, int height) {
        this(Bitmap.Config.ARGB_8888, scaleType, width, height);
    }

    public ParseBitmap(@NonNull final Bitmap.Config config, @NonNull final ImageView.ScaleType scaleType, final int width, final int height) {
        this.mConfig = config;
        this.mScaleType = scaleType;
        this.mMaxWidth = width;
        this.mMaxHeight = height;
    }

    @Nullable
    @Override
    public Bitmap parse(@NonNull Response response, @Nullable String encode, @NonNull IProgress progress) throws IOException {
        final ResponseBody body = response.body();
        return BitmapUtil.getBitmap(body == null ? null : body.bytes(), mConfig, mScaleType, mMaxWidth, mMaxHeight);
    }
}
