package com.colin.library.android.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.colin.library.android.utils.data.Constants;
import com.colin.library.android.utils.data.UtilHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * 作者： ColinLu
 * 时间： 2021-12-26 20:25
 * <p>
 * 描述： Bitmap Util
 */
public final class BitmapUtil {
    private BitmapUtil() {
        throw new UnsupportedOperationException("don't instantiate");
    }

    /*判断bitmap对象是否为空*/
    public static boolean isEmpty(@Nullable final Bitmap bitmap) {
        return null == bitmap || bitmap.getWidth() == 0 || bitmap.getHeight() == 0;
    }

    /**
     * 获得指定大小的图片
     *
     * @param file      文件
     * @param maxWidth  最大宽度
     * @param maxHeight 最大高度
     * @return bitmap
     */
    @Nullable
    public static Bitmap getBitmap(@Nullable final File file,
                                   @IntRange(from = 0) final int maxWidth,
                                   @IntRange(from = 0) final int maxHeight) {
        if (!FileUtil.isFile(file)) return null;
        assert file != null;
        String filePath = file.getAbsolutePath();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    @Nullable
    public static Bitmap getBitmap(@Nullable final String filePath) {
        return getBitmap(filePath, null);
    }

    @Nullable
    public static Bitmap getBitmap(@Nullable final String filePath, BitmapFactory.Options options) {
        if (!FileUtil.isFile(filePath)) return null;
        return BitmapFactory.decodeFile(filePath, options);
    }

    @Nullable
    public static Bitmap getBitmap(@Nullable final File file) {
        if (null == file || !file.exists() || !file.isFile()) return null;
        return BitmapFactory.decodeFile(file.getAbsolutePath(), null);
    }

    @Nullable
    public static Bitmap getBitmap(@DrawableRes final int drawableRes) {
        if (drawableRes == 0) return null;
        Drawable drawable = ContextCompat.getDrawable(UtilHelper.getInstance().getContext(), drawableRes);
        if (null == drawable) return null;
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;
    }


    /*View -> Bitmap https://github.com/tyrantgit/ExplosionField */
    @Nullable
    public static Bitmap getBitmap(@Nullable final View view) {
        return getBitmap(view, 1.0F);
    }

    @Nullable
    public static Bitmap getBitmap(@Nullable final View view, @FloatRange(from = 0, to = 1) final float scale) {
        if (null == view || !view.isShown()) return null;
        if (view instanceof ImageView) return getBitmap((ImageView) view);
        view.clearFocus();
        Bitmap bitmap = createBitmapSafely((int) (view.getWidth() * scale), (int) (view.getHeight() * scale), Bitmap.Config.ARGB_8888, 1);
        if (bitmap != null) {
            Canvas canvas = new Canvas();
            canvas.setBitmap(bitmap);
            canvas.save();
            canvas.drawColor(Color.WHITE); // 防止 View 上面有些区域空白导致最终 Bitmap 上有些区域变黑
            canvas.scale(scale, scale);
            view.draw(canvas);
            canvas.restore();
            canvas.setBitmap(null);
        }
        return bitmap;
    }

    /*ImageView -> Bitmap*/
    @Nullable
    public static Bitmap getBitmap(@Nullable final ImageView imageView) {
        final Drawable drawable = null == imageView || !imageView.isShown() ? null : imageView.getDrawable();
        if (drawable instanceof BitmapDrawable) return ((BitmapDrawable) drawable).getBitmap();
        return null;
    }

    @Nullable
    public static Bitmap getBitmap(@Nullable final Context context, @Nullable final Uri uri) {
        if (null == context || null == uri) return null;
        try {
            return BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public static Bitmap getBitmap(@Nullable final InputStream is) {
        return null == is ? null : BitmapFactory.decodeStream(is);
    }

    @Nullable
    public static Bitmap getBitmap(@Nullable final InputStream is,
                                   @IntRange(from = 0) final int maxWidth,
                                   @IntRange(from = 0) final int maxHeight) {
        if (is == null) return null;
        return getBitmap(IOUtil.getBytes(is), 0, maxWidth, maxHeight);
    }

    @Nullable
    public static Bitmap getBitmap(@Nullable final byte[] data, @IntRange(from = 0) final int offset,
                                   @IntRange(from = 0) final int maxWidth,
                                   @IntRange(from = 0) final int maxHeight) {
        if (null == data || data.length == 0) return null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, offset, data.length, options);
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(data, offset, data.length, options);
    }

    /**
     * 得到bitmap的大小
     */
    public static long getBitmapSize(@Nullable Bitmap bitmap) {
        if (isEmpty(bitmap)) return 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) // API 19
            return bitmap.getAllocationByteCount();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1)  // API 12
            return bitmap.getByteCount();
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    /**
     * 重新编码 Bitmap
     *
     * @param bitmap  需要重新编码的 bitmap
     * @param format  编码后的格式 如 Bitmap.CompressFormat.PNG
     * @param quality 质量
     * @return 重新编码后的图片
     */
    public static Bitmap recode(@Nullable final Bitmap bitmap, @Nullable final Bitmap.CompressFormat format, @IntRange(from = 0, to = 100) final int quality) {
        return recode(bitmap, format, null, quality);
    }

    /**
     * 重新编码 Bitmap
     *
     * @param bitmap  需要重新编码的 bitmap
     * @param format  编码后的格式 如 Bitmap.CompressFormat.PNG
     * @param options {@link BitmapFactory.Options}
     * @param quality 质量
     * @return 重新编码后的图片
     */
    public static Bitmap recode(@Nullable final Bitmap bitmap, @Nullable final Bitmap.CompressFormat format,
                                @Nullable final BitmapFactory.Options options,
                                @IntRange(from = 0, to = 100) final int quality) {
        if (isEmpty(bitmap) || format == null) return null;
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            bitmap.compress(format, quality, baos);
            byte[] data = baos.toByteArray();
            return BitmapFactory.decodeByteArray(data, 0, data.length, options);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtil.flush(baos);
            IOUtil.close(baos);
        }
        return null;
    }

    /**
     * 旋转图片
     *
     * @param bitmap  源图片
     * @param degrees 旋转角度
     * @return 旋转后的图片
     */
    public static Bitmap rotate(@Nullable final Bitmap bitmap, final float degrees) {
        if (isEmpty(bitmap)) return null;
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
    }

    /**
     * 旋转图片
     *
     * @param bitmap  源图片
     * @param degrees 旋转角度
     * @param px      旋转点横坐标
     * @param py      旋转点纵坐标
     * @return 旋转后的图片
     */
    public static Bitmap rotate(@Nullable final Bitmap bitmap, final int degrees, final float px, final float py) {
        return rotate(bitmap, degrees, px, py, true);
    }

    /**
     * 旋转图片
     *
     * @param bitmap  源图片
     * @param degrees 旋转角度
     * @param px      旋转点横坐标
     * @param py      旋转点纵坐标
     * @param recycle 是否回收
     * @return 旋转后的图片
     */
    public static Bitmap rotate(@Nullable final Bitmap bitmap, final int degrees, final float px, final float py, final boolean recycle) {
        if (isEmpty(bitmap) || degrees == 0) return bitmap;
        Matrix matrix = new Matrix();
        matrix.setRotate(degrees, px, py);
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        if (recycle && !bitmap.isRecycled()) bitmap.recycle();
        return newBitmap;
    }

    /**
     * 水平翻转图片 ( 左右颠倒 )
     *
     * @param bitmap 源图片
     * @return 翻转后的图片
     */
    public static Bitmap reverseByHorizontal(@Nullable final Bitmap bitmap) {
        return reverse(bitmap, true);
    }

    /**
     * 垂直翻转图片 ( 上下颠倒 )
     *
     * @param bitmap 源图片
     * @return 翻转后的图片
     */
    public static Bitmap reverseByVertical(@Nullable final Bitmap bitmap) {
        return reverse(bitmap, false);
    }

    /**
     * 翻转图片
     *
     * @param bitmap     待操作源图片
     * @param horizontal 是否水平翻转 true 水平 false 垂直
     * @return 翻转后的图片
     */
    public static Bitmap reverse(@Nullable final Bitmap bitmap, final boolean horizontal) {
        if (isEmpty(bitmap)) return null;
        Matrix matrix = new Matrix();
        if (horizontal) matrix.preScale(-1, 1);
        else matrix.preScale(1, -1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
    }

    /**
     * 读取图片属性, 获取图片被旋转的角度
     *
     * @param filePath 文件路径
     * @return 旋转角度
     */
    public static int getRotateDegree(@Nullable final String filePath) {
        if (TextUtils.isEmpty(filePath)) return Constants.INVALID;
        try {
            ExifInterface exifInterface = new ExifInterface(filePath);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270;
                default:
                    return 0;
            }
        } catch (Exception e) {
            LogUtil.log(e);
        }
        return Constants.INVALID;
    }


    /**
     * 倾斜图片
     *
     * @param bitmap 待操作源图片
     * @param kx     X 轴倾斜因子
     * @param ky     Y 轴倾斜因子
     * @return 倾斜后的图片
     */
    public static Bitmap skew(final Bitmap bitmap, final float kx, final float ky) {
        return skew(bitmap, kx, ky, 0, 0, true);
    }

    /**
     * 倾斜图片
     * 倾斜因子 以小数点倾斜 如: 0.1 防止数值过大 Canvas: trying to draw too large
     *
     * @param bitmap 待操作源图片
     * @param kx     X 轴倾斜因子
     * @param ky     Y 轴倾斜因子
     * @param px     X 轴轴心点
     * @param py     Y 轴轴心点
     * @return 倾斜后的图片
     */
    public static Bitmap skew(@Nullable final Bitmap bitmap, final float kx, final float ky, final float px, final float py) {
        return skew(bitmap, kx, ky, px, py, true);
    }


    /**
     * 倾斜图片
     * 倾斜因子 以小数点倾斜 如: 0.1 防止数值过大 Canvas: trying to draw too large
     *
     * @param bitmap  待操作源图片
     * @param kx      X 轴倾斜因子
     * @param ky      Y 轴倾斜因子
     * @param px      X 轴轴心点
     * @param py      Y 轴轴心点
     * @param recycle 是否回收
     * @return 倾斜后的图片
     */
    @Nullable
    public static Bitmap skew(@Nullable final Bitmap bitmap, final float kx, final float ky, final float px, final float py,
                              final boolean recycle) {
        if (isEmpty(bitmap)) return null;
        Matrix matrix = new Matrix();
        matrix.setSkew(kx, ky, px, py);
        Bitmap ret = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        if (recycle && !bitmap.isRecycled()) bitmap.recycle();
        return ret;
    }

    /**
     * 裁剪图片 ( 返回指定比例图片 )
     *
     * @param bitmap 待操作源图片
     * @return 裁剪指定比例的图片
     */
    public static Bitmap crop(@Nullable final Bitmap bitmap) {
        return crop(bitmap, 16.0f, 9.0f);
    }

    /**
     * 裁剪图片 ( 返回指定比例图片 )
     *
     * @param bitmap      待操作源图片
     * @param widthScale  宽度比例
     * @param heightScale 高度比例
     * @return 裁剪指定比例的图片
     */
    public static Bitmap crop(@Nullable final Bitmap bitmap, final float widthScale, final float heightScale) {
        if (isEmpty(bitmap)) return null;
        try {
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            // 获取需要裁剪的高度
            int reHeight = (int) ((width * heightScale) / widthScale);
            // 判断需要裁剪的高度与偏移差距
            int diffHeight = height - reHeight;

            // 属于宽度 * 对应比例 >= 高度
            if (diffHeight >= 0) { // 以高度做偏移
                return Bitmap.createBitmap(bitmap, 0, diffHeight / 2, width, reHeight, null, false);
            } else { // 以宽度做偏移
                // 获取需要裁剪的宽度
                int reWidth = (int) ((height * widthScale) / heightScale);
                // 判断需要裁剪的宽度与偏移差距
                int diffWidth = width - reWidth;
                // 创建图片
                return Bitmap.createBitmap(bitmap, diffWidth / 2, 0, reWidth, height, null, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 裁剪图片
     *
     * @param bitmap 待操作源图片
     * @param x      X 轴开始坐标
     * @param y      Y 轴开始坐标
     * @param width  裁剪宽度
     * @param height 裁剪高度
     * @return 裁剪后的图片
     */
    @Nullable
    public static Bitmap crop(@Nullable final Bitmap bitmap, final int x, final int y, final int width, final int height) {
        return crop(bitmap, x, y, width, height, false);
    }

    /**
     * 裁剪图片
     *
     * @param bitmap  待操作源图片
     * @param x       X 轴开始坐标
     * @param y       Y 轴开始坐标
     * @param width   裁剪宽度
     * @param height  裁剪高度
     * @param recycle 是否回收
     * @return 裁剪后的图片
     */
    @Nullable
    public static Bitmap crop(@Nullable final Bitmap bitmap, final int x, final int y,
                              final int width, final int height, final boolean recycle) {
        if (isEmpty(bitmap)) return null;
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, x, y, width, height);
        if (recycle && !bitmap.isRecycled()) bitmap.recycle();
        return newBitmap;
    }

    /**
     * 复制 Bitmap
     *
     * @param bitmap
     * @return {@link Bitmap}
     */
    public static Bitmap copy(@Nullable Bitmap bitmap) {
        return copy(bitmap, false);
    }

    /**
     * 复制 Bitmap
     *
     * @param bitmap    {@link Bitmap}
     * @param isMutable 是否允许编辑
     * @return {@link Bitmap}
     */
    private static Bitmap copy(Bitmap bitmap, boolean isMutable) {
        if (isEmpty(bitmap)) return null;
        return bitmap.copy(bitmap.getConfig(), isMutable);
    }

    /**
     * 合并图片
     *
     * @param bgBitmap   后景 Bitmap
     * @param showBitmap 前景 Bitmap
     * @return 合并后的图片
     */
    public static Bitmap combine(@Nullable final Bitmap bgBitmap, @Nullable final Bitmap showBitmap) {
        return combine(bgBitmap, showBitmap, PorterDuff.Mode.SRC_ATOP, null, null);
    }

    /**
     * 合并图片
     *
     * @param bgBitmap   后景 Bitmap
     * @param showBitmap 前景 Bitmap
     * @param mode       合并模式 {@link PorterDuff.Mode}
     * @return 合并后的图片
     */
    public static Bitmap combine(@Nullable final Bitmap bgBitmap, @Nullable final Bitmap showBitmap, @Nullable final PorterDuff.Mode mode) {
        return combine(bgBitmap, showBitmap, mode, null, null);
    }

    /**
     * 合并图片
     *
     * @param bgBitmap   后景 Bitmap
     * @param showBitmap 前景 Bitmap
     * @param mode       合并模式 {@link PorterDuff.Mode}
     * @param bgdPoint   后景绘制 left、top 坐标
     * @param fgPoint    前景绘制 left、top 坐标
     * @return 合并后的图片
     */
    public static Bitmap combine(@Nullable final Bitmap bgBitmap, @Nullable final Bitmap showBitmap,
                                 @Nullable final PorterDuff.Mode mode, @Nullable final Point bgdPoint,
                                 @Nullable final Point fgPoint) {
        if (isEmpty(bgBitmap) || isEmpty(showBitmap)) return null;

        int width = bgBitmap.getWidth() > showBitmap.getWidth() ? bgBitmap.getWidth() : showBitmap.getWidth();
        int height = bgBitmap.getHeight() > showBitmap.getHeight() ? bgBitmap.getHeight() : showBitmap.getHeight();

        Paint paint = new Paint();
        if (mode != null) paint.setXfermode(new PorterDuffXfermode(mode));

        Bitmap newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawBitmap(bgBitmap, (bgdPoint != null) ? bgdPoint.x : 0, (bgdPoint != null) ? bgdPoint.y : 0, null);
        canvas.drawBitmap(showBitmap, (fgPoint != null) ? fgPoint.x : 0, (fgPoint != null) ? fgPoint.y : 0, paint);
        return newBitmap;
    }

    /**
     * 合并图片 ( 居中 )
     *
     * @param bgBitmap   后景 Bitmap
     * @param showBitmap 前景 Bitmap
     * @return 合并后的图片
     */
    public static Bitmap combineToCenter(@Nullable final Bitmap bgBitmap, @Nullable final Bitmap showBitmap) {
        return combineToCenter(bgBitmap, showBitmap, null);
    }

    /**
     * 合并图片 ( 居中 )
     *
     * @param bgBitmap   后景 Bitmap
     * @param showBitmap 前景 Bitmap
     * @param mode       合并模式 {@link PorterDuff.Mode}
     * @return 合并后的图片
     */
    public static Bitmap combineToCenter(@Nullable Bitmap bgBitmap, @Nullable Bitmap showBitmap, @Nullable final PorterDuff.Mode mode) {
        if (isEmpty(bgBitmap) || isEmpty(showBitmap)) return null;

        // 绘制坐标点
        Point bgdPoint = new Point();
        Point fgPoint = new Point();

        // 宽高信息
        int bgdWidth = bgBitmap.getWidth();
        int bgdHeight = bgBitmap.getHeight();

        int fgWidth = showBitmap.getWidth();
        int fgHeight = showBitmap.getHeight();

        if (bgdWidth > fgWidth) fgPoint.x = (bgdWidth - fgWidth) / 2;
        else bgdPoint.x = (fgWidth - bgdWidth) / 2;

        if (bgdHeight > fgHeight) fgPoint.y = (bgdHeight - fgHeight) / 2;
        else bgdPoint.y = (fgHeight - bgdHeight) / 2;

        return combine(bgBitmap, showBitmap, mode, bgdPoint, fgPoint);
    }

    /**
     * 合并图片 ( 转为相同大小 )
     *
     * @param bgBitmap   后景 Bitmap
     * @param showBitmap 前景 Bitmap
     * @return 合并后的图片
     */
    public static Bitmap combineToSameSize(@Nullable Bitmap bgBitmap, @Nullable Bitmap showBitmap) {
        return combineToSameSize(bgBitmap, showBitmap, PorterDuff.Mode.SRC_ATOP);
    }

    /**
     * 合并图片 ( 转为相同大小 )
     *
     * @param bgBitmap   后景 Bitmap
     * @param showBitmap 前景 Bitmap
     * @param mode       合并模式 {@link PorterDuff.Mode}
     * @return 合并后的图片
     */
    public static Bitmap combineToSameSize(@Nullable Bitmap bgBitmap, @Nullable Bitmap showBitmap, @Nullable final PorterDuff.Mode mode) {
        if (isEmpty(bgBitmap) || isEmpty(showBitmap)) return null;

        int width = bgBitmap.getWidth() < showBitmap.getWidth() ? bgBitmap.getWidth() : showBitmap.getWidth();
        int height = bgBitmap.getHeight() < showBitmap.getHeight() ? bgBitmap.getHeight() : showBitmap.getHeight();

        if (showBitmap.getWidth() != width && showBitmap.getHeight() != height) {
            showBitmap = scale(showBitmap, width, height);
        }

        if (bgBitmap.getWidth() != width && bgBitmap.getHeight() != height) {
            bgBitmap = scale(bgBitmap, width, height);
        }

        Paint paint = new Paint();
        if (mode != null) paint.setXfermode(new PorterDuffXfermode(mode));

        Bitmap newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawBitmap(bgBitmap, 0, 0, null);
        canvas.drawBitmap(showBitmap, 0, 0, paint);
        return newBitmap;
    }

    /**
     * 图片倒影处理
     *
     * @param bitmap 待操作源图片
     * @return 倒影处理后的图片
     */
    public static Bitmap addReflection(final Bitmap bitmap) {
        if (isEmpty(bitmap)) return null;
        return addReflection(bitmap, 0, bitmap.getHeight(), false);
    }

    /**
     * 图片倒影处理
     *
     * @param bitmap           待操作源图片
     * @param reflectionHeight 倒影高度
     * @return 倒影处理后的图片
     */
    public static Bitmap addReflection(final Bitmap bitmap, final int reflectionHeight) {
        return addReflection(bitmap, 0, reflectionHeight, false);
    }

    /**
     * 图片倒影处理
     *
     * @param bitmap            待操作源图片
     * @param reflectionSpacing 源图片与倒影之间的间距
     * @param reflectionHeight  倒影高度
     * @return 倒影处理后的图片
     */
    public static Bitmap addReflection(final Bitmap bitmap, final int reflectionSpacing, final int reflectionHeight, boolean recycle) {
        if (isEmpty(bitmap)) return null;
        if (reflectionHeight <= 0) return null;
        // 获取图片宽高
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // 创建画布, 画布分为上中下三部分, 上: 是源图片, 中: 是源图片与倒影的间距, 下: 是倒影

        // 创建倒影图片
        Bitmap reflectionImage = reverseByVertical(bitmap); // 垂直翻转图片 ( 上下颠倒 )
        // 创建一张宽度与源图片相同, 但高度等于 源图片的高度 + 间距 + 倒影的高度的图片
        Bitmap bitmapWithReflection = Bitmap.createBitmap(width, height + reflectionSpacing + reflectionHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapWithReflection);

        // 将源图片画到画布的上半部分, 将倒影画到画布的下半部分, 倒影与画布顶部的间距是源图片的高度加上源图片与倒影之间的间距
        canvas.drawBitmap(bitmap, 0, 0, null);
        canvas.drawBitmap(reflectionImage, 0, height + reflectionSpacing, null);
        reflectionImage.recycle();

        // 边距负数处理
        int spacing = Math.max(reflectionSpacing, 0);

        // 将倒影改成半透明, 创建画笔, 并设置画笔的渐变从半透明的白色到全透明的白色, 然后再倒影上面画半透明效果
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setShader(new LinearGradient(0, bitmap.getHeight(), 0, bitmapWithReflection.getHeight() + spacing,
                0x70FFFFFF, 0x00FFFFFF, Shader.TileMode.CLAMP));
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawRect(0, height + spacing, width, bitmapWithReflection.getHeight() + spacing, paint);
        if (recycle) recycle(bitmap);
        return bitmapWithReflection;
    }

    /**
     * 添加文字水印
     *
     * @param bitmap   待操作源图片
     * @param content  水印文本
     * @param textSize 水印字体大小 pixel
     * @param color    水印字体颜色
     * @param x        起始坐标 x
     * @param y        起始坐标 y
     * @return 添加文字水印后的图片
     */
    public static Bitmap addTextWatermark(@Nullable final Bitmap bitmap, @Nullable final String content, final float textSize,
                                          @ColorInt final int color, final float x, final float y) {
        return addTextWatermark(bitmap, content, textSize, color, x, y, true);
    }

    /**
     * 添加文字水印
     *
     * @param bitmap   待操作源图片
     * @param content  水印文本
     * @param textSize 水印字体大小 pixel
     * @param color    水印字体颜色
     * @param x        起始坐标 x
     * @param y        起始坐标 y
     * @param recycle  是否回收
     * @return 添加文字水印后的图片
     */
    public static Bitmap addTextWatermark(@Nullable final Bitmap bitmap, @Nullable final String content, final float textSize,
                                          @ColorInt final int color, final float x, final float y, boolean recycle) {
        if (isEmpty(bitmap) || StringUtil.isEmpty(content)) return null;
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(color);
        paint.setTextSize(textSize);
        Rect bounds = new Rect();
        paint.getTextBounds(content, 0, content.length(), bounds);

        Bitmap newBitmap = bitmap.copy(bitmap.getConfig(), true);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawText(content, x, y + textSize, paint);
        if (recycle && !bitmap.isRecycled()) bitmap.recycle();
        return newBitmap;
    }

    /**
     * 添加图片水印
     *
     * @param bitmap    待操作源图片
     * @param watermark 水印图片
     * @param x         起始坐标 x
     * @param y         起始坐标 y
     * @param alpha     透明度
     * @return 添加图片水印后的图片
     */
    public static Bitmap addImageWatermark(final Bitmap bitmap, final Bitmap watermark, final int x, final int y,
                                           @IntRange(from = 0, to = 255) final int alpha) {
        if (isEmpty(bitmap)) return null;
        Bitmap newBitmap = bitmap.copy(bitmap.getConfig(), true);
        if (!isEmpty(watermark)) {
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            Canvas canvas = new Canvas(newBitmap);
            paint.setAlpha(alpha);
            canvas.drawBitmap(watermark, x, y, paint);
        }
        return newBitmap;
    }

    /**
     * 添加图片水印
     *
     * @param bitmap    待操作源图片
     * @param watermark 水印图片
     * @param x         起始坐标 x
     * @param y         起始坐标 y
     * @param alpha     透明度
     * @param recycle   是否回收
     * @return 添加图片水印后的图片
     */
    public static Bitmap addImageWatermark(final Bitmap bitmap, final Bitmap watermark, final int x, final int y,
                                           @IntRange(from = 0, to = 255) final int alpha, boolean recycle) {
        if (isEmpty(bitmap)) return null;
        Bitmap newBitmap = bitmap.copy(bitmap.getConfig(), true);
        if (!isEmpty(watermark)) {
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            Canvas canvas = new Canvas(newBitmap);
            paint.setAlpha(alpha);
            canvas.drawBitmap(watermark, x, y, paint);
        }
        if (recycle && !bitmap.isRecycled()) bitmap.recycle();
        return newBitmap;
    }

    /**
     * 图片圆角处理 ( 非圆形 )
     * 以宽高中最小值设置为圆角尺寸, 如果宽高一致, 则处理为圆形图片
     *
     * @param bitmap 待操作源图片
     * @return 圆角处理后的图片
     */
    @Nullable
    public static Bitmap setRound(@Nullable final Bitmap bitmap, int borderSize) {
        return setRound(bitmap, borderSize, Color.TRANSPARENT, false);
    }

    /**
     * 图片圆角处理 ( 非圆形 )
     * 以宽高中最小值设置为圆角尺寸, 如果宽高一致, 则处理为圆形图片
     *
     * @param bitmap      待操作源图片
     * @param borderSize  圆角大小
     * @param borderColor 圆角颜色
     * @return 圆角处理后的图片
     */
    @Nullable
    public static Bitmap setRound(@Nullable final Bitmap bitmap, @FloatRange(from = 0) final float borderSize,
                                  @ColorInt final int borderColor) {
        return setRound(bitmap, borderSize, borderColor, false);
    }

    /**
     * 图片圆角处理 ( 非圆形 )
     * 以宽高中最小值设置为圆角尺寸, 如果宽高一致, 则处理为圆形图片
     *
     * @param bitmap      待操作源图片
     * @param borderSize  圆角大小
     * @param borderColor 圆角颜色
     * @param recycle     是否回收
     * @return 圆角处理后的图片
     */
    @Nullable
    public static Bitmap setRound(@Nullable final Bitmap bitmap, @FloatRange(from = 0) final float borderSize,
                                  @ColorInt final int borderColor, final boolean recycle) {
        if (isEmpty(bitmap)) return null;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int size = Math.min(width, height);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Bitmap ret = Bitmap.createBitmap(width, height, bitmap.getConfig());
        float center = size / 2f;
        RectF rectF = new RectF(0, 0, width, height);
        rectF.inset((width - size) / 2f, (height - size) / 2f);
        Matrix matrix = new Matrix();
        matrix.setTranslate(rectF.left, rectF.top);
        matrix.preScale((float) size / width, (float) size / height);
        BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        shader.setLocalMatrix(matrix);
        paint.setShader(shader);
        Canvas canvas = new Canvas(ret);
        canvas.drawRoundRect(rectF, center, center, paint);
        if (borderSize > 0) {
            paint.setShader(null);
            paint.setColor(borderColor);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(borderSize);
            float radius = center - borderSize / 2f;
            paint.setStrokeCap(Paint.Cap.ROUND);
            canvas.drawCircle(width / 2f, height / 2f, radius, paint);
        }
        if (recycle && !bitmap.isRecycled()) bitmap.recycle();
        return ret;
    }

    /**
     * 图片圆角处理 ( 非圆形 ) - 只有 leftTop、rightTop
     *
     * @param bitmap    待操作源图片
     * @param roundSize 圆角大小
     * @return 圆角处理后的图片
     */
    public static Bitmap setRoundTop(final Bitmap bitmap, final float roundSize) {
        return setRound(bitmap, roundSize, Color.TRANSPARENT, new boolean[]{true, true, true, false});
    }

    public static Bitmap setRoundTop(final Bitmap bitmap, final float roundSize, @ColorInt int roundColor) {
        return setRound(bitmap, roundSize, roundColor, new boolean[]{true, true, true, false});
    }

    /**
     * 图片圆角处理 ( 非圆形 ) - 只有 leftBottom、rightBottom
     *
     * @param bitmap    待操作源图片
     * @param roundSize 圆角大小
     * @return 圆角处理后的图片
     */
    public static Bitmap setRoundBottom(final Bitmap bitmap, final float roundSize) {
        return setRound(bitmap, roundSize, Color.TRANSPARENT, new boolean[]{true, false, true, true});
    }

    public static Bitmap setRoundBottom(final Bitmap bitmap, final float pixels, @ColorInt int roundColor) {
        return setRound(bitmap, pixels, roundColor, new boolean[]{true, false, true, true});
    }

    /**
     * 图片圆角处理 ( 非圆形 )
     * 只要左上圆角: new boolean[] {true, true, false, false};
     * 只要右上圆角: new boolean[] {false, true, true, false};
     * 只要左下圆角: new boolean[] {true, false, false, true};
     * 只要右下圆角: new boolean[] {false, false, true, true};
     *
     * @param bitmap     待操作源图片
     * @param roundSize  圆角大小
     * @param roundColor 圆角大小
     * @param directions 需要圆角的方向 [left, top, right, bottom]
     * @return 圆角处理后的图片
     */
    public static Bitmap setRound(final Bitmap bitmap, final float roundSize, @ColorInt int roundColor, final boolean[] directions) {
        if (isEmpty(bitmap)) return null;
        if (directions == null || directions.length != 4) return null;
        // 创建一个同源图片一样大小的矩形, 用于把源图片绘制到这个矩形上
        Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        RectF rectF = new RectF(rect); // 创建一个精度更高的矩形, 用于画出圆角效果
        //圆角方向控制
        if (!directions[0]) rectF.left -= roundSize;

        if (!directions[1]) rectF.top -= roundSize;

        if (!directions[2]) rectF.right += roundSize;

        if (!directions[3]) rectF.bottom += roundSize;

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(roundColor); // 设置画笔的颜色为不透明的灰色

        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(rectF, roundSize, roundSize, paint);
        // 绘制底圆后, 进行合并 ( 交集处理 )
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return newBitmap;
    }


    /**
     * 圆形图片
     *
     * @param bitmap
     * @return
     */
    @Nullable
    public static Bitmap getCircleBitmap(@Nullable final Bitmap bitmap) {
        if (isEmpty(bitmap)) return null;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx, left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height) {
            roundPx = width / 2;
            top = 0;
            bottom = width;
            left = 0;
            right = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xFF424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst);

        paint.setAntiAlias(true);

        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, src, dst, paint);
        return output;
    }

    /**
     * 计算采样率
     *
     * @param options   The options.
     * @param maxWidth  The maximum width.
     * @param maxHeight The maximum height.
     * @return the sample size
     */
    public static int calculateInSampleSize(@NonNull final BitmapFactory.Options options,
                                            @IntRange(from = 0) final int maxWidth,
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

    /**
     * 按缩放宽高压缩
     *
     * @param bitmap    待操作源图片
     * @param newWidth  新宽度
     * @param newHeight 新高度
     * @return 缩放宽高压缩后的图片
     * 通过缩放图片像素来减少图片占用内存大小。
     */
    @Nullable
    public static Bitmap compressByScale(@Nullable final Bitmap bitmap, final int newWidth, final int newHeight) {
        return scale(bitmap, newWidth, newHeight, false);
    }

    /**
     * 按缩放宽高压缩
     *
     * @param bitmap    待操作源图片
     * @param newWidth  新宽度
     * @param newHeight 新高度
     * @param recycle   是否回收
     * @return 缩放宽高压缩后的图片
     */
    @Nullable
    public static Bitmap compressByScale(@Nullable final Bitmap bitmap, final int newWidth, final int newHeight, final boolean recycle) {
        return scale(bitmap, newWidth, newHeight, recycle);
    }

    /**
     * 按缩放比例压缩
     *
     * @param bitmap      待操作源图片
     * @param scaleWidth  横向缩放比例 ( 缩放宽度倍数 )
     * @param scaleHeight 纵向缩放比例 ( 缩放高度倍数 )
     * @return 缩放比例压缩后的图片
     */
    @Nullable
    public static Bitmap compressByScale(@Nullable final Bitmap bitmap, final float scaleWidth, final float scaleHeight) {
        return scale(bitmap, scaleWidth, scaleHeight, false);
    }

    /**
     * 按缩放比例压缩
     *
     * @param bitmap      待操作源图片
     * @param scaleWidth  横向缩放比例 ( 缩放宽度倍数 )
     * @param scaleHeight 纵向缩放比例 ( 缩放高度倍数 )
     * @param recycle     是否回收
     * @return 缩放比例压缩后的图片
     */
    @Nullable
    public static Bitmap compressByScale(@Nullable final Bitmap bitmap, final float scaleWidth, final float scaleHeight, final boolean recycle) {
        return scale(bitmap, scaleWidth, scaleHeight, recycle);
    }

    /**
     * 缩放图片 ( 指定所需宽高 )
     *
     * @param bitmap    待操作源图片
     * @param newWidth  新宽度
     * @param newHeight 新高度
     * @return 缩放后的图片
     */
    @Nullable
    public static Bitmap scale(@Nullable final Bitmap bitmap, final int newWidth, final int newHeight) {
        return scale(bitmap, newWidth, newHeight, false);
    }

    /**
     * 缩放图片 ( 指定所需宽高 )
     *
     * @param bitmap    待操作源图片
     * @param newWidth  新宽度
     * @param newHeight 新高度
     * @param recycle   是否回收
     * @return 缩放后的图片
     */
    @Nullable
    public static Bitmap scale(@Nullable final Bitmap bitmap, final int newWidth, final int newHeight, final boolean recycle) {
        if (isEmpty(bitmap)) return null;
        Bitmap newBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
        if (recycle) recycle(bitmap);
        return newBitmap;
    }

    /**
     * 缩放图片 ( 比例缩放 )
     *
     * @param bitmap 待操作源图片
     * @param scale  缩放倍数
     * @return 缩放后的图片
     */
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
        if (isEmpty(bitmap)) return null;
        Matrix matrix = new Matrix();
        matrix.setScale(scaleX, scaleY);
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        if (recycle) recycle(bitmap);
        return newBitmap;
    }

    /**
     * 按质量压缩图片
     *
     * @param bitmap  被压缩的图片图片
     * @param quality 质量要求 0--100
     * @return 返回压缩的图片
     * <p>
     * 质量压缩不会减少图片的像素。它是在保持像素不变的前提下改变图片的位深及透明度等，来达到压缩图片的目的。
     * 进过它压缩的图片文件大小会有改变，但是导入成bitmap后占得内存是不变的。因为要保持像素不变，所以它就无法无限压缩，
     * 到达一个值之后就不会继续变小了。显然这个方法并不适用于缩略图，其实也不适用于想通过压缩图片减少内存的适用，
     * 仅仅适用于想在保证图片质量的同时减少文件大小的情况而已。
     */
    @Nullable
    public static Bitmap compressByQuality(@Nullable Bitmap bitmap, @IntRange(from = 0, to = 100) final int quality) {
        return compressByQuality(bitmap, Bitmap.CompressFormat.JPEG, quality, null, false);
    }

    /**
     * 按质量压缩图片
     *
     * @param bitmap  被压缩的图片图片
     * @param quality 质量要求 0--100
     * @param options {@link BitmapFactory.Options}
     * @return 返回压缩的图片
     */
    @Nullable
    public static Bitmap compressByQuality(@Nullable final Bitmap bitmap,
                                           @IntRange(from = 0, to = 100) int quality,
                                           @Nullable final BitmapFactory.Options options) {
        return compressByQuality(bitmap, Bitmap.CompressFormat.JPEG, quality, options, false);
    }

    /**
     * 按质量压缩
     *
     * @param bitmap  待操作源图片
     * @param format  图片压缩格式
     * @param quality 质量
     * @param options {@link BitmapFactory.Options}
     * @param recycle 是否回收
     * @return 质量压缩后的图片
     */
    public static Bitmap compressByQuality(@Nullable final Bitmap bitmap, @Nullable final Bitmap.CompressFormat format,
                                           @IntRange(from = 0, to = 100) final int quality,
                                           @Nullable final BitmapFactory.Options options, boolean recycle) {
        if (isEmpty(bitmap) || format == null) return null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        Bitmap newBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        if (recycle && !bitmap.isRecycled()) bitmap.recycle();
        IOUtil.flush(byteArrayOutputStream);
        IOUtil.close(byteArrayOutputStream);
        return newBitmap;
    }

    /**
     * 质量压缩法 像素不会减少
     *
     * @param bitmap  原图
     * @param maxSize 压缩之后的图片大小
     * @param recycle 是否回收
     * @return 质量压缩后的图片
     */
    @Nullable
    public static Bitmap compressByQuality(@Nullable final Bitmap bitmap, long maxSize, boolean recycle) {
        if (isEmpty(bitmap)) return null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        //质量压缩方法，这里100表示不压缩，把压缩后的数据存放到byteArrayOutputStream中
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        int options = 100;
        //循环判断如果压缩后图片是否大于 maxSize kb,大于继续压缩
        while (byteArrayOutputStream.toByteArray().length / 1024 > maxSize) {
            //重置baos即清空baos
            byteArrayOutputStream.reset();
            //这里压缩options%，把压缩后的数据存放到byteArrayOutputStream中
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, byteArrayOutputStream);
            //每次都减少5
            options -= 5;
            if (options <= 5) break;
        }
        byte[] bytes = byteArrayOutputStream.toByteArray();
        IOUtil.flush(byteArrayOutputStream);
        IOUtil.close(byteArrayOutputStream);
        if (recycle && !bitmap.isRecycled()) bitmap.recycle();
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**
     * 按质量压缩图片 递归处理 得到指定大小的图片
     *
     * @param bitmap      被压缩的图片图片
     * @param maxByteSize 图片最大值
     * @return 返回压缩的图片
     */
    @Nullable
    public static Bitmap compressByByteSize(@Nullable final Bitmap bitmap, long maxByteSize) {
        return compressByByteSize(bitmap, Bitmap.CompressFormat.JPEG, maxByteSize, null, false);
    }

    @Nullable
    public static Bitmap compressByByteSize(@Nullable final Bitmap bitmap, @Nullable final BitmapFactory.Options options, long maxByteSize) {
        return compressByByteSize(bitmap, Bitmap.CompressFormat.JPEG, maxByteSize, options, false);
    }

    /**
     * 按质量压缩图片 递归处理 得到指定大小的图片
     *
     * @param bitmap      被压缩的图片图片
     * @param maxByteSize 图片最大值
     * @param recycle     是否回收原图片
     * @return 返回压缩的图片
     */
    @Nullable
    public static Bitmap compressByByteSize(@Nullable final Bitmap bitmap,
                                            @Nullable final Bitmap.CompressFormat format,
                                            long maxByteSize, @Nullable final BitmapFactory.Options options,
                                            boolean recycle) {
        if (isEmpty(bitmap) || maxByteSize <= 0) return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(format, 100, baos);
        byte[] bytes;
        if (baos.size() <= maxByteSize) bytes = baos.toByteArray();
        else {
            baos.reset();
            bitmap.compress(format, 0, baos);
            if (baos.size() >= maxByteSize) bytes = baos.toByteArray();
            else {
                // find the best quality using binary search
                int st = 0, end = 100, mid = 0;
                while (st < end) {// 二分法寻找最佳质量
                    mid = (st + end) / 2;
                    baos.reset();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, mid, baos);
                    int len = baos.size();
                    if (len == maxByteSize) break;
                    else if (len > maxByteSize) end = mid - 1;
                    else st = mid + 1;
                }
                if (end == mid - 1) {
                    baos.reset();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, st, baos);
                }
                bytes = baos.toByteArray();
            }
        }
        Bitmap newBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        if (recycle && !bitmap.isRecycled()) bitmap.recycle();
        IOUtil.flush(baos);
        IOUtil.close(baos);
        return newBitmap;
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
    public static Bitmap compressBySampleSize(@Nullable final Bitmap bitmap, final int sampleSize) {
        return compressBySampleSize(bitmap, sampleSize, false);
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
    public static Bitmap compressBySampleSize(@Nullable final Bitmap bitmap, int sampleSize, boolean recycle) {
        if (isEmpty(bitmap)) return null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        Bitmap newBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        if (recycle && !bitmap.isRecycled()) bitmap.recycle();
        IOUtil.flush(byteArrayOutputStream);
        IOUtil.close(byteArrayOutputStream);
        return newBitmap;
    }

    /**
     * 按采样大小压缩
     *
     * @param bitmap    待操作源图片
     * @param maxWidth  最大宽度
     * @param maxHeight 最大高度
     * @return 按采样率压缩后的图片
     */
    public static Bitmap compressBySampleSize(@Nullable final Bitmap bitmap, final int maxWidth, final int maxHeight) {
        return compressBySampleSize(bitmap, Bitmap.CompressFormat.JPEG, maxWidth, maxHeight, false);
    }

    /**
     * 按采样大小压缩
     *
     * @param bitmap    待操作源图片
     * @param format    图片压缩格式
     * @param maxWidth  最大宽度
     * @param maxHeight 最大高度
     * @return 按采样率压缩后的图片
     */
    public static Bitmap compressBySampleSize(@Nullable final Bitmap bitmap, Bitmap.CompressFormat format, final int maxWidth, final int maxHeight,
                                              final boolean recycle) {
        if (isEmpty(bitmap)) return null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        options.inJustDecodeBounds = true;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(format, 100, baos);
        byte[] bytes = baos.toByteArray();
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight);
        options.inJustDecodeBounds = false;
        Bitmap newBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        if (recycle && !bitmap.isRecycled()) bitmap.recycle();
        IOUtil.flush(baos);
        IOUtil.close(baos);
        return newBitmap;
    }

    public static void recycle(@Nullable Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) bitmap.recycle();
    }

    /**
     * 安全的创建bitmap。
     * 如果新建 Bitmap 时产生了 OOM，可以主动进行一次 GC - System.gc()，然后再次尝试创建。
     *
     * @param width      Bitmap 宽度。
     * @param height     Bitmap 高度。
     * @param config     传入一个 Bitmap.Config。
     * @param retryCount 创建 Bitmap 时产生 OOM 后，主动重试的次数。
     * @return 返回创建的 Bitmap。
     */
    public static Bitmap createBitmapSafely(int width, int height, Bitmap.Config config, int retryCount) {
        try {
            return Bitmap.createBitmap(width, height, config);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            if (retryCount > 0) {
                System.gc();
                return createBitmapSafely(width, height, config, retryCount - 1);
            }
            return null;
        }
    }

}
