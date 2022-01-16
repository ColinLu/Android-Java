package com.colin.android.demo.java.ui.notifications;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.colin.android.demo.java.R;
import com.colin.android.demo.java.app.AppFragment;

import java.util.Arrays;
import java.util.List;

public class NotificationsFragment extends AppFragment {
    @Override
    public int layoutRes() {
        return R.layout.fragment_notifications;
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void initView(@Nullable Bundle bundle) {
        final List<String> list = Arrays.asList("banner-1", "banner-2", "banner-3", "banner-4", "banner-5");
    }

    @Override
    public void initData(@Nullable Bundle bundle) {

    }
}