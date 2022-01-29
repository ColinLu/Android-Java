package com.colin.android.demo.java.ui.view;

import android.os.Bundle;
import android.widget.SeekBar;

import androidx.annotation.Nullable;

import com.colin.android.demo.java.app.AppFragment;
import com.colin.android.demo.java.databinding.FragmentCircleImageBinding;
import com.colin.android.demo.java.databinding.FragmentMotionBinding;
import com.colin.library.android.widgets.image.CircleImageView;


/**
 * 作者： ColinLu
 * 时间： 2022-01-16 22:52
 * <p>
 * 描述： TODO
 */
public class CircleImageFragment extends AppFragment<FragmentCircleImageBinding> {

    @Override
    public void initView(@Nullable Bundle bundle) {
        mBinding.seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mBinding.imageSet.setRadius(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void initData(@Nullable Bundle bundle) {

    }

    @Override
    public void loadData(boolean refresh) {

    }
}
