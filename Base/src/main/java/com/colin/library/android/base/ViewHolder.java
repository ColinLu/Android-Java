package com.colin.library.android.base;

import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.text.util.Linkify;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.Dimension;
import androidx.annotation.DrawableRes;
import androidx.annotation.FloatRange;
import androidx.annotation.IdRes;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.RecyclerView;

import com.colin.library.android.utils.StringUtil;

/**
 * 作者： ColinLu
 * 时间： 2021-12-14 22:56
 * <p>
 * 描述： ViewHolder 基类
 */
public class ViewHolder extends RecyclerView.ViewHolder {
    @NonNull
    private final SparseArray<View> mArray;

    public ViewHolder(@NonNull View itemView) {
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

    ///////////////////////////////////////////////////////////////////////////
    // View  重用属性
    ///////////////////////////////////////////////////////////////////////////
    @NonNull
    public ViewHolder setDescription(@IdRes int idRes, @Nullable CharSequence text) {
        final View view = getView(idRes);
        view.setContentDescription(text);
        return this;
    }

    @NonNull
    public ViewHolder setTag(@IdRes int idRes, @Nullable Object tag) {
        final View view = getView(idRes);
        view.setTag(tag);
        return this;
    }

    @NonNull
    public ViewHolder setTag(@IdRes int idRes, int key, @Nullable Object tag) {
        final View view = getView(idRes);
        view.setTag(key, tag);
        return this;
    }

    @NonNull
    public ViewHolder setBackground(@IdRes int idRes, @Nullable Drawable drawable) {
        final View view = getView(idRes);
        view.setBackground(drawable);
        return this;
    }

    @NonNull
    public ViewHolder setBackgroundColor(@IdRes int idRes, @ColorInt int color) {
        final View view = getView(idRes);
        view.setBackgroundColor(color);
        return this;
    }

    @NonNull
    public ViewHolder setBackgroundRes(@IdRes int idRes, @DrawableRes int drawableRes) {
        final View view = getView(idRes);
        view.setBackgroundResource(drawableRes);
        return this;
    }

    @NonNull
    public ViewHolder setAlpha(@IdRes int idRes, @FloatRange(from = 0.0F, to = 1.0F) float value) {
        final View view = getView(idRes);
        view.setAlpha(value);
        return this;
    }

    @NonNull
    public ViewHolder setVisible(@IdRes int idRes, boolean visible) {
        final View view = getView(idRes);
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }

    @NonNull
    public ViewHolder setVisible(@IdRes int idRes, int visible) {
        final View view = getView(idRes);
        view.setVisibility(visible);
        return this;
    }

    @NonNull
    public ViewHolder setSelected(@IdRes int idRes, boolean select) {
        final View view = getView(idRes);
        view.setSelected(select);
        return this;
    }

    @NonNull
    public ViewHolder setEnable(@IdRes int idRes, boolean enable) {
        final View view = getView(idRes);
        view.setClickable(enable);
        view.setEnabled(enable);
        return this;
    }

    @NonNull
    public ViewHolder setOnClickListener(@IdRes int idRes, @Nullable View.OnClickListener listener) {
        final View view = getView(idRes);
        view.setOnClickListener(listener);
        return this;
    }

    @NonNull
    public ViewHolder setOnTouchListener(@IdRes int idRes, @Nullable View.OnTouchListener listener) {
        final View view = getView(idRes);
        view.setOnTouchListener(listener);
        return this;
    }

    @NonNull
    public ViewHolder setOnLongClickListener(@IdRes int idRes, @Nullable View.OnLongClickListener listener) {
        final View view = getView(idRes);
        view.setOnLongClickListener(listener);
        return this;
    }

    ///////////////////////////////////////////////////////////////////////////
    // TextView
    ///////////////////////////////////////////////////////////////////////////
    @NonNull
    public ViewHolder setText(@IdRes int idRes, @StringRes int stringRes) {
        final TextView view = getView(idRes);
        view.setText(stringRes);
        return this;
    }

    @NonNull
    public ViewHolder setText(@IdRes int idRes, @Nullable CharSequence text) {
        final TextView view = getView(idRes);
        view.setText(text);
        return this;
    }

    @NonNull
    public ViewHolder append(@IdRes int idRes, @Nullable CharSequence text) {
        final TextView view = getView(idRes);
        view.append(text);
        return this;
    }


    @NonNull
    public ViewHolder append(@IdRes int idRes, @StringRes int stringRes) {
        final TextView view = getView(idRes);
        view.append(view.getContext().getString(stringRes));
        return this;
    }

    @NonNull
    public ViewHolder setTextColor(@IdRes int idRes, @ColorInt int colorInt) {
        final TextView view = getView(idRes);
        view.setTextColor(colorInt);
        return this;
    }

    @NonNull
    public ViewHolder setTextSize(@IdRes int idRes, @Dimension int size) {
        final TextView view = getView(idRes);
        view.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
        return this;
    }

    @NonNull
    public ViewHolder setHtml(@IdRes int idRes, @Nullable String html) {
        final TextView view = getView(idRes);
        if (StringUtil.isEmpty(html)) return this;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) view.setText(Html.fromHtml(html));
        else view.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY));
        return this;
    }

    @NonNull
    public ViewHolder setTextBold(@IdRes int idRes, boolean bold) {
        final TextView view = getView(idRes);
        view.getPaint().setFakeBoldText(bold);
        return this;
    }

    @NonNull
    public ViewHolder linkify(@IdRes int idRes) {
        final TextView view = getView(idRes);
        Linkify.addLinks(view, Linkify.ALL);
        return this;
    }

    @NonNull
    public ViewHolder setTypeface(@IdRes int idRes, @NonNull Typeface typeface) {
        final TextView view = getView(idRes);
        view.setTypeface(typeface);
        view.setPaintFlags(view.getPaintFlags() | Paint.SUBPIXEL_TEXT_FLAG);
        return this;
    }

    ///////////////////////////////////////////////////////////////////////////
    // ImageView
    ///////////////////////////////////////////////////////////////////////////
    @NonNull
    public ViewHolder setImageResource(@IdRes int idRes, @DrawableRes int drawableRes) {
        final ImageView view = getView(idRes);
        view.setImageResource(drawableRes);
        return this;
    }


    @NonNull
    public ViewHolder setImageDrawable(@IdRes int idRes, @Nullable Drawable drawable) {
        final ImageView view = getView(idRes);
        view.setImageDrawable(drawable);
        return this;
    }

    @NonNull
    public ViewHolder setImageURI(@IdRes int idRes, @Nullable Uri uri) {
        final ImageView view = getView(idRes);
        view.setImageURI(uri);
        return this;
    }

    @NonNull
    public ViewHolder setImageBitmap(@IdRes int idRes, @Nullable Bitmap bitmap) {
        final ImageView view = getView(idRes);
        view.setImageBitmap(bitmap);
        return this;
    }

    @NonNull
    public ViewHolder setChecked(@IdRes int idRes, boolean checked) {
        final CompoundButton view = getView(idRes);
        view.setChecked(checked);
        return this;
    }

    @NonNull
    public ViewHolder setOnCheckedChangeListener(@IdRes int idRes, @Nullable CompoundButton.OnCheckedChangeListener listener) {
        final CompoundButton view = getView(idRes);
        view.setOnCheckedChangeListener(listener);
        return this;
    }

    @NonNull
    public ViewHolder setProgress(@IdRes int idRes, @IntRange(from = 0, to = 100) int progress) {
        final ProgressBar view = getView(idRes);
        view.setProgress(progress);
        return this;
    }

    @NonNull
    public ViewHolder setProgressMax(@IdRes int idRes, @IntRange(from = 0, to = 100) int max) {
        final ProgressBar view = getView(idRes);
        view.setMax(max);
        return this;
    }

    @NonNull
    public ViewHolder setProgress(@IdRes int idRes, @IntRange(from = 0, to = 100) int max, @IntRange(from = 0, to = 100) int progress) {
        final ProgressBar view = getView(idRes);
        view.setMax(max);
        view.setProgress(progress);
        return this;
    }


    @NonNull
    public ViewHolder setRating(@IdRes int idRes, float rating) {
        final RatingBar view = getView(idRes);
        view.setRating(rating);
        return this;
    }


    @NonNull
    public ViewHolder setRating(@IdRes int idRes, int max, float rating) {
        final RatingBar view = getView(idRes);
        view.setMax(max);
        view.setRating(rating);
        return this;
    }


    @NonNull
    public ViewHolder setVisible(boolean show) {
        ViewGroup.LayoutParams param = itemView.getLayoutParams();
        if (show) {
            param.height = LinearLayout.LayoutParams.MATCH_PARENT;
            param.width = LinearLayout.LayoutParams.MATCH_PARENT;
            itemView.setVisibility(View.VISIBLE);
        } else {
            itemView.setVisibility(View.GONE);
            param.height = 0;
            param.width = 0;
        }
        itemView.setLayoutParams(param);
        return this;
    }


}
