package com.colin.library.android.widgets.layout;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.widget.Toolbar;

import com.colin.library.android.widgets.R;
import com.google.android.material.appbar.AppBarLayout;

/**
 * 作者： ColinLu
 * 时间： 2022-04-30 20:10
 * <p>
 * 描述： TODO
 */
public class TextListLayout extends ViewGroup {
    private int mViewWidth;
    private int mViewHeight;
    private CharSequence mTitle;
    private CharSequence[] mTextArrays;
    /**
     * 计算子控件的布局位置.
     */
    private final Rect mTmpContainerRect = new Rect();
    private final Rect mTmpChildRect = new Rect();

    public TextListLayout(Context context) {
        this(context, null, Resources.ID_NULL);
    }

    public TextListLayout(Context context, AttributeSet attrs) {
        this(context, attrs, Resources.ID_NULL);
    }

    public TextListLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.AutoFitTextView, defStyleAttr, Resources.ID_NULL);
        mViewWidth = array.getDimensionPixelSize(R.styleable.Layout_android_layout_width, 0);
        mViewHeight = array.getDimensionPixelSize(R.styleable.Layout_android_layout_height, 0);
        array.recycle();
    }

//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        if (!isHide()) {
//            setMeasuredDimension(0, 0);
//            return;
//        }
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//    }

    /**
     * 测量容器的宽高 = 所有子控件的尺寸 + 容器本身的尺寸 -->综合考虑
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        ///< 定义最大宽度和高度
        int maxWidth = 0;
        int maxHeight = 0;
        ///< 获取子控件的个数
        int count = getChildCount();
        for (int i = 0; i < count; ++i) {
            View view = getChildAt(i);
            ///< 子控件如果是GONE - 不可见也不占据任何位置则不进行测量
            if (view.getVisibility() != GONE) {
                ///< 获取子控件的宽高
                ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
                ///< 调用子控件测量的方法getChildMeasureSpec(先不考虑margin、padding)
                ///<  - 内部处理还是比我们自己的麻烦的，后面我们可能要研究和参考
                final int childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec, 0, layoutParams.width);
                final int childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec, 0, layoutParams.height);
                ///< 然后真正测量下子控件 - 到这一步我们就对子控件进行了宽高的设置了咯
                view.measure(childWidthMeasureSpec, childHeightMeasureSpec);

                ///< 然后再次获取测量后的子控件的属性
                layoutParams = view.getLayoutParams();
                ///< 然后获取宽度的最大值、高度的累加
                maxWidth = Math.max(maxWidth, layoutParams.width);
                maxHeight += layoutParams.height;
            }
        } ///< 然后再与容器本身的最小宽高对比，取其最大值 - 有一种情况就是带背景图片的容器，要考虑图片尺寸
        maxWidth = Math.max(maxWidth, getMinimumWidth());
        maxHeight = Math.max(maxHeight, getMinimumHeight());

        ///< 然后根据容器的模式进行对应的宽高设置 - 参考我们之前的自定义View的测试方式
        int wSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int wSize = MeasureSpec.getSize(widthMeasureSpec);
        int hSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int hSize = MeasureSpec.getSize(heightMeasureSpec);

        ///< wrap_content的模式
        if (wSpecMode == MeasureSpec.AT_MOST && hSpecMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(maxWidth, maxHeight);
        }
        ///< 精确尺寸的模式
        else if (wSpecMode == MeasureSpec.EXACTLY && hSpecMode == MeasureSpec.EXACTLY) {
            setMeasuredDimension(wSize, hSize);
        }
        ///< 宽度尺寸不确定，高度确定
        else if (wSpecMode == MeasureSpec.UNSPECIFIED) {
            setMeasuredDimension(maxWidth, hSize);
        }
        ///< 宽度确定，高度不确定
        else if (hSpecMode == MeasureSpec.UNSPECIFIED) {
            setMeasuredDimension(wSize, maxHeight);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (isHide()) {
            return;
        }
        int childTop;
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            final int childWidth = child.getMeasuredWidth();
            final int childHeight = child.getMeasuredHeight();

        }
        for (int i = 0; i < count; i++) {
            final View child = getVirtualChildAt(i);
            if (child == null) {
                childTop += measureNullChild(i);
            } else if (child.getVisibility() != GONE) {
                final int childWidth = child.getMeasuredWidth();
                final int childHeight = child.getMeasuredHeight();

                final LinearLayout.LayoutParams lp =
                        (LinearLayout.LayoutParams) child.getLayoutParams();

                int gravity = lp.gravity;
                if (gravity < 0) {
                    gravity = minorGravity;
                }
                final int layoutDirection = getLayoutDirection();
                final int absoluteGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection);
                switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
                    case Gravity.CENTER_HORIZONTAL:
                        childLeft = paddingLeft + ((childSpace - childWidth) / 2)
                                + lp.leftMargin - lp.rightMargin;
                        break;

                    case Gravity.RIGHT:
                        childLeft = childRight - childWidth - lp.rightMargin;
                        break;

                    case Gravity.LEFT:
                    default:
                        childLeft = paddingLeft + lp.leftMargin;
                        break;
                }

                if (hasDividerBeforeChildAt(i)) {
                    childTop += mDividerHeight;
                }

                childTop += lp.topMargin;
                setChildFrame(child, childLeft, childTop + getLocationOffset(child),
                        childWidth, childHeight);
                childTop += childHeight + lp.bottomMargin + getNextLocationOffset(child);

                i += getChildrenSkipCount(child, i);
            }
        }
    }


    private boolean isHide() {
        return mTextArrays == null || mTextArrays.length == 0;
    }
}
