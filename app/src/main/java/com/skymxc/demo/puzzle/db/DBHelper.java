package com.skymxc.demo.puzzle.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sky-mxc
 * sqlite帮助类
 */
public class DBHelper extends SQLiteOpenHelper {
    private static  final String DB_NAME ="puzzle.db";
    private  SQLiteDatabase database;
    private static  DBHelper helper ;
    private static Context mContext ;
    private static int mVersion ;

    /**
     * 初始化
     * @param context
     * @param version
     */
    public static void init(Context context,int version){
        mContext =context;
        mVersion =version;
    }

    /**
     * 得到 SQLiteDatabase
     * @return
     */
    public static SQLiteDatabase getDB(){
        return getInstance(mContext,mVersion).getDatabase();
    }

    /**
     * 得到 DBHelper 实例
     */
    public synchronized static DBHelper getInstance(Context context ,int version){
        if (helper==null){
            helper = new DBHelper(context,version);
        }
        return  helper;
    }
    public SQLiteDatabase getDatabase (){
        if (database ==null){
            database = getWritableDatabase();
        }
        return  database;
    }
    private DBHelper(Context context,  int version) {
        super(context, DB_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table image(" +
                "_id integer not null primary key autoincrement," +
                "path varchar(300) UNIQUE not null," +
                "level integer(1)," +
                "type integer(1) not null" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
