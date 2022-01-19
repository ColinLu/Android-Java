package com.colin.android.demo.java.ui.view;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.colin.android.demo.java.R;
import com.colin.android.demo.java.def.Constants;
import com.colin.android.demo.java.def.LoadState;
import com.colin.android.demo.java.utils.DemoUtils;
import com.colin.library.android.utils.thread.ThreadUtil;

import java.util.List;

/**
 * 作者： ColinLu
 * 时间： 2022-01-18 22:48
 * <p>
 * 描述： TODO
 */
public class EdgeFragmentViewModel extends ViewModel {
    private final MutableLiveData<List<String>> mList;
    private final MutableLiveData<Integer> mLoadState;

    public EdgeFragmentViewModel() {
        mList = new MutableLiveData<>();
        mLoadState = new MutableLiveData<>();
    }

    public void refresh(boolean refresh) {
        mLoadState.setValue(refresh ? LoadState.ING : LoadState.MORE);
        ThreadUtil.doAsync(() -> mList.postValue(DemoUtils.getStringList(R.array.flow_data)));
        ThreadUtil.runUiDelayed(() -> mLoadState.setValue(LoadState.SUCCESS), Constants.DURATION_DELAYED);
    }

    public MutableLiveData<List<String>> getList() {
        return mList;
    }

    public MutableLiveData<Integer> getLoadState() {
        return mLoadState;
    }
}
