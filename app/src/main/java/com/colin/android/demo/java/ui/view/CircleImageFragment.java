package com.colin.android.demo.java.ui.view;

import android.os.Bundle;
import android.widget.SeekBar;

import androidx.annotation.Nullable;

import com.colin.android.demo.java.app.AppFragment;
import com.colin.android.demo.java.databinding.FragmentCircleImageBinding;


/**
 * 作者： ColinLu
 * 时间： 2022-01-16 22:52
 * <p>
 * 描述： 圆形控件展示
 */
public class CircleImageFragment extends AppFragment<FragmentCircleImageBinding> {

    @Override
    public void initView(@Nullable Bundle bundle) {
        mBinding.seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mBinding.imageSet.setRadius(progress * 4);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
