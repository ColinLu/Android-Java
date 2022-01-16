package com.colin.android.demo.java.ui.home;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.navigation.fragment.NavHostFragment;

import com.colin.android.demo.java.R;
import com.colin.android.demo.java.app.AppFragment;

public class HomeFragment extends AppFragment {
    @Override
    public int layoutRes() {
        return R.layout.fragment_home;
    }

    @Override
    public void initView(@Nullable Bundle bundle) {
        findViewById(R.id.button_motion).setOnClickListener(view1 ->
                NavHostFragment.findNavController(HomeFragment.this)
                        .navigate(R.id.action_Home_to_Motion));
    }

    @Override
    public void initData(@Nullable Bundle bundle) {

    }
}