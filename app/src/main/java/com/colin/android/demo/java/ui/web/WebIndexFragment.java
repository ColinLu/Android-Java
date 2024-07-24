package com.colin.android.demo.java.ui.web;

import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;

import com.colin.android.demo.java.MainActivity;
import com.colin.android.demo.java.R;
import com.colin.android.demo.java.adapter.StringAdapter;
import com.colin.android.demo.java.app.AppFragment;
import com.colin.android.demo.java.databinding.FragmentWebIndexBinding;
import com.colin.android.demo.java.def.Constants;
import com.colin.android.demo.java.utils.DemoUtils;
import com.colin.library.android.utils.LogUtil;
import com.colin.library.android.utils.StringUtil;
import com.colin.library.android.widgets.def.OnItemClickListener;
import com.colin.library.android.widgets.web.IWebClient;

import java.util.Objects;


public class WebIndexFragment extends AppFragment<FragmentWebIndexBinding> implements IWebClient, OnItemClickListener {
    private WebIndexFragmentViewModel mViewModel;
    private StringAdapter mAdapter;

    @Override
    public void initView(@Nullable Bundle bundle) {
        setHasOptionsMenu(true);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(mBinding.mToolbar);
        if (mAdapter == null) mAdapter = new StringAdapter(this);
        mAdapter.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
        DemoUtils.initRecyclerView(mBinding.mRecyclerView, mAdapter);
    }

    @Override
    public void initData(@Nullable Bundle bundle) {
        mViewModel = new ViewModelProvider(this).get(WebIndexFragmentViewModel.class);
        mViewModel.history.observe(this, (list -> mAdapter.setData(list)));
        mViewModel.refresh(getResources().getStringArray(R.array.url_address), true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        initSearch(menu);
    }

    @Override
    public void onDestroyView() {
        setHasOptionsMenu(false);
        requireActivity().invalidateOptionsMenu();
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setExpanded(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity) getActivity()).setExpanded(true);
    }

    /**
     * 初始化搜索框
     *
     * @param menu
     */
    private void initSearch(Menu menu) {
        menu.removeItem(R.id.action_settings);
        final MenuItem searchItem = menu.findItem(R.id.menu_search);
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

        mSearchView.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == EditorInfo.IME_ACTION_DONE) {
                LogUtil.i("setOnKeyListener keyCode:$d event:%s", keyCode, event.toString());
            }
            return false;
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