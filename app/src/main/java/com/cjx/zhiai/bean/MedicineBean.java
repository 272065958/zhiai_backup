package com.cjx.zhiai.bean;

import java.io.Serializable;

/**
 * Created by cjx on 2016-12-29.
 */
public class MedicineBean implements Serializable{
    public String medicine_id;
    public String medicine_name;
    public String min_picture;
    public String max_picture;
    public String producer; //制药厂
    public String ratify_number; //药准字
    public String symptom; // 症状
    public String ilness; // 疾病
    public String dose; // 用量
    public String medicine_type; // 产品剂型
    public String market_price; // 市场价
    public String associator_price; // app价
    public String sales_volume; // 销量
    public String guarantee_period; // 保质期
    public String effect;

    public String buyCount;
    public boolean isSelect;
}
