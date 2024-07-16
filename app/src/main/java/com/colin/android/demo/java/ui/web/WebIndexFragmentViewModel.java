package com.colin.android.demo.java.ui.web;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.colin.android.demo.java.def.Constants;
import com.colin.library.android.helper.ThreadHelper;
import com.colin.library.android.utils.SpUtil;
import com.colin.library.android.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;


public class WebIndexFragmentViewModel extends ViewModel {
    final MutableLiveData<List<String>> history = new MutableLiveData<>();


    public WebIndexFragmentViewModel() {

    }

    public void refresh(@NonNull String[] urls, boolean refresh) {
        ThreadHelper.getInstance().doAsync(() -> {
            List<String> list = new ArrayList<>(10);
            for (String url : urls) {
                if (!StringUtil.isEmpty(url)) list.add(url);
            }
            final String data = SpUtil.getString(Constants.SP_WEB_HISTORY, "");
            for (String url : data.split(",")) {
                if (!StringUtil.isEmpty(url)) list.add(url);
            }
            history.postValue(list);
        });
    }

    public void updateHistory(@NonNull String url) {
        final String data = SpUtil.getString(Constants.SP_WEB_HISTORY, "");
        SpUtil.put(Constants.SP_WEB_HISTORY, data + "," + url);
    }
}