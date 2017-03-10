package com.cjx.zhiai.my;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cjx.zhiai.MyApplication;
import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseActivity;
import com.cjx.zhiai.bean.ResultBean;
import com.cjx.zhiai.component.RatingView;
import com.cjx.zhiai.http.HttpUtils;
import com.cjx.zhiai.http.MyCallbackInterface;

/**
 * Created by Administrator on 2017-01-22.
 * 评论咨询
 */
public class AdvisoryCommentActivity extends BaseActivity implements TextWatcher, RatingView.OnRatingChangeListener {
    EditText commentView;
    Button submitView;
    RatingView ratingView;
    TextView countView;

    Bitmap starNor, starSel;
    int currentStar = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advisory_comment);
        setToolBar(true, null, R.string.advisory_comment);

        commentView = (EditText) findViewById(R.id.advisory_content);
        commentView.addTextChangedListener(this);
        countView = (TextView) findViewById(R.id.advisory_count);
        submitView = (Button) findViewById(R.id.advisory_submit);

        int starSize = getResources().getDimensionPixelSize(R.dimen.icon_size);
        starNor = Bitmap.createScaledBitmap(
                ((BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.star_big_nor)).getBitmap(),
                starSize, starSize, true);
        starSel = Bitmap.createScaledBitmap(
                ((BitmapDrawable) ContextCompat.getDrawable(this, R.drawable.star_big_sel)).getBitmap(),
                starSize, starSize, true);
        ratingView = (RatingView) findViewById(R.id.rating_view);
        ratingView.setRatingSize(starSize, ratingView.getLayoutParams(), starSel, starNor);
        ratingView.drawStar(currentStar + 1);
        ratingView.setOnRatingChangeListener(this);
    }

    public void submit(View view){
        // 提交评论
        String comment = commentView.getText().toString();
        if(TextUtils.isEmpty(comment)){
            showToast("请输入评论内容");
            return;
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
        HttpUtils.getInstance().postEnqueue(this, callbackInterface, "base/saveCommentary", "doctor_id", intent.getStringExtra("doctor_id"),
                "patient_id", MyApplication.getInstance().user.user_id, "content", comment, "bespeak_id", intent.getStringExtra("bespeak_id"),
                "star", String.valueOf(currentStar));
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
    public void change(int count) {
        // 星星数改变
        currentStar = count;
    }
}
