package com.colin.android.demo.java.ui.web;

import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;

import com.colin.android.demo.java.MainActivity;
import com.colin.android.demo.java.R;
import com.colin.android.demo.java.adapter.StringAdapter;
import com.colin.android.demo.java.app.AppFragment;
import com.colin.android.demo.java.databinding.LayoutListBinding;
import com.colin.android.demo.java.def.Constants;
import com.colin.android.demo.java.utils.DemoUtils;
import com.colin.library.android.utils.LogUtil;
import com.colin.library.android.utils.StringUtil;
import com.colin.library.android.widgets.def.OnItemClickListener;
import com.colin.library.android.widgets.web.IWebClient;

import java.util.Objects;


public class WebIndexFragment extends AppFragment<LayoutListBinding> implements IWebClient, OnItemClickListener {
    private WebIndexFragmentViewModel mViewModel;
    private StringAdapter mAdapter;

    @Override
    public void initView(@Nullable Bundle bundle) {
        mBinding.mRefreshList.setColorSchemeResources(Constants.REFRESH_IDS);
        mBinding.mRefreshList.setOnRefreshListener(() -> loadData(true));
        if (mAdapter == null) mAdapter = new StringAdapter(this);
        mAdapter.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        DemoUtils.initRecyclerView(mBinding.mRecyclerView, mAdapter);
        initSearch(((MainActivity) requireActivity()).getMenuItem(R.id.menu_search));
    }

    @Override
    public void initData(@Nullable Bundle bundle) {
        mViewModel = new ViewModelProvider(this).get(WebIndexFragmentViewModel.class);
        mViewModel.history.observe(this, (list -> mAdapter.setData(list)));
        mViewModel.refresh(getResources().getStringArray(R.array.url_address), true);
    }

    @Override
    public void loadData(boolean refresh) {
        mBinding.mRefreshList.setRefreshing(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        ((MainActivity) requireActivity()).setItemVisible(R.id.menu_search, true);
        super.onResume();
    }

    @Override
    public void onPause() {
        ((MainActivity) requireActivity()).setItemVisible(R.id.menu_search, false);
        ((MainActivity) requireActivity()).setExpanded(true);
        super.onPause();
    }

    /**
     * 初始化搜索框
     *
     * @param searchItem
     */
    private void initSearch(final MenuItem searchItem) {
        LogUtil.i("searchItem:" + (searchItem == null ? "searchItem is null" : searchItem.toString()));
        if (searchItem == null) return;
        searchItem.setVisible(true);
        SearchView mSearchView = (SearchView) searchItem.getActionView();
        assert mSearchView != null;
        mSearchView.setQueryHint(getString(R.string.query_web_hint_link));
        Objects.requireNonNull(mSearchView).setSubmitButtonEnabled(false);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String key) {
                LogUtil.i(key);
                toWebView(key, true);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String key) {
                LogUtil.i(key);
                return true;
            }
        });

    }

    private void toWebView(@Nullable final String url, boolean record) {
        if (StringUtil.isEmpty(url)) {
            return;
        }
        if (record) {
            mViewModel.updateHistory(url);
            final int position = mAdapter.getItemCount();
            mAdapter.addData(url, position);
        }
        final Bundle bundle = new Bundle();
        bundle.putInt(Constants.EXTRAS_WEB_TYPE, 0);
        bundle.putString(Constants.EXTRAS_WEB_URL, url);
        DemoUtils.toNavigate(this, R.id.action_webIndex_to_web, bundle);
    }

    @Override
    public void item(@NonNull View view, int position, @Nullable Object object) {
        toWebView(object == null ? null : object.toString(), false);
    }
}