package com.colin.library.android.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.colin.library.android.utils.data.Constants;


/**
 * 作者： ColinLu
 * 时间： 2019-12-29 12:53
 * <p>
 * 描述： 剪切板工具类
 */
public class ClipboardUtil {
    private ClipboardUtil() {
        throw new UnsupportedOperationException("don't instantiate");
    }

    /**
     * 复制Text 文本内容到剪切板
     *
     * @param text
     */
    public static void copy(@Nullable final CharSequence text) {
        if (TextUtils.isEmpty(text)) return;
        final ClipboardManager manager = AppUtil.getClipboardManager();
        if (null == manager) return;
        manager.setPrimaryClip(ClipData.newPlainText(Constants.CLIP_LABEL_TEXT, text));
    }

    /**
     * 复制uri到剪贴板
     *
     * @param uri uri
     */
    public static void copy(@Nullable final Uri uri) {
        if (null == uri) return;
        final ClipboardManager manager = AppUtil.getClipboardManager();
        final ContentResolver contentResolver = AppUtil.getContentResolver();
        if (null == manager || null == contentResolver) return;
        manager.setPrimaryClip(ClipData.newUri(contentResolver, Constants.CLIP_LABEL_URI, uri));
    }

    /**
     * 复制意图到剪贴板
     *
     * @param intent 意图
     */
    public static void copy(@Nullable final Intent intent) {
        if (null == intent) return;
        final ClipboardManager manager = AppUtil.getClipboardManager();
        if (manager != null) {
            manager.setPrimaryClip(ClipData.newIntent(Constants.CLIP_LABEL_INTENT, intent));
        }
    }

    /**
     * 获取剪贴板的文本
     *
     * @param position 默认0
     * @return 剪贴板的文本
     */
    @Nullable
    public static ClipData.Item getItem(int position) {
        ClipboardManager clipboardManager = AppUtil.getClipboardManager();
        ClipData clip = null == clipboardManager ? null : clipboardManager.getPrimaryClip();
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
