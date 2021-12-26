package com.colin.android.demo.java.base;

import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

import com.colin.android.demo.java.base.def.IActivity;

/**
 * 作者： ColinLu
 * 时间： 2021-12-20 20:23
 * <p>
 * 描述： Fragment 基类
 */
public abstract class BaseFragment<Bind extends ViewBinding> extends Fragment implements IActivity {
    protected Bind mBinding;

}
