package com.cjx.zhiai.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseDialog;

/**
 * Created by cjx on 2016/6/8.
 */
public class TipDialog extends BaseDialog implements View.OnClickListener {

    TextView titleView, describeView, comfirmView, cancelView;

    public TipDialog(Context context, boolean noCancel){
        super(context);
        init(noCancel);
    }

    public TipDialog(Context context) {
        super(context);
        init(false);
    }

    public TextView getDescribeView() {
        return describeView;
    }

    private void init(boolean noCancel) {

        View v = View.inflate(getContext(), R.layout.dialog_tip, null);
        titleView = (TextView) v.findViewById(R.id.tip_title);
        describeView = (TextView) v.findViewById(R.id.tip_describe);
        comfirmView = (TextView) v.findViewById(R.id.tip_comfirm);
        comfirmView.setOnClickListener(this);
        cancelView = (TextView) v.findViewById(R.id.tip_cancel);
        if(!noCancel){
            cancelView.setVisibility(View.VISIBLE);
            cancelView.setOnClickListener(this);
            v.findViewById(R.id.tip_btn_line).setVisibility(View.VISIBLE);
            comfirmView.setBackgroundResource(R.drawable.dialog_right_button);
        }

        setContentView(v);
    }

    public TipDialog setText(String title, String describe, String comfirm, String cancel){
        titleView.setText(title);
        describeView.setText(describe);
        comfirmView.setText(comfirm);
        cancelView.setText(cancel);
        return this;
    }

    public TipDialog setText(String title, String describe){
        titleView.setText(title);
        describeView.setText(describe);
        return this;
    }

    public TipDialog setText(int titleRes, int describeRes, int comfirmRes, int cancelRes){
        titleView.setText(titleRes);
        describeView.setText(describeRes);
        comfirmView.setText(comfirmRes);
        cancelView.setText(cancelRes);
        return this;
    }

    public TipDialog setText(int titleRes, int describeRes, int comfirmRes){
        titleView.setText(titleRes);
        describeView.setText(describeRes);
        comfirmView.setText(comfirmRes);
        cancelView.setVisibility(View.GONE);
        findViewById(R.id.tip_btn_line).setVisibility(View.GONE);
        return this;
    }

    ComfirmListener listener;
    public TipDialog setTipComfirmListener(ComfirmListener listener){
        this.listener = listener;
        return this;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tip_cancel:
                if(listener != null){
                    listener.cancel();
                }
                break;
            case R.id.tip_comfirm:
                if(listener != null){
                    listener.comfirm();
                }
                break;
        }
    }

    public interface ComfirmListener{
        void comfirm();
        void cancel();
    }
}
