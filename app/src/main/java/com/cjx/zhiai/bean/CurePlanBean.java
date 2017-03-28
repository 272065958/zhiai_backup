package com.cjx.zhiai.bean;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by cjx on 2017/1/19.
 */
public class CurePlanBean implements Serializable {
    private final static SimpleDateFormat srcSdf = new SimpleDateFormat("yyyyMMdd");
    private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    public String user_real_name;
    public String office_name;
    public String r_time;
    public String recipe_content;

    public void format() {
        Date date = null;
        try {
            date = srcSdf.parse(r_time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(date != null){
            r_time = sdf.format(date);
        }
    }
}
