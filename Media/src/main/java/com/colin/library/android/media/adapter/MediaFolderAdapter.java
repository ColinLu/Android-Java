package com.colin.library.android.media.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.colin.library.android.base.BaseAdapter;
import com.colin.library.android.base.ViewHolder;
import com.colin.library.android.media.R;
import com.colin.library.android.media.def.MediaFile;
import com.colin.library.android.media.def.MediaFolder;
import com.colin.library.android.utils.ResourceUtil;

import java.util.List;

public class MediaFolderAdapter extends BaseAdapter<MediaFolder> {
    private final LayoutInflater mInflater;
    private int mPosition;


    public MediaFolderAdapter(@NonNull Context context, List<MediaFolder> list, int position) {
        super(list);
        this.mPosition = position;
        this.mInflater = LayoutInflater.from(context);
    }

    public MediaFolderAdapter setPosition(int position) {
        this.mPosition = position;
        return this;
    }

    @Override
    public int layoutRes(int viewType) {
        return R.layout.item_media_folder;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(layoutRes(viewType), parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) onBindViewHolder(holder, position);
        else {
            holder.setChecked(R.id.check_item_media_selected, mPosition == position);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final MediaFolder mediaFolder = mItemList.get(position);
        final List<MediaFile> list = mediaFolder.mList;
        holder.setText(R.id.text_item_media_folder, mediaFolder.mName)
                .append(R.id.text_item_media_folder, getSelectedCount(list))
                .setText(R.id.text_item_media_folder_size, ResourceUtil.getString(R.string.media_folder_size, list.size()))
                .setChecked(R.id.check_item_media_selected, mPosition == position)
                .setOnClickListener(R.id.layout_item, new MyOnClickListener(position, mediaFolder));
    }


    private String getSelectedCount(List<MediaFile> list) {
        int count = 0;
        for (MediaFile albumFile : list) if (albumFile.mSelected) count += 1;
        return (count == 0) ? "" : ResourceUtil.getString(R.string.media_count, count);
    }


    private class MyOnClickListener implements View.OnClickListener {
        private final MediaFolder mMediaFolder;
        private final int mPosition;

        public MyOnClickListener(int position, MediaFolder folder) {
            this.mMediaFolder = folder;
            this.mPosition = position;
        }

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) mItemClickListener.item(v, mPosition, mMediaFolder);
        }
    }


}
