package com.colin.android.demo.java.dialog;

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

    @Override
    public void initView(@Nullable Bundle bundle) {

    }

    @Override
    public void initData(@Nullable Bundle bundle) {
        if (mBinding == null) return;
        final Bitmap bitmap = mBitmapRef == null ? null : mBitmapRef.get();
        if (!BitmapUtil.isEmpty(bitmap)) mBinding.imageDialog.setImageBitmap(bitmap);
    }

    public void setImage(@NonNull Bitmap bitmap) {
        mBitmapRef = new WeakReference<>(bitmap);
        initData(null);
    }


}
