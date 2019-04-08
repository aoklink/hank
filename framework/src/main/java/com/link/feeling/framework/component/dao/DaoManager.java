package com.link.feeling.framework.component.dao;

import com.link.feeling.framework.base.BaseApplication;
import com.link.feeling.framework.utils.data.L;
import com.link.feeling.framework.utils.data.StringUtils;

import java.util.List;

/**
 * 数据库统一管理
 * <p>
 * Created on 2019/3/5  15:00
 * chenpan pan.chen@linkfeeling.cn
 */
@SuppressWarnings("unused")
public final class DaoManager extends BaseDao<ConfigDao> {

    private final String TAG = this.getClass().getCanonicalName();

    private static class LazyHolder {
        private static final DaoManager INSTANCE = new DaoManager();
    }

    private DaoManager() {
        box = BaseApplication.getBoxStore().boxFor(ConfigDao.class);
    }

    public static DaoManager getInstance() {
        return DaoManager.LazyHolder.INSTANCE;
    }

    /**
     * 返回该用户所有的key-value
     */
    public List<ConfigDao> queryByUserId(int userId) {
        return box.query().equal(ConfigDao_.uid, userId).build().find();
    }

    /**
     * 用户没有登录或者key没有指定，无法入库
     *
     * @param item 数据
     */
    @Override
    public void putOrUpdateItem(final ConfigDao item) {
        if (BaseApplication.sUID != 0 && !StringUtils.isEmpty(item.getKey())) {
            try {
                ConfigDao res = box.query()
                        .equal(ConfigDao_.uid, BaseApplication.sUID)
                        .equal(ConfigDao_.key, item.getKey())
                        .build()
                        .findUnique();
                if (res != null) {
                    item.setId(res.getId());
                }
                item.setUid(BaseApplication.sUID);
            } catch (Exception e) {
                e.printStackTrace();
                L.e(TAG, "查询出错 key = " + item.getKey() + "，错误原因：" + e.getMessage());
            }
            DaoManager.super.putOrUpdateItem(item);
        }
    }

    /**
     * 批量新增/修改
     *
     * @param list 数据
     */
    @Override
    public void putOrUpdateItems(final List<ConfigDao> list) {
        if (BaseApplication.sUID != 0 && list != null && list.size() > 0) {
            try {
                for (ConfigDao item : list) {
                    item.setUid(BaseApplication.sUID);
                    ConfigDao res = box.query()
                            .equal(ConfigDao_.uid, BaseApplication.sUID)
                            .equal(ConfigDao_.key, item.getKey())
                            .build()
                            .findUnique();
                    if (res != null) {
                        item.setId(res.getId());
                    }
                }
                DaoManager.super.putOrUpdateItems(list);
            } catch (Exception e) {
                L.e(TAG, "查询出错 ，错误原因：" + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @param key
     * @return 用户没有登录或value不存在，返回null
     */
    public ConfigDao queryByKey(String key) {
        if (BaseApplication.sUID != 0 && !StringUtils.isEmpty(key)) {
            try {
                ConfigDao res = box.query()
                        .equal(ConfigDao_.key, key)
                        .equal(ConfigDao_.uid, BaseApplication.sUID)
                        .build()
                        .findUnique();
                if (res != null) {
                    return res;
                }
            } catch (Exception e) {
                e.printStackTrace();
                L.e(TAG, "查询出错 key = " + key + "错误原因：" + e.getMessage());
            }
        }
        return new ConfigDao(key, "");
    }

    /**
     * 仅仅限于String
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public ConfigDao queryByKey(String key, String defaultValue) {
        if (BaseApplication.sUID != 0 && !StringUtils.isEmpty(key)) {
            try {
                ConfigDao res = box.query()
                        .equal(ConfigDao_.key, key)
                        .equal(ConfigDao_.uid, BaseApplication.sUID)
                        .build()
                        .findUnique();
                if (res != null) {
                    if (StringUtils.isEmpty(res.getValue())) {
                        res.setValue(defaultValue);
                    }
                    return res;
                }
            } catch (Exception e) {
                e.printStackTrace();
                L.e(TAG, "查询出错 key = " + key + "错误原因：" + e.getMessage());
            }
        }
        return new ConfigDao(key, defaultValue);
    }


    /**
     * 根据用户ID，删除该用户的所有数据
     */
    public void clearAllByUserId() {
        if (BaseApplication.sUID != 0) {
            box.remove(queryByUserId(BaseApplication.sUID));
        }
    }

    /**
     * 清空表
     */
    public void clearAll() {
        box.removeAll();
    }
}
