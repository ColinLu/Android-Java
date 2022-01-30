package com.colin.android.demo.java.ui.home;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.android.demo.java.R;
import com.colin.android.demo.java.app.AppFragment;
import com.colin.android.demo.java.databinding.FragmentHomeBinding;
import com.colin.android.demo.java.utils.ContactUtils;
import com.colin.library.android.utils.thread.ThreadUtil;

import java.security.Permission;

public class HomeFragment extends AppFragment<FragmentHomeBinding> {
    @Override
    public void registerForContextMenu(@NonNull View view) {
        super.registerForContextMenu(view);
    }

    @Override
    public void initView(@Nullable Bundle bundle) {
        mBinding.buttonMethod.setOnClickListener(v -> toNavigate(R.id.action_Home_to_Method));
        mBinding.buttonView.setOnClickListener(v -> toNavigate(R.id.action_Home_to_View));
    }

    @Override
    public void initData(@Nullable Bundle bundle) {

    }

    @Override
    public void loadData(boolean refresh) {

    }
}