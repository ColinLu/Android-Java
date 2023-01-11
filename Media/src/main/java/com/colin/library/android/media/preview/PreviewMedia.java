package com.colin.library.android.media.preview;

import android.net.Uri;

import androidx.annotation.Nullable;

import com.colin.library.android.media.def.PreviewFile;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者： ColinLu
 * 时间： 2023-01-07 03:05
 * <p>
 * 描述： TODO
 */
public class PreviewMedia implements IPreviewMedia<PreviewMedia> {
    protected final List<PreviewFile> mList;

    public PreviewMedia() {
        mList = new ArrayList<>();
    }

    @Override
    public PreviewMedia uri(@Nullable Uri uri) {
        return null;
    }

    @Override
    public PreviewMedia url(@Nullable String url) {
        return null;
    }

    @Override
    public PreviewMedia raw(int res) {
        return null;
    }

    @Override
    public PreviewMedia drawable(int res) {
        return null;
    }
}
