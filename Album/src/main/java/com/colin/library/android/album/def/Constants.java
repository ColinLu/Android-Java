package com.colin.library.android.album.def;

import android.Manifest;

/**
 * 作者： ColinLu
 * 时间： 2023-01-04 21:27
 * <p>
 * 描述： TODO
 */
public interface Constants {
    String ALBUM_TITLE = "ALBUM_TITLE";
    String ALBUM_MEDIA_TYPE = "ALBUM_MEDIA_TYPE";
    String ALBUM_URI = "ALBUM_URI";
    String ALBUM_NEED_CROP = "ALBUM_NEED_CROP";
    String ALBUM_CAMERA_FACING = "ALBUM_CAMERA_FACING";
    String ALBUM_DISPLAY_CAMERA = "ALBUM_DISPLAY_CAMERA";
    String ALBUM_DISPLAY_INVALID = "ALBUM_DISPLAY_INVALID";
    String ALBUM_LIST_COLUMN = "ALBUM_LIST_COLUMN";
    String ALBUM_LIMIT_COUNT = "ALBUM_CHOOSE_LIMIT_COUNT";
    String ALBUM_LIMIT_QUALITY = "ALBUM_CHOOSE_LIMIT_QUALITY";
    String ALBUM_LIMIT_DURATION = "ALBUM_CHOOSE_LIMIT_DURATION";
    String ALBUM_LIMIT_SIZE = "ALBUM_CHOOSE_LIMIT_SIZE";
    String ALBUM_SELECTED_LIST = "ALBUM_SELECTED_LIST";

    /*浏览相册权限*/
    String[] PERMISSION_ALBUM = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};

    /*下载权限*/
    String[] PERMISSION_DOWN = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /*录音权限*/
    String[] PERMISSION_AUDIO = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};


    /*相机拍照*/
    String[] PERMISSION_CAMERA = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};


    /*录像视频*/
    String[] PERMISSION_VIDEO = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA};

    int REQUEST_ALBUM = 1000;
    int REQUEST_IMAGE = 1001;
    int REQUEST_VIDEO = 1002;
    int REQUEST_AUDIO = 1003;
    int REQUEST_DOWN = 1004;

    long DEFAULT_LIMIT_SIZE = 20 * 1024 * 1024;     //默认文件 大小20M
    long DEFAULT_LIMIT_DURATION = 20 * 1000;        //默认视频 时长10s
    int DEFAULT_LIMIT_QUALITY = 1;                  //默认视频 质量1
}
