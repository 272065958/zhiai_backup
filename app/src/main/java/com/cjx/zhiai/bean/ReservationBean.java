package com.cjx.zhiai.bean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by cjx on 2017/1/20.
 */
public class ReservationBean {
    public String bespeak_id;
    public String doctor_id;
    public String doctor_name;
    public String office_name;
    public String state;
    public String bespeak_time;
    public String bespeak_content;
    public String money;
    public String bespeak_number;
    public int operateTime = 0;
    public void format(){
        bespeak_time = bespeak_time.replace("=", " ");
        if(state.equals("2")){
            int index = bespeak_time.indexOf(" ");
            String day = bespeak_time.substring(0, index);
            String time1 = bespeak_time.substring(index+1, index+6);
            String time2 = bespeak_time.substring(index+7, index+12);
            String date1 = day+" "+time1;
            String date2 = day+" "+time2;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
            try {
                Date d1 = sdf.parse(date1);
                Date d2 = sdf.parse(date2);
                Date now = new Date();
                if(now.after(d2)){
                    operateTime = -1;
                }else if(now.before(d2) && now.after(d1)){
                    operateTime = 1;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }
}
