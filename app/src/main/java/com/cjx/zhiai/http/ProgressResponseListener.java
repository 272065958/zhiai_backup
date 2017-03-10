package com.cjx.zhiai.http;

/**
 * Created by cjx on 2016/9/2.
 * 下载进度回调
 */
public interface ProgressResponseListener {
    void onResponseProgress(long bytesResd, long longcontentLength, boolean done);
}
