package com.colin.library.android.media.def;

import android.Manifest;

/**
 * 作者： ColinLu
 * 时间： 2023-01-04 21:27
 * <p>
 * 描述： TODO
 */
public interface Constants {
    String MEDIA_TITLE = "MEDIA_TITLE";
    String MEDIA_TYPE = "MEDIA_TYPE";
    String MEDIA_URI = "MEDIA_URI";
    String MEDIA_NEED_CROP = "MEDIA_NEED_CROP";
    String MEDIA_CAMERA_FACING = "MEDIA_CAMERA_FACING";
    String MEDIA_DISPLAY_CAMERA = "MEDIA_DISPLAY_CAMERA";
    String MEDIA_DISPLAY_INVALID = "MEDIA_DISPLAY_INVALID";
    String MEDIA_LIST_COLUMN = "MEDIA_LIST_COLUMN";
    String MEDIA_LIMIT_COUNT = "MEDIA_CHOOSE_LIMIT_COUNT";
    String MEDIA_LIMIT_QUALITY = "MEDIA_CHOOSE_LIMIT_QUALITY";
    String MEDIA_LIMIT_DURATION = "MEDIA_CHOOSE_LIMIT_DURATION";
    String MEDIA_LIMIT_SIZE = "MEDIA_CHOOSE_LIMIT_SIZE";
    String MEDIA_SELECTED_LIST = "MEDIA_SELECTED_LIST";
    String MEDIA_MULTIPLE_MODE = "MEDIA_MULTIPLE_MODE";

    String ADAPTER_PAYLOAD_CHECK = "ADAPTER_PAYLOAD_CHECK";

    /*浏览相册权限*/
    String[] PERMISSION_MEDIA = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};

    /*下载权限*/
    String[] PERMISSION_DOWN = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /*录音权限*/
    String[] PERMISSION_AUDIO = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};


    /*相机拍照*/
    String[] PERMISSION_IMAGE = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA};


    /*录像视频*/
    String[] PERMISSION_VIDEO = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA};

    int REQUEST_MEDIA = 1000;
    int REQUEST_IMAGE = 1001;
    int REQUEST_VIDEO = 1002;
    int REQUEST_AUDIO = 1003;
    int REQUEST_DOWN = 1004;

    long DEFAULT_LIMIT_SIZE = 20 * 1024 * 1024;     //默认文件 大小20M
    long DEFAULT_LIMIT_DURATION = 20 * 1000;        //默认视频 时长10s
    int DEFAULT_LIMIT_QUALITY = 1;                  //默认视频 质量1
}
