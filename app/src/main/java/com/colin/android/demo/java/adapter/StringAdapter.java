package com.colin.android.demo.java.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.android.demo.java.R;
import com.colin.library.android.base.BaseAdapter;
import com.colin.library.android.base.BaseViewHolder;
import com.colin.library.android.utils.LogUtil;

import java.util.List;

/**
 * 作者： ColinLu
 * 时间： 2021-12-14 23:59
 * <p>
 * 描述： TODO
 */
public class StringAdapter extends BaseAdapter<String> {
    public StringAdapter(@Nullable List<String> strings) {
        super(strings);
    }

    @Override
    public int layoutRes(int type) {
        return R.layout.item_string;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        final String text = mItemList.get(position);
        holder.setText(R.id.button_title, text)
                .setOnClickListener(R.id.button_title, v -> {
                    if (mItemClickListener != null) {
                        mItemClickListener.item(v, position, text);
                    }
                });
    }
}
