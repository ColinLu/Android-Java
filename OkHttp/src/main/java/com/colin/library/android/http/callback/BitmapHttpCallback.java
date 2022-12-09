package com.colin.library.android.http.callback;

import static android.widget.ImageView.ScaleType;

import android.graphics.Bitmap;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.http.parse.BitmapParseResponse;
import com.colin.library.android.http.parse.IParseResponse;

import java.io.IOException;

import okhttp3.Response;

/**
 * 作者： ColinLu
 * 时间： 2022-02-22 00:06
 * <p>
 * 描述： 下载图片解析
 */
public class BitmapHttpCallback implements IHttpCallback<Bitmap> {
    @NonNull
    protected final IParseResponse<Bitmap> mParseResponse;

    public BitmapHttpCallback() {
        this(Bitmap.Config.ARGB_8888, ImageView.ScaleType.FIT_CENTER, 0, 0);
    }

    public BitmapHttpCallback(int width, int height) {
        this(new BitmapParseResponse(Bitmap.Config.ARGB_8888, ImageView.ScaleType.FIT_CENTER, width, height));
    }

    public BitmapHttpCallback(@NonNull final Bitmap.Config config, int width, int height) {
        this(new BitmapParseResponse(config, ImageView.ScaleType.FIT_CENTER, width, height));
    }

    public BitmapHttpCallback(@NonNull final ImageView.ScaleType scaleType, int width, int height) {
        this(new BitmapParseResponse(Bitmap.Config.ARGB_8888, scaleType, width, height));
    }

    public BitmapHttpCallback(@NonNull final Bitmap.Config config, @NonNull final ScaleType scaleType, final int width, final int height) {
        this(new BitmapParseResponse(config, scaleType, width, height));
    }

    public BitmapHttpCallback(@NonNull IParseResponse<Bitmap> parseResponse) {
        this.mParseResponse = parseResponse;
    }

    @Nullable
    @Override
    public Bitmap parse(@NonNull Response response) throws IOException {
        return mParseResponse.parse(response);
    }
}
