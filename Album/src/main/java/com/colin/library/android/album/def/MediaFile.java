package com.colin.library.android.album.def;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

/**
 * 作者： ColinLu
 * 时间： 2023-01-04 22:56
 * <p>
 * 描述： 构建多媒体对象
 */
public class MediaFile implements Parcelable, Comparable<MediaFile> {
    /*资源ID*/
    public int id;
    /*文件路径*/
    public String mPath;
    /*文件Uri路径*/
    public Uri mUri;
    /*文件夹*/
    public String mFolder;
    @MediaType
    public int mMediaType;
    /*文件格式*/
    public String mMimeType;
    /*文件宽度*/
    public int mWidth;
    /*文件高度*/
    public int mHeight;
    /*文件大小*/
    public long mSize;
    /*文件生成时间*/
    public long mDate;
    /*视频时长*/
    public long mDuration;
    /*被选中*/
    public boolean mSelected;
    /*是否筛选不符合要求*/
    public boolean mFilter;

    public MediaFile() {
    }

    public MediaFile(@MediaType int mediaType) {
        this.mMediaType = mediaType;
    }

    protected MediaFile(Parcel in) {
        id = in.readInt();
        mPath = in.readString();
        mUri = in.readParcelable(Uri.class.getClassLoader());
        mFolder = in.readString();
        mMediaType = in.readInt();
        mMimeType = in.readString();
        mWidth = in.readInt();
        mHeight = in.readInt();
        mSize = in.readLong();
        mDate = in.readLong();
        mDuration = in.readLong();
        mSelected = in.readByte() != 0;
        mFilter = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(mPath);
        dest.writeParcelable(mUri, flags);
        dest.writeString(mFolder);
        dest.writeInt(mMediaType);
        dest.writeString(mMimeType);
        dest.writeInt(mWidth);
        dest.writeInt(mHeight);
        dest.writeLong(mSize);
        dest.writeLong(mDate);
        dest.writeLong(mDuration);
        dest.writeByte((byte) (mSelected ? 1 : 0));
        dest.writeByte((byte) (mFilter ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<MediaFile> CREATOR = new Parcelable.Creator<MediaFile>() {
        @Override
        public MediaFile createFromParcel(Parcel in) {
            return new MediaFile(in);
        }

        @Override
        public MediaFile[] newArray(int size) {
            return new MediaFile[size];
        }
    };

    @Override
    public int compareTo(MediaFile o) {
        long time = o.mDate - mDate;
        if (time > Integer.MAX_VALUE) return Integer.MAX_VALUE;
        else if (time < -Integer.MAX_VALUE) return -Integer.MAX_VALUE;
        return (int) time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MediaFile)) return false;
        MediaFile albumFile = (MediaFile) o;
        return mPath != null && TextUtils.equals(mPath, albumFile.mPath);
    }

    @Override
    public int hashCode() {
        return mPath != null ? mPath.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "MediaFile{" +
                "id=" + id +
                ", mPath='" + mPath + '\'' +
                ", mUri=" + mUri +
                ", mFolder='" + mFolder + '\'' +
                ", mMediaType=" + mMediaType +
                ", mMimeType='" + mMimeType + '\'' +
                ", mWidth=" + mWidth +
                ", mHeight=" + mHeight +
                ", mSize=" + mSize +
                ", mDate=" + mDate +
                ", mDuration=" + mDuration +
                ", mSelected=" + mSelected +
                ", mFilter=" + mFilter +
                '}';
    }
}
