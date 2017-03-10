package com.cjx.zhiai.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.cjx.zhiai.R;
import com.cjx.zhiai.dialog.LoadDialog;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by cjx on 2016-11-25.
 */
public class BaseActivity extends AppCompatActivity {
    public final int RESULT_LOGIN = 201;
    TextView toolbarTitle;
    protected Toolbar toolbar;
    public LoadDialog loadDialog;
    BroadcastReceiver refreshReceiver;
    @Override
    public void setContentView(View view) {
        setStatuuBar();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            view.setFitsSystemWindows(true);
        }
        super.setContentView(view);
    }

    @Override
    public void setContentView(int layoutResID) {
        setStatuuBar();
        super.setContentView(layoutResID);
    }

    private void setStatuuBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        }
    }

    /**
     * 设置toolbar
     *
     * @param hasBack      是否包含返回按钮
     * @param backListener 返回按钮监听
     * @param title     标题
     */
    public void setToolBar(boolean hasBack, View.OnClickListener backListener, Object title) {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            if (hasBack) {
                toolbar.setNavigationIcon(R.drawable.back_white_icon);
                setSupportActionBar(toolbar);
                if (backListener == null) {
                    toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    });
                } else {
                    toolbar.setNavigationOnClickListener(backListener);
                }

            } else {
                setSupportActionBar(toolbar);
            }
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
            if (toolbarTitle != null) {
                if(title instanceof String){
                    toolbarTitle.setText((String)title);
                }else{
                    int res = (int) title;
                    if (res > 0) {
                        toolbarTitle.setText(res);
                    }
                }
            }
        }
    }

    /**
     * 设置toolbar的title
     *
     * @param title 标题
     */
    public void setToolbarTitle(String title) {
        if (toolbarTitle != null) {
            toolbarTitle.setText(title);
        }
    }

    /**
     * 设置toolbar的title
     *
     * @param titleRes 标题资源
     */
    public void setToolbarTitle(int titleRes) {
        if (toolbarTitle != null) {
            toolbarTitle.setText(titleRes);
        }
    }

    /**
     * 显示加载对话框
     */
    public void showLoadDislog() {
        if (loadDialog == null) {
            loadDialog = new LoadDialog(this);
        }
        loadDialog.show();
    }

    /**
     * 显示加载对话框
     */
    public void showLoadDislog(DialogInterface.OnCancelListener listener) {
        if (loadDialog == null) {
            loadDialog = new LoadDialog(this);
        }
        loadDialog.setOnCancelListener(listener);
        loadDialog.show();
    }

    // 设置加载匡的文字提示
    public void setLoadTip(String tip) {
        if (loadDialog != null) {
            loadDialog.setTip(tip);
            if(!loadDialog.isShowing()){
                loadDialog.show();
            }
        } else {
            showLoadDislog(tip);
        }
    }

    public void showLoadDislog(String tip) {
        if (loadDialog == null) {
            loadDialog = new LoadDialog(this, tip);
        }
        loadDialog.show();
    }

    /**
     * 隐藏加载对话框
     */
    public void dismissLoadDialog() {
        if (loadDialog != null && loadDialog.isShowing()) {
            loadDialog.dismiss();
        }
    }

    Timer timer;
    InputMethodManager imm;

    // 显示键盘
    public void showInput() {
        if (timer == null) {
            timer = new Timer();
        }
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                if (imm == null) {
                    imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                }
                imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
            }

        }, 200);//这里的时间大概是自己测试的
    }

    /**
     * @return -1=没有网络
     */
    public int checkNetEnvironment() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info != null && info.isConnected()) { // 打开了网络
            return info.getType();
        } else {
            return -1;
        }
    }

    // 注册广播
    protected void registerRefreshReceiver(IntentFilter filter){
        if(refreshReceiver != null){
            return ;
        }
        refreshReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                onBroadcastReceive(intent);
            }
        };
        registerReceiver(refreshReceiver, filter);
    }

    // 收到广播回调
    protected void onBroadcastReceive(Intent intent){

    }

    public void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        if (timer != null) {
            timer.cancel();
            timer = null;
            imm = null;
        }
        if(refreshReceiver != null){
            unregisterReceiver(refreshReceiver);
        }
        super.onDestroy();
    }
}
