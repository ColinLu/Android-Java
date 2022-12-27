package com.colin.android.demo.java.ui.view;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.colin.android.demo.java.R;
import com.colin.android.demo.java.adapter.StringAdapter;
import com.colin.android.demo.java.app.AppFragment;
import com.colin.android.demo.java.databinding.LayoutListBinding;
import com.colin.android.demo.java.def.LoadState;
import com.colin.android.demo.java.utils.DemoUtils;
import com.colin.library.android.widgets.def.OnItemClickListener;

/**
 * 作者： ColinLu
 * 时间： 2022-01-19 09:19
 * <p>
 * 描述： TODO
 */
public class ViewFragment extends AppFragment<LayoutListBinding> implements OnItemClickListener {
    private ViewFragmentViewModel mViewModel;
    private StringAdapter mAdapter;

    @Override
    public void initView(@Nullable Bundle bundle) {
        initRecyclerView(getActivity());
        mBinding.mRefreshList.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary);
        mBinding.mRefreshList.setOnRefreshListener(() -> loadData(true));
    }

    private void initRecyclerView(@Nullable Context context) {
        if (context == null) {
            return;
        }
        if (mAdapter == null) {
            mAdapter = new StringAdapter();
        }
        mAdapter.setOnItemClickListener(this);
        mBinding.mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mBinding.mRecyclerView.setHasFixedSize(true);
        mBinding.mRecyclerView.setAdapter(mAdapter);
        mBinding.mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void initData(@Nullable Bundle bundle) {
        mViewModel = DemoUtils.getViewModel(this, ViewFragmentViewModel.class);
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
            case "Motion":
                DemoUtils.toNavigate(this, R.id.action_View_to_Motion);
                break;
            case "Edge":
                DemoUtils.toNavigate(this, R.id.action_View_to_Edge);
                break;
            case "Circle":
                DemoUtils.toNavigate(this, R.id.action_View_to_Circle);
                break;
            case "Text":
                DemoUtils.toNavigate(this, R.id.action_View_to_Text);
                break;
            default:
                break;
        }
    }
}
