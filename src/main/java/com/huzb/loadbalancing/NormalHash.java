package com.huzb.loadbalancing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * @author huzb
 * @version v1.0.0
 * @date 2018/5/19
 */
public class NormalHash<T> {
    ArrayList<T> nodes;
    /**
     * 单例模式
     */
    private volatile static NormalHash<Node> normalHash;

    private NormalHash() {
    }

    /**
     * 获得哈希环实例
     *
     * @return
     */
    static NormalHash<Node> getNormalHash() {
        if (normalHash == null) {
            synchronized (NormalHash.class) {
                if (normalHash == null) {
                    normalHash = new NormalHash<>();
                    normalHash.nodes = new ArrayList<>();
                }
            }
        }
        return normalHash;
    }

    public void add(T node) {
        synchronized (NormalHash.class) {
            if (!nodes.contains(node)) {
                nodes.add(node);
            }
        }
    }

    public void remove(T node) {
        synchronized (NormalHash.class) {
            if (nodes.contains(node)) {
                nodes.remove(node);
            }
        }
    }

    public T get(String key) {
        return nodes.get(HashFunction.hash(key) % nodes.size());
    }


}
