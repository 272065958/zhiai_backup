package com.cjx.zhiai.base;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewStub;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.cjx.zhiai.MyApplication;
import com.cjx.zhiai.R;

/**
 * Created by cjx on 2016/11/30.
 * 搜索界面
 */
public abstract class BaseSearchActivity extends BaseListActivity {

    ImageView searchBg;
    protected EditText searchView;
    protected View listViewContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        findViewById(R.id.back_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initView();
    }

    private void initView(){
        int width = ((MyApplication)getApplication()).getScreen_width();
        int imageHeight = (int) (width * 420 / 720f);

        searchBg = (ImageView) findViewById(R.id.search_bg);
        RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) searchBg.getLayoutParams();
        rl.height = imageHeight;
        searchView = (EditText) findViewById(R.id.search_view);

    }

    public void search(View v){
        String queryText = searchView.getText().toString();
        if(!TextUtils.isEmpty(queryText)){
            showListView();
            searchValue(queryText);
        }else{
            Toast.makeText(this, "请输入要搜索的内容", Toast.LENGTH_SHORT).show();
        }
    }

    // 初始化搜索界面
    protected void initSearch(int bgRes, String hint, boolean alignBgBottom, TextWatcher listener){
        searchBg.setImageResource(bgRes);
        searchView.setHint(hint);
        if (!alignBgBottom){
            View content = findViewById(R.id.search_view_content);
            RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) content.getLayoutParams();
            int yOff = getResources().getDimensionPixelOffset(R.dimen.button_height) / 2;
            rl.bottomMargin = -yOff;
        }
        if(listener != null){
            searchView.addTextChangedListener(listener);
        }
    }

    // 显示listview
    private void showListView(){
        if(listViewContent == null){
            ViewStub stub = (ViewStub) findViewById(R.id.item_list_view);
            listViewContent = stub.inflate();
            initListView(true, false);
        }else if(listViewContent.getVisibility() == View.GONE){
            listViewContent.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }
    }

    // 隐藏listView
    protected void hideListView(){
        if(listViewContent != null && listViewContent.getVisibility() == View.VISIBLE){
            listViewContent.setVisibility(View.GONE);
        }
    }

    protected abstract void searchValue(String quertText);
}
