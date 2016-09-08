package com.skymxc.demo.puzzle.utils;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;

import com.skymxc.demo.puzzle.db.DBUtil;
import com.skymxc.demo.puzzle.model.Image;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by sky-mxc
 * 文件工具类
 */
public class FileUtil {

    public  static boolean initData(Activity context){
        //检查 SDK是否已挂载 和是否可读写
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    T.show("SD卡不可读写");
                }
            });
            return false;
        }
        try {
            File root = Environment.getExternalStorageDirectory();//SD卡 根目录
            File  dst = new File(root,"puzzle/gameimage/"); //创建键 存储图片的目录
            Log.e("Tag","=========dst ==exists:"+dst.exists());
            //创建
            if (!dst.exists()){
                dst.mkdirs();
            }

            Log.e("Tag","=========dst:"+dst.getPath()+"===========exists:"+dst.exists());
            //将asset下的文件copy到是sd卡下
            String [] names=context.getAssets().list("picks");     //返回某个路径下所有文件名称
            List<Image> images = new ArrayList<>();     //需要存到数据库中的图片数据
            for (int i =0;i<names.length;i++){
                String name = names[i];
                File save = new File(dst,name); //要保存的文件
                if (!DBUtil.isExists(save.getPath())) {
                    //如果数据库中不存在
                    images.add(new Image(save.getPath(),0,0));
                }
                if (save.exists())continue;  //文件存在继续下一个
                //如果创建成功，copy文件
                Log.e("Tag","=========dst:"+save.getPath()+"===========exists:"+save.exists());
                if (save.createNewFile()){
                    InputStream is = context.getAssets().open("picks/"+name);    //以名称打开这个文件流
                    FileOutputStream fos = new FileOutputStream(save);
                    copy(is,fos);
                }



            }
            //存入到数据库中
            if (!images.isEmpty()){
                DBUtil.saveImage(images);
            }

            Log.e("Tag","文件夹文件夹目录："+ Arrays.toString(names));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 复制文件
     * @param is
     * @param os
     */
    private static  void copy(InputStream is, OutputStream os){
        int len =-1;
        byte[] b = new byte[2048];
        try {
            while ((len=is.read(b))!=-1){
                os.write(b,0,len);
            }
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {

            try {
                os.close();
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}
