package com.example.test0;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class StorageHelper extends SQLiteOpenHelper {

    public StorageHelper(Context context){
        super(context,"storage.db",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //数据库的构成：名字name、数量number、标签tag、说明note，存储位置location、图片picture、更新时间time
        sqLiteDatabase.execSQL("create table if not exists store(name String,number double,tag String,note String,location String,picture String,time String)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
