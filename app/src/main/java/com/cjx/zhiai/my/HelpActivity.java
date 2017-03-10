package com.cjx.zhiai.my;

import android.os.Bundle;

import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseActivity;

/**
 * Created by cjx on 2016-12-31.
 */
public class HelpActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        setToolBar(true, null, R.string.my_help);
    }
}
