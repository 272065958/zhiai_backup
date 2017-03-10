package com.cjx.zhiai.http;

import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseActivity;

/**
 * Created by cjx on 2016/11/11.
 */
public class ProgressRequestHandler implements ProgressRequestListener, Handler.Callback {
    final int UPLOAD_SUCCESS = 200;
    TextView view;
    Handler handler;
    BaseActivity activity;
    int progress;
    public ProgressRequestHandler(BaseActivity activity, TextView view){
        this.activity = activity;
        this.view = view;
        handler = new Handler(this);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what){
            case UPLOAD_SUCCESS:
                view.setText(null);
                break;
            default:
                view.setText(String.format(activity.getString(R.string.upload_progress), msg.what));
                break;
        }
        return false;
    }

    @Override
    public void onRequestProgress(long bytesWritten, long contentLength, boolean done) {
        if (contentLength != -1) {
            //长度未知的情况下回返回-1
            int p = (int) ((100 * bytesWritten) / contentLength);
            if (progress != p) {
                progress = p;
                handler.sendEmptyMessage(progress);
            }
            if (progress == 100 && done) {
                handler.sendEmptyMessage(UPLOAD_SUCCESS);
            }

        }
    }
}
