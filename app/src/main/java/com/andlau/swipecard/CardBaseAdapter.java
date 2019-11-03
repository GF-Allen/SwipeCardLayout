package com.andlau.swipecard;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.annotation.IdRes;

import java.util.List;

public abstract class CardBaseAdapter<E, V extends CardBaseAdapter.BaseViewHolder> extends BaseAdapter {
    private static final String TAG = "CardBaseAdapter";
    private Context context;
    private List<E> dataSource;

    public CardBaseAdapter(Context context, List<E> dataSource) {
        this.context = context;
        this.dataSource = dataSource;
    }

    @Override
    public int getCount() {
        return dataSource.size();
    }

    @Override
    public E getItem(int i) {
        return dataSource.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        V viewHolder = null;

        if (convertView == null) {
            viewHolder = createHolder(i, viewGroup);
            if (viewHolder.getItemView() == null) {
                throw new NullPointerException("createViewHolder不能返回null或view为null的实例");
            }
            convertView = viewHolder.getItemView();
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (V) convertView.getTag();
        }
        viewHolder.setPosition(i);
        bindViewHoder(viewHolder, i, getItem(i));
        return viewHolder.getItemView();
    }

    protected abstract V createHolder(int position, ViewGroup parent);

    protected abstract void bindViewHoder(V holder, int position, E data);

    public static class BaseViewHolder {
        private View itemView;
        private SparseArray<View> cacheViewList = new SparseArray<>();
        private int position;

        public View getItemView() {
            return this.itemView;

        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public BaseViewHolder(View itemView) {
            this.itemView = itemView;

        }

        public <R> R getView(@IdRes int viewId) {
            View viewCache = cacheViewList.get(viewId);
            if (viewCache == null) {
                viewCache = itemView.findViewById(viewId);
                cacheViewList.put(viewId, viewCache);
            }
            return (R) viewCache;
        }
    }
}