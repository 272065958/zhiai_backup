package com.cjx.zhiai.scan;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseSelectImageActivity;
import com.cjx.zhiai.bean.ResultBean;
import com.cjx.zhiai.component.ImageInsertView;
import com.cjx.zhiai.http.HttpUtils;
import com.cjx.zhiai.http.MyCallbackInterface;

import java.util.ArrayList;

/**
 * Created by cjx on 2016-12-04.
 * 发帖
 */
public class ScanCreateActivity extends BaseSelectImageActivity  implements TextWatcher {

    EditText contentView;
    TextView countView, locationView;
    ImageInsertView imageInsertView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_create);
        selectType = IMAGE_TYPE_OTHER;
        setToolBar(true, null, R.string.scan_create_title);

        imageInsertView = (ImageInsertView) findViewById(R.id.image_insert_view);
        View view = View.inflate(this, R.layout.advisory_image_hint_view, null);
        view.findViewById(R.id.image_hint_tip).setVisibility(View.GONE);
        imageInsertView.setAddImageListener(this, view);

        contentView = (EditText) findViewById(R.id.advisory_content);
        contentView.addTextChangedListener(this);
        countView = (TextView) findViewById(R.id.advisory_count);
        locationView = (TextView) findViewById(R.id.create_location);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        publish();
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.publish_menu, menu);
        return super.onCreateOptionsMenu(menu);
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(data == null){
                locationView.setTag(null);
                locationView.setText("地理位置");
            }else{
                String address = data.getAction();
                locationView.setText(address);
                locationView.setTag(address);
            }
        }
    }

    public void positionSelect(View view){
        Intent intent = new Intent(this, LocationActivity.class);
        startActivityForResult(intent, 1);
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

    // 发布朋友圈
    private void publish() {
        String content = contentView.getText().toString();
        if(TextUtils.isEmpty(content)){
            showToast(getString(R.string.scan_content_null));
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
        String address = locationView.getTag() != null ? (String) locationView.getTag() : "";

        showLoadDislog();
        MyCallbackInterface callbackInterface = new MyCallbackInterface() {
            @Override
            public void success(ResultBean response) {
                dismissLoadDialog();
                showToast(response.errorMsg);
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void error() {
                dismissLoadDialog();
            }
        };
        HttpUtils.getInstance().postEnqueue(this, callbackInterface, "article/publishArticle", "content", content,
                "imageAddress", images.toString(), "location", address);
    }
}
