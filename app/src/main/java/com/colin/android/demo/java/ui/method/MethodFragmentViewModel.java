package com.colin.android.demo.java.ui.method;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.colin.android.demo.java.R;
import com.colin.android.demo.java.def.Constants;
import com.colin.android.demo.java.def.LoadState;
import com.colin.library.android.utils.data.UtilHelper;
import com.colin.library.android.utils.thread.ThreadUtil;

import java.util.Arrays;
import java.util.List;

/**
 * 作者： ColinLu
 * 时间： 2022-01-18 22:48
 * <p>
 * 描述： TODO
 */
public class MethodFragmentViewModel extends ViewModel {
    private final MutableLiveData<List<String>> mMethodList;
    private final MutableLiveData<Integer> mLoadState;

    public MethodFragmentViewModel() {
        mMethodList = new MutableLiveData<>();
        mLoadState = new MutableLiveData<>();
    }

    public void refresh(boolean refresh) {
        mLoadState.setValue(LoadState.ING);
        ThreadUtil.doAsync(() -> mMethodList.postValue(getList()));
        ThreadUtil.runOnUiDelayed(() -> mLoadState.setValue(LoadState.SUCCESS), Constants.DURATION_DELAYED);
    }

    private List<String> getList() {
        final String[] array = UtilHelper.getInstance().getContext().getResources()
                .getStringArray(R.array.method_list);
        return Arrays.asList(array);
    }

    public MutableLiveData<List<String>> getMethodList() {
        return mMethodList;
    }

    public MutableLiveData<Integer> getLoadState() {
        return mLoadState;
    }
}
