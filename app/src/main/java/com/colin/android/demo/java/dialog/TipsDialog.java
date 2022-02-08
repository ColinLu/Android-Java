package com.colin.android.demo.java.dialog;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.android.demo.java.app.AppDialog;
import com.colin.android.demo.java.databinding.DialogAppBinding;
import com.colin.android.demo.java.def.bean.ContactBean;

/**
 * 作者： ColinLu
 * 时间： 2022-01-30 13:57
 * <p>
 * 描述： TODO
 */
public final class TipsDialog extends AppDialog<DialogAppBinding> {
    private ContactBean mContactBean;

    @Override
    public void initView(@Nullable Bundle bundle) {

    }

    @Override
    public void initData(@Nullable Bundle bundle) {
        if (mContactBean != null) {
            contactToView(mContactBean);
        }
    }

    public void setContact(@NonNull ContactBean bean) {
        if (mBinding != null && !bean.equals(mContactBean)) {
            contactToView(bean);
        }
        this.mContactBean = bean;
    }


    private void contactToView(@NonNull ContactBean bean) {
        if (TextUtils.isEmpty(bean.photo)) {
            mBinding.imageDialog.setImageURI(null);
        } else {
            mBinding.imageDialog.setImageURI(Uri.parse(bean.photo));
        }
        mBinding.textDialogMessage.setText(bean.toString());
    }
}
