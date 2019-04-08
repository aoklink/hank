package com.link.feeling.framework.component.adapterdelegate.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.link.feeling.framework.component.adapterdelegate.AdapterDelegatesManager;

import java.util.List;

/**
 * Created on 2019/1/8  16:51
 * chenpan pan.chen@linkfeeling.cn
 */
@SuppressWarnings("unused")
public abstract class DelegationAdapter <T> extends RecyclerView.Adapter {

    protected AdapterDelegatesManager<T> delegatesManager;
    protected T items;

    public DelegationAdapter() {
        this(new AdapterDelegatesManager<T>());
    }

    protected DelegationAdapter(@NonNull AdapterDelegatesManager<T> delegatesManager) {
        if (delegatesManager == null) {
            throw new NullPointerException("AdapterDelegatesManager is null");
        }
        this.delegatesManager = delegatesManager;
    }

    @NonNull
    @Override public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return delegatesManager.onCreateViewHolder(parent, viewType);
    }

    @Override public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        delegatesManager.onBindViewHolder(items, position, holder, null);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List payloads) {
        delegatesManager.onBindViewHolder(items, position, holder, payloads);
    }

    @Override public int getItemViewType(int position) {
        return delegatesManager.getItemViewType(items, position);
    }

    @Override public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        delegatesManager.onViewRecycled(holder);
    }

    @Override public boolean onFailedToRecycleView(@NonNull RecyclerView.ViewHolder holder) {
        return delegatesManager.onFailedToRecycleView(holder);
    }

    @Override public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        delegatesManager.onViewAttachedToWindow(holder);
    }

    @Override public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        delegatesManager.onViewDetachedFromWindow(holder);
    }

    /**
     * Get the items / data source of this adapter
     * @return The items / data source
     */
    protected T getSource() {
        return items;
    }

    /**
     * Set the items / data source of this adapter
     * @param items The items / data source
     */
    protected void setSource(T items) {
        this.items = items;
    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
