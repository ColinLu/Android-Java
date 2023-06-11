package com.colin.library.android.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.ByteArrayOutputStream;

/**
 * 作者： ColinLu
 * 时间： 2023-06-10 23:44
 * <p>
 * 描述： 压缩图片工具
 */
public final class BitmapCompressUtil {
    ///////////////////////////////////////////////////////////////////////////
    // 质量
    //质量压缩不会减少图片的像素。它是在保持像素不变的前提下改变图片的位深及透明度等，来达到压缩图片的目的。
    //进过它压缩的图片文件大小会有改变，但是导入成bitmap后占得内存是不变的。因为要保持像素不变，所以它就无法无限压缩，
    //到达一个值之后就不会继续变小了。显然这个方法并不适用于缩略图，其实也不适用于想通过压缩图片减少内存的适用，
    //仅仅适用于想在保证图片质量的同时减少文件大小的情况而已。
    ///////////////////////////////////////////////////////////////////////////

    /**
     * 图片压缩【质量】 Bitmap
     *
     * @param bitmap  需要重新编码的 bitmap
     * @param quality 质量[0,100]
     * @return 重新编码后的图片
     */
    @Nullable
    public static Bitmap quality(@Nullable Bitmap bitmap, @IntRange(from = 0, to = 100) final int quality) {
        return quality(bitmap, Bitmap.CompressFormat.JPEG, null, quality, true);
    }

    /**
     * 图片压缩【质量】 Bitmap
     *
     * @param bitmap  需要重新编码的 bitmap
     * @param options {@link BitmapFactory.Options}
     * @param quality 质量[0,100]
     * @return 重新编码后的图片
     */
    @Nullable
    public static Bitmap quality(@Nullable final Bitmap bitmap, @Nullable final BitmapFactory.Options options,
            @IntRange(from = 0, to = 100) int quality) {
        return quality(bitmap, Bitmap.CompressFormat.JPEG, options, quality, true);
    }


    /**
     * 图片压缩【质量】 Bitmap
     *
     * @param bitmap  需要重新编码的 bitmap
     * @param format  编码后的格式 如 Bitmap.CompressFormat.PNG
     * @param quality 质量[0,100]
     * @return 重新编码后的图片
     */
    @Nullable
    public static Bitmap quality(@Nullable final Bitmap bitmap, @NonNull final Bitmap.CompressFormat format,
            @IntRange(from = 0, to = 100) final int quality) {
        return quality(bitmap, format, null, quality, true);
    }

    /**
     * 图片压缩【质量】 Bitmap
     *
     * @param bitmap  需要重新编码的 bitmap
     * @param format  编码后的格式 如 Bitmap.CompressFormat.PNG
     * @param options {@link BitmapFactory.Options}
     * @param quality 质量[0,100]
     * @param recycle 是否回收
     * @return 重新编码后的图片
     */
    @Nullable
    public static Bitmap quality(@Nullable final Bitmap bitmap, @NonNull final Bitmap.CompressFormat format,
            @Nullable final BitmapFactory.Options options, @IntRange(from = 0, to = 100) final int quality, final boolean recycle) {
        if (BitmapUtil.isEmpty(bitmap)) return null;
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            bitmap.compress(format, quality, baos);
            final byte[] data = baos.toByteArray();
            return BitmapFactory.decodeByteArray(data, 0, data.length, options);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtil.flush(baos);
            IOUtil.close(baos);
            BitmapUtil.recycle(recycle, bitmap);
        }
        return null;
    }


    /**
     * 图片压缩【质量】 Bitmap
     *
     * @param bitmap 需要重新编码的 bitmap
     * @param max    压缩到最大值
     * @return 重新编码后的图片
     */
    @Nullable
    public static Bitmap quality(@Nullable final Bitmap bitmap, long max) {
        return quality(bitmap, Bitmap.CompressFormat.JPEG, max, true);
    }

    /**
     * 图片压缩【质量】 Bitmap
     *
     * @param bitmap 需要重新编码的 bitmap
     * @param format 编码后的格式 如 Bitmap.CompressFormat.PNG
     * @param max    压缩到最大值
     * @return 重新编码后的图片
     */
    @Nullable
    public static Bitmap quality(@Nullable final Bitmap bitmap, @NonNull final Bitmap.CompressFormat format, long max) {
        return quality(bitmap, format, max, true);
    }


    /**
     * 图片压缩 options减 Bitmap
     *
     * @param bitmap  需要重新编码的 bitmap
     * @param format  编码后的格式 如 Bitmap.CompressFormat.PNG
     * @param max     压缩到最大值
     * @param recycle 是否回收
     * @return 重新编码后的图片
     */
    @Nullable
    public static Bitmap quality(@Nullable final Bitmap bitmap, @NonNull final Bitmap.CompressFormat format, final long max, final boolean recycle) {
        if (BitmapUtil.isEmpty(bitmap)) return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //质量压缩方法，这里100表示不压缩，把压缩后的数据存放到byteArrayOutputStream中
        bitmap.compress(format, 100, baos);
        int options = 100;
        //循环判断如果压缩后图片是否大于 maxSize kb,大于继续压缩
        while (baos.toByteArray().length / 1024 > max) {
            //重置baos即清空baos
            baos.reset();
            //这里压缩options%，把压缩后的数据存放到byteArrayOutputStream中
            bitmap.compress(format, options, baos);
            //每次都减少5
            options -= 5;
            if (options <= 5) break;
        }
        byte[] bytes = baos.toByteArray();
        final Bitmap result = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        IOUtil.flush(baos);
        IOUtil.close(baos);
        BitmapUtil.recycle(recycle, bitmap);
        return result;
    }


    /**
     * 图片压缩 byte 递归相减
     *
     * @param bitmap  需要重新编码的 bitmap
     * @param max     压缩到最大值
     * @return 重新编码后的图片
     */
    @Nullable
    public static Bitmap qualityBest(@Nullable final Bitmap bitmap, long max) {
        return qualityBest(bitmap, Bitmap.CompressFormat.JPEG, null, max, true);
    }
    /**
     * 图片压缩 byte 递归相减
     *
     * @param bitmap  需要重新编码的 bitmap
     * @param options 压缩到最大值
     * @param max     压缩到最大值
     * @return 重新编码后的图片
     */
    @Nullable
    public static Bitmap qualityBest(@Nullable final Bitmap bitmap, @Nullable final BitmapFactory.Options options, long max) {
        return qualityBest(bitmap, Bitmap.CompressFormat.JPEG, options, max, true);
    }

    /**
     * 图片压缩 byte 递归相减
     *
     * @param bitmap  需要重新编码的 bitmap
     * @param format  编码后的格式 如 Bitmap.CompressFormat.PNG
     * @param options 压缩到最大值
     * @param max     压缩到最大值
     * @param recycle 是否回收
     * @return 重新编码后的图片
     */
    @Nullable
    public static Bitmap qualityBest(@Nullable final Bitmap bitmap, @NonNull final Bitmap.CompressFormat format,
            @Nullable final BitmapFactory.Options options, long max, boolean recycle) {
        if (BitmapUtil.isEmpty(bitmap) || max <= 0) return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(format, 100, baos);
        byte[] bytes;
        if (baos.size() <= max) bytes = baos.toByteArray();
        else {
            baos.reset();
            bitmap.compress(format, 0, baos);
            if (baos.size() >= max) bytes = baos.toByteArray();
            else {
                // find the best quality using binary search
                int st = 0, end = 100, mid = 0;
                while (st < end) {// 二分法寻找最佳质量
                    mid = (st + end) / 2;
                    baos.reset();
                    bitmap.compress(format, mid, baos);
                    int len = baos.size();
                    if (len == max) break;
                    else if (len > max) end = mid - 1;
                    else st = mid + 1;
                }
                if (end == mid - 1) {
                    baos.reset();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, st, baos);
                }
                bytes = baos.toByteArray();
            }
        }
        final Bitmap result = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        IOUtil.flush(baos);
        IOUtil.close(baos);
        BitmapUtil.recycle(recycle, bitmap);
        return result;
    }

    /**
     * 图片压缩 按缩放宽高压缩
     *
     * @param bitmap 待操作源图片
     * @param width  指定图片宽度
     * @param height 指定图片高度
     * @return 缩放宽高压缩后的图片
     * 通过缩放图片像素来减少图片占用内存大小。
     */
    @Nullable
    public static Bitmap scale(@Nullable final Bitmap bitmap, final int width, final int height) {
        return scale(bitmap, width, height, true);
    }


    /**
     * 图片压缩 按缩放宽高压缩
     *
     * @param bitmap  待操作源图片
     * @param width   指定图片宽度
     * @param height  指定图片高度
     * @param recycle 是否回收
     * @return 缩放宽高压缩后的图片
     */
    @Nullable
    public static Bitmap scale(@Nullable final Bitmap bitmap, final int width, final int height, final boolean recycle) {
        if (BitmapUtil.isEmpty(bitmap)) return null;
        Bitmap result = Bitmap.createScaledBitmap(bitmap, width, height, true);
        BitmapUtil.recycle(recycle, bitmap);
        return result;
    }

    /**
     * 图片压缩 ( 比例缩放 )
     *
     * @param bitmap 待操作源图片
     * @param scale  宽高等比例缩放倍数
     * @return 缩放后的图片
     */
    @Nullable
    public static Bitmap scale(@Nullable final Bitmap bitmap, final float scale) {
        return scale(bitmap, scale, scale, true);
    }

    /**
     * 缩放图片 ( 比例缩放 )
     *
     * @param bitmap 待操作源图片
     * @param scaleX 横向缩放比例 ( 缩放宽度倍数 )
     * @param scaleY 纵向缩放比例 ( 缩放高度倍数 )
     * @return 缩放后的图片
     */
    @Nullable
    public static Bitmap scale(@Nullable final Bitmap bitmap, final float scaleX, final float scaleY) {
        return scale(bitmap, scaleX, scaleY, true);
    }

    /**
     * 缩放图片 ( 比例缩放 )
     *
     * @param bitmap  待操作源图片
     * @param scaleX  横向缩放比例 ( 缩放宽度倍数 )
     * @param scaleY  纵向缩放比例 ( 缩放高度倍数 )
     * @param recycle 是否回收
     * @return 缩放后的图片
     */
    @Nullable
    public static Bitmap scale(@Nullable final Bitmap bitmap, final float scaleX, final float scaleY, final boolean recycle) {
        if (BitmapUtil.isEmpty(bitmap)) return null;
        Matrix matrix = new Matrix();
        matrix.setScale(scaleX, scaleY);
        Bitmap result = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        BitmapUtil.recycle(recycle, bitmap);
        return result;
    }


    /**
     * 按采样大小压缩
     *
     * @param bitmap     被压缩的图片图片
     * @param sampleSize 图片压缩比例
     * @return 返回压缩的图片
     * <p>
     * 这个方法的好处是大大的缩小了内存的使用，在读存储器上的图片时，如果不需要高清的效果，可以先只读取图片的边，
     * 通过宽和高设定好取样率后再加载图片，这样就不会过多的占用内存。
     */
    @Nullable
    public static Bitmap sampleSize(@Nullable final Bitmap bitmap, final int sampleSize) {
        return sampleSize(bitmap, sampleSize, false);
    }

    /**
     * 按采样大小压缩
     *
     * @param bitmap     被压缩的图片图片
     * @param sampleSize 图片压缩比例
     * @param recycle    是否回收原图片
     * @return 返回压缩的图片
     */
    @Nullable
    public static Bitmap sampleSize(@Nullable final Bitmap bitmap, int sampleSize, boolean recycle) {
        if (BitmapUtil.isEmpty(bitmap)) return null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        final Bitmap result = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        IOUtil.flush(byteArrayOutputStream);
        IOUtil.close(byteArrayOutputStream);
        BitmapUtil.recycle(recycle, bitmap);
        return result;
    }

    /**
     * 按采样大小压缩
     *
     * @param bitmap 待操作源图片
     * @param width  指定宽度
     * @param height 指定高度
     * @return 按采样率压缩后的图片
     */
    public static Bitmap sampleSize(@Nullable final Bitmap bitmap, final int width, final int height) {
        return sampleSize(bitmap, Bitmap.CompressFormat.JPEG, width, height, false);
    }

    /**
     * 按采样大小压缩
     *
     * @param bitmap 待操作源图片
     * @param format 图片压缩格式
     * @param width  指定宽度
     * @param height 指定高度
     * @return 按采样率压缩后的图片
     */
    public static Bitmap sampleSize(@Nullable final Bitmap bitmap, Bitmap.CompressFormat format, final int width, final int height,
            final boolean recycle) {
        if (BitmapUtil.isEmpty(bitmap)) return null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        options.inJustDecodeBounds = true;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(format, 100, baos);
        final byte[] bytes = baos.toByteArray();
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        options.inSampleSize = calculateInSampleSize(options, width, height);
        options.inJustDecodeBounds = false;
        final Bitmap result = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        IOUtil.flush(baos);
        IOUtil.close(baos);
        BitmapUtil.recycle(recycle, bitmap);
        return result;
    }

    /**
     * 计算采样率
     *
     * @param options   The options.
     * @param maxWidth  The maximum width.
     * @param maxHeight The maximum height.
     * @return the sample size
     */
    public static int calculateInSampleSize(@NonNull final BitmapFactory.Options options, @IntRange(from = 0) final int maxWidth,
            @IntRange(from = 0) final int maxHeight) {
        if (maxHeight == 0 || maxWidth == 0) return 1;
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        while ((width >>= 1) >= maxWidth && (height >>= 1) >= maxHeight) {
            inSampleSize <<= 1;
        }
        return inSampleSize;
    }

}
