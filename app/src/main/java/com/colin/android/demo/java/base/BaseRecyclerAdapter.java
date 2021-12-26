package com.colin.android.demo.java.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.colin.android.demo.java.widgets.def.OnItemClickListener;

import java.util.List;

/**
 * 作者： ColinLu
 * 时间： 2021-12-14 23:05
 * <p>
 * 描述： Adapter 基类
 */
public abstract class BaseRecyclerAdapter<ITEM> extends RecyclerView.Adapter<BaseViewHolder> {
    protected final Context mContext;
    protected final List<ITEM> mItemList;
    protected final LayoutInflater mLayoutInflater;
    protected OnItemClickListener mItemClickListener;

    public BaseRecyclerAdapter(@NonNull Context context, @Nullable List<ITEM> itemList) {
        this.mContext = context;
        this.mItemList = itemList;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    public void setOnItemClickListener(@Nullable OnItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    @Override
    public int getItemCount() {
        return mItemList == null ? 0 : mItemList.size();
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BaseViewHolder(mLayoutInflater.inflate(layoutRes(viewType), parent, false));
    }

    @LayoutRes
    public abstract int layoutRes(int type);

}
