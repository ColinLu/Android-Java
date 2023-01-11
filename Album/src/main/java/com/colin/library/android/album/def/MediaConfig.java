package com.colin.library.android.album.def;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;

import java.util.Locale;

/**
 * 作者： ColinLu
 * 时间： 2023-01-04 21:22
 * <p>
 * 描述： TODO
 */
public final class MediaConfig {
    @NonNull
    private final MediaLoader mLoader;
    @NonNull
    private final Locale mLocale;

    @NonNull
    private final String mAuthority;      //生成文件路径
    @IntRange(from = 2, to = 5)
    private final int mColumn;

    public MediaConfig(@NonNull Builder builder) {
        this.mLoader = builder.mLoader;
        this.mLocale = builder.mLocale;
        this.mAuthority = builder.mAuthority;
        this.mColumn = builder.mColumn;
    }

    @NonNull
    public MediaLoader getMediaLoader() {
        return mLoader;
    }

    @NonNull
    public Locale getLocale() {
        return mLocale;
    }

    @IntRange(from = 2, to = 5)
    public int getColumn() {
        return mColumn;
    }

    @NonNull
    public String getAuthority() {
        return mAuthority;
    }

    public static class Builder {
        @NonNull
        private Locale mLocale = Locale.getDefault();
        @NonNull
        private MediaLoader mLoader = MediaLoader.DEFAULT;
        @NonNull
        private String mAuthority = "/external_media";
        @IntRange(from = 2, to = 5)
        private int mColumn = 3;

        public Builder setMediaLoader(@NonNull MediaLoader loader) {
            this.mLoader = loader;
            return this;
        }

        public Builder setLocale(@NonNull Locale locale) {
            this.mLocale = locale;
            return this;
        }

        public Builder setColumn(@IntRange(from = 2, to = 5) int column) {
            mColumn = column;
            return this;
        }

        public MeidaConfig build() {
            return new MediaConfig(this);
        }


    }
}
