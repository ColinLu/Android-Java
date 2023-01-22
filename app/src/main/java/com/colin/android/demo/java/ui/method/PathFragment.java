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
import com.colin.library.android.utils.ToastUtil;
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
        LogUtil.e("isWrite:" + PathUtil.canWrite() + "\tstorageState:" + PathUtil.storageState());
        switch (title) {
            case "root system":
                path = PathUtil.getPath(PathUtil.getRootSystem());
                break;
            case "root data":
                path = PathUtil.getPath(PathUtil.getRootData());
                break;
            case "root storage":
                path = PathUtil.getPath(PathUtil.getRootStorage());
                break;
            case "external":
                ToastUtil.show("not support");
                return;
            case "external music":
                path = PathUtil.getPath(PathUtil.getExternalFile(Environment.DIRECTORY_MUSIC));
                break;
            case "external android music":
                path = PathUtil.getPath(PathUtil.getExternalFile(requireContext(), Environment.DIRECTORY_MUSIC));
                break;
            case "external dcim":
                path = PathUtil.getPath(PathUtil.getExternalFile(Environment.DIRECTORY_DCIM));
                break;
            case "external android dcim":
                path = PathUtil.getPath(PathUtil.getExternalFile(requireContext(), Environment.DIRECTORY_DCIM));
                break;
            case "external picture":
                path = PathUtil.getPath(PathUtil.getExternalFile(Environment.DIRECTORY_PICTURES));
                break;
            case "external android picture":
                path = PathUtil.getPath(PathUtil.getExternalFile(requireContext(), Environment.DIRECTORY_PICTURES));
                break;
            case "external download":
                path = PathUtil.getPath(PathUtil.getExternalFile(Environment.DIRECTORY_DOWNLOADS));
                break;
            case "external android download":
                path = PathUtil.getPath(PathUtil.getExternalFile(requireContext(), Environment.DIRECTORY_DOWNLOADS));
                break;
            case "external custom":
                path = PathUtil.getPath(PathUtil.getExternalFile("custom"));
                break;
            case "external android custom":
                path = PathUtil.getPath(PathUtil.getExternalFile(requireContext(), "custom"));
                break;
            case "external cache":
                path = PathUtil.getPath(PathUtil.getExternalCache());
                break;
            case "user data":
                path = PathUtil.getPath(PathUtil.getUserData());
                break;
            case "user cache":
                path = PathUtil.getPath(PathUtil.getUserCache());
                break;
            case "user code cache":
                path = PathUtil.getPath(PathUtil.getUserCodeCache());
                break;
            case "user files":
                path = PathUtil.getPath(PathUtil.getUserFiles());
                break;
            case "sp":
                path = PathUtil.getPath(PathUtil.getSp());
                break;
            case "sp path":
                path = PathUtil.getSpPath();
                break;
            case "db":
                path = PathUtil.getPath(PathUtil.getDatabase());
                break;
            default:
                break;
        }
        LogUtil.i(title, path);
        DialogManager.getInstance().showTip(getChildFragmentManager(), title, path);
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public String getAbsolutePath() {
// CacheDir:        /data/user/0/com.colin.android.demo.java/cache
// CodeCacheDir:    /data/user/0/com.colin.android.demo.java/code_cache
// DataDir:         /data/user/0/com.colin.android.demo.java
// FilesDir:        /data/user/0/com.colin.android.demo.java/files
// ExternalCacheDir:/storage/emulated/0/Android/data/com.colin.android.demo.java/cache
// Colin:           /storage/emulated/0/Android/data/com.colin.android.demo.java/files/Colin
// NULL:            /storage/emulated/0/Android/data/com.colin.android.demo.java/files

        final StringBuilder sb = new StringBuilder();
        final File CacheDir = getFile("CacheDir");
        final File CodeCacheDir = getFile("CodeCacheDir");
        final File ExternalCacheDir = getFile("ExternalCacheDir");
        final File DataDir = getFile("DataDir");
        final File FilesDir = getFile("FilesDir");
        final File Colin = getFile("Colin");
        final File NULL = getFile(null);
        sb.append("DataDir:").append(DataDir == null ? "null" : DataDir.getAbsolutePath()).append('\n');
        sb.append("FilesDir:").append(FilesDir == null ? "null" : FilesDir.getAbsolutePath()).append('\n');
        sb.append("CacheDir:").append(CacheDir == null ? "null" : CacheDir.getAbsolutePath()).append('\n');
        sb.append("CodeCacheDir:").append(CodeCacheDir == null ? "null" : CodeCacheDir.getAbsolutePath()).append('\n');
        sb.append("NULL:").append(NULL == null ? "NULL" : NULL.getAbsolutePath()).append('\n');
        sb.append("ExternalCacheDir:").append(ExternalCacheDir == null ? "null" : ExternalCacheDir.getAbsolutePath()).append('\n');
        sb.append("Colin:").append(Colin == null ? "null" : Colin.getAbsolutePath()).append('\n');
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
            LogUtil.e(title, LogUtil.format(e));
        }
        return null;

    }
}
