package com.cjx.zhiai.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by cjx on 2016/8/8.
 * 数据库管理工具
 */
public class SQLiteHelper extends SQLiteOpenHelper {
    private static SQLiteHelper instance;

    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "zhiai.db";

    public final String TABLE_NAME_SHOP_CART = "shop_cart";//购物车
    public final String TABLE_NAME_CITY = "city";//省市县

    public final String ID = "_id";
    public final String P_ID = "p_id";
    public final String P_IMAGE = "p_image";
    public final String P_NAME = "p_name";
    public final String P_OLD_PRICE = "p_old_price";
    public final String P_NOW_PRICE = "p_now_price";
    public final String P_COUNT = "p_count";

    public final String LEVEL = "level";
    public final String PARENT_ID = "parent_id";

    public final String TEXT_TYPE = " TEXT";
    public final String COMMA_SEP = ",";

    public static SQLiteHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (SQLiteHelper.class) {
                if (instance == null) {
                    instance = new SQLiteHelper(context);
                }
            }
        }
        return instance;
    }

    private SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            createTable(db);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    private void createTable(SQLiteDatabase db) {
        final String SQL_CREATE_SHOP_CART = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_SHOP_CART + " (" + ID + " INTEGER PRIMARY KEY," +
                P_ID + TEXT_TYPE + COMMA_SEP +
                P_IMAGE + TEXT_TYPE + COMMA_SEP +
                P_NAME + TEXT_TYPE + COMMA_SEP +
                P_OLD_PRICE + TEXT_TYPE + COMMA_SEP +
                P_NOW_PRICE + TEXT_TYPE + COMMA_SEP +
                P_COUNT + TEXT_TYPE + " )";
        final String SQL_CREATE_CITY = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_CITY + " (" + ID + " INTEGER PRIMARY KEY," +
                P_ID + TEXT_TYPE + COMMA_SEP +
                PARENT_ID + TEXT_TYPE + COMMA_SEP +
                P_NAME + TEXT_TYPE + COMMA_SEP +
                LEVEL + TEXT_TYPE + " )";
        db.execSQL(SQL_CREATE_SHOP_CART);
        db.execSQL(SQL_CREATE_CITY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    SQLiteDatabase sqLiteDatabase;

    public synchronized SQLiteDatabase getDatabase() {
        if (sqLiteDatabase == null) {
            sqLiteDatabase = getReadableDatabase();
        }
        return sqLiteDatabase;
    }

    // 判断是否存在table这个表
    public synchronized boolean checkTableExists(String table) {
        boolean result = false;
        SQLiteDatabase db = getDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name='" + table+"'", null);
        if(cursor.moveToNext()){
            int count = cursor.getInt(0);
            if(count>0){
                result = true;
            }
        }
        cursor.close();
        return result;
    }

    public void executeSql(String sql){
        SQLiteDatabase db = getDatabase();
        db.beginTransaction();
        try {
            db.execSQL(sql);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }
}
