package com.cjx.zhiai.manager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.cjx.zhiai.MyApplication;
import com.cjx.zhiai.R;
import com.cjx.zhiai.base.BaseActivity;
import com.cjx.zhiai.base.BaseListActivity;
import com.cjx.zhiai.base.MyBaseAdapter;
import com.cjx.zhiai.bean.PatientBean;
import com.cjx.zhiai.http.HttpUtils;
import com.cjx.zhiai.util.Tools;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

/**
 * Created by cjx on 2016-12-31.
 * 患者管理
 */
public class PatientActivity extends BaseListActivity {
    EditText searchView;
    PatientAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);
        setToolBar(true, null, R.string.main_manager);

        initListView(false, false);
        setListViweDivider(null, getResources().getDimensionPixelOffset(R.dimen.auto_margin));

        loadData();
    }

    @Override
    protected void loadData() {
        HttpUtils.getInstance().postEnqueue(this, getMycallback(new TypeToken<ArrayList<PatientBean>>() {
                }.getType()), "base/getHistory", "user_id", MyApplication.getInstance().user.user_id, "query_type", "4",
                "page", "1", "limit", "100");
    }

    @Override
    protected MyBaseAdapter getAdapter(ArrayList<?> list) {
        adapter = new PatientAdapter(list, this);
        return adapter;
    }

    // 显示列表数据
    @Override
    protected void displayData(ArrayList<?> list) {
        super.displayData(list);
        if(searchView == null){
            searchView = (EditText) findViewById(R.id.search_view);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    public void search(View view){
        if(adapter == null || searchView == null){
            return ;
        }
        String text = searchView.getText().toString();
        if(TextUtils.isEmpty(text)){
            adapter.clearTextFilter();
        }else{
            adapter.getFilter().filter(text);
        }
    }

    class PatientAdapter extends MyBaseAdapter implements Filterable {
        ArrayList<?> autoList;
        Filter filter;
        public PatientAdapter(ArrayList<?> list, BaseActivity context) {
            super(list, context);
            autoList = list;
        }

        @Override
        protected View createView(Context context) {
            return View.inflate(context, R.layout.item_patient, null);
        }

        @Override
        protected MyViewHolder bindViewHolder(View view) {
            return new ViewHolder(view);
        }

        @Override
        protected void bindData(int position, MyViewHolder holder) {
            PatientBean pb = (PatientBean) getItem(position);
            ViewHolder ho = (ViewHolder) holder;
            ho.recordView.setTag(R.id.patient_content, pb);
            Tools.setImageInView(context, pb.head_image, ho.headView);
            ho.nameView.setText(pb.user_name);
            ho.sexView.setImageResource(pb.sex.equals("f") ? R.drawable.woman : R.drawable.man);
            ho.timeView.setText(String.format(getString(R.string.advisort_time_format), pb.bespeak_time));
            Tools.setPatientState(ho.stateView, pb.state);
        }

        @Override
        public Filter getFilter() {
            if(filter == null){
                filter = new Filter() {
                    @Override
                    protected FilterResults performFiltering(CharSequence constraint) {
                        if(autoList == null){
                            return null;
                        }
                        final FilterResults result = new FilterResults();
                        result.values = listFiltering(autoList, constraint);
                        return result;
                    }

                    @Override
                    protected void publishResults(CharSequence constraint, FilterResults results) {
                        notifyDataSetChanged((ArrayList<PatientBean>)results.values);
                    }
                };
            }
            return filter;
        }

        protected ArrayList<?> listFiltering(ArrayList<?> autoList, CharSequence constraint) {
            final ArrayList<PatientBean> filterList = new ArrayList<>();
            for(Object obj : autoList){
                PatientBean ob = (PatientBean) obj;
                if(ob.user_name.contains(constraint)){ // 返回内容或者地址包含输入内容的数据
                    filterList.add(ob);
                }
            }
            return filterList;
        }

        // 清除筛选条件
        public void clearTextFilter() {
            notifyDataSetChanged(autoList);
        }

        class ViewHolder extends MyViewHolder implements View.OnClickListener {
            View recordView;
            ImageView headView, sexView;
            TextView nameView, timeView, stateView;
            public ViewHolder(View view) {
                super(view);
                headView = (ImageView) view.findViewById(R.id.discover_head);
                sexView = (ImageView) view.findViewById(R.id.discover_sex);
                nameView = (TextView) view.findViewById(R.id.discover_name);
                timeView = (TextView) view.findViewById(R.id.patient_content);
                recordView = view.findViewById(R.id.patient_record);
                recordView.setOnClickListener(this);
                stateView = (TextView) view.findViewById(R.id.patient_state);
            }

            @Override
            public void onClick(View v) {
                PatientBean pb = (PatientBean) v.getTag(R.id.patient_content);
                Intent intent = new Intent(context, PatientRecordActivity.class);
                intent.putExtra("patient", pb);
                startActivity(intent);
            }
        }
    }
}
