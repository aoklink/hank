package com.link.feeling.framework.component.adapterdelegate.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.link.feeling.framework.component.adapterdelegate.AdapterDelegate;
import com.link.feeling.framework.utils.data.L;

import java.util.List;

/**
 * Created on 2019/1/8  17:15
 * chenpan pan.chen@linkfeeling.cn
 */
@SuppressWarnings("unused")
public final class EmptyAdapterDelegate extends AdapterDelegate {

    @Override
    protected boolean isForViewType(@NonNull Object items, int position) {
        return false;
    }

    @NonNull
    @Override
    protected RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View emptyView = new View(parent.getContext());
        emptyView.setLayoutParams(
                new ViewGroup.LayoutParams(0, 0)
        );
        return new ViewHolder(emptyView);
    }

    @Override
    protected void onBindViewHolder(@NonNull Object items, int position, @NonNull RecyclerView.ViewHolder holder, @NonNull List payloads) {
        // 抓取异常数据
        if (items instanceof List) {
            try {
                Object item = ((List) items).get(position);
                L.e(item.toString());
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {


        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
