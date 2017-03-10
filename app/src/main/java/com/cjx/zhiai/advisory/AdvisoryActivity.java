package com.cjx.zhiai.advisory;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseActivity;

/**
 * Created by cjx on 2016-11-27.
 * 选择找医生还是找医院
 */
public class AdvisoryActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advisory);

        setToolBar(true, null, R.string.main_find_expert);
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.advisory_online_expert: // 在线专家
                Intent onlineIntent = new Intent(this, ExpertActivity.class);
                onlineIntent.putExtra("title", R.string.advisory_online_expert);
                onlineIntent.putExtra("user_type", "2");
                startActivity(onlineIntent);
                break;
            case R.id.advisory_parttime_expert: // 兼职专家
                Intent parttimeIntent = new Intent(this, ExpertActivity.class);
                parttimeIntent.putExtra("title", R.string.advisory_parttime_expert);
                parttimeIntent.putExtra("user_type", "1");
                startActivity(parttimeIntent);
                break;
            case R.id.advisory_project: // 治疗方案
                Intent cureIntent = new Intent(this, CurePlanActivity.class);
                startActivity(cureIntent);
                break;
        }
    }
}
