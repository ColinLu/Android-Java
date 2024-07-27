package com.colin.android.demo.java.ui.method;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
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
import com.colin.android.demo.java.utils.DialogManager;
import com.colin.library.android.utils.LogUtil;
import com.colin.library.android.utils.StorageUtil;
import com.colin.library.android.widgets.def.OnItemClickListener;

/**
 * 作者： ColinLu
 * 时间： 2022-01-18 22:41
 * <p>
 * 描述： Android系统文件路径
 */
public class PathFragment extends AppFragment<LayoutListBinding> implements OnItemClickListener {
    private PathFragmentViewModel mViewModel;
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
        mViewModel = new ViewModelProvider(this).get(PathFragmentViewModel.class);
        mViewModel.getList().observe(this, list -> mAdapter.setData(list));
        mViewModel.getLoadState().observe(this, state -> mBinding.mRefreshList.setRefreshing(state == LoadState.ING));
    }

    @Override
    public void loadData(boolean refresh) {
        mViewModel.refresh(refresh);
    }

    @Override
    public void item(@NonNull View view, int position, @Nullable Object object) {
        final String title = object == null ? "" : object.toString();
        String msg = null;
        switch (title) {
            case "can write":
                msg = String.valueOf(StorageUtil.canWrite());
                break;
            case "has sd card":
                msg = String.valueOf(StorageUtil.hasSDCard());
                break;
            case "root system":
                msg = StorageUtil.getPath(StorageUtil.getRootSystem());
                break;
            case "root data":
                msg = StorageUtil.getPath(StorageUtil.getRootData());
                break;
            case "user data":
                msg = StorageUtil.getPath(StorageUtil.getUserData());
                break;
            case "user cache":
                msg = StorageUtil.getPath(StorageUtil.getUserCache());
                break;
            case "user code cache":
                msg = StorageUtil.getPath(StorageUtil.getUserCodeCache());
                break;
            case "internal files":
                msg = StorageUtil.getPath(StorageUtil.getInternalFilesDir());
                break;
            case "internal data":
                msg = StorageUtil.getPath(StorageUtil.getInternalDataDir());
                break;
            case "internal cache":
                msg = StorageUtil.getPath(StorageUtil.getInternalCacheDir());
                break;
            case "internal shared prefs":
                msg = StorageUtil.getPath(StorageUtil.getInternalSpDir());
                break;
            case "internal databases":
                msg = StorageUtil.getPath(StorageUtil.getInternalDatabaseDir());
                break;
            case "internal code cache":
                msg = StorageUtil.getPath(StorageUtil.getInternalCodeCacheDir());
                break;
            case "internal append":
                msg = StorageUtil.getPath(StorageUtil.getInternalDir(title, Context.MODE_APPEND));
                break;
            case "internal private":
                msg = StorageUtil.getPath(StorageUtil.getInternalDir(title, Context.MODE_PRIVATE));
                break;
            case "external code cache":
                msg = StorageUtil.getPath(StorageUtil.getExternalCacheDir());
                break;
            case "external empty":
                msg = StorageUtil.getPath(StorageUtil.getExternalDir(null));
                break;
            case "external music":
                msg = StorageUtil.getPath(StorageUtil.getExternalDir(Environment.DIRECTORY_MUSIC));
                break;
            case "external podcasts":
                msg = StorageUtil.getPath(StorageUtil.getExternalDir(Environment.DIRECTORY_PODCASTS));
                break;
            case "external alarms":
                msg = StorageUtil.getPath(StorageUtil.getExternalDir(Environment.DIRECTORY_ALARMS));
                break;
            case "external notifications":
                msg = StorageUtil.getPath(StorageUtil.getExternalDir(Environment.DIRECTORY_NOTIFICATIONS));
                break;
            case "external pictures":
                msg = StorageUtil.getPath(StorageUtil.getExternalDir(Environment.DIRECTORY_PICTURES));
                break;
            case "external movies":
                msg = StorageUtil.getPath(StorageUtil.getExternalDir(Environment.DIRECTORY_MOVIES));
                break;
            case "external download":
                msg = StorageUtil.getPath(StorageUtil.getExternalDir(Environment.DIRECTORY_DOWNLOADS));
                break;
            case "external dcim":
                msg = StorageUtil.getPath(StorageUtil.getExternalDir(Environment.DIRECTORY_DCIM));
                break;
            case "external documents":
                msg = StorageUtil.getPath(StorageUtil.getExternalDir(Environment.DIRECTORY_DOCUMENTS));
                break;
            case "external public empty":
                msg = StorageUtil.getPath(StorageUtil.getExternalPublicDir(""));
                break;
            case "external public music":
                msg = StorageUtil.getPath(StorageUtil.getExternalPublicDir(Environment.DIRECTORY_MUSIC));
                break;
            case "external public podcasts":
                msg = StorageUtil.getPath(StorageUtil.getExternalPublicDir(Environment.DIRECTORY_PODCASTS));
                break;
            case "external public alarms":
                msg = StorageUtil.getPath(StorageUtil.getExternalPublicDir(Environment.DIRECTORY_ALARMS));
                break;
            case "external public notifications":
                msg = StorageUtil.getPath(StorageUtil.getExternalPublicDir(Environment.DIRECTORY_NOTIFICATIONS));
                break;
            case "external public pictures":
                msg = StorageUtil.getPath(StorageUtil.getExternalPublicDir(Environment.DIRECTORY_PICTURES));
                break;
            case "external public movies":
                msg = StorageUtil.getPath(StorageUtil.getExternalPublicDir(Environment.DIRECTORY_MOVIES));
                break;
            case "external public download":
                msg = StorageUtil.getPath(StorageUtil.getExternalPublicDir(Environment.DIRECTORY_DOWNLOADS));
                break;
            case "external public dcim":
                msg = StorageUtil.getPath(StorageUtil.getExternalPublicDir(Environment.DIRECTORY_DCIM));
                break;
            case "external public documents":
                msg = StorageUtil.getPath(StorageUtil.getExternalPublicDir(Environment.DIRECTORY_DOCUMENTS));
                break;
            default:
                break;
        }
        LogUtil.i(title, msg);
        DialogManager.getInstance().showTip(getChildFragmentManager(), title, msg);
    }
}
