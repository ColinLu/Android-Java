package com.colin.library.android.media.preview;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.annotation.RawRes;

/**
 * 作者： ColinLu
 * 时间： 2023-01-07 02:57
 * <p>
 * 描述： TODO
 */
public interface IPreviewMedia<Returner> {

    Returner uri(@Nullable Uri uri);

    Returner url(@Nullable String url);

    Returner raw(@RawRes int res);

    Returner drawable(@DrawableRes int res);

    interface Image<Returner> extends IPreviewMedia<Returner> {

        Returner path(@Nullable String path);

        Returner bitmap(@Nullable Bitmap bitmap);

        Returner drawable(@Nullable Drawable drawable);
    }


}
