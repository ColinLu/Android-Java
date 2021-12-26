package com.colin.android.demo.java;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.colin.android.demo.java.app.AppActivity;
import com.colin.android.demo.java.databinding.ActivityMainBinding;

public class MainActivity extends AppActivity<ActivityMainBinding> {

    @Override
    public void initView(@Nullable Bundle bundle) {
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(mBinding.navView, navController);
    }

    @Override
    public void initData(@Nullable Bundle bundle) {

    }
}