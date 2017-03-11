package com.cjx.zhiai.my;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.cjx.zhiai.MyApplication;
import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseSelectImageActivity;
import com.cjx.zhiai.bean.ResultBean;
import com.cjx.zhiai.bean.UserBean;
import com.cjx.zhiai.http.HttpUtils;
import com.cjx.zhiai.http.MyCallbackInterface;
import com.cjx.zhiai.util.Tools;

/**
 * Created by cjx on 2016-12-07.
 * 修改用户资料
 */
public class UpdatePeopleInfoActivity extends BaseSelectImageActivity {
    EditText trueNameView, nameView, addressView, emailView;
    View manView, womenView;
    ImageView headView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_peopel_info);
        setToolBar(true, null, R.string.my_setting_info);
        selectType = IMAGE_TYPE_USER;
        findViewById();
    }

    private void findViewById(){
        trueNameView = (EditText) findViewById(R.id.update_true_name);
        nameView = (EditText) findViewById(R.id.update_name);
        addressView = (EditText) findViewById(R.id.update_address);
        emailView = (EditText) findViewById(R.id.update_email);
        manView = findViewById(R.id.update_sex_man);
        womenView = findViewById(R.id.update_sex_women);
        headView = (ImageView) findViewById(R.id.update_head);
        headView.setOnClickListener(this);

        UserBean user = ((MyApplication)getApplication()).user;
        trueNameView.setText(user.user_real_name);
        nameView.setText(user.user_name);
        addressView.setText(user.user_address);
        emailView.setText(user.mailbox);
        if("f".equals(user.sex)){
            womenView.setSelected(true);
        }else{
            manView.setSelected(true);
        }
        if (TextUtils.isEmpty(user.head_image)) {
            headView.setImageBitmap(null);
        } else {
            Tools.setImageInView(this, user.head_image, headView);
            headView.setTag(R.id.update_head, user.head_image);
        }
    }

    public void viewClick(View v){
        switch (v.getId()){
            case R.id.update_sex_man:
                if(womenView.isSelected()){
                    womenView.setSelected(false);
                    manView.setSelected(true);
                }
                break;
            case R.id.update_sex_women:
                if(manView.isSelected()){
                    manView.setSelected(false);
                    womenView.setSelected(true);
                }
                break;
            case R.id.update_button: // 保存信息
                save();
                break;
        }
    }

    private void save(){
        final String trueName = trueNameView.getText().toString();
        if(TextUtils.isEmpty(trueName)){
            Toast.makeText(this, getString(R.string.setting_info_true_name_hint), Toast.LENGTH_SHORT).show();
            return ;
        }
        final String name = nameView.getText().toString();
        if(TextUtils.isEmpty(name)){
            Toast.makeText(this, getString(R.string.setting_info_name_hint), Toast.LENGTH_SHORT).show();
            return ;
        }
        final String address = addressView.getText().toString();
        if(TextUtils.isEmpty(name)){
            Toast.makeText(this, getString(R.string.setting_info_address_hint), Toast.LENGTH_SHORT).show();
            return ;
        }
        final String email = emailView.getText().toString();
        if(TextUtils.isEmpty(name)){
            Toast.makeText(this, getString(R.string.setting_info_email_hint), Toast.LENGTH_SHORT).show();
            return ;
        }
        final String sex = manView.isSelected() ? "m" : "f";
        final String head = (String) headView.getTag(R.id.update_head);
        MyCallbackInterface callbackInterface = new MyCallbackInterface() {
            @Override
            public void success(ResultBean response) {
                dismissLoadDialog();
                Toast.makeText(UpdatePeopleInfoActivity.this, response.errorMsg, Toast.LENGTH_SHORT).show();
                UserBean user = ((MyApplication)getApplication()).user;
                user.head_image = head;
                user.user_real_name = trueName;
                user.user_name = name;
                user.sex = sex;
                user.user_address = address;
                user.mailbox = email;
                sendBroadcast(new Intent(MyApplication.ACTION_USER_INFO_UPDATE));
                setResult(RESULT_OK);
            }

            @Override
            public void error() {
                dismissLoadDialog();
            }
        };
        showLoadDislog();
        HttpUtils.getInstance().postEnqueue(this, callbackInterface, "user/modifyUserInfo", "realName", trueName,
                "userName", name, "sex", sex, "addr", address, "mailbox", email, "type", "0", "imageAddress", head);
    }

    @Override
    protected void uploadResult(String path) {
        Tools.setImageInView(this, path, headView);
        headView.setTag(R.id.update_head, path);
    }
}
