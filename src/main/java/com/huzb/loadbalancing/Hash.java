package com.huzb.loadbalancing;

/**
 * @author huzb
 * @version v1.0.0
 * @date 2018/6/2
 */
public interface Hash<T> {
    /**
     * 哈希环添加节点
     * @param node
     */
    void add(T node);

    /**
     * 根据键值返回相应节点
     * @param key
     * @return
     */
    T get(String key);

    /**
     * 在哈希环中移除节点
     * @param node
     */
    void remove(T node);
}
