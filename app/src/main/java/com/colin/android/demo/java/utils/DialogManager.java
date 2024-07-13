package com.colin.android.demo.java.utils;

import android.graphics.Bitmap;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.colin.android.demo.java.dialog.ImageDialog;
import com.colin.android.demo.java.dialog.PathDialog;
import com.colin.android.demo.java.dialog.TipsDialog;
import com.colin.library.android.utils.BitmapUtil;
import com.colin.library.android.utils.LogUtil;
import com.colin.library.android.utils.StringUtil;

import java.lang.ref.WeakReference;

/**
 * 作者： ColinLu
 * 时间： 2022-01-30 13:30
 * <p>
 * 描述： 弹框统一管理
 */
public final class DialogManager {
    private static final String TAG = "DialogManager";
    private WeakReference<Fragment> mDialogRef;

    private static final class Holder {
        private static final DialogManager instance = new DialogManager();
    }

    public static DialogManager getInstance() {
        return DialogManager.Holder.instance;
    }

    private DialogManager() {
    }


    public void showTip(@Nullable FragmentManager manager, @Nullable String msg) {
        if (manager == null || StringUtil.isEmpty(msg)) return;
        final TipsDialog dialog = getTipsDialog(manager, TAG);
        dialog.setTips(msg);
        dialog.show(manager, TAG);
    }

    public void showImage(@NonNull FragmentManager manager, @Nullable Bitmap bitmap) {
        if (BitmapUtil.isEmpty(bitmap)) return;
        final ImageDialog dialog = getImageDialog(manager, TAG);
        dialog.setImage(bitmap);
        dialog.show(manager, TAG);
    }
    public void showImage(@NonNull FragmentManager manager, @DrawableRes int res) {
        final ImageDialog dialog = new ImageDialog();
        dialog.setImageRes(res);
        dialog.show(manager, TAG);
    }
    public synchronized void showTip(@NonNull FragmentManager manager, @Nullable String title, @Nullable String msg) {
        if (StringUtil.isEmpty(title, msg)) return;
        final TipsDialog dialog = getTipsDialog(manager, "TipsDialog");
        dialog.setTips(title, msg);
        dialog.show(manager, "TipsDialog");
    }

    private TipsDialog getTipsDialog(@NonNull FragmentManager manager, @NonNull String tag) {
        Fragment fragment = mDialogRef == null ? null : mDialogRef.get();
        if (fragment instanceof TipsDialog) {
            LogUtil.i("dialog mDialogRef");
            return (TipsDialog) fragment;
        }
        fragment = manager.findFragmentByTag(tag);
        if (fragment instanceof TipsDialog) {
            LogUtil.i("dialog findFragmentByTag");
            return (TipsDialog) fragment;
        }
        final TipsDialog dialog = new TipsDialog();
        mDialogRef = new WeakReference<>(dialog);
        return dialog;
    }

    private ImageDialog getImageDialog(@NonNull FragmentManager manager, @NonNull String tag) {
        Fragment fragment = mDialogRef == null ? null : mDialogRef.get();
        if (fragment instanceof ImageDialog) {
            LogUtil.i("dialog mDialogRef");
            return (ImageDialog) fragment;
        }
        fragment = manager.findFragmentByTag(tag);
        if (fragment instanceof ImageDialog) {
            LogUtil.i("dialog findFragmentByTag");
            return (ImageDialog) fragment;
        }
        for (Fragment dialog : manager.getFragments()) {
            if (dialog instanceof ImageDialog) {
                LogUtil.i("Fragment already added: ImageDialog{");
                return (ImageDialog) dialog;
            }
        }
        final ImageDialog dialog = new ImageDialog();
        mDialogRef = new WeakReference<>(dialog);
        return dialog;
    }

    private PathDialog getPathDialog(@NonNull FragmentManager manager, @NonNull String tag) {
        Fragment fragment = mDialogRef == null ? null : mDialogRef.get();
        if (fragment instanceof PathDialog) {
            LogUtil.i("dialog mDialogRef");
            return (PathDialog) fragment;
        }
        fragment = manager.findFragmentByTag(tag);
        if (fragment instanceof PathDialog) {
            LogUtil.i("dialog findFragmentByTag");
            return (PathDialog) fragment;
        }
        final PathDialog dialog = new PathDialog();
        mDialogRef = new WeakReference<>(dialog);
        return dialog;
    }
}
