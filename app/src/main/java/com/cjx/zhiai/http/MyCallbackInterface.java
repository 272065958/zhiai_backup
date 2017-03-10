package com.cjx.zhiai.http;

import com.cjx.zhiai.bean.ResultBean;

/**
 * Created by cjx on 2016/7/18.
 */
public interface MyCallbackInterface {
    void success(ResultBean response);
    void error();
}
