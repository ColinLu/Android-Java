package com.colin.library.android.base;

import android.net.Uri;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 作者： ColinLu
 * 时间： 2021-12-14 22:56
 * <p>
 * 描述： ViewHolder 基类
 */
public class BaseViewHolder extends RecyclerView.ViewHolder {
    @NonNull
    private final SparseArray<View> mArray;

    public BaseViewHolder(@NonNull View itemView) {
        super(itemView);
        this.mArray = new SparseArray<>();
    }

    /**
     * @param id view id
     * @return View
     */
    @NonNull
    public <T extends View> T getView(@IdRes int id) {
        View view = mArray.get(id);
        if (null == view) {
            view = itemView.findViewById(id);
            mArray.put(id, view);
        }
        return (T) view;
    }

    @NonNull
    public BaseViewHolder setText(@IdRes int id, @StringRes int res) {
        final TextView textView = getView(id);
        textView.setText(res);
        return this;
    }

    @NonNull
    public BaseViewHolder setText(@IdRes int id, @Nullable CharSequence text) {
        final TextView textView = getView(id);
        textView.setText(text);
        return this;
    }

    @NonNull
    public BaseViewHolder setImageResource(@IdRes int id, @DrawableRes int res) {
        final ImageView imageView = getView(id);
        imageView.setImageResource(res);
        return this;
    }

    @NonNull
    public BaseViewHolder setImageURI(@IdRes int id, @Nullable Uri uri) {
        final ImageView imageView = getView(id);
        imageView.setImageURI(uri);
        return this;
    }

    @NonNull
    public BaseViewHolder setOnClickListener(@IdRes int id, @Nullable View.OnClickListener listener) {
        final View view = getView(id);
        view.setOnClickListener(listener);
        return this;
    }

}
