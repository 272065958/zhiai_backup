package com.cjx.zhiai.advisory;

import android.os.Bundle;
import android.widget.TextView;

import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseActivity;
import com.cjx.zhiai.bean.CurePlanBean;

/**
 * Created by cjx on 2017/1/19.
 * 治疗方案详情
 */
public class CurePlanDetailActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cure_plan_detail);

        setToolBar(true, null, R.string.advisory_project);

        CurePlanBean cpb = (CurePlanBean) getIntent().getSerializableExtra("cure_plan");
        TextView contentView = (TextView) findViewById(R.id.cure_plan_detail);
        TextView doctorView = (TextView) findViewById(R.id.cure_plan_doctor);
        TextView timeView = (TextView) findViewById(R.id.cure_plan_date);

        contentView.setText(cpb.recipe_content);
        doctorView.setText(cpb.office_name + "-" + cpb.user_real_name);
        timeView.setText(cpb.r_time);
    }
}
