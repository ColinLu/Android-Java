package com.colin.android.demo.java.ui.method;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.colin.android.demo.java.R;
import com.colin.android.demo.java.adapter.StringAdapter;
import com.colin.android.demo.java.app.AppFragment;
import com.colin.android.demo.java.databinding.LayoutListBinding;
import com.colin.android.demo.java.def.Constants;
import com.colin.android.demo.java.def.LoadState;
import com.colin.android.demo.java.utils.DemoUtils;
import com.colin.library.android.widgets.def.OnItemClickListener;

/**
 * 作者： ColinLu
 * 时间： 2022-01-18 22:41
 * <p>
 * 描述： TODO
 */
public class MethodFragment extends AppFragment<LayoutListBinding> implements OnItemClickListener {
    private MethodFragmentViewModel mViewModel;
    private StringAdapter mAdapter;

    @Override
    public void initView(@Nullable Bundle bundle) {
        initRecyclerView();
        mBinding.mRefreshList.setColorSchemeResources(Constants.REFRESH_IDS);
        mBinding.mRefreshList.setOnRefreshListener(() -> loadData(true));
    }

    private void initRecyclerView() {
        if (mAdapter == null) mAdapter = new StringAdapter(this);
        DemoUtils.initRecyclerView(mBinding.mRecyclerView, mAdapter);
    }

    @Override
    public void initData(@Nullable Bundle bundle) {
        mViewModel = new ViewModelProvider(this).get(MethodFragmentViewModel.class);
        mViewModel.getList().observe(this, list -> mAdapter.setData(list));
        mViewModel.getLoadState().observe(this, state -> mBinding.mRefreshList.setRefreshing(state == LoadState.ING));
    }

    @Override
    public void loadData(boolean refresh) {
        mViewModel.refresh(refresh);
    }

    @Override
    public void item(@NonNull View view, int position, @Nullable Object object) {
        final String value = object == null ? "" : object.toString();
        switch (value) {
            case "Log":
                toNavigate(R.id.action_Method_to_Log);
                break;
            case "Contact":
                toNavigate(R.id.action_Method_to_ContactList);
                break;
            case "Path":
                toNavigate(R.id.action_Method_to_Path);
                break;
            case "Http":
                toNavigate(R.id.action_Method_to_Http);
                break;
            default:
                break;
        }
    }
}
