package com.colin.library.android.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.colin.library.android.base.def.IActivity;

/**
 * 作者： ColinLu
 * 时间： 2022-01-15 11:27
 * <p>
 * 描述： Fragment 基类
 */
public abstract class BaseFragment extends Fragment implements IActivity {
    protected static final String STATE_SAVE_IS_HIDDEN = "STATE_SAVE_IS_HIDDEN";
    protected View mRootView;                                   //根布局


    /*Fragment开始*/
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final FragmentManager fragmentManager = getParentFragmentManager();
        if (savedInstanceState != null) {
            final boolean isSupportHidden = savedInstanceState.getBoolean(STATE_SAVE_IS_HIDDEN);
            final FragmentTransaction transaction = fragmentManager.beginTransaction();
            if (isSupportHidden) transaction.hide(this);
            else transaction.show(this);
            transaction.commitAllowingStateLoss();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (null != mRootView) {
            final ViewGroup parent = (ViewGroup) mRootView.getParent();
            if (null != parent) parent.removeView(mRootView);
        } else mRootView = inflater.inflate(layoutRes(), container, false);
        return mRootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initView(savedInstanceState);
        initData(getArguments());
    }

    @Nullable
    public <T extends View> T findViewById(@IdRes int id) {
        return (T) mRootView.findViewById(id);
    }
}
