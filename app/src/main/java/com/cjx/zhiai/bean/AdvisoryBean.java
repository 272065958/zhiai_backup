package com.cjx.zhiai.bean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by cjx on 2017-01-10.
 */
public class AdvisoryBean {
    private final static SimpleDateFormat srcSdf = new SimpleDateFormat("yyyyMMddhhmmss");
    private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");

    public String bespeak_id;
    public String content;
    public String time;
    public String picture_address;
    public String user_name;
    public String head_image;

    public void format(){
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
