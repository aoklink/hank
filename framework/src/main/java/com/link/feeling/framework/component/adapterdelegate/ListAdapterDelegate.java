package com.link.feeling.framework.component.adapterdelegate;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.link.feeling.framework.component.adapterdelegate.display.BaseDisplayList;
import com.link.feeling.framework.utils.ui.ViewUtils;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 2019/1/8  15:47
 * chenpan pan.chen@linkfeeling.cn
 *
 *列表用的AdapterDelegate，默认使用泛型类型来判断是否需要自己来处理
 */
@SuppressWarnings("unused")
public abstract class ListAdapterDelegate <T> extends AdapterDelegate<BaseDisplayList> {

    private ItemClickListener<T> mItemClickListener;

    public void setOnItemClickListener(ItemClickListener<T> listener) {
        mItemClickListener = listener;
    }

    public ItemClickListener<T> getOnItemClickListener() {
        return mItemClickListener;
    }

    private List<DelegateViewBindFilter<T>> delegateViewBindFilters;

    protected abstract boolean isForviewType(Object item, int position);

    /**
     * 在每次调用onBindViewHolder之后，会将ViewHolder代理到UI中，使UI能做一些个性化的操作
     */
    public void addDelegateViewBindFilter(DelegateViewBindFilter<T> filter) {
        if (delegateViewBindFilters == null) {
            delegateViewBindFilters = new ArrayList<>();
        }
        if (!delegateViewBindFilters.contains(filter)) {
            delegateViewBindFilters.add(filter);
        }
    }

    public void removeDelegateViewBindFilter(DelegateViewBindFilter<T> filter) {
        if (delegateViewBindFilters != null) {
            delegateViewBindFilters.remove(filter);
        }
    }

    protected int forItemViewType(Object item , int position) {
        return -1;
    }

    @Override
    protected int getItemViewType(@NonNull BaseDisplayList items, int position) {
        return forItemViewType(items.getByDisplayPosition(position), position);
    }

    @Override
    protected final boolean isForViewType(@NonNull BaseDisplayList items, int position) {
        return isForviewType(items.getByDisplayPosition(position), position);
    }

    protected RecyclerView.ViewHolder forContentHolder(@NonNull RecyclerView.ViewHolder holder) {
        return holder;
    }

    @Override
    protected final void onBindViewHolder(@NonNull BaseDisplayList items, final int position, @NonNull RecyclerView.ViewHolder holder, @NonNull List<Object> payloads) {
        final T value = (T) items.getByDisplayPosition(position);
        dispatchOnBindItemViewHolder(value, position, holder, payloads);
    }

    public void dispatchOnBindItemViewHolder(@NonNull final T value, final int position, @NonNull RecyclerView.ViewHolder holder, @NonNull List<Object> payloads) {
        if (mItemClickListener != null) {
            holder.itemView.setOnClickListener(v -> {
                if (ViewUtils.isQuickClick()) return;

                if (mItemClickListener != null) {
                    mItemClickListener.onItemClicked(v, value, position);
                }
            });
        } else {
            boolean clickable = holder.itemView.isClickable();
            holder.itemView.setOnClickListener(null);
            holder.itemView.setClickable(clickable);
        }
        onBindItemViewHolder(value, position, holder, payloads);

        if (delegateViewBindFilters != null && delegateViewBindFilters.size() > 0) {
            for (DelegateViewBindFilter<T> delegateViewBindFilter : delegateViewBindFilters) {
                delegateViewBindFilter.onBindViewHolder(value, position, forContentHolder(holder));
            }
        }
    }

    protected abstract void onBindItemViewHolder(@NonNull T item, int position, @NonNull RecyclerView.ViewHolder holder, @NonNull List<Object> payloads);

    /**
     * 获取泛型的Class
     * @return
     */
    protected Class getTypeClass() {
        return (Class) ((ParameterizedType) (getClass().getGenericSuperclass())).getActualTypeArguments()[0];
    }
}
