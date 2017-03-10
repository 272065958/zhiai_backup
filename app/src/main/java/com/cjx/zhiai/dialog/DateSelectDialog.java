package com.cjx.zhiai.dialog;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.cjx.zhiai.R;
import com.cjx.zhiai.component.wheel.WheelLayout;

import java.util.Calendar;

/**
 * Created by cjx on 2016/2/24.
 */
public class DateSelectDialog extends CustomDialog {
    WheelLayout timePickView;

    public DateSelectDialog(Context context, boolean selectDay, WheelLayout.TimeStyle style) {
        super(context);
        setContentView(R.layout.dialog_wheel_select);
        timePickView = (WheelLayout) findViewById(R.id.pick_view);
        Calendar c = Calendar.getInstance();
        if (selectDay) {
            timePickView.setDate(c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH), -1, style);
        } else {
            timePickView.initDateWheelView(-1, -1, -1, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), style);
        }

        findViewById(R.id.pick_comfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView time = (TextView) timePickView.getTag();
                time.setText(timePickView.getTime());
                dismiss();
            }
        });
    }

    public DateSelectDialog bind(TextView v) {
        timePickView.setTag(v);
        return this;
    }
}
