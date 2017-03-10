package com.cjx.zhiai.http;

/**
 * Created by cjx on 2016/9/2.
 * 上传进度回调
 */
public interface ProgressRequestListener {
    void onRequestProgress(long bytesWritten, long longcontentLength, boolean done);
}
