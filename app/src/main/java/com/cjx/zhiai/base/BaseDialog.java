package com.cjx.zhiai.base;

import android.app.Activity;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;

import com.cjx.zhiai.MyApplication;
import com.cjx.zhiai.dialog.TagDialog;


/**
 * Created by cjx on 2016/8/3.
 */
public class BaseDialog extends TagDialog {

    public BaseDialog(Context context){
        super(context);
        if(context instanceof Activity){
            Window dw = getWindow();
            WindowManager.LayoutParams wl = dw.getAttributes();
            float scale = ((MyApplication)((Activity)context).getApplication()).getScreen_width() / 720f;
            wl.width = (int) (460 * scale);
            dw.setAttributes(wl);
        }
    }
}
