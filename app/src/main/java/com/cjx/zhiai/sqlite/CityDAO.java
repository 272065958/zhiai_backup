package com.cjx.zhiai.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;


import com.cjx.zhiai.bean.CityBean;
import com.cjx.zhiai.util.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by cjx on 2016/7/14.
 * 草稿操作工具
 */
public class CityDAO {

    private static CityDAO instance;
    private SQLiteHelper helper;

    public static CityDAO getInstance(Context context) {
        if (instance == null) {
            synchronized (CityDAO.class) {
                if (instance == null) {
                    instance = new CityDAO(context);
                }
            }
        }
        return instance;
    }

    private CityDAO(Context context) {
        helper = SQLiteHelper.getInstance(context);
        if(hasInit() == 0){
            initCity(context);
        }
    }

    private void initCity(Context context) {
        try { // 读取缓存流
            InputStream stream = context.getAssets().open("city.txt");
            InputStreamReader reader = new InputStreamReader(stream);
            BufferedReader bufferedReader = new BufferedReader(reader);
            StringBuffer buffer = new StringBuffer();
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            String addressStr = buffer.toString();

            ArrayList<CityBean> list = JsonParser.getInstance().fromJson(addressStr, new TypeToken<ArrayList<CityBean>>() {
            }.getType());

            SQLiteDatabase db = helper.getDatabase();
            try { // 将数据写到数据库
                String sql = "INSERT INTO " + helper.TABLE_NAME_CITY + " (" + helper.P_ID + ", " + helper.PARENT_ID + ", " +
                        helper.P_NAME + ", " + helper.LEVEL + ") VALUES (?, ?, ?, ?)";
                db.beginTransaction();
                SQLiteStatement statement = db.compileStatement(sql);
                for (CityBean cb : list) {
                    statement.bindString(1, cb.id);
                    statement.bindString(2, cb.parentId == null ? "" : cb.parentId);
                    statement.bindString(3, cb.name);
                    statement.bindString(4, String.valueOf(cb.level));
                    statement.executeInsert();
                }
                statement.clearBindings();
                db.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                db.endTransaction();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<CityBean> getCity(String parentId, String level) {
        SQLiteDatabase db = helper.getDatabase();
        StringBuilder whereBuilder = new StringBuilder();
        whereBuilder.append(helper.LEVEL);
        whereBuilder.append(" = '");
        whereBuilder.append(level);
        whereBuilder.append("' ");
        if (parentId != null) {
            whereBuilder.append(" AND ");
            whereBuilder.append(helper.PARENT_ID);
            whereBuilder.append(" = '");
            whereBuilder.append(parentId);
            whereBuilder.append("'");
        }
        ArrayList<CityBean> list = new ArrayList<>();
        String hasChildSql = "(SELECT COUNT(1) FROM "+ helper.TABLE_NAME_CITY +" c " +
                "WHERE c."+ helper.PARENT_ID +" = city."+helper.P_ID+") AS hasChild ";
        Cursor cursor = db.query(helper.TABLE_NAME_CITY + " city", new String[]{"city." + helper.P_ID, "city." + helper.PARENT_ID,
                "city." + helper.P_NAME, "city." + helper.LEVEL, hasChildSql}, whereBuilder.toString(), null, null, null, null);
        while (cursor.moveToNext()) {
            CityBean cb = new CityBean();
            cb.id = cursor.getString(0);
            cb.parentId = cursor.getString(1);
            cb.name = cursor.getString(2);
            cb.level = cursor.getInt(3);
            cb.hasChild = cursor.getLong(4);
            list.add(cb);
        }
        return list;
    }

    private long hasInit() {
        long count = 0L;
        try {
            SQLiteDatabase db = helper.getDatabase();
            String sql = "SELECT COUNT(1) FROM " + helper.TABLE_NAME_CITY;
            SQLiteStatement statement = db.compileStatement(sql);
            count = statement.simpleQueryForLong();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }
}
