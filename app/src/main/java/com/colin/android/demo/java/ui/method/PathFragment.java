package com.colin.android.demo.java.ui.method;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.colin.android.demo.java.R;
import com.colin.android.demo.java.adapter.StringAdapter;
import com.colin.android.demo.java.app.AppFragment;
import com.colin.android.demo.java.databinding.LayoutListBinding;
import com.colin.android.demo.java.def.LoadState;
import com.colin.android.demo.java.utils.DialogManager;
import com.colin.library.android.utils.LogUtil;
import com.colin.library.android.utils.PathUtil;
import com.colin.library.android.widgets.def.OnItemClickListener;

import java.io.File;

/**
 * 作者： ColinLu
 * 时间： 2022-01-18 22:41
 * <p>
 * 描述： TODO
 */
public class PathFragment extends AppFragment<LayoutListBinding> implements OnItemClickListener {
    private PathFragmentViewModel mViewModel;
    private StringAdapter mAdapter;

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
        mViewModel = new ViewModelProvider(this).get(PathFragmentViewModel.class);
        mViewModel.getList().observe(this, list -> mAdapter.setData(list));
        mViewModel.getLoadState().observe(this, state -> mBinding.mRefreshList.setRefreshing(state == LoadState.ING));
    }

    @Override
    public void loadData(boolean refresh) {
        mViewModel.refresh(refresh);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    public void item(@NonNull View view, int position, @Nullable Object object) {
        final String title = object == null ? "" : object.toString();
        String path = null;
        LogUtil.e(getAbsolutePath());
        switch (title) {
            case "root system":
                path = PathUtil.getRootSystem().getAbsolutePath();
                break;
            case "root data":
                path = PathUtil.getRootData().getAbsolutePath();
                break;
            case "root storage":
                final File rootStorage = PathUtil.getRootStorage();
                path = rootStorage == null ? "rootStorage==null" : PathUtil.getRootStorage().getAbsolutePath();
                break;
            case "data app":
                path = PathUtil.getInternalApp().getAbsolutePath();
                break;
            case "External":
                path = PathUtil.getExternalStorage().getAbsolutePath();
                break;
            case "external music":
                path = PathUtil.getExternalFile(Environment.DIRECTORY_MUSIC).getAbsolutePath();
                break;
            case "external dcim":
                path = PathUtil.getExternalFile(Environment.DIRECTORY_DCIM).getAbsolutePath();
                break;
            case "external picture":
                path = PathUtil.getExternalFile(Environment.DIRECTORY_PICTURES).getAbsolutePath();
                break;
            case "external download":
                path = PathUtil.getExternalFile(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                break;
            case "external custom":
                path = PathUtil.getExternalFile("custom").getAbsolutePath();
                break;
            case "user data":
                path = PathUtil.getUserData().getAbsolutePath();
                break;
            case "user cache":
                path = PathUtil.getUserCache().getAbsolutePath();
                break;
            case "user code cache":
                path = PathUtil.getUserCodeCache().getAbsolutePath();
                break;
            case "user files":
                path = PathUtil.getUserFiles().getAbsolutePath();
                break;
            default:
                break;
        }
        DialogManager.getInstance().showPath(getChildFragmentManager(), title, path);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public String getAbsolutePath() {
        final StringBuilder sb = new StringBuilder();
        final File CacheDir = getFile("CacheDir");
        final File CodeCacheDir = getFile("CodeCacheDir");
        final File ExternalCacheDir = getFile("ExternalCacheDir");
        final File DataDir = getFile("DataDir");
        final File FilesDir = getFile("FilesDir");
        final File Colin = getFile("Colin");
        final File NULL = getFile(null);
        sb.append("CacheDir:").append(CacheDir == null ? "null" : CacheDir.getAbsolutePath()).append('\n');
        sb.append("CodeCacheDir:").append(CodeCacheDir == null ? "null" : CodeCacheDir.getAbsolutePath()).append('\n');
        sb.append("ExternalCacheDir:").append(ExternalCacheDir == null ? "null" : ExternalCacheDir.getAbsolutePath()).append('\n');
        sb.append("DataDir:").append(DataDir == null ? "null" : DataDir.getAbsolutePath()).append('\n');
        sb.append("FilesDir:").append(FilesDir == null ? "null" : FilesDir.getAbsolutePath()).append('\n');
        sb.append("Colin:").append(Colin == null ? "null" : Colin.getAbsolutePath()).append('\n');
        sb.append("NULL:").append(NULL == null ? "NULL" : NULL.getAbsolutePath()).append('\n');
        return sb.toString().trim();
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public File getFile(String title) {
        if (title == null) return requireContext().getExternalFilesDir(null);
        try {
            switch (title) {
                case "CacheDir":
                    return requireContext().getCacheDir();
                case "CodeCacheDir":
                    return requireContext().getCodeCacheDir();
                case "ExternalCacheDir":
                    return requireContext().getExternalCacheDir();
                case "DataDir":
                    return requireContext().getDataDir();
                case "FilesDir":
                    return requireContext().getFilesDir();
                default:
                    return requireContext().getExternalFilesDir("Colin");
            }
        } catch (Exception e) {
            LogUtil.i(title);
            LogUtil.log(e);
        }
        return null;

    }
}
