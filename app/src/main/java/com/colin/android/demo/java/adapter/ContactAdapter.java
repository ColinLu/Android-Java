package com.colin.android.demo.java.adapter;

import androidx.annotation.NonNull;

import com.colin.android.demo.java.R;
import com.colin.android.demo.java.def.bean.ContactBean;
import com.colin.library.android.base.BaseAdapter;
import com.colin.library.android.base.BaseViewHolder;

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
        return R.layout.item_string;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        final ContactBean bean = mItemList.get(position);
        holder.setText(R.id.button_title, bean.toString())
                .setOnClickListener(R.id.button_title, v -> {
                    if (mItemClickListener != null) mItemClickListener.item(v, position, bean);

                });
    }
}