package com.cjx.zhiai.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.cjx.zhiai.R;

/**
 * Created by cjx on 2015/11/30.
 */
public class CustomDialog extends TagDialog {

    public CustomDialog(Context context) {
        super(context);
    }

    public void setContentView(View view){
        super.setContentView(view);
        setDialogStyle();
    }

    public void setContentView(int viewRes){
        super.setContentView(viewRes);
        setDialogStyle();
    }

    private void setDialogStyle(){
        // 设置对话框在底部, 重用的时候优化的不是很好, 切换的时候不同选项的时候有问题
        Window mWindow = getWindow();
        mWindow.setWindowAnimations(R.style.my_dialog);
        mWindow.setGravity(Gravity.BOTTOM);
        mWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}
