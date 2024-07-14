package com.colin.android.demo.java.adapter;

import android.view.Gravity;

import androidx.annotation.NonNull;

import com.colin.android.demo.java.R;
import com.colin.library.android.base.BaseAdapter;
import com.colin.library.android.base.ViewHolder;
import com.colin.library.android.widgets.def.OnItemClickListener;

/**
 * 作者： ColinLu
 * 时间： 2021-12-14 23:59
 * <p>
 * 描述： TODO
 */
public class StringAdapter extends BaseAdapter<String> {
    private Gravity mGravity;

    public StringAdapter(OnItemClickListener listener) {
        super();
        this.mItemClickListener = listener;
    }

    @Override
    public int layoutRes(int type) {
        return R.layout.item_string;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final String text = mItemList.get(position);
        holder.setText(R.id.button_title, text).setOnClickListener(R.id.button_title, v -> {
            if (mItemClickListener != null) mItemClickListener.item(v, position, text);
        });
    }
}
