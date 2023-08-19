package com.colin.library.android.media.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;

import com.colin.library.android.base.BaseActivity;
import com.colin.library.android.media.R;
import com.colin.library.android.media.def.Action;
import com.colin.library.android.media.def.Constants;
import com.colin.library.android.media.def.Facing;
import com.colin.library.android.media.def.MediaFile;
import com.colin.library.android.media.def.MediaType;
import com.colin.library.android.utils.IntentUtil;
import com.colin.library.android.utils.PermissionUtil;

import java.util.List;

/**
 * 作者： ColinLu
 * 时间： 2023-07-22 13:08
 * <p>
 * 描述： 拍照、录像、录音
 */
public class CameraActivity extends BaseActivity {
    public static Action<MediaFile> sResult;                //成功 返回文件路径
    public static Action<String> sCancel;                   //取消 返回原因
    private CharSequence mTitle;                            //显示标题
    @MediaType
    private int mMediaType;
    private Uri mUri;                                       //多媒体文件输出路径
    private int mLimitQuality = 1;                          //视频    限制质量 0 or 1
    private long mLimitDuration = Long.MAX_VALUE;           //视频    限制时间 1 - 0x7fffffffffffffffL
    private long mLimitSize = Long.MAX_VALUE;               //视频    限制大小 1 - 0x7fffffffffffffffL
    private boolean mNeedCrop = false;
    @Facing
    private int mFacing = Facing.BACK;
    private boolean toSettingPermission = false;

    @Override
    public int layoutRes() {
        return R.layout.activity_camera;
    }

    @Override
    public void initView(@Nullable Bundle bundle) {
        if (bundle != null) {
            mTitle = bundle.getCharSequence(Constants.MEDIA_TITLE, null);
            mUri = bundle.getParcelable(Constants.MEDIA_URI);
            mMediaType = bundle.getInt(Constants.MEDIA_TYPE, mMediaType);
            mFacing = bundle.getInt(Constants.MEDIA_CAMERA_FACING, mFacing);
            mLimitQuality = bundle.getInt(Constants.MEDIA_LIMIT_QUALITY, mLimitQuality);
            mLimitDuration = bundle.getLong(Constants.MEDIA_LIMIT_DURATION, mLimitDuration);
            mLimitSize = bundle.getLong(Constants.MEDIA_LIMIT_SIZE, mLimitSize);
            mNeedCrop = bundle.getBoolean(Constants.MEDIA_NEED_CROP, mNeedCrop);
        }
    }

    @Override
    public void initData(@Nullable Bundle bundle) {
        if (bundle != null) {
            mTitle = bundle.getCharSequence(Constants.MEDIA_TITLE, null);
            mUri = bundle.getParcelable(Constants.MEDIA_URI);
            mMediaType = bundle.getInt(Constants.MEDIA_TYPE, mMediaType);
            mFacing = bundle.getInt(Constants.MEDIA_CAMERA_FACING, mFacing);
            mLimitQuality = bundle.getInt(Constants.MEDIA_LIMIT_QUALITY, mLimitQuality);
            mLimitDuration = bundle.getLong(Constants.MEDIA_LIMIT_DURATION, mLimitDuration);
            mLimitSize = bundle.getLong(Constants.MEDIA_LIMIT_SIZE, mLimitSize);
            mNeedCrop = bundle.getBoolean(Constants.MEDIA_NEED_CROP, mNeedCrop);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (toSettingPermission) {
            toSettingPermission = false;
            loadData(true);
        }
    }

    @Override
    public void loadData(boolean refresh) {
        if (mMediaType == MediaType.VIDEO) takeVideo();
        else if (mMediaType == MediaType.AUDIO) takeAudio();
        else takeImage();
    }

    /*界面销毁 存储变量*/
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (!TextUtils.isEmpty(mTitle)) outState.putCharSequence(Constants.MEDIA_TITLE, mTitle);
        outState.putParcelable(Constants.MEDIA_URI, mUri);
        outState.putBoolean(Constants.MEDIA_NEED_CROP, mNeedCrop);
        outState.putInt(Constants.MEDIA_TYPE, mMediaType);
        outState.putInt(Constants.MEDIA_CAMERA_FACING, mFacing);
        outState.putInt(Constants.MEDIA_LIMIT_QUALITY, mLimitQuality);
        outState.putLong(Constants.MEDIA_LIMIT_SIZE, mLimitSize);
        outState.putLong(Constants.MEDIA_LIMIT_DURATION, mLimitDuration);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.REQUEST_IMAGE:
                if (PermissionUtil.isGranted(grantResults)) takeImage();
                else permissionDialog(requestCode, R.string.media_permission_camera_image, permissions);
                break;
            case Constants.REQUEST_AUDIO:
                if (PermissionUtil.isGranted(grantResults)) takeAudio();
                else permissionDialog(requestCode, R.string.media_permission_camera_audio, permissions);
                break;
            case Constants.REQUEST_VIDEO:
                if (PermissionUtil.isGranted(grantResults)) takeVideo();
                else permissionDialog(requestCode, R.string.media_permission_camera_video, permissions);
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    private void permissionDialog(final int request, @StringRes final int res, final String... permissions) {
        final List<String> list = PermissionUtil.getSetting(this, permissions);
        final boolean setting = list.size() > 0;
        final boolean cancel = !(setting || request == Constants.REQUEST_MEDIA);
        new AlertDialog

                .Builder(this).setTitle(R.string.media_permission_title_tips)
                              .setMessage(res)
                              .setCancelable(cancel)
                              .setNegativeButton(R.string.media_cancel, (dialog, which) -> {
                                  dialog.dismiss();
                                  if (!cancel) callbackCancel(R.string.media_refuse_permission);
                              })
                              .setPositiveButton(setting ? R.string.media_setting : R.string.media_ok, (dialog, which) -> {
                                  dialog.dismiss();
                                  if (setting) if (setting) {
                                      toSettingPermission = true;
                                      IntentUtil.toPermissionView(CameraActivity.this);
                                  } else PermissionUtil.request(CameraActivity.this, request, permissions);
                              })
                              .show();
    }

    private void callbackCancel(int res) {

    }

    /*拍照*/
    private void takeImage() {
        if (!PermissionUtil.request(this, Constants.REQUEST_IMAGE, Constants.PERMISSION_IMAGE)) {
            return;
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
        intent.putExtra(Constants.INTENT_FACING, mFacing);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        IntentUtil.request(this, intent, Constants.REQUEST_IMAGE);
    }

    private void takeAudio() {
        if (!PermissionUtil.request(this, Constants.REQUEST_AUDIO, Constants.PERMISSION_AUDIO)) {
            return;
        }

    }

    /*录视频*/
    private void takeVideo() {
        if (!PermissionUtil.request(this, Constants.REQUEST_VIDEO, Constants.PERMISSION_VIDEO)) {
            return;
        }
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
        intent.putExtra(Constants.INTENT_FACING, mFacing);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, mLimitQuality);
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, mLimitDuration);
        intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, mLimitSize);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        IntentUtil.request(this, intent, Constants.REQUEST_VIDEO);
    }
}