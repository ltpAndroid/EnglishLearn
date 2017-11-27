package com.tarena.app.app;

import android.app.Application;

import com.tarena.app.huanxin.HXAccount;

import cn.bmob.v3.Bmob;

/**
 * Created by Administrator on 2017/11/9 0009.
 */

public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initBmob();
        HXAccount.initHX(this);
    }

    private void initBmob() {
        Bmob.initialize(this, "8c025c3e19d67ef93cbf0186e3e3fcd0");
    }

}
