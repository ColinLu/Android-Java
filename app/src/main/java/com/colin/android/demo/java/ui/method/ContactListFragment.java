package com.colin.android.demo.java.ui.method;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.colin.android.demo.java.R;
import com.colin.android.demo.java.adapter.ContactAdapter;
import com.colin.android.demo.java.app.AppFragment;
import com.colin.android.demo.java.databinding.LayoutListBinding;
import com.colin.android.demo.java.def.LoadState;
import com.colin.android.demo.java.def.bean.ContactBean;
import com.colin.android.demo.java.utils.DialogManager;
import com.colin.library.android.utils.ToastUtil;
import com.colin.library.android.widgets.def.OnItemClickListener;

/**
 * 作者： ColinLu
 * 时间： 2022-01-30 00:02
 * <p>
 * 描述： 通讯录列表
 */
public class ContactListFragment extends AppFragment<LayoutListBinding> implements OnItemClickListener {
    private ContactListFragmentViewModel mViewModel;
    private ContactAdapter mAdapter;
    private ActivityResultLauncher<String> mContactLauncher;

    @Override
    public void initView(@Nullable Bundle bundle) {
        initRecyclerView(getActivity());
        mBinding.mRefreshList.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary);
        mBinding.mRefreshList.setOnRefreshListener(() -> loadData(true));
    }

    private void initRecyclerView(Context context) {
        if (context == null) {
            return;
        }
        if (mAdapter == null) {
            mAdapter = new ContactAdapter();
        }
        mAdapter.setOnItemClickListener(this);
        mBinding.mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mBinding.mRecyclerView.setHasFixedSize(true);
        mBinding.mRecyclerView.setAdapter(mAdapter);
        mBinding.mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void initData(@Nullable Bundle bundle) {
        mViewModel = new ViewModelProvider(this).get(ContactListFragmentViewModel.class);
        mViewModel.getList().observe(this, list -> mAdapter.setData(list));
        mViewModel.getLoadState().observe(this, state -> mBinding.mRefreshList.setRefreshing(state == LoadState.ING));
        mContactLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) mViewModel.refresh(true);
            else ToastUtil.show("permissions alert");
        });
    }

    @Override
    public void loadData(boolean refresh) {
        if (refresh) mContactLauncher.launch(Manifest.permission.READ_CONTACTS);
        else mViewModel.refresh(false);
    }


    @Override
    public void item(@NonNull View view, int position, @Nullable Object object) {
        if (object instanceof ContactBean) {
//            DialogManager.getInstance().show(getChildFragmentManager(), (ContactBean) object);
        }
    }
}
