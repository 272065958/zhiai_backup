package com.cjx.zhiai.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.cjx.zhiai.MyApplication;
import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseActivity;
import com.cjx.zhiai.base.MyBaseAdapter;
import com.cjx.zhiai.bean.DepartmentBean;
import com.cjx.zhiai.bean.ResultBean;
import com.cjx.zhiai.http.HttpUtils;
import com.cjx.zhiai.http.MyCallbackInterface;
import com.cjx.zhiai.util.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by cjx on 2016-12-20.
 * 选择科室
 */
public class DepartmentSelectActivity extends BaseActivity {

    ListView rightListView;
    View listViewContent;
    View loadView;
    DepartmentAdapter departmentAdapter;
    DepartmentDetailAdapter departmentDetailAdapter;
    ArrayList<DepartmentBean> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_department_select);
        setToolBar(true, null, R.string.register_department_select);
        loadView = findViewById(R.id.loading_view);
        listViewContent = findViewById(R.id.list_view_content);
        list = MyApplication.getInstance().getDepartmentList();
        if(list == null || list.isEmpty()){
            loadData();
        }else{
            displatData();
        }
    }

    protected void loadData() {
        MyCallbackInterface myCallbackInterface = new MyCallbackInterface() {
            @Override
            public void success(ResultBean response) {
                Type type = new TypeToken<ArrayList<DepartmentBean>>() {
                }.getType();
                list = JsonParser.getInstance().fromJson(response.datas, type);
                MyApplication.getInstance().setDepartmentList(list);
                displatData();
            }

            @Override
            public void error() {
                loadView.setVisibility(View.GONE);
            }
        };
        HttpUtils.getInstance().postEnqueue(this, myCallbackInterface, "base/office");
    }

    private void displatData() {
        loadView.setVisibility(View.GONE);
        listViewContent.setVisibility(View.VISIBLE);
        departmentAdapter = new DepartmentAdapter(list, this);
        ListView leftListView = (ListView) findViewById(R.id.list_view_left);
        leftListView.setAdapter(departmentAdapter);
        leftListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                departmentAdapter.setCurrentSelect(position, view);
                displayChild(((DepartmentBean) departmentAdapter.getItem(position)).officeChList);
            }
        });
        if (list != null && list.size() > 0) {
            displayChild(list.get(0).officeChList);
        }
    }

    private void displayChild(ArrayList<DepartmentBean> childs) {
        if (departmentDetailAdapter == null) {
            departmentDetailAdapter = new DepartmentDetailAdapter(childs, this);
            rightListView = (ListView) findViewById(R.id.list_view_right);
            rightListView.setAdapter(departmentDetailAdapter);
            rightListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    DepartmentBean db = (DepartmentBean) departmentDetailAdapter.getItem(position);
                    Intent data = new Intent();
                    data.putExtra("id", db.office_id);
                    data.putExtra("name", ((DepartmentBean) departmentAdapter.getItem(departmentAdapter.currentSelectPosition)).office_name
                            + "-" + db.office_name);
                    setResult(RESULT_OK, data);
                    finish();
                }
            });
        } else {
            departmentDetailAdapter.notifyDataSetChanged(childs);
            rightListView.setVisibility(View.VISIBLE);
        }
    }

    class DepartmentAdapter extends MyBaseAdapter {
        int currentSelectPosition = 0;
        View currentSelectView;

        public DepartmentAdapter(ArrayList<?> list, BaseActivity context) {
            super(list, context);
        }

        @Override
        protected View createView(Context context) {
            return View.inflate(context, R.layout.item_department_select, null);
        }

        @Override
        protected MyViewHolder bindViewHolder(View view) {
            return new ViewHolder(view);
        }

        @Override
        protected void bindData(int position, MyViewHolder holder) {
            ViewHolder ho = (ViewHolder) holder;
            View v = ho.getView();
            if (position == currentSelectPosition) {
                currentSelectView = v;
                v.setBackgroundColor(Color.WHITE);
                ho.iconView.setVisibility(View.VISIBLE);
                ho.bottomLineView.setVisibility(View.VISIBLE);
                ho.dividerView.setVisibility(View.GONE);
                ho.rightLineView.setVisibility(View.GONE);
            } else {
                v.setBackgroundColor(ContextCompat.getColor(context, R.color.background_divider));
                ho.iconView.setVisibility(View.GONE);
                ho.bottomLineView.setVisibility(View.GONE);
                ho.dividerView.setVisibility(View.VISIBLE);
                ho.rightLineView.setVisibility(View.VISIBLE);
            }
            DepartmentBean db = (DepartmentBean) getItem(position);
            ho.nameView.setText(db.office_name);
        }

        public void setCurrentSelect(int position, View v) {
            if (position != currentSelectPosition) {
                currentSelectPosition = position;
                notifyDataSetChanged();
            }
        }

        class ViewHolder extends MyViewHolder {
            View iconView, bottomLineView, rightLineView, dividerView;
            TextView nameView;

            public ViewHolder(View view) {
                super(view);
                iconView = view.findViewById(R.id.department_select_icon);
                bottomLineView = view.findViewById(R.id.department_bottom_line);
                dividerView = view.findViewById(R.id.department_bottom_divider_line);
                rightLineView = view.findViewById(R.id.department_right_line);
                nameView = (TextView) view.findViewById(R.id.department_name);
            }
        }
    }

    class DepartmentDetailAdapter extends MyBaseAdapter {

        public DepartmentDetailAdapter(ArrayList<?> list, BaseActivity context) {
            super(list, context);
        }

        @Override
        protected View createView(Context context) {
            return View.inflate(context, R.layout.item_department_detail_select, null);
        }

        @Override
        protected MyViewHolder bindViewHolder(View view) {
            return new ViewHolder(view);
        }

        @Override
        protected void bindData(int position, MyViewHolder holder) {
            DepartmentBean db = (DepartmentBean) getItem(position);
            ViewHolder ho = (ViewHolder) holder;
            ho.nameView.setText(db.office_name);
        }

        class ViewHolder extends MyViewHolder {
            TextView nameView;

            public ViewHolder(View view) {
                super(view);
                nameView = (TextView) view.findViewById(R.id.department_name);
            }
        }
    }
}
