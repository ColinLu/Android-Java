package com.colin.android.demo.java.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.SyncStateContract;
import android.util.Log;

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
            Phone._ID,
            Phone.CONTACT_ID,
            Phone.DISPLAY_NAME,
            Phone.NUMBER,
            Phone.PHOTO_URI
    };

    @NonNull
    public static List<ContactBean> getContactList(@Nullable Context context) {
        final List<ContactBean> list = new ArrayList<>();
        print(context, Phone.CONTENT_URI);
        print(context, ContactsContract.RawContacts.CONTENT_URI);
        print(context, ContactsContract.Contacts.CONTENT_URI);
        print(context, ContactsContract.RawContactsEntity.CONTENT_URI);
        print(context, ContactsContract.RawContactsEntity.CONTENT_URI);
        final Cursor cursor = getCursor(context, Phone.CONTENT_URI, PROJECTION_CONTACT_LIST,
                null, null, null);
        if (cursor == null) {
            return list;
        }
        if (cursor.getCount() == 0) {
            cursor.close();
            return list;
        }
        final StringBuilder sb = new StringBuilder();
        ContactBean bean = null;
        if (cursor.moveToFirst()) {
            do {
                bean = new ContactBean(cursor.getLong(cursor.getColumnIndexOrThrow(Phone._ID)));
                bean.contact_id = cursor.getLong(cursor.getColumnIndexOrThrow(Phone.CONTACT_ID));
                bean.name = cursor.getString(cursor.getColumnIndexOrThrow(Phone.DISPLAY_NAME));
                bean.number = cursor.getString(cursor.getColumnIndexOrThrow(Phone.NUMBER));
                bean.photo = cursor.getString(cursor.getColumnIndexOrThrow(Phone.PHOTO_URI));
                sb.append("id        :").append(bean.id).append('\n');
                sb.append("contact_id:").append(bean.contact_id).append('\n');
                sb.append("name      :").append(bean.name).append('\n');
                sb.append("number    :").append(bean.number).append('\n');
                sb.append("photo     :").append(bean.photo).append('\n');
                list.add(bean);
            } while (cursor.moveToNext());
        }
        LogUtil.e(sb.toString().trim());
        cursor.close();
        return list;
    }

    private static void print(Context context, Uri uri) {
        LogUtil.e(uri.toString());
        final Cursor cursor = getCursor(context, uri, null, null, null, null);
        if (cursor != null) {
            print(cursor);
            cursor.close();
        }
    }

    private static void print(Cursor cursor) {
        final StringBuilder sb = new StringBuilder();
        if (cursor.moveToFirst()) {
            do {
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    final String name = cursor.getColumnName(i);
                    final String value = cursor.getString(i);
                    sb.append("name").append(name).append('\n');
                    sb.append("value").append(value == null ? "null" : value).append('\n');
                }
            } while (cursor.moveToNext());
        }
        LogUtil.e(sb.toString().trim());
    }


    @Nullable
    private static Cursor getCursor(@Nullable Context context, @Nullable Uri uri, @Nullable String[] projection,
                                    @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        if (context == null || uri == null) {
            return null;
        }
        return context.getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
    }

}
