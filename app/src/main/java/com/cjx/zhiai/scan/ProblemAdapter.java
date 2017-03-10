package com.cjx.zhiai.scan;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cjx.zhiai.R;
import com.cjx.zhiai.activity.ImagesActivity;
import com.cjx.zhiai.util.Tools;

/**
 * Created by cjx on 2016-12-04.
 */
public class ProblemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    Context context;
    String[] photos;
    public ProblemAdapter(Context context, String[] photos) {
        this.context = context;
        this.photos = photos;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(context);
        imageView.setOnClickListener(this);
        int size = context.getResources().getDimensionPixelSize(R.dimen.tab_height);
        RecyclerView.LayoutParams rlp = new RecyclerView.LayoutParams(size,
                size);
        rlp.leftMargin = context.getResources().getDimensionPixelOffset(R.dimen.auto_margin);
        if (viewType == getItemCount() - 1) {
            rlp.rightMargin = context.getResources().getDimensionPixelOffset(R.dimen.auto_margin);
        }
        imageView.setLayoutParams(rlp);
        return new ViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ImageView imageView = (ImageView) holder.itemView;
        Tools.setImageInView(context, photos[position], imageView);
        imageView.setTag(R.id.hospital_position, position);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return photos == null ? 0 : photos.length;
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag(R.id.hospital_position);
        Intent imageIntent = new Intent(context, ImagesActivity.class);
        imageIntent.putExtra("photo", photos);
        imageIntent.putExtra("page", position);
        context.startActivity(imageIntent);
    }

    public void notifyDataSetChanged(String[] photos) {
        this.photos = photos;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);

        }
    }
}