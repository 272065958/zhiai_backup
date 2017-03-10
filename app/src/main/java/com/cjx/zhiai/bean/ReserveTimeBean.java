package com.cjx.zhiai.bean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by cjx on 2016-12-29.
 */
public class ReserveTimeBean {
    public String date;
    public ArrayList<TimeBean> times;

    private static SimpleDateFormat sdfSrc = new SimpleDateFormat("yyyyMMdd");
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    public void format() {
        try {
            date = sdf.format(sdfSrc.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
