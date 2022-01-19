package com.colin.android.demo.java.ui.method;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.colin.android.demo.java.app.AppFragment;
import com.colin.android.demo.java.databinding.FragmentLogBinding;
import com.colin.android.demo.java.databinding.FragmentMotionBinding;
import com.colin.library.android.utils.LogUtil;

/**
 * 作者： ColinLu
 * 时间： 2022-01-18 22:33
 * <p>
 * 描述： TODO
 */
public class LogFragment extends AppFragment<FragmentLogBinding> {
    public static final String TAG = "LogFragment";

    @Override
    public void initView(@Nullable Bundle bundle) {
        mBinding.mButtonLogI.setOnClickListener(v -> LogUtil.i(TAG, "log i", "log i"));
        mBinding.mButtonLogV.setOnClickListener(v -> LogUtil.v(TAG, "log v", "log v"));
        mBinding.mButtonLogW.setOnClickListener(v -> LogUtil.w(TAG, "log w", "log w"));
        mBinding.mButtonLogA.setOnClickListener(v -> LogUtil.a(TAG, "log a", "log a"));
        mBinding.mButtonLogE.setOnClickListener(v -> LogUtil.e(TAG, "log e", "log e"));
        mBinding.mButtonLogThrowable.setOnClickListener(v -> {
            LogUtil.log(new NullPointerException("log throwable"));
        });
    }

    @Override
    public void initData(@Nullable Bundle bundle) {

    }

    @Override
    public void loadData(boolean refresh) {

    }
}
