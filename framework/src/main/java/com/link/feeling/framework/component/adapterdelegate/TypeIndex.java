package com.link.feeling.framework.component.adapterdelegate;

import com.link.feeling.framework.utils.data.L;

import java.util.HashMap;

/**
 * Created on 2019/1/8  15:07
 * chenpan pan.chen@linkfeeling.cn
 *
 * 处理AdapterDelegate中的多Index对象
 * 处理多种Type对应一种AdapterDelegate的情况
 * 大于1则判断为自定义Type
 * 大于TYPE_START则判定为自动匹配Type
 * 自动匹配Type根据类型来获取Delegate，自定义Type则严格按照Type类型来获取处理的Delegate
 */
@SuppressWarnings("unused")
public final class TypeIndex extends HashMap<Integer, Integer> {

    public static final int TYPE_NOLL = -1;

    // 最大Type类型不能超过100
    private static final int TYPE_START = (Integer.MAX_VALUE - 100);

    /**
     * 对应的Delegate处理多种类型的Type，Index为当前处理的Delegate在List中的位置
     * @param index
     * @param types
     */
    void putTypes(int index, int[] types) {
        for (int type : types) {
            put(type, index);
        }
    }

    /**
     * 对应Type类型的Index
     * @param inedx
     * @param type
     */
    void putType(int inedx, int type) {
        if (!containsKey(type)) {
            put(type, inedx);
        }
    }

    /**
     * 唯一Type，不处理，直接存储Index
     * @param index
     */
    int putType(int index) {
        int key = generateKey(index);
        put(key, index);
        return key;
    }

    /**
     * @param type （type >= 0 未自定义ViewType， Type > TYPE_START 自定义ViewType）
     * @return type对应的Delegate所在列表中的Index
     */
    int getTypeIndex(int type) {
        L.e("TypeIndex", "=============================>>>>>>>>>>>>>>>>> type " + type);
        Integer intValue = get(type);
        return intValue != null ? intValue : -1;
    }

    /**
     * 仅仅是为了生成一个有效的Key，切小于-1,一般-1未保留字段，避免冲突
     * @param index
     * @return
     */
    private int generateKey(int index) {
        return TYPE_START + index;
    }
}
