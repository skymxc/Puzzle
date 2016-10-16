package com.skymxc.demo.puzzle.utils;

import android.app.Application;
import android.util.Log;

import com.skymxc.demo.puzzle.db.DBHelper;

/**
 * Created by sky-mxc
 * 在清单文件配置中调用  ,不引用不会走这边
 */
public class App extends Application {

    /**
     * 当程序启动的时候调用的
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("App","========onCreate===========");
        //初始化 Toast的工具类
        T.init(this);
        //初始化 DBHelper
        DBHelper.init(this,1);
    }
}
