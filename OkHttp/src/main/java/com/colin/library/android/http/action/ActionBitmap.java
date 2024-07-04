package com.colin.library.android.http.action;

import android.graphics.Bitmap;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.colin.library.android.http.parse.IParse;
import com.colin.library.android.http.parse.ParseBitmap;
import com.colin.library.android.http.progress.IProgress;

import java.io.IOException;

import okhttp3.Response;

/**
 * 作者： ColinLu
 * 时间： 2023-04-23 21:00
 * <p>
 * 描述： Callback Result->Bitmap
 */
public class ActionBitmap implements IAction<Bitmap> {
    @NonNull
    private final IParse<Bitmap> mParseResult;

    public ActionBitmap() {
        this(Bitmap.Config.ARGB_8888, ImageView.ScaleType.FIT_CENTER, 0, 0);
    }

    public ActionBitmap(int maxWidth, int maxHeight) {
        this(Bitmap.Config.ARGB_8888, ImageView.ScaleType.FIT_CENTER, maxWidth, maxHeight);
    }

    public ActionBitmap(@NonNull Bitmap.Config decodeConfig, @NonNull ImageView.ScaleType scaleType) {
        this(decodeConfig, scaleType, 0, 0);
    }

    public ActionBitmap(@NonNull Bitmap.Config decodeConfig, @NonNull ImageView.ScaleType scaleType, int maxWidth, int maxHeight) {
        mParseResult = new ParseBitmap(decodeConfig, scaleType, maxWidth, maxHeight);
    }

    @Nullable
    @Override
    @WorkerThread
    public Bitmap parse(@NonNull Response response, @Nullable String encode, @NonNull IProgress progress) throws IOException {
        return mParseResult.parse(response, encode, progress);
    }
}
