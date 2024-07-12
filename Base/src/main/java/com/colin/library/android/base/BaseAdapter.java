package com.colin.library.android.base;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.colin.library.android.widgets.def.OnItemCheckedListener;
import com.colin.library.android.widgets.def.OnItemClickListener;
import com.colin.library.android.widgets.def.OnItemLongClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者： ColinLu
 * 时间： 2022-01-15 11:30
 * 描述： 基类 Adapter
 */
public abstract class BaseAdapter<ITEM> extends RecyclerView.Adapter<ViewHolder> {
    protected final List<ITEM> mItemList;
    protected OnItemClickListener mItemClickListener;
    protected OnItemLongClickListener mItemLongClickListener;
    protected OnItemCheckedListener mItemCheckedListener;

    public BaseAdapter() {
        this(null);
    }

    public BaseAdapter(@Nullable List<ITEM> list) {
        mItemList = new ArrayList<>();
        if (list != null && !list.isEmpty()) mItemList.addAll(list);
    }

    public void setOnItemClickListener(@Nullable OnItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    public void setOnItemLongClickListener(@Nullable OnItemLongClickListener itemLongClickListener) {
        this.mItemLongClickListener = itemLongClickListener;
    }

    public void setOnItemCheckedListener(@Nullable OnItemCheckedListener itemCheckedListener) {
        this.mItemCheckedListener = itemCheckedListener;
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(layoutRes(viewType), parent, false));
    }


    @SuppressLint("NotifyDataSetChanged")
    public void setData(@Nullable List<ITEM> items) {
        mItemList.clear();
        if (items != null && !items.isEmpty()) mItemList.addAll(items);
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void addData(@Nullable List<ITEM> items) {
        final int size = items == null ? 0 : items.size();
        if (size > 0) {
            mItemList.addAll(items);
            notifyDataSetChanged();
        }
    }

    @LayoutRes
    public abstract int layoutRes(int viewType);
}
