package com.cjx.zhiai.dialog;

import android.app.Dialog;
import android.content.Context;
import android.widget.TextView;

import com.cjx.zhiai.R;

/**
 * Created by cjx on 2016/7/18.
 */
public class LoadDialog extends Dialog {

    TextView tipView;

    public LoadDialog(Context context){
        super(context, R.style.loading_dialog);
        init();

    }

    public LoadDialog(Context context, String tip){
        super(context, R.style.loading_dialog);
        init();
        setTip(tip);
    }

    public LoadDialog(Context context, int tipRes){
        super(context, R.style.loading_dialog);
        init();
        setTip(tipRes);
    }

    public void setTip(String tip){
        tipView.setText(tip);
    }

    public void setTip(int tipRes){
        tipView.setText(tipRes);
    }

    private void init(){

        setContentView(R.layout.dialog_load);
        setCanceledOnTouchOutside(false);
        tipView = (TextView) findViewById(R.id.loading_tip);
    }

    public TextView getTipView(){
        return tipView;
    }
}
