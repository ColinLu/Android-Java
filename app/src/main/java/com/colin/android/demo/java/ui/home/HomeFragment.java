package com.colin.android.demo.java.ui.home;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.colin.android.demo.java.MainActivity;
import com.colin.android.demo.java.R;
import com.colin.android.demo.java.app.AppFragment;
import com.colin.android.demo.java.databinding.FragmentHomeBinding;
import com.colin.android.demo.java.utils.DemoUtils;
import com.colin.android.demo.java.utils.DialogManager;
import com.colin.library.android.http.annotation.Method;


public class HomeFragment extends AppFragment<FragmentHomeBinding> implements View.OnClickListener {
    @Override
    public void registerForContextMenu(@NonNull View view) {
        super.registerForContextMenu(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void initView(@Nullable Bundle bundle) {
        mBinding.buttonMethod.setOnClickListener(this);
        mBinding.buttonView.setOnClickListener(this);
        mBinding.buttonWeb.setOnClickListener(this);
        mBinding.buttonImage.setOnClickListener(this);
        mBinding.buttonCamera.setOnClickListener(this);
        mBinding.buttonVideo.setOnClickListener(this);
    }

    @Override
    public void initData(@Nullable Bundle bundle) {

    }

    @Override
    public void loadData(boolean refresh) {

    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setExpanded(true);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_view:
                DemoUtils.toNavigate(this, R.id.action_Home_to_View);
                break;
            case R.id.button_method:
                DemoUtils.toNavigate(this, R.id.action_Home_to_Method);
                break;
            case R.id.button_web:
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).setExpanded(false);
                }
                DemoUtils.toNavigate(this, R.id.action_Home_to_Web);
                break;
            case R.id.button_image:
//                MediaHelper.getInstance().image().multiple(3).column(2).title("title").result(this::showResult).cancel(ToastUtil::show)
//                           .start(requireContext());
                break;
            case R.id.button_camera:
//                final Uri uri = MediaUtil.createImageUri(requireContext());
//                MediaHelper.getInstance().camera().image(uri).result(this::showResult).cancel(ToastUtil::show).start(requireContext());
                break;
            case R.id.button_video:
                DialogManager.getInstance().showImage(getChildFragmentManager(), R.drawable.ticket);
                break;
            default:
                break;
        }
    }

//    private void showResult(MediaFile mediaFile) {
//        LogUtil.d(mediaFile.toString());
//    }
//
//    private void showResult(List<MediaFile> mediaFile) {
//        LogUtil.d(mediaFile.toString());
//    }

    private void http(@Method @NonNull String method) {

    }
}