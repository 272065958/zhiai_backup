package com.cjx.zhiai.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

/**
 * Created by cjx on 2016/10/24.
 */
public class LoadListView extends ListView implements AbsListView.OnScrollListener{

    boolean isLoading = false;
//    OnScrollListener scrollListener;
    FooterLoadListener loadListener;
    private int firstItem = 0, last, total;

    public LoadListView(Context context) {
        super(context);
    }

    public LoadListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setFooterLoadListener(FooterLoadListener loadListener){
        super.setOnScrollListener(this);
        this.loadListener = loadListener;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
//        if(scrollListener != null){
//            scrollListener.onScrollStateChanged(view, scrollState);
//        }
        if ((scrollState == SCROLL_STATE_FLING || scrollState == SCROLL_STATE_IDLE) && last == total && !isLoading) {
            checkLoad();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//        if(scrollListener != null){
//            scrollListener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
//        }
        if (totalItemCount == getHeaderViewsCount() + getFooterViewsCount())
            return;
        if (visibleItemCount == totalItemCount && !isLoading) {
            checkLoad();
        }
        total = totalItemCount;
        last = firstVisibleItem + visibleItemCount;
        firstItem = firstVisibleItem;
    }

//    @Override
//    public void setOnScrollListener(OnScrollListener l) {
//        scrollListener = l;
//    }

    private void footerLoad(){
        isLoading = true;
        loadListener.loadMore();
    }

    private void checkLoad(){
        int childCount = getChildCount();
        if(childCount > 0){
            View v = getChildAt(childCount - 1);
            if (v.getBottom() <= getHeight() + 3) {
                footerLoad();
            }
        }
    }

    public void setFooterLoadState(boolean state){
        isLoading = state;
    }

    public interface FooterLoadListener{
        void loadMore();
    }
}
