package com.cjx.zhiai.bean;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by cjx on 2017/2/9.
 */
public class OrderBean implements Serializable {
    public String order_id;
    public String order_time; // 时间
    public String order_total; // 总额
    public String subtotal; // 小计
    public String deduct_integral; // 抵扣积分
    public String order_mumber; // 订单号
    public String order_status; // 状态 1:待支付  2:支付完成  3:已取消
    public String FlagshipName; // 旗舰店名字
    public ArrayList<OrderItemBean> orderItem;

    SimpleDateFormat fromFormat = new SimpleDateFormat("yyyyMMddhhmmss");
    SimpleDateFormat toFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    public void format(){
        switch (order_status){
            case "1":
                order_status = "待支付";
                break;
            case "2":
                order_status = "已支付";
                break;
            case "3":
                order_status = "已取消";
                break;
        }
        try {
            order_time = toFormat.format(fromFormat.parse(order_time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
