package com.colin.library.android.media.def;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者： ColinLu
 * 时间： 2020-04-17 22:47
 * <p>
 * 描述： 相册文件夹
 */
public class MediaFolder implements Parcelable {
    public final String mName;
    public List<MediaFile> mList = new ArrayList<>();

    public MediaFolder(@NonNull String name) {
        this.mName = name;
    }

    protected MediaFolder(Parcel in) {
        mName = in.readString();
        mList = in.createTypedArrayList(MediaFile.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeTypedList(mList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MediaFolder> CREATOR = new Creator<MediaFolder>() {
        @Override
        public MediaFolder createFromParcel(Parcel in) {
            return new MediaFolder(in);
        }

        @Override
        public MediaFolder[] newArray(int size) {
            return new MediaFolder[size];
        }
    };

    public void add(@NonNull MediaFile mediaFile) {
        if (null == mList) mList = new ArrayList<>();
        mList.add(mediaFile);
    }
}