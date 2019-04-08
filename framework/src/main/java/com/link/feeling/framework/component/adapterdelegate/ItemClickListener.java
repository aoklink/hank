package com.link.feeling.framework.component.adapterdelegate;

import android.view.View;

/**
 * Created on 2019/1/8  15:44
 * chenpan pan.chen@linkfeeling.cn
 *
 * Item点击事件.
 */
@SuppressWarnings("unused")
public interface ItemClickListener<T> {
    void onItemClicked(View view, T value, int position);
}
