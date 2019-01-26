package com.makerloom.common;

import android.support.multidex.MultiDexApplication;

import com.makerloom.ujcbt.R;

// import io.paperdb.Paper;

/**
 * Created by michael on 2/25/18.
 */

public class MyApp extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
//        Paper.init(getApplicationContext());
    }
}
