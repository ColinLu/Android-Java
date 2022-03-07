package com.colin.android.demo.java.dialog;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.android.demo.java.app.AppDialog;
import com.colin.android.demo.java.databinding.DialogPathBinding;

/**
 * 作者： ColinLu
 * 时间： 2022-01-30 13:57
 * <p>
 * 描述： TODO
 */
public final class PathDialog extends AppDialog<DialogPathBinding> {
    private String mTitle;
    private String mPath;

    @Override
    public void initView(@Nullable Bundle bundle) {

    }

    @Override
    public void initData(@Nullable Bundle bundle) {
        if (mTitle != null && mPath != null) {
            setPathToView(mTitle, mPath);
        }
    }

    public void setPath(@NonNull String title, @NonNull String path) {
        if (mBinding != null && !title.equals(mTitle) && !path.equals(mPath)) {
            setPathToView(title, path);
        }
        this.mTitle = title;
        this.mPath = path;
    }

    private void setPathToView(@NonNull String title, @NonNull String path) {
        mBinding.textDialogTitle.setText(title);
        mBinding.textDialogMessage.setText(path);
    }

}
