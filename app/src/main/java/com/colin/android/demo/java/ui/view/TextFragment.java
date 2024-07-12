package com.colin.android.demo.java.ui.view;

import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.colin.android.demo.java.R;
import com.colin.android.demo.java.app.AppFragment;
import com.colin.android.demo.java.databinding.FragmentTextBinding;
import com.colin.library.android.utils.ToastUtil;
import com.colin.library.android.widgets.annotation.Orientation;
import com.colin.library.android.widgets.span.TouchableSpan;

/**
 * 作者： ColinLu
 * 时间： 2022-01-27 22:14
 * <p>
 * 描述： TODO
 */
public class TextFragment extends AppFragment<FragmentTextBinding> {

    @Override
    public void initView(@Nullable Bundle bundle) {
        mBinding.buttonHorizontal.setOnClickListener(v -> mBinding.gradientText.setOrientation(Orientation.HORIZONTAL));
        mBinding.buttonVertical.setOnClickListener(v -> mBinding.gradientText.setOrientation(Orientation.VERTICAL));
        mBinding.buttonTextColor.setOnClickListener(v -> mBinding.gradientText.setTextColor(Color.BLUE));
        mBinding.buttonGradient.setOnClickListener(v -> mBinding.gradientText.setTextColor(Color.WHITE, Color.BLACK));
    }

    @Override
    public void initData(@Nullable Bundle bundle) {
        mBinding.spanFix1.setMovementMethodDefault();
        mBinding.spanFix1.setText(generateSp(mBinding.spanFix1, getResources().getString(R.string.span_touch_fix_1)));

        mBinding.spanFix2.setMovementMethodDefault();
        mBinding.spanFix2.setNeedForceEventToParent(true);
        mBinding.spanFix2.setText(generateSp(mBinding.spanFix2, getResources().getString(R.string.span_touch_fix_2)));

    }


    @Override
    public void loadData(boolean refresh) {

    }


    private SpannableString generateSp(TextView tv, String text) {
        String highlight1 = "@Colin";
        String highlight2 = "#Colin#";
        SpannableString sp = new SpannableString(text);
        int start = 0, end;
        int index;
        while ((index = text.indexOf(highlight1, start)) > -1) {
            end = index + highlight1.length();
            sp.setSpan(new TouchableSpan(
                    Color.WHITE,
                    Color.GRAY,
                    Color.BLUE,
                    Color.YELLOW) {
                @Override
                public void onClickSpan(@NonNull View view) {
                    ToastUtil.show("click @Colin");
                    Toast.makeText(getContext(), "click @Colin", Toast.LENGTH_SHORT).show();
                }
            }, index, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            start = end;
        }

        start = 0;
        while ((index = text.indexOf(highlight2, start)) > -1) {
            end = index + highlight2.length();
            sp.setSpan(new TouchableSpan(
                    Color.BLACK,
                    Color.GRAY,
                    Color.YELLOW,
                    Color.BLUE) {
                @Override
                public void onClickSpan(@NonNull View view) {
                    ToastUtil.show("click #Colin#");
                }
            }, index, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            start = end;
        }
        return sp;
    }

}
