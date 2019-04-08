package com.link.feeling.framework.component.adapterdelegate.adapter;

import android.support.annotation.NonNull;

import com.link.feeling.framework.component.adapterdelegate.AdapterDelegate;
import com.link.feeling.framework.component.adapterdelegate.AdapterDelegatesManager;
import com.link.feeling.framework.component.adapterdelegate.display.BaseDisplayList;
import com.link.feeling.framework.component.adapterdelegate.display.SimpleDisplayList;

import java.util.List;

/**
 * Created on 2019/1/8  17:57
 * chenpan pan.chen@linkfeeling.cn
 */
@SuppressWarnings("all")
public final class ListDelegationAdapter <T> extends DelegationAdapter<BaseDisplayList<T>> {

    public ListDelegationAdapter() {
        super();
        items = new SimpleDisplayList<>();
    }
    @SuppressWarnings("unchecked")
    private ListDelegationAdapter(@NonNull AdapterDelegatesManager<BaseDisplayList<T>> delegatesManager) {
        super(delegatesManager);
    }

    @Override
    public int getItemViewType(int position) {
        try {
            return super.getItemViewType(position);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return -1;
    }

    /**
     * 添加{@link AdapterDelegate}
     *
     * @param delegate
     */
    public void addDelegate(AdapterDelegate delegate) {
        delegatesManager.addDelegate(delegate);
    }

    /**
     * 返回所有item的数量，包含{@link #setSource(BaseDisplayList)} 传入的所有元素
     * @return item数量
     */
    @Override
    public int getItemCount() {
        return items.getDisplayCount();
    }

    /**
     * 设置item，包含要显示所有元素的数据
     */
    @Override
    protected final void setSource(BaseDisplayList<T> displayList) {
        this.items = displayList;
        notifyDataSetChanged();
    }

    /**
     * 返回所有元素的数据，不区分类型的
     *
     * @return
     */
    @Override
    protected final BaseDisplayList<T> getSource() {
        return items;
    }

    /**
     * 设置item，包含要显示所有元素的数据
     *
     * @param source The items / data source
     */
    public void appendItems(List<? extends T> source) {
        if (source != null) {
            items.addAll(source);
        }
        items.display();
        notifyDataSetChanged();
    }

    /**
     * 设置item，包含要显示所有元素的数据
     *
     * @param source The items / data source
     */
    public void setItems(List<? extends T> source) {
        setItems(source, true);
    }

    public void setItems(List<? extends T> source, boolean notifyItems) {
        items.clear();
        if (source != null) {
            items.addAll(source);
        }
        items.display();
        if (notifyItems) {
            notifyDataSetChanged();
        }
    }

    public List<T> getItems() {
        return items;
    }

    public final void remove(int position) {
        items.remove(position);
        items.display();
        notifyItemRemoved(position);
    }

    /**
     * 列表数据移除
     * @param object
     */
    public final void removeElement(T object) {
        int index = items.displayPosition(object);
        if (index != -1) {
            items.remove(items.getByDisplayPosition(index));
            items.display();
            notifyItemRemoved(index);
        }
    }

    /**
     * 列表数据更新
     * @param object
     */
    public final void updateElement(T object) {
        int index = items.update(object);
        if (index != -1) {
//            notifyItemChanged(index);
            notifyDataSetChanged();
        }
    }

    /**
     * 插入一个数据
     * @param object
     */
    public final void insertElement(T object) {
        if (!items.contains(object)) {
            items.add(0, object);
            notifyItemChanged(items.displayPosition(object));
        }
    }

    /**
     * 清除Header
     */
    public final void clearHeader() {
        List headers = items.getHeaders();
        if (headers != null && !headers.isEmpty()) {
            items.removeAll(headers);
        }
        items.display();
    }

    /**
     * 获取HeaderCount
     * @return
     */
    public final int getHeaderSize() {
        return items.getHeaderCount();
    }

    /**
     * 插入header
     * @param header
     */
    public final void addHeader(T header) {
        items.insertHeader(header);
        items.display();
    }

    /**
     * 移除Header
     * @param header
     */
    public final void removeHeader(T header) {
        items.removeHeader(header);
    }

    /**
     * 插入header
     * @param footer
     */
    public final void addFooter(T footer) {
        items.insertFooter(footer);
    }

    /**
     * 移除Header
     * @param footer
     */
    public final void removeFooter(T footer) {
        items.removeFooter(footer);
    }

    /**
     * 获取FooterCount
     * @return
     */
    public final int getFooterSize() {
        return items.getFooterCount();
    }

    public final void clearFooter() {
        List footers = items.getHeaders();
        if (footers != null && !footers.isEmpty()) {
            items.removeAll(footers);
        }
        items.display();
    }
}
