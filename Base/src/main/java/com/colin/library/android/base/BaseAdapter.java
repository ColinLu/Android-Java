package com.colin.library.android.base;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.colin.library.android.widgets.def.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者： ColinLu
 * 时间： 2022-01-15 11:30
 * <p>
 * 描述： TODO
 */
public abstract class BaseAdapter<ITEM> extends RecyclerView.Adapter<BaseViewHolder> {
    protected final List<ITEM> mItemList;
    protected OnItemClickListener mItemClickListener;

    public BaseAdapter() {
        this(null);
    }

    public BaseAdapter(@Nullable List<ITEM> list) {
        mItemList = new ArrayList<>();
        if (list != null && list.size() > 0) mItemList.addAll(list);
    }

    public void setOnItemClickListener(@Nullable OnItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BaseViewHolder(LayoutInflater.from(parent.getContext()).inflate(layoutRes(viewType), parent, false));
    }


    @LayoutRes
    public abstract int layoutRes(int viewType);

    public void setData(List<ITEM> items) {
        mItemList.clear();
        if (items != null && items.size() > 0) mItemList.addAll(items);
        notifyDataSetChanged();
    }


    @SuppressLint("NotifyDataSetChanged")
    public void addData(List<ITEM> items) {
        if (items != null && items.size() > 0) {
            mItemList.addAll(items);
            notifyDataSetChanged();
        }
    }
}
