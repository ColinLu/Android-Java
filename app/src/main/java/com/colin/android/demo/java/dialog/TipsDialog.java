package com.colin.android.demo.java.dialog;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.android.demo.java.app.AppDialog;
import com.colin.android.demo.java.databinding.DialogAppBinding;
import com.colin.android.demo.java.def.bean.ContactBean;
import com.colin.library.android.utils.StringUtil;

/**
 * 作者： ColinLu
 * 时间： 2022-01-30 13:57
 * <p>
 * 描述： TODO
 */
public final class TipsDialog extends AppDialog<DialogAppBinding> {
    private ContactBean mContactBean;
    private String mTips;

    @Override
    public void initView(@Nullable Bundle bundle) {

    }

    @Override
    public void initData(@Nullable Bundle bundle) {
        if (mContactBean != null) {
            contactToView(mContactBean);
        }
        if (!StringUtil.isEmpty(mTips)) {
            contactToView(mTips);
        }
    }

    public void setTips(@NonNull String tips) {
        if (!tips.equals(mTips)) contactToView(tips);
        this.mTips = tips;
    }

    public void setContact(@NonNull ContactBean bean) {
        if (!bean.equals(mContactBean)) contactToView(bean);
        this.mContactBean = bean;
    }


    private void contactToView(@NonNull ContactBean bean) {
        if (mBinding == null) return;
        if (TextUtils.isEmpty(bean.photo)) {
            mBinding.imageDialog.setImageURI(null);
        } else {
            mBinding.imageDialog.setImageURI(Uri.parse(bean.photo));
        }
        mBinding.textDialogMessage.setText(bean.toString());
    }


    private void contactToView(@NonNull String tips) {
        if (mBinding == null) return;
        mBinding.textDialogMessage.setText(tips);
    }
}
