package com.colin.library.android.media.def;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * 作者： ColinLu
 * 时间： 2023-01-07 03:19
 * <p>
 * 描述： TODO
 */
public class PreviewFile implements Parcelable {
    public int mOriginLeft;
    public int mOriginTop;
    public int mOriginWidth;
    public int mOriginHeight;
    public int mDrawableRes;
    public String mImageUrl;
    public String mMimeType;
    public Bitmap mBitmap;
    public boolean mSelected;

    protected PreviewFile(Parcel in) {
        mOriginLeft = in.readInt();
        mOriginTop = in.readInt();
        mOriginWidth = in.readInt();
        mOriginHeight = in.readInt();
        mDrawableRes = in.readInt();
        mImageUrl = in.readString();
        mMimeType = in.readString();
        mSelected = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mOriginLeft);
        dest.writeInt(mOriginTop);
        dest.writeInt(mOriginWidth);
        dest.writeInt(mOriginHeight);
        dest.writeInt(mDrawableRes);
        dest.writeString(mImageUrl);
        dest.writeString(mMimeType);
        dest.writeByte((byte) (mSelected ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PreviewFile> CREATOR = new Creator<PreviewFile>() {
        @Override
        public PreviewFile createFromParcel(Parcel in) {
            return new PreviewFile(in);
        }

        @Override
        public PreviewFile[] newArray(int size) {
            return new PreviewFile[size];
        }
    };
}
