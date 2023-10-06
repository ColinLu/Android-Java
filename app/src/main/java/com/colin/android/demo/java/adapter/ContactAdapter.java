package com.colin.android.demo.java.adapter;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.colin.android.demo.java.R;
import com.colin.android.demo.java.def.bean.ContactBean;
import com.colin.library.android.base.BaseAdapter;
import com.colin.library.android.base.ViewHolder;

/**
 * 作者： ColinLu
 * 时间： 2022-01-30 00:12
 * <p>
 * 描述： 联系人适配器
 */
public class ContactAdapter extends BaseAdapter<ContactBean> {
    public ContactAdapter() {
        super();
    }

    @Override
    public int layoutRes(int type) {
        return R.layout.item_contact;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final ContactBean bean = mItemList.get(position);
        holder.setImageURI(R.id.mContactPhoto, bean.photo == null ? null : Uri.parse(bean.photo)).setText(R.id.mContactName, bean.name)
              .setText(R.id.mContactNumber, bean.number).setOnClickListener(R.id.mItemLayout, v -> {
                  if (mItemClickListener != null) mItemClickListener.item(v, position, bean);

              });
    }
}