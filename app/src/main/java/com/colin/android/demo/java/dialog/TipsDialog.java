package com.colin.android.demo.java.dialog;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.android.demo.java.app.AppDialog;
import com.colin.android.demo.java.databinding.DialogTipsBinding;
import com.colin.library.android.utils.StringUtil;

/**
 * 作者： ColinLu
 * 时间： 2022-01-30 13:57
 * <p>
 * 描述： TODO
 */
public final class TipsDialog extends AppDialog<DialogTipsBinding> {
    private String mTitle;
    private String mTips;


    @Override
    public void initData(@Nullable Bundle bundle) {
        if (mBinding == null) return;
        if (!StringUtil.isEmpty(mTitle)) {
            mBinding.textDialogTitle.setText(mTitle);
            mBinding.textDialogTitle.setVisibility(View.VISIBLE);
        } else mBinding.textDialogTitle.setVisibility(View.GONE);
        mBinding.textDialogMessage.setText(mTips);
    }

    public void setTips(@NonNull String tips) {
        if (!tips.equals(mTips)) {
            this.mTips = tips;
            initData(null);
        }
    }

    public void setTips(@NonNull String title, @NonNull String tips) {
        if (!tips.equals(mTitle) || !tips.equals(mTips)) {
            this.mTitle = title;
            this.mTips = tips;
            initData(null);
        }
    }
}
