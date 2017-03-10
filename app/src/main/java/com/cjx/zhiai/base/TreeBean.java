package com.cjx.zhiai.base;

/**
 * Created by cjx on 2017/2/14.
 */
public abstract class TreeBean {
    public long hasChild;
    public abstract String getName();
    public abstract String getId();
    public boolean isChild(){
        return hasChild > 0;
    }
}
