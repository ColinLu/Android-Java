package com.colin.android.demo.java.ui.home;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.colin.android.demo.java.MainActivity;
import com.colin.android.demo.java.R;
import com.colin.android.demo.java.app.AppFragment;
import com.colin.android.demo.java.databinding.FragmentHomeBinding;
import com.colin.android.demo.java.utils.DemoUtils;
import com.colin.android.demo.java.utils.DialogManager;
import com.colin.library.android.utils.LogUtil;
import com.colin.library.android.utils.ToastUtil;


public class HomeFragment extends AppFragment<FragmentHomeBinding> {
    @Override
    public void registerForContextMenu(@NonNull View view) {
        super.registerForContextMenu(view);
    }

    private String mMimeType = null;
    private final ActivityResultLauncher<String> mLauncherContent = registerForActivityResult(new ActivityResultContracts.GetContent(), (result -> {
        LogUtil.log("mLauncherContent  mimeType:%s result:%s", mMimeType, result == null ? "null" : result.toString());
        DialogManager.getInstance().showTip(getChildFragmentManager(), mMimeType, result == null ? "null" : result.toString());
    }));

    private final ActivityResultLauncher<String> mLauncherPermission = registerForActivityResult(new ActivityResultContracts.RequestPermission(), (result -> {
        LogUtil.log("requestPermission mimeType:%s result:%s", mMimeType, result);
        if (result) {
            mLauncherContent.launch(mMimeType);
        }
    }));


    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    public void initView(@Nullable Bundle bundle) {
        mBinding.buttonView.setOnClickListener(v -> DemoUtils.toNavigate(this, R.id.action_Home_to_View));
        mBinding.buttonMethod.setOnClickListener(v -> DemoUtils.toNavigate(this, R.id.action_Home_to_Method));
        mBinding.buttonWeb.setOnClickListener(v -> DemoUtils.toNavigate(this, R.id.action_Home_to_WebIndex));
        mBinding.buttonImage.setOnClickListener(v -> requestImageIntent());
        mBinding.buttonCamera.setOnClickListener(v -> requestCameraIntent());
        mBinding.buttonVideo.setOnClickListener(v -> requestVideoIntent());
    }


    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setExpanded(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void requestVideoIntent() {
        mMimeType = "video/*";
        mLauncherPermission.launch(Manifest.permission.READ_MEDIA_VIDEO);
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void requestCameraIntent() {
        mMimeType = "audio/*";
        mLauncherPermission.launch(Manifest.permission.READ_MEDIA_AUDIO);
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void requestImageIntent() {
        mMimeType = "image/*";
        mLauncherPermission.launch(Manifest.permission.READ_MEDIA_IMAGES);
    }

    private void requestContent(@NonNull final String permission, @NonNull final String mimeType) {
        requestPermission(permission, (result -> {
            LogUtil.log("requestPermission permission:%s mimeType:%s result:%s", permission, mimeType, result);
            if (result) {
                requestContent(mimeType);
            } else {
                ToastUtil.show(String.format("requestPermission permission:%s failed", permission));
            }
        }));
    }

    private void requestPermission(@NonNull final String permission, @NonNull ActivityResultCallback<Boolean> callback) {
        final ActivityResultLauncher<String> launcher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), callback);
        launcher.launch(permission);
    }

    private void requestContent(@NonNull final String mimeType) {
        final ActivityResultLauncher<String> launcher = registerForActivityResult(new ActivityResultContracts.GetContent(), (result -> {
            ToastUtil.show(String.format("requestContent mime type:%s result:%s", mimeType, result == null ? "null" : result.toString()));
        }));
        launcher.launch(mimeType);
    }
}