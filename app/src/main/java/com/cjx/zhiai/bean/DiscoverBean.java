package com.cjx.zhiai.bean;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by cjx on 2016-12-26.
 */
public class DiscoverBean implements Serializable{
    private final static SimpleDateFormat srcSdf = new SimpleDateFormat("yyyyMMddhhmm");
    private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");

    public String article_id;
    public String content;
    public String article_time;
    public String image_address;
    public String my_location;
    public String user_id;
    public String commend;
    public String commentary;
    public String user_name;
    public String sex;
    public String head_image;
    public String user_real_name;
    public int user_type;

    public void format(){
        Date date = null;
        try {
            date = srcSdf.parse(article_time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(date != null){
            article_time = sdf.format(date);
        }
    }
}
