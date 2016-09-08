package com.skymxc.demo.puzzle.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.skymxc.demo.puzzle.model.Image;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sky-mxc
 */
public class DBUtil {

    /**
     * 保存image
     * @param images image集合
     */
    public static void saveImage( List<Image> images){
        SQLiteDatabase db = DBHelper.getDB();
        try {
            db.beginTransaction();
            ContentValues cv = new ContentValues();
            for (Image image :images){
                Log.e("Tag","   saveImage ,Path:"+image.path);
                cv.clear();
                cv.put("path",image.path);
                cv.put("level",image.level);
                cv.put("type",image.type);
                db.insert("image","level",cv);
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * 判断 图片在 数据库中吗
     * @param path
     * @return
     */
    public static boolean isExists(String path){
        SQLiteDatabase db = DBHelper.getDB();
        Cursor cursor = db.rawQuery("select count(0) from image where path = ?",new String[]{path});
        if (cursor!=null && cursor.moveToFirst() && cursor.getInt(0)>0){

                return true;

        }else{
            return false;
        }
    }

    /**
     * 查询出所有图片
     * @return
     */
    public static List<Image> loadImages(){
        SQLiteDatabase db = DBHelper.getDB();
        Cursor cursor = db.rawQuery("select * from image ",null);
        List<Image> images = new ArrayList<>();
        while (cursor!=null && cursor.moveToNext()){

            images.add(createImage(cursor));

        }
        return images;
    }

    /**
     * 提取出 游标中的Image
     * @param cursor 游标
     * @return Image
     */
    private static Image createImage(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex("_id"));
        String  path = cursor.getString(cursor.getColumnIndex("path"));
        int level = cursor.getInt(cursor.getColumnIndex("level"));
        int type = cursor.getInt(cursor.getColumnIndex("type"));
        Image image = new Image(id,path,level,type);
        return  image;
    }

    /**
     * 保存 图片
     * @param image 图片
     */
    public static void saveImage(Image image){
        SQLiteDatabase db = DBHelper.getDB();
        ContentValues cv = new ContentValues();
        cv.put("path",image.path);
        cv.put("level",image.level);
        cv.put("type",image.type);
        db.insert("image","level",cv);
    }
}
