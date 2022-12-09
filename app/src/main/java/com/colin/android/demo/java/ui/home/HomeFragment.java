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
import com.colin.library.android.http.OkHttp;
import com.colin.library.android.http.annotation.Method;
import com.colin.library.android.http.callback.IHttpCallback;

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
        mBinding.buttonGet.setOnClickListener(this);
        mBinding.buttonPost.setOnClickListener(this);
    }

    @Override
    public void initData(@Nullable Bundle bundle) {

    }

    @Override
    public void loadData(boolean refresh) {

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
            case R.id.button_get:
                final int i = 0 / 2;
                final int j = 0 / 0;
                break;
            default:
                break;
        }
    }

    private void http(@Method @NonNull String method) {
        OkHttp.getInstance().get("")
                .execute(new IHttpCallback<String>() {
                    @Override
                    public void success(@Nullable String s) {

                    }
                });
    }
}