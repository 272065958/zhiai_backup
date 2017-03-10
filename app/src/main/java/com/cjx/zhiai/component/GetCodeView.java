package com.cjx.zhiai.component;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import com.cjx.zhiai.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by cjx on 2016-11-26.
 * 获取验证码的view
 */
public class GetCodeView extends Button {

    int timeOut = 60;
    Timer timer;
    public GetCodeView(Context context) {
        super(context);
        init();
    }

    public GetCodeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init(){

    }

    public void startTimer() {
        setClickable(false);
        setSelected(true);
        timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                post(new Runnable() {
                    @Override
                    public void run() {
                        if (timeOut > 0) {
                            timeOut--;
                            setText(timeOut + " s");
                        } else {
                            cancel();
                            timer = null;
                            timeOut = 60;
                            setText(R.string.button_get_code);
                            setSelected(false);
                            setClickable(true);
                        }
                    }
                });
            }
        }, 0, 1000);
    }

    // 终止定时器
    public void stopTimer(){
        if(timer != null){
            timer.cancel();
            timer = null;
        }
    }
}
