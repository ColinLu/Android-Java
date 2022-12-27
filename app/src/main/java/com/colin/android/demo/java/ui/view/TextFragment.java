package com.colin.android.demo.java.ui.view;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.colin.android.demo.java.app.AppFragment;
import com.colin.android.demo.java.databinding.FragmentTextBinding;
import com.colin.library.android.widgets.annotation.Orientation;

/**
 * 作者： ColinLu
 * 时间： 2022-01-27 22:14
 * <p>
 * 描述： TODO
 */
public class TextFragment extends AppFragment<FragmentTextBinding> {

    @Override
    public void initView(@Nullable Bundle bundle) {
        mBinding.buttonHorizontal.setOnClickListener(v -> mBinding.gradientText.setOrientation(Orientation.HORIZONTAL));
        mBinding.buttonVertical.setOnClickListener(v -> mBinding.gradientText.setOrientation(Orientation.VERTICAL));
        mBinding.buttonTextColor.setOnClickListener(v -> mBinding.gradientText.setTextColor(Color.BLUE));
        mBinding.buttonGradient.setOnClickListener(v -> mBinding.gradientText.setTextColor(Color.WHITE, Color.BLACK));
    }

    @Override
    public void initData(@Nullable Bundle bundle) {

    }

    @Override
    public void loadData(boolean refresh) {

    }
}
