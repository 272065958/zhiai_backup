package com.cjx.zhiai.bean;

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

    public void format(){
        bespeak_time = bespeak_time.replace("=", " ");
    }
}
