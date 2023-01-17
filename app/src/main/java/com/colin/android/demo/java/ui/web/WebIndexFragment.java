package com.colin.android.demo.java.ui.web;

import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.SharedElementCallback;

import com.colin.android.demo.java.MainActivity;
import com.colin.android.demo.java.R;
import com.colin.android.demo.java.app.AppFragment;
import com.colin.android.demo.java.databinding.FragmentWebIndexBinding;
import com.colin.library.android.utils.LogUtil;

import java.io.FileDescriptor;
import java.io.PrintWriter;


public class WebIndexFragment extends AppFragment<FragmentWebIndexBinding> {
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
//        if (context instanceof MainActivity) {
//            ((MainActivity) context).setExpanded(false);
//        }
    }

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
    public void setExitSharedElementCallback(@Nullable SharedElementCallback callback) {
        super.setExitSharedElementCallback(callback);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        initSearch(menu);
    }

    @Override
    public void dump(@NonNull String prefix, @Nullable FileDescriptor fd, @NonNull PrintWriter writer, @Nullable String[] args) {
        super.dump(prefix, fd, writer, args);
        LogUtil.e("dump");
    }

    @Override
    public void onDestroyView() {
        setHasOptionsMenu(false);
        requireActivity().invalidateOptionsMenu();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    /**
     * 初始化搜索框
     *
     * @param menu
     */
    private void initSearch(Menu menu) {
        final MenuItem searchItem = menu.findItem(R.id.menu_search);
        SearchView mSearchView = (SearchView) searchItem.getActionView();
        mSearchView.setSubmitButtonEnabled(false);
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