package com.cjx.zhiai.bean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by cjx on 2016-12-29.
 */
public class ExpertCommentBean {
    private final static SimpleDateFormat srcSdf = new SimpleDateFormat("yyyyMMddhhmmss");
    private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
    public String content;
    public String date_time;
    public String user_name;
    public String head_image;
    public void format(){
        Date date = null;
        try {
            date = srcSdf.parse(date_time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(date != null){
            date_time = sdf.format(date);
        }
    }
}
