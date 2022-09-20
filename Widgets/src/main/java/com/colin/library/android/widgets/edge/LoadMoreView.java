package com.colin.library.android.widgets.edge;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;

import com.colin.library.android.widgets.R;
import com.colin.library.android.widgets.progress.LoadingView;


public class LoadMoreView extends ConstraintLayout implements EdgeWatcher {
    private final LoadingView mLoadingView;
    private final AppCompatImageView mArrowView;
    private final AppCompatTextView mTextView;
    private final int mHeight;
    private CharSequence mPullText;
    private CharSequence mReleaseText;
    private boolean mRunning = false;
    private boolean mIsRelease = false;

    public LoadMoreView(Context context) {
        this(context, null);
    }

    public LoadMoreView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.LoadMoreStyle);
    }

    public LoadMoreView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final int height = context.getResources().getDimensionPixelSize(R.dimen.load_more_height_default);
        final TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.LoadMoreView, defStyleAttr, 0);
        mPullText = array.getString(R.styleable.LoadMoreView_pullText);
        mReleaseText = array.getString(R.styleable.LoadMoreView_releaseText);
        mHeight = array.getDimensionPixelSize(R.styleable.LoadMoreView_android_layout_height, height);
        int arrowTextGap = array.getDimensionPixelSize(R.styleable.LoadMoreView_loadSpace, height >> 2);
        final Drawable arrow = array.getDrawable(R.styleable.LoadMoreView_arrow);
        final int bgColor = array.getColor(R.styleable.LoadMoreView_backgroundColor, Color.TRANSPARENT);
        final int arrowColor = array.getColor(R.styleable.LoadMoreView_arrowColor, ContextCompat.getColor(context, R.color.colorAccent));
        array.recycle();
        mLoadingView = new LoadingView(context, attrs);
        mLoadingView.setVisibility(View.GONE);
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.leftToLeft = LayoutParams.PARENT_ID;
        lp.rightToRight = LayoutParams.PARENT_ID;
        lp.topToTop = LayoutParams.PARENT_ID;
        lp.bottomToBottom = LayoutParams.PARENT_ID;
        addView(mLoadingView, lp);

        mArrowView = new AppCompatImageView(context);
        mArrowView.setId(View.generateViewId());
        mArrowView.setImageDrawable(arrow);
        mArrowView.setRotation(180);
        ImageViewCompat.setImageTintList(mArrowView, ColorStateList.valueOf(arrowColor));

        mTextView = new AppCompatTextView(context, attrs);
        mTextView.setId(View.generateViewId());
        mTextView.setText(mPullText);

        lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.leftToLeft = LayoutParams.PARENT_ID;
        lp.rightToLeft = mTextView.getId();
        lp.topToTop = LayoutParams.PARENT_ID;
        lp.bottomToBottom = LayoutParams.PARENT_ID;
        lp.horizontalChainStyle = LayoutParams.CHAIN_PACKED;
        addView(mArrowView, lp);

        lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.leftToRight = mArrowView.getId();
        lp.rightToRight = LayoutParams.PARENT_ID;
        lp.topToTop = LayoutParams.PARENT_ID;
        lp.bottomToBottom = LayoutParams.PARENT_ID;
        lp.leftMargin = arrowTextGap;
        addView(mTextView, lp);
        setBackgroundColor(bgColor);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(mHeight, MeasureSpec.EXACTLY));
    }

    @Override
    public void start() {
        mRunning = true;
        mLoadingView.setVisibility(View.VISIBLE);
        mLoadingView.start();
        mArrowView.setVisibility(View.GONE);
        mTextView.setVisibility(View.GONE);
    }

    @Override
    public void offset(@NonNull Edge edge, int offset) {
        if (mRunning) return;
        if (mIsRelease) {
            if (edge.getTargetOffset() > offset) {
                mIsRelease = false;
                mTextView.setText(mPullText);
                mArrowView.animate().rotation(180).start();
            }
        } else {
            if (edge.getTargetOffset() <= offset) {
                mIsRelease = true;
                mTextView.setText(mReleaseText);
                mArrowView.animate().rotation(0).start();
            }
        }
    }


    @Override
    public void finish() {
        mLoadingView.stop();
        mLoadingView.setVisibility(View.GONE);
        mArrowView.setVisibility(View.VISIBLE);
        mTextView.setVisibility(View.VISIBLE);
        mRunning = false;
    }

    public LoadMoreView setText(@Nullable CharSequence pull, @Nullable CharSequence release) {
        this.mPullText = pull;
        this.mReleaseText = release;
        return this;
    }

}
