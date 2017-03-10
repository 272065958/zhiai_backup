package com.cjx.zhiai.bean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by cjx on 2017-01-10.
 */
public class AdvisoryCommentBean {
    private final static SimpleDateFormat srcSdf = new SimpleDateFormat("yyyyMMddhhmmss");
    private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");

    public String content;
    public String time;
    public String user_name;
    public String head_image;
    public int belong_to;
    public String doctor_name;
    public String picture_address;
//    public String sex;
//    public String content_id;
//    public String bespeak_id;
    public void format(){
        if(belong_to == 1){ // 显示医生的名字
            user_name = doctor_name;
        }
        Date date = null;
        try {
            date = srcSdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(date != null){
            time = sdf.format(date);
        }
    }
}
