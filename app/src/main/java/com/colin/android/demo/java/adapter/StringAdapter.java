package com.colin.android.demo.java.adapter;

import android.view.Gravity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;

import com.colin.android.demo.java.R;
import com.colin.library.android.base.BaseAdapter;
import com.colin.library.android.base.ViewHolder;
import com.colin.library.android.utils.data.Constants;
import com.colin.library.android.widgets.def.OnItemClickListener;

import java.util.List;

/**
 * 作者： ColinLu
 * 时间： 2021-12-14 23:59
 * <p>
 * 描述： String list
 */
public class StringAdapter extends BaseAdapter<String> {
    private int mGravity = Gravity.CENTER;
    private int mSelected = Constants.INVALID;

    public StringAdapter(@Nullable OnItemClickListener listener) {
        this(null, listener);
    }

    public StringAdapter(@Nullable List<String> list, @Nullable OnItemClickListener listener) {
        super(list, listener);
    }

    public void setGravity(int gravity) {
        this.mGravity = gravity;
    }

    public void setSelected(int selected) {
        mSelected = selected;
    }

    public int getSelected() {
        return mSelected;
    }

    @Override
    public int layoutRes(int type) {
        return R.layout.item_string;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final String text = mItemList.get(position);
        ((AppCompatButton) holder.getView(R.id.button_title)).setGravity(mGravity);
        holder.setText(R.id.button_title, text).setOnClickListener(R.id.button_title, v -> {
            if (mItemClickListener != null) mItemClickListener.item(v, position, text);
        }).setSelected(R.id.button_title, mSelected == position);
    }
}
