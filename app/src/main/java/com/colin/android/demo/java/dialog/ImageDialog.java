package com.colin.android.demo.java.dialog;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.android.demo.java.app.AppDialog;
import com.colin.android.demo.java.databinding.DialogImageBinding;
import com.colin.library.android.utils.BitmapUtil;

import java.lang.ref.WeakReference;

/**
 * 作者： ColinLu
 * 时间： 2022-01-30 13:57
 * <p>
 * 描述： TODO
 */
public final class ImageDialog extends AppDialog<DialogImageBinding> {
    private WeakReference<Bitmap> mBitmapRef;
    private String mImage;
    private int mRes = Resources.ID_NULL;

    public void setImage(@NonNull Bitmap bitmap) {
        mBitmapRef = new WeakReference<>(bitmap);
        initData(null);
    }

    public void setImageRes(int res) {
        this.mRes = res;
        initData(null);
    }

    @Override
    public void initView(@Nullable Bundle bundle) {
        mBinding.imageDialog.setOnLongClickListener(v -> {
            final Bitmap bitmap = mBitmapRef == null ? null : mBitmapRef.get();
            if (!BitmapUtil.isEmpty(bitmap)) {

                return true;
            }
            if (mRes != Resources.ID_NULL) {

                return true;
            }
            return false;
        });
    }

    @Override
    public void initData(@Nullable Bundle bundle) {
        if (mBinding == null) return;
        final Bitmap bitmap = mBitmapRef == null ? null : mBitmapRef.get();
        if (!BitmapUtil.isEmpty(bitmap)) mBinding.imageDialog.setImageBitmap(bitmap);
        if (mRes != Resources.ID_NULL) mBinding.imageDialog.setImageResource(mRes);
    }
}
