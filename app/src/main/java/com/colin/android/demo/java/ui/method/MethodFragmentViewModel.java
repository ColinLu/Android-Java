package com.colin.android.demo.java.ui.method;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.colin.android.demo.java.R;
import com.colin.android.demo.java.def.Constants;
import com.colin.android.demo.java.def.LoadState;
import com.colin.android.demo.java.utils.DemoUtils;
import com.colin.library.android.helper.ThreadHelper;

import java.util.List;

/**
 * 作者： ColinLu
 * 时间： 2022-01-18 22:48
 * <p>
 * 描述： TODO
 */
public class MethodFragmentViewModel extends ViewModel {
    private final MutableLiveData<List<String>> mList;
    private final MutableLiveData<Integer> mLoadState;

    public MethodFragmentViewModel() {
        mList = new MutableLiveData<>();
        mLoadState = new MutableLiveData<>();
    }

    public void refresh(boolean refresh) {
        mLoadState.setValue(LoadState.ING);
        ThreadHelper.getInstance().doAsync(
                () -> mList.postValue(DemoUtils.getStringList(R.array.method_list)));
        ThreadHelper.getInstance().postDelayed(() -> mLoadState.setValue(LoadState.SUCCESS), Constants.DURATION_DELAYED);
    }

    public MutableLiveData<List<String>> getList() {
        return mList;
    }

    public MutableLiveData<Integer> getLoadState() {
        return mLoadState;
    }
}
