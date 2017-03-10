package com.cjx.zhiai.dialog;

import android.app.Dialog;
import android.content.Context;

import com.cjx.zhiai.R;

/**
 * Created by cjx on 2016/8/3.
 */
public class TagDialog extends Dialog {

    public TagDialog(Context context){
        super(context, R.style.loading_dialog);
    }

    Object tag;
    /**
     * 设置一个标签
     * */
    public void setTag(Object tag){
        this.tag = tag;
        return;
    }

    public Object getTag(){
        return tag;
    }
}
