package com.colin.library.android.media.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.colin.library.android.media.R;
import com.colin.library.android.media.adapter.MediaFolderAdapter;
import com.colin.library.android.media.def.Constants;
import com.colin.library.android.media.def.MediaFolder;
import com.colin.library.android.widgets.def.OnItemClickListener;
import com.colin.library.android.widgets.recycler.SpaceItemDecoration;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 作者： ColinLu
 * 时间： 2019-12-23 21:46
 * <p>
 * 描述： 相册 文件夹路径
 */
public class FolderDialog extends BottomSheetDialogFragment {
    private View mView;
    private RecyclerView recycler_list;
    private boolean mOutViewCancel;
    private List<MediaFolder> mList;
    private MediaFolderAdapter mAdapter;
    private int mSelectedPosition;
    private OnItemClickListener mOnItemClickListener;


    public FolderDialog setOutViewCancel(boolean outViewCancel) {
        this.mOutViewCancel = outViewCancel;
        return this;
    }

    public FolderDialog setAlbumFolders(List<MediaFolder> list) {
        this.mList = list;
        return this;
    }

    public FolderDialog setSelectedPosition(int position) {
        this.mSelectedPosition = position;
        return this;
    }

    public FolderDialog setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
        return this;
    }

    public static FolderDialog getInstance() {
        return new FolderDialog();
    }

    public boolean isShowing() {
        return getDialog() != null && getDialog().isShowing();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Media_Dialog_Folder);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (null == mView) mView = inflater.inflate(R.layout.dialog_media_file, container, false);
        else {
            ViewGroup parent = (ViewGroup) mView.getParent();
            if (null != parent) parent.removeView(mView);
        }
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initParams(null == getDialog() ? null : getDialog().getWindow());
    }

    private void initParams(Window window) {
        //注意下面这个方法会将布局的根部局忽略掉，所以需要嵌套一个布局
        if (null == window) return;
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        window.setAttributes(layoutParams);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCancelable(mOutViewCancel);
        dialog.setCanceledOnTouchOutside(mOutViewCancel);
        dialog.setOnKeyListener((dialog1, keyCode, event)
                -> keyCode == KeyEvent.KEYCODE_BACK && !mOutViewCancel);
        return dialog;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(getActivity());
    }

    private void initView(@Nullable Context context) {
        if (null == context) return;
        recycler_list = mView.findViewById(R.id.recycler_list);
        recycler_list.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new MediaFolderAdapter(context, mList, mSelectedPosition);
        mAdapter.setOnItemClickListener((view, position, object) -> {
            if (mSelectedPosition != position) {
                mAdapter.setPosition(position);
                mAdapter.notifyItemChanged(mSelectedPosition, Constants.ADAPTER_PAYLOAD_CHECK);
                mAdapter.notifyItemChanged(position, Constants.ADAPTER_PAYLOAD_CHECK);
                mSelectedPosition = position;
                if (mOnItemClickListener != null) mOnItemClickListener.item(view, position, object);
                dismiss();
            }
        });
        final SpaceItemDecoration decoration = new SpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen.dp_4));
        recycler_list.addItemDecoration(decoration);
        recycler_list.setAdapter(mAdapter);
    }

    public void show(AppCompatActivity appCompatActivity) {
        if (null == appCompatActivity) return;
        show(appCompatActivity.getSupportFragmentManager(), appCompatActivity.getClass().getSimpleName());
    }

    @Override
    public void show(@NonNull FragmentManager manager, @Nullable String tag) {
        try {
            Class<?> clazz = Class.forName("androidx.fragment.app.DialogFragment");
            Field mDismissed = clazz.getDeclaredField("mDismissed");
            Field mShownByMe = clazz.getDeclaredField("mShownByMe");
            Object newInstance = clazz.getConstructor().newInstance();
            mDismissed.setAccessible(true);
            mDismissed.set(newInstance, false);
            mShownByMe.setAccessible(true);
            mShownByMe.set(newInstance, false);
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            ft.commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
