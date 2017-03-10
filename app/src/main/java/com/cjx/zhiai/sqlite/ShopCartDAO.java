package com.cjx.zhiai.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import com.cjx.zhiai.bean.MedicineBean;

import java.util.ArrayList;

/**
 * Created by cjx on 2016/7/14.
 * 草稿操作工具
 */
public class ShopCartDAO {

    private static ShopCartDAO instance;
    private SQLiteHelper helper;

    public static ShopCartDAO getInstance(Context context) {
        if (instance == null) {
            synchronized (ShopCartDAO.class) {
                if (instance == null) {
                    instance = new ShopCartDAO(context);

                }
            }
        }
        return instance;
    }

    private ShopCartDAO(Context context) {
        helper = SQLiteHelper.getInstance(context);
    }

    // 插入一条草稿数据
    public synchronized long insertShop(MedicineBean mb) {
        long result = -1;
        SQLiteDatabase db = helper.getDatabase();
        String where = helper.P_ID + " = " + mb.medicine_id;

        db.beginTransaction();
        try {
            ContentValues cv = new ContentValues();
            cv.put(helper.P_COUNT, mb.buyCount);
            cv.put(helper.P_IMAGE, mb.min_picture);
            cv.put(helper.P_OLD_PRICE, mb.market_price);
            cv.put(helper.P_NOW_PRICE, mb.associator_price);
            cv.put(helper.P_NAME, mb.medicine_name);
            int count = db.update(helper.TABLE_NAME_SHOP_CART, cv, where, null);
            if (count < 1) {
                cv.put(helper.P_ID, mb.medicine_id);
                result = db.insert(helper.TABLE_NAME_SHOP_CART, null, cv);
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        return result;
    }

    // 插入一条草稿数据
    public synchronized long updateShop(String pid, String count) {
        long result = -1;
        SQLiteDatabase db = helper.getDatabase();
        String where = helper.P_ID + " = " + pid;

        db.beginTransaction();
        try {
            ContentValues cv = new ContentValues();
            cv.put(helper.P_COUNT, count);
            result = db.update(helper.TABLE_NAME_SHOP_CART, cv, where, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        return result;
    }

    // 根据taskid查找草稿
    public synchronized ArrayList<MedicineBean> queryShop(){
        SQLiteDatabase db = helper.getDatabase();
        Cursor cursor = db.query(helper.TABLE_NAME_SHOP_CART, new String[]{helper.P_ID, helper.P_IMAGE,
                helper.P_COUNT, helper.P_NAME, helper.P_NOW_PRICE, helper.P_OLD_PRICE}, null, null, null, null, null);
        ArrayList<MedicineBean> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            MedicineBean mb = new MedicineBean();
            mb.medicine_id = cursor.getString(0);
            mb.min_picture = cursor.getString(1);
            mb.buyCount = cursor.getString(2);
            mb.medicine_name = cursor.getString(3);
            mb.associator_price = cursor.getString(4);
            mb.market_price = cursor.getString(5);
            list.add(mb);
        }
        cursor.close();
        return list;
    }

    // 删除超过一天未上传的数据
    public synchronized int deleteShop(String pid) {
        SQLiteDatabase db = helper.getDatabase();
        int count = 0;
        db.beginTransaction();
        try {
            String where = helper.P_ID + " = " + pid;
            // 删除过期的记录
            count = db.delete(helper.TABLE_NAME_SHOP_CART, where, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        return count;
    }

    // 获取待上传水表数量
    public synchronized long getShopCount() {
        long count = 0L;
        try {
            if(helper.checkTableExists(helper.TABLE_NAME_SHOP_CART)){
                SQLiteDatabase db = helper.getDatabase();
                String sql = "SELECT COUNT(1) FROM " + helper.TABLE_NAME_SHOP_CART;
                SQLiteStatement statement = db.compileStatement(sql);
                count = statement.simpleQueryForLong();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    public int deleteShops(ArrayList<MedicineBean> buyList) {
        StringBuilder sb = new StringBuilder();
        for(MedicineBean mb : buyList){
            if(sb.length() >= 0){
                sb.append(",");
            }
            sb.append(mb.medicine_id);
        }
        SQLiteDatabase db = helper.getDatabase();
        int count = 0;
        db.beginTransaction();
        try {
            String where = helper.P_ID + " IN (" + sb.toString() + ")";
            // 删除过期的记录
            count = db.delete(helper.TABLE_NAME_SHOP_CART, where, null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        return count;
    }
}
