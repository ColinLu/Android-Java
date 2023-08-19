package com.colin.library.android.media.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;

import com.colin.library.android.base.BaseAdapter;
import com.colin.library.android.base.ViewHolder;
import com.colin.library.android.media.MediaHelper;
import com.colin.library.android.media.R;
import com.colin.library.android.media.def.MediaFile;
import com.colin.library.android.media.def.MediaType;
import com.colin.library.android.utils.TimeUtil;

import java.util.List;

/**
 * 作者： ColinLu
 * 时间： 2020-04-05 12:48
 * <p>
 * 描述： 相册 图片适配器 （拍照）
 */
public class MediaAdapter extends BaseAdapter<MediaFile> {
    private static final int TYPE_CAMERA = 0;
    private static final int TYPE_MEDIA = 1;
    private final boolean mDisplayCamera;
    private final boolean mMultipleMode;
    private final LayoutInflater mInflater;

    public MediaAdapter(@NonNull Context context, boolean displayCamera, boolean multipleMode) {
        super();
        this.mInflater = LayoutInflater.from(context);
        this.mDisplayCamera = displayCamera;
        this.mMultipleMode = multipleMode;
    }

    @Override
    public int getItemCount() {
        final int camera = mDisplayCamera ? 1 : 0;
        return null == mItemList ? camera : mItemList.size() + camera;
    }

    @Override
    public int getItemViewType(int position) {
        return isDisplayCamera(position) ? TYPE_CAMERA : TYPE_MEDIA;
    }

    @Override
    public int layoutRes(int viewType) {
        return viewType == TYPE_CAMERA ? R.layout.item_camera : R.layout.item_media;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) onBindViewHolder(holder, position);
        else {
            final MediaFile mediaFile = mItemList.get(mDisplayCamera ? position - 1 : position);
            holder.setOnCheckedChangeListener(R.id.check_item_media_selected, null)
                    .setChecked(R.id.check_item_media_selected, mediaFile.mSelected)
                    .setOnCheckedChangeListener(R.id.check_item_media_selected, new MyOnCheckedChangeListener(position, mediaFile));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final int viewType = getItemViewType(position);
        if (viewType == TYPE_CAMERA) {
            holder.setOnClickListener(R.id.image_album_camera, new MyOnClickListener(position));
        } else {
            final MediaFile mediaFile = mItemList.get(mDisplayCamera ? position - 1 : position);
            holder.setOnCheckedChangeListener(R.id.check_item_media_selected, null)
                    .setVisible(R.id.text_item_media_duration, mediaFile.mMediaType == MediaType.AUDIO || mediaFile.mMediaType == MediaType.VIDEO)
                    .setVisible(R.id.check_item_media_selected, mMultipleMode)
                    .setVisible(R.id.text_item_media_invalid, mediaFile.mInvalid)
                    .setText(R.id.text_item_media_duration, TimeUtil.formatDuration(mediaFile.mDuration))
                    .setChecked(R.id.check_item_media_selected, mediaFile.mSelected)
                    .setOnClickListener(R.id.image_item_media, new MyOnClickListener(position, mediaFile))
                    .setOnClickListener(R.id.text_item_media_invalid, new MyOnClickListener(position, mediaFile))
                    .setOnCheckedChangeListener(R.id.check_item_media_selected, new MyOnCheckedChangeListener(position, mediaFile));
            MediaHelper.getInstance().getMediaConfig().getMediaLoader().load(holder.getView(R.id.image_item_media), mediaFile);
        }
    }

    private boolean isDisplayCamera(int position) {
        return mDisplayCamera && position == 0;
    }

    private class MyOnClickListener implements View.OnClickListener {
        private final MediaFile mMediaFile;
        private final int mPosition;

        protected MyOnClickListener(int position) {
            this.mPosition = position;
            this.mMediaFile = null;
        }

        protected MyOnClickListener(int position, MediaFile albumFile) {
            this.mPosition = position;
            this.mMediaFile = albumFile;
        }

        @Override
        public void onClick(@NonNull View v) {
            if (mItemClickListener != null) mItemClickListener.item(v, mPosition, mMediaFile);
        }
    }

    private class MyOnCheckedChangeListener implements CompoundButton.OnCheckedChangeListener {
        private final MediaFile mMediaFile;
        private final int mPosition;

        protected MyOnCheckedChangeListener(int position, MediaFile albumFile) {
            this.mPosition = position;
            this.mMediaFile = albumFile;
        }

        @Override
        public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
            if (null != mItemCheckedListener)
                mItemCheckedListener.onItemChecked(buttonView, mPosition, mMediaFile);
        }
    }


}
