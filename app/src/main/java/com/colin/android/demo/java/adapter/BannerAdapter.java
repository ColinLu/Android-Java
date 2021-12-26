package com.colin.android.demo.java.adapter;

import android.content.Context;
import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.android.demo.java.R;
import com.colin.android.demo.java.base.BaseRecyclerAdapter;
import com.colin.android.demo.java.base.BaseViewHolder;
import com.colin.android.demo.java.ui.home.HomeFragment;

import java.util.List;

/**
 * 作者： ColinLu
 * 时间： 2021-12-14 22:55
 * <p>
 * 描述： TODO
 */
public class BannerAdapter extends BaseRecyclerAdapter<Integer> {
    public BannerAdapter(@NonNull Context context, @Nullable List<Integer> integers) {
        super(context, integers);
    }

    @LayoutRes
    @Override
    public int layoutRes(int type) {
        return R.layout.item_banner;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        assert mItemList != null;
        final Integer res = mItemList.get(position);
        holder.setImageResource(R.id.image_banner, res)
                .setOnClickListener(R.id.image_banner, v -> {
                    if (mItemClickListener != null) {
                        mItemClickListener.item(v, position, res);
                    }
                });
    }
}
