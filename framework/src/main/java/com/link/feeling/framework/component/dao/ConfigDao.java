package com.link.feeling.framework.component.dao;

import com.link.feeling.framework.utils.data.StringUtils;
import com.link.feeling.framework.widgets.NumParseUtil;
import io.objectbox.annotation.Entity;
import io.objectbox.annotation.Id;
import io.objectbox.annotation.Index;
import io.objectbox.annotation.NameInDb;

/**
 * 用来存储KEY-VALUE，与用户相关的数据都存数据库，与软件相关的设置才入SP
 * <p>
 * Created on 2019/3/5  14:45
 * chenpan pan.chen@linkfeeling.cn
 */
@SuppressWarnings("unused")
@Entity
@NameInDb("key_value_table")
public final class ConfigDao {

    // item唯一标识，由ObjectBox维护，不需要指定
    @Id
    private long id;
    // 登录用户唯一标识
    @Index
    private int uid;
    private String key;
    private String value;

    public ConfigDao() {
    }

    /**
     * 仅仅限于基本数据类型：int long double boolean String
     *
     * @param key
     * @param value
     */
    public ConfigDao(String key, Object value) {
        this.key = key;
        this.value = value == null ? "" : String.valueOf(value);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return StringUtils.isEmpty(value) ? "" : value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean getBoolean() {
        return "true".equals(value);
    }

    public int getInt() {
        return NumParseUtil.parseInt(value);
    }

    public long getLong() {
        return NumParseUtil.parseLong(value);
    }

    public double getDouble() {
        return NumParseUtil.parseDouble(value);
    }

    public boolean isEmpty() {
        return StringUtils.isEmpty(value);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ConfigDao && ((ConfigDao) obj).getKey() != null && key != null && key.equals(((ConfigDao) obj).getKey());
    }
}
