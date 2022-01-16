package com.colin.android.demo.java.adapter;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.android.demo.java.R;
import com.colin.library.android.base.BaseAdapter;
import com.colin.library.android.base.BaseViewHolder;

import java.util.List;

/**
 * 作者： ColinLu
 * 时间： 2021-12-14 22:55
 * <p>
 * 描述： TODO
 */
public class BannerAdapter extends BaseAdapter<Integer> {
    public BannerAdapter(@Nullable List<Integer> integers) {
        super(integers);
    }

    @LayoutRes
    @Override
    public int layoutRes(int type) {
        return R.layout.item_banner;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        final Integer res = mItemList.get(position);
        holder.setImageResource(R.id.image_banner, res)
                .setOnClickListener(R.id.image_banner, v -> {
                    if (mItemClickListener != null) {
                        mItemClickListener.item(v, position, res);
                    }
                });
    }
}
