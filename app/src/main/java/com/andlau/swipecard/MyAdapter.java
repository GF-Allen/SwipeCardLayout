package com.andlau.swipecard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MyAdapter extends CardBaseAdapter<SwipeBean, MyAdapter.ViewHolder> {
    private Context context;
    private LayoutInflater inflater;

    public MyAdapter(Context context, List dataSource) {
        super(context, dataSource);
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    protected ViewHolder createHolder(int position, ViewGroup parent) {
        View view = inflater.inflate(R.layout.item_swipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    protected void bindViewHoder(ViewHolder holder, int position, SwipeBean data) {
//        holder.txtTitle.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    static class ViewHolder extends CardBaseAdapter.BaseViewHolder {
        private TextView txtTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            txtTitle = getView(R.id.name);
        }
    }
}