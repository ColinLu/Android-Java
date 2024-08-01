package com.colin.android.demo.java;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.colin.android.demo.java.app.AppActivity;
import com.colin.android.demo.java.databinding.ActivityMainBinding;
import com.colin.library.android.base.def.IBack;
import com.colin.library.android.utils.LogUtil;
import com.colin.library.android.utils.ToastUtil;
import com.colin.library.android.utils.data.Constants;
import com.colin.library.android.widgets.def.OnAppBarStateChangeListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppActivity<ActivityMainBinding> {
    private AppBarConfiguration appBarConfiguration;
    @OnAppBarStateChangeListener.State
    private int mState;
    private int mOffset;
    private long mLastBackPressTime = Constants.INVALID;
    private final OnBackPressedCallback mBackCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            Fragment currentFragment = getCurrentFragment();
            if (currentFragment instanceof IBack && ((IBack) currentFragment).onBack()) {
                LogUtil.i(currentFragment.getClass().getSimpleName() + "->back");
                return;
            }
            if (onSupportNavigateUp()) {
                return;
            }
            final long currentTime = System.currentTimeMillis();
            if (mLastBackPressTime == Constants.INVALID || currentTime - mLastBackPressTime >= 1000) {
                ToastUtil.show(R.string.exit_toast);
                mLastBackPressTime = currentTime;
            } else {
                finish();
            }
        }
    };

    @Nullable
    private Fragment getCurrentFragment() {
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            final boolean visible = fragment.isVisible();
            LogUtil.i(String.format("fragment:%s visible:%s", fragment.getClass().getSimpleName(), visible));
            if (visible) return fragment;
        }
        return null;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        getOnBackPressedDispatcher().addCallback(this, mBackCallback);
    }

    @Override
    public void initView(@Nullable Bundle bundle) {
        setSupportActionBar(mBinding.mToolbar);
        final NavController controller = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(controller.getGraph()).build();
        NavigationUI.setupWithNavController(mBinding.mToolbar, controller, appBarConfiguration);

        mBinding.mFab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show());

        mBinding.mAppBar.addOnOffsetChangedListener(new OnAppBarStateChangeListener() {
            @Override
            public void onStateChanged(@NonNull AppBarLayout appBarLayout, @State int state, int offset) {
                LogUtil.log("state:%d\toffset:%d", state, offset);
                mState = state;
                mOffset = offset;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        if (id == R.id.action_settings) {
            ToastUtil.show(R.string.action_settings);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onSupportNavigateUp() {
        final NavController controller = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        final boolean navigateUp = NavigationUI.navigateUp(controller, appBarConfiguration) || super.onSupportNavigateUp();
        LogUtil.e("onSupportNavigateUp navigateUp:" + navigateUp);
        return navigateUp;
    }

    public void setExpanded() {
        LogUtil.d("expanded:" + mState);
        if (mState == OnAppBarStateChangeListener.State.EXPANDED) {
            mBinding.mAppBar.setExpanded(false);
        } else mBinding.mAppBar.setExpanded(mState == OnAppBarStateChangeListener.State.COLLAPSED);
    }

    public void setExpanded(boolean expanded) {
        LogUtil.d("expanded:" + expanded);
        mBinding.mAppBar.setExpanded(expanded);
    }

    public void setToolbar(boolean show) {
        LogUtil.d("show:" + show);
        mBinding.mAppBar.setVisibility(show ? View.VISIBLE : View.GONE);
        mBinding.mToolbar.setVisibility(show ? View.VISIBLE : View.GONE);
        mBinding.mAppBar.setExpanded(show);
    }


    public void updateTitle(@Nullable CharSequence title) {
        mBinding.mToolbar.setTitle(title);
    }


}