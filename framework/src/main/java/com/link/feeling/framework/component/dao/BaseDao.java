package com.link.feeling.framework.component.dao;

import java.util.List;

import io.objectbox.Box;

/**
 * Created on 2019/3/5  14:57
 * chenpan pan.chen@linkfeeling.cn
 */
@SuppressWarnings("unused")
public abstract class BaseDao<T> {
    public Box<T> box;

    /**
     * 查询条目数
     */
    public long queryCount() {
        return box.query().build().count();
    }

    /**
     * 批量新增/修改
     */
    public void putOrUpdateItems(List<T> list) {
        box.put(list);
    }

    /**
     * 新增/修改一行
     */
    public void putOrUpdateItem(T item) {
        box.put(item);
    }

    /**
     * 查询所有数据
     */
    public List<T> findAll() {
        return box.query().build().find();
    }

    /**
     * 清空表数据
     */
    public void clearAll() {
        box.removeAll();
    }
}
