package com.cjx.zhiai.dialog;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.cjx.zhiai.R;
import com.cjx.zhiai.bean.ValueTextBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cjx on 2016/2/24.
 */
public class ListItemSelectDialog extends CustomDialog implements View.OnClickListener {
    OnListItemClickListener listener;
    OldItemSelectAdapter oldadapter;
    ItemSelectAdapter adapter;
    public ListItemSelectDialog(Context context) {
        super(context);
    }

    @Override
    public void onClick(View v) {
        if (listener != null) {
            int position = (int) v.getTag();
            listener.click(position);
        }
    }

    public ListItemSelectDialog setItems(String[] items, OnListItemClickListener listner) {
        if (items == null || items.length == 0) {
            return this;
        }
        this.listener = listner;
        if(oldadapter == null){
            View view = View.inflate(getContext(), R.layout.dialog_list_select, null);

            View transparentView = view.findViewById(R.id.dialog_hide_view);
            transparentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
            ViewGroup.LayoutParams params = transparentView.getLayoutParams();
            params.height = displayMetrics.heightPixels / 2;

            ListView listView = (ListView) view.findViewById(R.id.list_select_view);
            setContentView(view);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ListItemSelectDialog.this.listener.click(position);
                }
            });

            oldadapter = new OldItemSelectAdapter(items);
            listView.setAdapter(adapter);
        } else {
            oldadapter.notifyDataSetChanged(items);
        }

        return this;
    }


    public ListItemSelectDialog setItems(ArrayList<ValueTextBean> items, OnListItemClickListener listner) {
        if (items == null || items.size() == 0) {
            return this;
        }
        this.listener = listner;
        if(adapter == null){
            View view = View.inflate(getContext(), R.layout.dialog_list_select, null);

            View transparentView = view.findViewById(R.id.dialog_hide_view);
            transparentView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
            ViewGroup.LayoutParams params = transparentView.getLayoutParams();
            params.height = displayMetrics.heightPixels / 2;

            ListView listView = (ListView) view.findViewById(R.id.list_select_view);
            setContentView(view);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ListItemSelectDialog.this.listener.click(position);
                }
            });

            adapter = new ItemSelectAdapter(items);
            listView.setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged(items);
        }

        return this;
    }

    class OldItemSelectAdapter extends BaseAdapter{
        String[] items;
        int count;
        Resources res;
        public OldItemSelectAdapter(String[] items){
            this.items = items;
            count = this.items == null ? 0 : this.items.length;
            res = ListItemSelectDialog.this.getContext().getResources();
        }

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public String getItem(int position) {
            return items[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View v, ViewGroup parent) {
            if(v == null){
                v = initItemView(res);
            }
            v.setTag(position);
            ((TextView)v).setText(getItem(position));
            return v;
        }

        /**
         * 显示一个选项的view
         *
         * @param res      当前上下文的资源
         * @return
         */
        private TextView initItemView(Resources res) {
            TextView view = new TextView(getContext());
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    res.getDimensionPixelOffset(R.dimen.tab_height));
            view.setLayoutParams(lp);
            view.setGravity(Gravity.CENTER);
            view.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            view.setTextSize(18);
            view.getPaint().setFakeBoldText(true);
            return view;
        }

        public void notifyDataSetChanged(String[] items){
            this.items = items;
            count = this.items == null ? 0 : this.items.length;
            notifyDataSetChanged();
        }
    }

    class ItemSelectAdapter extends BaseAdapter{
        List<ValueTextBean> items;
        int count;
        Resources res;
        public ItemSelectAdapter(List<ValueTextBean> items){
            this.items = items;
            count = this.items == null ? 0 : this.items.size();
            res = ListItemSelectDialog.this.getContext().getResources();
        }

        @Override
        public int getCount() {
            return count;
        }

        @Override
        public ValueTextBean getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View v, ViewGroup parent) {
            if(v == null){
                v = initItemView(res);
            }
            v.setTag(position);
            ((TextView)v).setText(getItem(position).text);
            return v;
        }

        /**
         * 显示一个选项的view
         *
         * @param res      当前上下文的资源
         * @return
         */
        private TextView initItemView(Resources res) {
            TextView view = new TextView(getContext());
            AbsListView.LayoutParams lp = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    res.getDimensionPixelOffset(R.dimen.tab_height));
            view.setLayoutParams(lp);
            view.setGravity(Gravity.CENTER);
            view.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
            view.setTextSize(18);
            view.getPaint().setFakeBoldText(true);
            return view;
        }

        public void notifyDataSetChanged(List<ValueTextBean> items){
            this.items = items;
            count = this.items == null ? 0 : this.items.size();
            notifyDataSetChanged();
        }
    }

    public interface OnListItemClickListener {
        void click(int position);
    }
}
