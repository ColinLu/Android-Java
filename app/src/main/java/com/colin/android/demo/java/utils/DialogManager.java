package com.colin.android.demo.java.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.colin.android.demo.java.def.bean.ContactBean;
import com.colin.android.demo.java.dialog.TipsDialog;
import com.colin.library.android.utils.LogUtil;

import java.lang.ref.WeakReference;

/**
 * 作者： ColinLu
 * 时间： 2022-01-30 13:30
 * <p>
 * 描述： TODO
 */
public final class DialogManager {
    private static final String TAG = "DialogManager";
    private WeakReference<Fragment> mDialogRef;

    private DialogManager() {
    }

    private static final class Holder {
        private static final DialogManager instance = new DialogManager();
    }

    public static DialogManager getInstance() {
        return DialogManager.Holder.instance;
    }


    public void show(@Nullable FragmentManager manager, @Nullable ContactBean bean) {
        if (manager == null || bean == null) return;
        final TipsDialog dialog = getTipsDialog(manager, TAG);
        dialog.setContact(bean);
        dialog.show(manager, TAG);
    }

    private TipsDialog getTipsDialog(@NonNull FragmentManager manager, @NonNull String tag) {
        Fragment fragment = mDialogRef == null ? null : mDialogRef.get();
        if (fragment instanceof TipsDialog) {
            LogUtil.i(TAG, "dialog mDialogRef");
            return (TipsDialog) fragment;
        }
        fragment = manager.findFragmentByTag(tag);
        if (fragment instanceof TipsDialog) {
            LogUtil.i(TAG, "dialog findFragmentByTag");
            return (TipsDialog) fragment;
        }
        final TipsDialog dialog = new TipsDialog();
        mDialogRef = new WeakReference<>(dialog);
        return dialog;
    }
}
