package com.cjx.zhiai.base;

import android.util.Log;

/**
 * Created by cjx on 2017/2/14.
 */
public abstract class TreeBean {
    public long hasChild;
    public abstract String getName();
    public abstract String getId();
    public boolean isChild(){
        Log.e("TAG", "haschild = "+hasChild);
        return hasChild == 0;
    }
}
