package com.cjx.zhiai.bean;

import com.cjx.zhiai.base.TreeBean;

/**
 * Created by cjx on 2017/2/10.
 */
public class CityBean extends TreeBean{
    public String id;
    public String parentId;
    public String name;
    public int level;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getId() {
        return id;
    }
}
