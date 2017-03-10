package com.cjx.zhiai.advisory;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cjx.zhiai.MyApplication;
import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseSelectImageActivity;
import com.cjx.zhiai.bean.ResultBean;
import com.cjx.zhiai.component.ImageInsertView;
import com.cjx.zhiai.http.HttpUtils;
import com.cjx.zhiai.http.MyCallbackInterface;

import java.util.ArrayList;

/**
 * Created by cjx on 2016-11-29.
 * 新建图文问诊界面
 */
public class AdvisoryImageActivity extends BaseSelectImageActivity implements TextWatcher {

    EditText contentView;
    TextView countView;
    Button submitView;
    ImageInsertView imageInsertView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advisory_image);

        setToolBar(true, null, R.string.main_advisory);

        imageInsertView = (ImageInsertView) findViewById(R.id.image_insert_view);
        imageInsertView.setAddImageListener(this, View.inflate(this, R.layout.advisory_image_hint_view, null));

        contentView = (EditText) findViewById(R.id.advisory_content);
        contentView.addTextChangedListener(this);
        countView = (TextView) findViewById(R.id.advisory_count);
        submitView = (Button) findViewById(R.id.advisory_submit);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        int count = s.length();
        countView.setText(count + "/300");
        if (count == 0) {
            submitView.setClickable(false);
            submitView.setBackgroundResource(R.drawable.gray_solid_rectangle_bg);
        } else {
            submitView.setClickable(true);
            submitView.setBackgroundResource(R.drawable.red_rectangle_bg);
        }
    }

    @Override
    protected void uploadResult(String path) {
        if (path.contains(",")) {
            String[] paths = path.split(",");
            for (String p : paths) {
                imageInsertView.addImage(p);
            }
        } else {
            imageInsertView.addImage(path);
        }
    }

    // 提交咨询内容
    public void submit(View v) {
        String content = contentView.getText().toString();
        if(TextUtils.isEmpty(content)){
            showToast(getString(R.string.expert_advisory_null));
            return ;
        }
        ArrayList<String> list = imageInsertView.getImages();
        StringBuilder images = new StringBuilder();
        if(list != null && !list.isEmpty()){
            for(String s : list){
                if(images.length() > 0){
                    images.append(",");
                }
                images.append(s);
            }
        }
        showLoadDislog();
        MyCallbackInterface callbackInterface = new MyCallbackInterface() {
            @Override
            public void success(ResultBean response) {
                dismissLoadDialog();
                showToast(response.errorMsg);
                finish();
            }

            @Override
            public void error() {
                dismissLoadDialog();
            }
        };
        Intent intent = getIntent();
        HttpUtils.getInstance().postEnqueue(this, callbackInterface, "base/saveConsultInfo", "content", content,
                "image_address", images.toString(), "office_id", intent.getStringExtra("office_id"), "type", "0", "doctor_id", intent.getAction(),
                "patient_id", MyApplication.getInstance().user.user_id);
    }
}
