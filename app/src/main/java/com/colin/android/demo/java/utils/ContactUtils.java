package com.colin.android.demo.java.utils;

import android.content.Context;
import android.provider.ContactsContract;

import androidx.annotation.NonNull;

import com.colin.android.demo.java.def.bean.ContactBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者： ColinLu
 * 时间： 2022-01-30 00:17
 * <p>
 * 描述： 联系人辅助类
 */
public final class ContactUtils {
    @NonNull
    public static List<ContactBean> getContactList(@NonNull Context context) {
        final List<ContactBean> list = new ArrayList<>();

        getCustor(context, ContactsContract.Contacts.CONTENT_URI,)
    }


}
