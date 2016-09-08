package com.skymxc.demo.puzzle.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by sky-mxc
 * Toast的帮助类
 */
public class T {
    private static Context mContext;


    /**
     * 初始话
     * @param context
     */
    public static void init(Context context){
        mContext = context;
    }

    /**
     * Toast 出消息
     * @param text 消息
     */
    public static void show(String text){
        Toast.makeText(mContext,text,Toast.LENGTH_SHORT).show();
    }
}
