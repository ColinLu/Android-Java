package com.colin.library.android.base;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.colin.library.android.Utils;
import com.colin.library.android.base.def.IInitView;
import com.colin.library.android.base.def.ILife;
import com.colin.library.android.utils.StringUtil;
import com.colin.library.android.utils.data.Constants;

import java.lang.reflect.Field;

/**
 * 作者： ColinLu
 * 时间： 2022-01-20 12:48
 * <p>
 * 描述： 弹框基类
 */
public abstract class BaseDialog<Returner> extends DialogFragment implements IInitView, ILife {
    protected boolean mRefresh = true;

    /*Dialog标题*/
    @ColorInt
    protected int mTitleColor = Constants.INVALID;
    @StringRes
    protected int mTitleRes = Resources.ID_NULL;
    @Nullable
    protected CharSequence mTitle = "";
    protected boolean mShowTitle = true;

    /*Dialog提示文案*/
    @ColorInt
    protected int mMessageColor = Constants.INVALID;
    protected int mMessageRes = Resources.ID_NULL;
    @Nullable
    protected CharSequence mMessage = null;

    /*Dialog左边按钮效果*/
    @ColorInt
    protected int mLeftButtonColor = Constants.INVALID;
    protected int mLeftButtonRes = Resources.ID_NULL;
    @Nullable
    protected CharSequence mLeftButton = null;

    /*Dialog右边按钮效果*/
    @ColorInt
    protected int mRightButtonColor = Constants.INVALID;
    protected int mRightButtonRes = Resources.ID_NULL;
    @Nullable
    protected CharSequence mRightButton = null;

    /*Dialog点击弹框之外是否可以消失 true 可以  false 不能*/
    protected boolean mOutViewCancel = true;
    /*Dialog是否显示一个按钮*/
    protected boolean mSingleButton = false;
    @NonNull
    protected final DisplayMetrics mDisplayMetrics = Resources.getSystem().getDisplayMetrics();
    /*Dialog跟布局*/
    protected View mRootView;
    /*Dialog布局文件*/
    @LayoutRes
    protected int mLayoutRes = Resources.ID_NULL;

    /*宽高显示比例  0 不处理 1 全屏占比*/
    @FloatRange(from = 0F, to = 1.0F)
    protected float mDialogWidth = 0.8F;
    @FloatRange(from = 0F, to = 1.0F)
    protected float mDialogHeight = 0.0F;
    /*Dialog显示位置*/
    protected int mGravity = Gravity.CENTER;
    /*Dialog显示动画*/
    @StyleRes
    protected int mDialogAnim = android.R.style.Animation_Dialog;

    @Nullable
    protected DialogInterface.OnClickListener mOnClickListener = null;
    @Nullable
    protected DialogInterface.OnShowListener mOnShowListener = null;
    @Nullable
    protected DialogInterface.OnDismissListener mOnDismissListener = null;

    /*弹框开始 设置样式*/
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.App_Dialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (null != mRootView) {
            final ViewGroup parent = (ViewGroup) mRootView.getParent();
            if (null != parent) parent.removeView(mRootView);
        } else mRootView = inflater.inflate(layoutRes(), container, false);
        return mRootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initParams(null == getDialog() ? null : getDialog().getWindow());
        initView(savedInstanceState);
        initData(getArguments());
    }

    /*Dialog 渲染*/
    @Override
    public void onStart() {
        super.onStart();
        if (null != mOnShowListener) mOnShowListener.onShow(getDialog());
    }

    /*创建Dialog*/
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setCancelable(isOutViewCancel());
        dialog.setCanceledOnTouchOutside(isOutViewCancel());
        dialog.setOnKeyListener((dialog1, keyCode, event) -> keyCode == KeyEvent.KEYCODE_BACK && !isOutViewCancel());
        return dialog;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mRefresh) {
            mRefresh = false;
            loadData(true);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRefresh = true;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (null != mOnDismissListener) mOnDismissListener.onDismiss(dialog);
    }

    @NonNull
    public <T extends View> T findViewById(@IdRes int id) {
        if (null == mRootView) throw new RuntimeException("fragment dialog mRootView is Empty");
        return mRootView.findViewById(id);
    }

    @NonNull
    @Override
    public Context getContext() {
        return Utils.notNull(super.getContext(), "Fragment " + this + " not attached to a context.");
    }

    public Returner setTitle(@Nullable CharSequence title) {
        this.mTitle = title;
        return (Returner) this;
    }

    public Returner setTitle(@StringRes int titleRes) {
        this.mTitleRes = titleRes;
        return (Returner) this;
    }

    public Returner setTitleColor(@ColorInt int titleColor) {
        this.mTitleColor = titleColor;
        return (Returner) this;
    }

    public Returner setShowTitle(boolean showTitle) {
        this.mShowTitle = showTitle;
        return (Returner) this;
    }

    public Returner setMessage(@Nullable CharSequence message) {
        this.mMessage = message;
        return (Returner) this;
    }

    public Returner setMessage(@StringRes int messageRes) {
        this.mMessageRes = messageRes;
        return (Returner) this;
    }

    public Returner setMessageColor(@ColorInt int messageColor) {
        this.mMessageColor = messageColor;
        return (Returner) this;
    }

    public Returner setLeftButton(@Nullable CharSequence leftButton) {
        this.mLeftButton = leftButton;
        return (Returner) this;
    }

    public Returner setLeftButton(@StringRes int leftButtonRes) {
        this.mLeftButtonRes = leftButtonRes;
        return (Returner) this;
    }

    public Returner setLeftButtonColor(@ColorInt int leftButtonColor) {
        this.mLeftButtonColor = leftButtonColor;
        return (Returner) this;
    }


    public Returner setRightButton(@Nullable CharSequence rightButton) {
        this.mRightButton = rightButton;
        return (Returner) this;
    }

    public Returner setRightButton(@StringRes int rightButtonRes) {
        this.mRightButtonRes = rightButtonRes;
        return (Returner) this;
    }

    public Returner setRightButtonColor(@ColorInt int rightButtonColor) {
        this.mRightButtonColor = rightButtonColor;
        return (Returner) this;
    }

    public Returner setSingleButton(boolean singleButton) {
        this.mSingleButton = singleButton;
        return (Returner) this;
    }

    public Returner setOnDialogListener(@Nullable DialogInterface.OnClickListener onDialogListener) {
        this.mOnClickListener = onDialogListener;
        return (Returner) this;
    }

    public Returner setOnShowListener(@Nullable DialogInterface.OnShowListener onShowListener) {
        this.mOnShowListener = onShowListener;
        return (Returner) this;
    }

    public Returner setOnDismissListener(@Nullable DialogInterface.OnDismissListener onDismissListener) {
        this.mOnDismissListener = onDismissListener;
        return (Returner) this;
    }

    public Returner setView(@NonNull View view) {
        this.mRootView = view;
        return (Returner) this;
    }

    public Returner setLayoutRes(@LayoutRes int layoutRes) {
        this.mLayoutRes = layoutRes;
        return (Returner) this;
    }

    public Returner setDialogWidth(@FloatRange(from = 0.0F, to = 1.0F) float width) {
        this.mDialogWidth = width;
        return (Returner) this;
    }

    public Returner setDialogHeight(@FloatRange(from = 0.0F, to = 1.0F) float height) {
        this.mDialogHeight = height;
        return (Returner) this;
    }

    public Returner setGravity(int gravity) {
        this.mGravity = gravity;
        return (Returner) this;
    }

    public BaseDialog<Returner> setDialogAnim(@StyleRes int anim) {
        this.mDialogAnim = anim;
        return this;
    }

    public Returner setDialog(@FloatRange(from = 0.0F, to = 1.0F) float width, @FloatRange(from = 0.0F, to = 1.0F) float height, int gravity) {
        this.mDialogWidth = width;
        this.mDialogHeight = height;
        this.mGravity = gravity;
        return (Returner) this;
    }

    public Returner setOutViewCancel(boolean outViewCancel) {
        this.mOutViewCancel = outViewCancel;
        return (Returner) this;
    }


    public View getRootView() {
        return mRootView;
    }

    public int getLayoutRes() {
        return mLayoutRes;
    }

    @StyleRes
    public int getDialogAnim() {
        return mDialogAnim;
    }

    public int getGravity() {
        return mGravity;
    }


    public float getDialogWidth() {
        if (mDialogWidth == 0.0F) return ViewGroup.LayoutParams.WRAP_CONTENT;
        if (mDialogWidth == 1.0F) return ViewGroup.LayoutParams.MATCH_PARENT;
        return mDialogWidth * mDisplayMetrics.widthPixels;
    }


    public float getDialogHeight() {
        if (mDialogHeight == 0.0F) return ViewGroup.LayoutParams.WRAP_CONTENT;
        if (mDialogHeight == 1.0F) return ViewGroup.LayoutParams.MATCH_PARENT;
        return mDialogHeight * mDisplayMetrics.heightPixels;
    }


    @ColorInt
    public int getTitleColor() {
        return mTitleColor;
    }


    @Nullable
    public CharSequence getTitle() {
        return mTitle;
    }


    public boolean isShowTitle() {
        return mShowTitle;
    }


    @ColorInt
    public int getMessageColor() {
        return mMessageColor;
    }


    @Nullable
    public CharSequence getMessage() {
        return mMessage;
    }


    @ColorInt
    public int getLeftButtonColor() {
        return mLeftButtonColor;
    }


    @Nullable
    public CharSequence getLeftButton() {
        return mLeftButton;
    }


    @ColorInt
    public int getRightButtonColor() {
        return mRightButtonColor;
    }


    @Nullable
    public CharSequence getRightButton() {
        return mRightButton;
    }


    public boolean isOutViewCancel() {
        return mOutViewCancel;
    }

    public boolean isSingleButton() {
        return mSingleButton;
    }


    public boolean isShowing() {
        return null != getDialog() && getDialog().isShowing();
    }

    public void show(@Nullable FragmentActivity activity) {
        if (null == activity || activity.isFinishing()) return;
        show(activity.getSupportFragmentManager(), this.getClass().getSimpleName());
    }

    public void show(@Nullable Fragment fragment) {
        if (null == fragment || null == fragment.getActivity() || fragment.getActivity().isFinishing())
            return;
        show(fragment.getChildFragmentManager(), this.getClass().getSimpleName());
    }

    public void show(@Nullable FragmentManager manager) {
        if (null == manager) return;
        show(manager, this.getClass().getSimpleName());
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
            ft.add(this, StringUtil.isEmpty(tag) ? manager.getClass().getSimpleName() : tag);
            ft.commitNowAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*基本参数设置*/
    protected void initParams(@Nullable Window window) {
        //注意下面这个方法会将布局的根部局忽略掉，所以需要嵌套一个布局
        if (null == window) return;
        window.getAttributes().windowAnimations = getDialogAnim();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = getGravity();
        layoutParams.height = (int) getDialogHeight();
        layoutParams.width = (int) getDialogWidth();
        window.setAttributes(layoutParams);
    }

}
