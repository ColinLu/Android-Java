package com.colin.android.demo.java.ui.view;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.colin.android.demo.java.adapter.StringAdapter;
import com.colin.android.demo.java.app.AppFragment;
import com.colin.android.demo.java.databinding.LayoutEdgeBinding;
import com.colin.android.demo.java.def.LoadState;
import com.colin.android.demo.java.ui.method.MethodFragmentViewModel;
import com.colin.android.demo.java.utils.DemoUtils;
import com.colin.library.android.utils.ToastUtil;
import com.colin.library.android.widgets.annotation.Direction;
import com.colin.library.android.widgets.def.OnItemClickListener;

/**
 * 作者： ColinLu
 * 时间： 2022-01-19 14:10
 * <p>
 * 描述： TODO
 */
public class EdgeFragment extends AppFragment<LayoutEdgeBinding> implements OnItemClickListener {
    private EdgeFragmentViewModel mViewModel;
    private StringAdapter mAdapter;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mAdapter = null;
        mViewModel = null;
    }

    @Override
    public void initView(@Nullable Bundle bundle) {
        mBinding.mEdgeList.setOnEdgeListener(edge -> loadData(edge.getDirection() == Direction.TOP));
        initRecyclerView(getActivity());
    }

    @Override
    public void initData(@Nullable Bundle bundle) {
        mViewModel = DemoUtils.getViewModel(this, EdgeFragmentViewModel.class);
        mViewModel.getList().observe(this, list -> mAdapter.setData(list));
        mViewModel.getLoadState().observe(this, this::loadData);
    }

    private void loadData(@LoadState int state) {
        if (state == LoadState.SUCCESS || state == LoadState.ERROR) {
//            mBinding.mEdgeList.edgeFinish();
        }
    }

    @Override
    public void loadData(boolean refresh) {
        mViewModel.refresh(refresh);
    }

    private void initRecyclerView(Context context) {
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
    public void item(@NonNull View view, int position, @Nullable Object object) {
        ToastUtil.show(object == null ? "" : object.toString());
    }
}
