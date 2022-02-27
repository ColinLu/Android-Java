package com.colin.library.android.okHttp.callback;

import static android.widget.ImageView.*;

import android.graphics.Bitmap;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.colin.library.android.okHttp.parse.BitmapParseResponse;
import com.colin.library.android.okHttp.parse.IParseResponse;
import com.colin.library.android.okHttp.parse.StringParseResponse;

/**
 * 作者： ColinLu
 * 时间： 2022-02-22 00:06
 * <p>
 * 描述： 下载图片解析
 */
public final class BitmapHttpCallback implements IHttpCallback<Bitmap> {
    @NonNull
    protected final IParseResponse<Bitmap> mParseResponse;

    public BitmapHttpCallback() {
        this(Bitmap.Config.ARGB_8888, ImageView.ScaleType.FIT_CENTER, 0, 0);
    }

    public BitmapHttpCallback(int width, int height) {
        this(Bitmap.Config.ARGB_8888, ImageView.ScaleType.FIT_CENTER, width, height);
    }

    public BitmapHttpCallback(@NonNull final Bitmap.Config config, int width, int height) {
        this(config, ImageView.ScaleType.FIT_CENTER, width, height);
    }

    public BitmapHttpCallback(@NonNull final ImageView.ScaleType scaleType, int width, int height) {
        this(Bitmap.Config.ARGB_8888, scaleType, width, height);
    }
    public BitmapHttpCallback(@NonNull final Bitmap.Config config, @NonNull final ScaleType scaleType, final int width, final int height) {
        this(new BitmapParseResponse(config, scaleType, width, height));
    }

    public BitmapHttpCallback(@NonNull IParseResponse<Bitmap> parseResponse) {
        this.mParseResponse = parseResponse;
    }
}
