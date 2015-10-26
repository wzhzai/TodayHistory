package com.example.wangzhengze.todayhistory.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import com.example.wangzhengze.todayhistory.bean.LightingBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by WANGZHENGZE on 2015/10/23.
 */
public class DBManager {

    private static final String TAG = "DBManager";

    // database版本
    private final static int DB_VERSION = 1;
    // database名
    private final static String DB_NAME = "lighting.db";

    private Context context;

    private static DBManager dbManager;

    private SQLiteDatabase db = null;

    private DataBaseHelper dbHelper = null;

    private DBManager(Context context) {
        this.context = context;
    }

    private static class DataBaseHelper extends SQLiteOpenHelper {

        Context context;
        DataBaseHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
            this.context = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table task(_id integer primary key, title text, content text, reminder_time long, status int, create_time long)");
            ContentValues contentValues = new ContentValues();
            contentValues.put("title", "轻醒的妙用？");
            contentValues.put("content", "轻醒的妙用？\n又滑完成任务，左滑删除任务\n完成任务后可以归档，让界面更清爽");
            contentValues.put("reminder_time", 0);
            contentValues.put("status", 0);
            contentValues.put("create_time", System.currentTimeMillis());
            db.insert("task", null, contentValues);
            contentValues = new ContentValues();
            contentValues.put("title", "添加时间提醒，不再错过重要事项");
            contentValues.put("content", "添加时间提醒，不再错过重要事项");
            contentValues.put("reminder_time", 0);
            contentValues.put("status", 0);
            contentValues.put("create_time", System.currentTimeMillis());
            db.insert("task", null, contentValues);
            contentValues = new ContentValues();
            contentValues.put("title", "日历模式，让您轻松管理日常事务");
            contentValues.put("content", "日历模式，让您轻松管理日常事务");
            contentValues.put("reminder_time", 0);
            contentValues.put("status", 0);
            contentValues.put("create_time", System.currentTimeMillis());
            db.insert("task", null, contentValues);
            contentValues = new ContentValues();
            contentValues.put("title", "登录后可以给好友发提醒哦");
            contentValues.put("content", "登录后可以给好友发提醒哦");
            contentValues.put("reminder_time", 0);
            contentValues.put("status", 0);
            contentValues.put("create_time", System.currentTimeMillis());
            db.insert("task", null, contentValues);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }


    public static synchronized DBManager getInstance(Context context) {
        if (dbManager == null) {
            dbManager = new DBManager(context);
            dbManager.open();
        }
        return dbManager;
    }

    public void open() throws SQLException {
        if (isOpen()) {
            return;
        }
        dbHelper = new DataBaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public void quit() {
        try {
            if (isOpen() && dbHelper != null)
                dbHelper.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isOpen() {
        return db != null && db.isOpen();
    }

    public List<LightingBean> getHintList() {
        List<LightingBean> lightingBeanList = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from task order by create_time desc", null);
        while (cursor.moveToNext()) {
            LightingBean bean = new LightingBean();
            bean.id = cursor.getInt(cursor.getColumnIndex("_id"));
            bean.title = cursor.getString(cursor.getColumnIndex("title"));
            bean.content = cursor.getString(cursor.getColumnIndex("content"));
            bean.reminderTime = cursor.getLong(cursor.getColumnIndex("reminder_time"));
            bean.status = cursor.getInt(cursor.getColumnIndex("status"));
            bean.createTime = cursor.getLong(cursor.getColumnIndex("create_time"));
            lightingBeanList.add(bean);
        }
        cursor.close();
        return lightingBeanList;
    }

    public void saveTask(LightingBean bean) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", bean.title);
        contentValues.put("content", bean.content);
        contentValues.put("reminder_time", 0);
        contentValues.put("status", 0);
        contentValues.put("create_time", System.currentTimeMillis());
        db.insert("task", null, contentValues);
    }

    public void updateTask(LightingBean bean) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", bean.title);
        contentValues.put("content", bean.content);
        contentValues.put("reminder_time", 0);
        contentValues.put("status", 0);
        contentValues.put("create_time", System.currentTimeMillis());
        db.update("task", contentValues, "_id = ?", new String[]{String.valueOf(bean.id)});
    }
}
