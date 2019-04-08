package com.link.feeling.framework.component.adapterdelegate;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

/**
 * Created on 2019/1/8  15:23
 * chenpan pan.chen@linkfeeling.cn
 *
 *将UI刷新的部分操作代理到界面中，使UI能够配置界面
 */
@SuppressWarnings("unused")
public interface DelegateViewBindFilter<T> {
    void onBindViewHolder(T value, int position, @NonNull RecyclerView.ViewHolder holder);
}
