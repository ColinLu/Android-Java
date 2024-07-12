package com.colin.android.demo.java.ui.home;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.android.demo.java.MainActivity;
import com.colin.android.demo.java.R;
import com.colin.android.demo.java.app.AppFragment;
import com.colin.android.demo.java.databinding.FragmentHomeBinding;
import com.colin.android.demo.java.utils.DemoUtils;
import com.colin.library.android.utils.IntentUtil;


public class HomeFragment extends AppFragment<FragmentHomeBinding> {
    @Override
    public void registerForContextMenu(@NonNull View view) {
        super.registerForContextMenu(view);
    }

    @Override
    public void initView(@Nullable Bundle bundle) {
        mBinding.buttonView.setOnClickListener(v -> DemoUtils.toNavigate(this, R.id.action_Home_to_View));
        mBinding.buttonMethod.setOnClickListener(v -> DemoUtils.toNavigate(this, R.id.action_Home_to_Method));
        mBinding.buttonWeb.setOnClickListener(v -> DemoUtils.toNavigate(this, R.id.action_Home_to_Web));
        mBinding.buttonImage.setOnClickListener(v -> requestImageIntent());
        mBinding.buttonCamera.setOnClickListener(v -> requestCameraIntent());
        mBinding.buttonVideo.setOnClickListener(v -> requestVideoIntent());
    }


    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setExpanded(true);

    }

    private void requestVideoIntent() {
//        IntentUtil.requestVideo(this, UriUtil.getActivityetUri(getContext(),));
    }

    private void requestCameraIntent() {
        IntentUtil.requestAudio(this, 100);
    }

    private void requestImageIntent() {
        IntentUtil.requestImage(this, 100);
    }
}