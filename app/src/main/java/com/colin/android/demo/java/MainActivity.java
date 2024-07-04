package com.colin.android.demo.java;

import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.colin.android.demo.java.app.AppActivity;
import com.colin.android.demo.java.databinding.ActivityMainBinding;
import com.colin.library.android.utils.LogUtil;
import com.colin.library.android.utils.ToastUtil;
import com.colin.library.android.widgets.def.OnAppBarStateChangeListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppActivity<ActivityMainBinding> {
    private AppBarConfiguration appBarConfiguration;
    @OnAppBarStateChangeListener.State
    private int mState;
    private int mOffset;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void initView(@Nullable Bundle bundle) {
        setSupportActionBar(mBinding.mToolbar);
        final NavController controller = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(controller.getGraph()).build();
        NavigationUI.setupWithNavController(mBinding.mToolbar, controller, appBarConfiguration);

        mBinding.mFab.setOnClickListener(
                view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show());

        mBinding.mAppBar.addOnOffsetChangedListener(new OnAppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, @State int state, int offset) {
                LogUtil.log(String.format("state:%d\toffset:%d", state, offset));
                mState = state;
                mOffset = offset;
            }
        });
    }

    @Override
    public void initData(@Nullable Bundle bundle) {

    }

    @Override
    public void loadData(boolean refresh) {

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
        return NavigationUI.navigateUp(controller, appBarConfiguration) || super.onSupportNavigateUp();
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
        mBinding.mToolbar.setTitle(getTitle());
    }
}