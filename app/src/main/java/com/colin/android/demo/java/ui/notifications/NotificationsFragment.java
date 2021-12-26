package com.colin.android.demo.java.ui.notifications;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

import com.colin.android.demo.java.R;
import com.colin.android.demo.java.adapter.StringAdapter;
import com.colin.android.demo.java.app.AppFragment;
import com.colin.android.demo.java.databinding.FragmentNotificationsBinding;

import java.util.Arrays;
import java.util.List;

public class NotificationsFragment extends AppFragment<FragmentNotificationsBinding> {

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void initView(@Nullable Bundle bundle) {
        final List<String> list = Arrays.asList("banner-1", "banner-2", "banner-3", "banner-4", "banner-5");
        final StringAdapter adapter = new StringAdapter(requireContext(), list);
        adapter.setOnItemClickListener((view, position, object) -> {
            final String title = object == null ? "" : object.toString();
            switch (title) {
                case "banner-1":
                    mBinding.switchLayout.switchView(R.mipmap.banner1, true, "banner-1");
                    break;
                case "banner-2":
                    mBinding.switchLayout.switchView(R.mipmap.banner2, true, "banner-2");
                    break;
                case "banner-3":
                    mBinding.switchLayout.switchView(R.mipmap.banner3, true, "banner-3");
                    break;
                case "banner-4":
                    mBinding.switchLayout.switchView(R.mipmap.banner4, true, "banner-4");
                    break;
                case "banner-5":
                    mBinding.switchLayout.switchView(R.mipmap.banner5, true, "banner-5");
                    break;
                default:
                    break;
            }
        });
        mBinding.titleList.setHasFixedSize(true);
        mBinding.titleList.setItemAnimator(new DefaultItemAnimator());
        mBinding.titleList.setAdapter(adapter);
    }

    @Override
    public void initData(@Nullable Bundle bundle) {

    }
}