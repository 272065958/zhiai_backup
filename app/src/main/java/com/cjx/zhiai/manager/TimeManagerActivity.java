package com.cjx.zhiai.manager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cjx.zhiai.MyApplication;
import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseActivity;
import com.cjx.zhiai.base.BaseClassAdapter;
import com.cjx.zhiai.base.BaseListActivity;
import com.cjx.zhiai.base.MyBaseAdapter;
import com.cjx.zhiai.bean.ReserveTimeBean;
import com.cjx.zhiai.bean.ResultBean;
import com.cjx.zhiai.bean.TimeBean;
import com.cjx.zhiai.dialog.TipDialog;
import com.cjx.zhiai.http.HttpUtils;
import com.cjx.zhiai.http.MyCallbackInterface;
import com.cjx.zhiai.util.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by cjx on 2017-01-03.
 */
public class TimeManagerActivity extends BaseListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        setToolBar(true, null, R.string.time_manager_title);
        initListView(false, false);
        setListViweDivider(null, 0);
        loadData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            onRefresh();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.time_add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, TimeAddActivity.class);
        startActivityForResult(intent, 1);
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void loadData() {
        MyCallbackInterface callbackInterface = new MyCallbackInterface() {
            @Override
            public void success(ResultBean response) {
                ArrayList<ReserveTimeBean> list = JsonParser.getInstance().fromJson(response.datas,
                        new TypeToken<ArrayList<ReserveTimeBean>>(){}.getType());
                if(list != null && list.size() > 0){
                    for(ReserveTimeBean rtb : list){
                        rtb.format();
                    }
                }
                onLoadResult(list);
            }

            @Override
            public void error() {
                hideLoadView();
            }
        };
        HttpUtils.getInstance().postEnqueue(this, callbackInterface, "base/queryDoctorTime",
                "doctor_id", MyApplication.getInstance().user.user_id);
    }

    @Override
    protected MyBaseAdapter getAdapter(ArrayList<?> list) {
        return new AdvisoryAdapter(list, this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    class AdvisoryAdapter extends BaseClassAdapter {
        public AdvisoryAdapter(ArrayList<?> list, BaseActivity context) {
            super(list, context, 1);
        }

        @Override
        protected ParentViewHolder bindViewHolder(View view) {
            return new ViewHolder(view);
        }

        @Override
        protected void bindData(int position, ParentViewHolder holder) {
            ReserveTimeBean tb = (ReserveTimeBean) getItem(position);
            ViewHolder ho = (ViewHolder) holder;
            ho.timeView.setText(tb.date);
        }

        @Override
        protected ArrayList<?> getItemList(int position) {
            return ((ReserveTimeBean) getItem(position)).times;
        }

        @Override
        protected View createItemView(Context context) {
            return View.inflate(context, R.layout.item_time_manager_item, null);
        }

        @Override
        protected void bindItemData(int position, Object obj, ItemViewHolder holder) {
            TimeBean t = (TimeBean) obj;
            MyItemViewHolder ho = (MyItemViewHolder) holder;
            ho.timeView.setText(t.time);
            ho.deleteView.setTag(t.id);
        }

        @Override
        protected ItemViewHolder bindItemViewHolder(View v) {
            return new MyItemViewHolder(v);
        }

        @Override
        protected View createView(Context context) {
            return View.inflate(context, R.layout.item_time_manager, null);
        }

        class ViewHolder extends ParentViewHolder {
            TextView timeView;
            public ViewHolder(View view) {
                super(view);
                timeView = (TextView) view.findViewById(R.id.item_title);
            }

            @Override
            protected LinearLayout getContentView(View view) {
                return (LinearLayout) view.findViewById(R.id.item_content);
            }
        }

        class MyItemViewHolder extends ItemViewHolder implements View.OnClickListener {
            View deleteView;
            TextView timeView;
            public MyItemViewHolder(View v) {
                super(v);
                timeView = (TextView) v.findViewById(R.id.time_view);
                deleteView = v.findViewById(R.id.delete_time);
                deleteView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                showDeleteDialog(v);
            }
        }
    }

    TipDialog delDialog;
    private void showDeleteDialog(View v){
        if (delDialog == null) {
            delDialog = new TipDialog(this);
            delDialog.setText("提示", "是否删除当前时间段", getString(R.string.button_sure),
                    getString(R.string.button_cancel)).setTipComfirmListener(new TipDialog.ComfirmListener() {
                @Override
                public void comfirm() {
                    delDialog.dismiss();
                    View view = (View) delDialog.getTag();
                    deleteTime((String)view.getTag());
                }

                @Override
                public void cancel() {
                    delDialog.dismiss();
                }
            });
        }
        delDialog.setTag(v);
        delDialog.show();
    }

    private void deleteTime(String id){
        showLoadDislog();
        MyCallbackInterface callbackInterface = new MyCallbackInterface() {
            @Override
            public void success(ResultBean response) {
                dismissLoadDialog();
                showToast(response.errorMsg);
                onRefresh();
            }

            @Override
            public void error() {
                dismissLoadDialog();
            }
        };
        HttpUtils.getInstance().postEnqueue(this, callbackInterface, "base/deleteBespeakTime", "id", id);
    }
}
