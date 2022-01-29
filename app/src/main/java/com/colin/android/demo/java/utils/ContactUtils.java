package com.colin.android.demo.java.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.android.demo.java.def.bean.ContactBean;
import com.colin.library.android.utils.IOUtil;
import com.colin.library.android.utils.LogUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者： ColinLu
 * 时间： 2022-01-30 00:17
 * <p>
 * 描述： 联系人辅助类
 */
public final class ContactUtils {
    private static final String[] PROJECTION_CONTACT_LIST = {
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.Contacts.HAS_PHONE_NUMBER,
            ContactsContract.Contacts.PHOTO_ID,
            ContactsContract.Contacts.PHOTO_URI
    };

    @NonNull
    public static List<ContactBean> getContactList(@Nullable Context context) {
        final List<ContactBean> list = new ArrayList<>();
        final Cursor cursor = getCursor(context, Phone.CONTENT_URI, null,
                null, null, null);
        if (cursor == null) {
            return list;
        }
        if (cursor.getCount() == 0) {
            IOUtil.close(cursor);
            return list;
        }
        final StringBuilder sb = new StringBuilder();
        ContactBean bean = null;
        if (cursor.moveToFirst()) {
            do {
                final int has = cursor.getInt(cursor.getColumnIndexOrThrow(Phone.HAS_PHONE_NUMBER));
                if (has <= 0) continue;
                bean = new ContactBean(cursor.getInt(cursor.getColumnIndexOrThrow(Phone.CONTACT_ID)));
                bean.name = cursor.getString(cursor.getColumnIndexOrThrow(Phone.DISPLAY_NAME));
                bean.photo = cursor.getString(cursor.getColumnIndexOrThrow(Phone.PHOTO_URI));
                bean.number = cursor.getString(cursor.getColumnIndexOrThrow(Phone.NUMBER));
                String data2 = cursor.getString(cursor.getColumnIndexOrThrow("data2"));
                String data4 = cursor.getString(cursor.getColumnIndexOrThrow("data4"));
                sb.append("name  :").append(bean.name).append('\n');
                sb.append("photo :").append(bean.photo).append('\n');
                sb.append("number:").append(bean.number).append('\n');
                sb.append("data2 :").append(data2).append('\n');
                sb.append("data4 :").append(data4).append('\n');
                list.add(bean);
            } while (cursor.moveToNext());
        }
        LogUtil.e(sb.toString());
        IOUtil.close(cursor);
        return list;
    }

    @Nullable
    private static Cursor getCursor(@Nullable Context context, @Nullable Uri uri, @Nullable String[] projection, @Nullable String selection,
                                    @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        if (context == null || uri == null) {
            return null;
        }
        return context.getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
    }


}
