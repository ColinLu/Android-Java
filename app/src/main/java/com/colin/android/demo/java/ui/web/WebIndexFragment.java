package com.colin.android.demo.java.ui.web;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.colin.android.demo.java.R;
import com.colin.android.demo.java.app.AppFragment;
import com.colin.android.demo.java.databinding.FragmentWebIndexBinding;
import com.colin.library.android.utils.LogUtil;


public class WebIndexFragment extends AppFragment<FragmentWebIndexBinding> {

    @Override
    public void initView(@Nullable Bundle bundle) {
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mBinding.mToolbar);
    }

    @Override
    public void initData(@Nullable Bundle bundle) {

    }

    @Override
    public void loadData(boolean refresh) {

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_search, menu);
        initSearch(menu);
    }

    /**
     * 初始化搜索框
     *
     * @param menu
     */
    private void initSearch(Menu menu) {
        final MenuItem searchItem = menu.findItem(R.id.menu_search);
        SearchView mSearchView = (SearchView) searchItem.getActionView();
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String key) {
                LogUtil.i(key);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String key) {
                LogUtil.i(key);
                return true;
            }
        });
    }
}