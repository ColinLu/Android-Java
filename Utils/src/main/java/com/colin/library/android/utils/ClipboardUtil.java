package com.colin.library.android.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.library.android.utils.data.Constants;


/**
 * 作者： ColinLu
 * 时间： 2019-12-29 12:53
 * <p>
 * 描述： 剪切板工具类
 */
public final class ClipboardUtil {
    private ClipboardUtil() {
        throw new UnsupportedOperationException("don't instantiate");
    }

    /**
     * 复制Text 文本内容到剪切板
     *
     * @param text
     */
    public static void copy(@Nullable final CharSequence text) {
        if (StringUtil.isEmpty(text)) return;
        setClipData(ClipData.newPlainText(Constants.CLIP_LABEL_TEXT, text));
    }

    /**
     * 复制uri到剪贴板
     *
     * @param uri uri
     */
    public static void copy(@Nullable final Uri uri) {
        final ContentResolver resolver = AppUtil.getContentResolver();
        if (uri == null || resolver == null) return;
        setClipData(ClipData.newUri(resolver, Constants.CLIP_LABEL_URI, uri));
    }

    /**
     * 复制意图到剪贴板
     *
     * @param intent 意图
     */
    public static void copy(@Nullable final Intent intent) {
        if (null == intent) return;
        setClipData(ClipData.newIntent(Constants.CLIP_LABEL_INTENT, intent));
    }

    public static void setClipData(@NonNull final ClipData clip) {
        final ClipboardManager manager = AppUtil.getClipboardManager();
        if (manager != null) manager.setPrimaryClip(clip);
    }

    /**
     * 获取剪贴板的文本
     *
     * @param position 默认0
     * @return 剪贴板的文本
     */
    @Nullable
    public static ClipData.Item getItem(int position) {
        final ClipboardManager manager = AppUtil.getClipboardManager();
        final ClipData clip = null == manager ? null : manager.getPrimaryClip();
        if (null == clip || clip.getItemCount() <= position) return null;
        return clip.getItemAt(position);
    }

    /**
     * 获取剪贴板的文本
     *
     * @return 剪贴板的文本
     */
    @Nullable
    public static CharSequence getText() {
        final ClipData.Item item = getItem(0);
        return null == item ? null : item.getText();
    }


    /**
     * 获取剪贴板的uri
     *
     * @return 剪贴板的uri
     */
    @Nullable
    public static Uri getUri() {
        final ClipData.Item item = getItem(0);
        return null == item ? null : item.getUri();
    }


    /**
     * 获取剪贴板的意图
     *
     * @return 剪贴板的意图
     */
    @Nullable
    public static Intent getIntent() {
        final ClipData.Item item = getItem(0);
        return null == item ? null : item.getIntent();
    }
}
