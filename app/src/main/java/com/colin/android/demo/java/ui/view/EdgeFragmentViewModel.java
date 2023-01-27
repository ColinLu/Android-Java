package com.colin.android.demo.java.ui.view;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.colin.android.demo.java.R;
import com.colin.android.demo.java.def.Constants;
import com.colin.android.demo.java.def.LoadState;
import com.colin.android.demo.java.utils.DemoUtils;
import com.colin.library.android.helper.ThreadHelper;
import com.colin.library.android.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 作者： ColinLu
 * 时间： 2022-01-18 22:48
 * <p>
 * 描述： TODO
 */
public class EdgeFragmentViewModel extends ViewModel {
    private final MutableLiveData<List<String>> mList;
    private final MutableLiveData<Integer> mLoadState;

    private int mPage = Constants.PAGE_START;

    public EdgeFragmentViewModel() {
        mList = new MutableLiveData<>();
        mLoadState = new MutableLiveData<>();
    }

    public void refresh(boolean refresh) {
        if (refresh) {
            mPage = Constants.PAGE_START;
            mLoadState.setValue(LoadState.ING);
        } else {
            mPage += 1;
            mLoadState.setValue(LoadState.MORE);
        }
        ThreadHelper.getInstance().doAsync(() -> {
            LogUtil.i("current page:" + mPage);
            mList.postValue(loadPate(mPage));
        });
        ThreadHelper.getInstance().postDelayed(() -> mLoadState.setValue(LoadState.SUCCESS), Constants.DURATION_DELAYED);
    }

    private List<String> loadPate(int page) {
        List<String> value = mList.getValue();
        if (value == null) value = new ArrayList<>();
        if (page == Constants.PAGE_START) value.clear();
        final List<String> list = new ArrayList<>(Constants.PAGE_SIZE);
        for (int i = 0; i < Constants.PAGE_SIZE; i++) {
            list.add(String.format(Locale.US, "current page:%d item:%d", page, i));
        }
        value.addAll(list);
        return value;
    }

    public MutableLiveData<List<String>> getList() {
        return mList;
    }

    public MutableLiveData<Integer> getLoadState() {
        return mLoadState;
    }
}
