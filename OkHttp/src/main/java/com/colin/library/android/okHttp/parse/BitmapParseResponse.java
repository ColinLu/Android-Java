package com.colin.library.android.okHttp.parse;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Size;

import com.colin.library.android.utils.BitmapUtil;

import okhttp3.Response;
import okhttp3.ResponseBody;

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

    public BitmapParseResponse(@NonNull final Bitmap.Config config, @NonNull final ImageView.ScaleType scaleType, final int width, final int height) {
        this.mConfig = config;
        this.mScaleType = scaleType;
        this.mMaxWidth = width;
        this.mMaxHeight = height;
    }

    @Nullable
    @Override
    public Bitmap parse(@NonNull Response response) throws Throwable {
        final ResponseBody body = response.body();
        final byte[] bytes = null == body ? null : body.bytes();
        if (mMaxWidth == 0 || mMaxHeight == 0) return BitmapUtil.getBitmap(bytes, mConfig);
        else return BitmapUtil.getBitmap(bytes, mConfig, mScaleType, mMaxWidth, mMaxHeight);
    }

    @Nullable
    private Bitmap parse(@NonNull @Size(min = 1) final byte[] byteArray, @NonNull final Bitmap.Config decodeConfig,
                         @NonNull final ImageView.ScaleType scaleType, final int width, final int height) {
        final BitmapFactory.Options options = getBitmapOptions(decodeConfig);
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, options);

        final int outWidth = options.outWidth;
        final int outHeight = options.outHeight;

        final int resizeWidth = getResizedDimension(width, height, outWidth, outHeight, scaleType);
        final int resizeHeight = getResizedDimension(height, width, outHeight, outWidth, scaleType);

        options.inJustDecodeBounds = false;
        options.inSampleSize = bastSampleSize(outWidth, outHeight, resizeWidth, resizeHeight);

        return getCustomBitmap(byteArray, options, resizeWidth, resizeHeight);
    }


    @NonNull
    private BitmapFactory.Options getBitmapOptions(@NonNull Bitmap.Config decodeConfig) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = decodeConfig;
        return options;
    }


    /**
     * 计算长度 根据 ImageView.ScaleType
     *
     * @param userMaxSize       用户想要的最大Size
     * @param userMaxSizeSecond 用户想要的最大Size 备用
     * @param outSize           图片本来输出Size
     * @param outSizeSecond     图片本来输出Size 备用
     * @param scaleType         图片显示效果
     * @return
     */
    private int getResizedDimension(int userMaxSize, int userMaxSizeSecond, int outSize, int outSizeSecond, ImageView.ScaleType scaleType) {

        if (scaleType == ImageView.ScaleType.FIT_XY)
            return userMaxSize == 0 ? outSize : userMaxSize;

        if (userMaxSize == 0) return (int) (outSize * getRatio(userMaxSizeSecond, outSizeSecond));

        if (userMaxSizeSecond == 0) return userMaxSize;

        final double ratio = getRatio(outSizeSecond, outSize);

        if (scaleType == ImageView.ScaleType.CENTER_CROP)
            return centerCropResizedDimension(ratio, userMaxSize, userMaxSizeSecond);
        else return defaultResizedDimension(ratio, userMaxSize, userMaxSizeSecond);
    }

    @Nullable
    private Bitmap getCustomBitmap(@NonNull @Size(min = 0) byte[] byteArray, BitmapFactory.Options bitmapOptions, int resizeWidth, int resizeHeight) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, bitmapOptions);
        if (null == bitmap || bitmap.getWidth() == 0 || bitmap.getHeight() == 0) return null;
        if (bitmap.getWidth() > resizeWidth || bitmap.getHeight() > resizeHeight)
            bitmap = Bitmap.createScaledBitmap(bitmap, resizeWidth, resizeHeight, true);
        if (!bitmap.isRecycled()) bitmap.recycle();
        return bitmap;
    }


    private int defaultResizedDimension(double ratio, int userMaxSize, int userMaxSizeSecond) {
        if (ratio == 0) return userMaxSize;
        if (userMaxSize * ratio < userMaxSizeSecond)
            userMaxSize = (int) (userMaxSizeSecond / ratio);
        return userMaxSize;
    }

    private int centerCropResizedDimension(double ratio, int userMaxSize, int userMaxSizeSecond) {
        if (ratio == 0) return userMaxSize;
        if (userMaxSize * ratio < userMaxSizeSecond)
            userMaxSize = (int) (userMaxSizeSecond / ratio);
        return userMaxSize;
    }

    private double getRatio(int max, int out) {
        return out == 0 ? 0 : (double) max / (double) out;
    }

    private int bastSampleSize(int actualWidth, int actualHeight, int desiredWidth, int desiredHeight) {
        double wr = (double) actualWidth / desiredWidth;
        double hr = (double) actualHeight / desiredHeight;
        double ratio = Math.min(wr, hr);
        float n = 1.0f;
        while ((n * 2) <= ratio) n *= 2;
        return (int) n;
    }
}
