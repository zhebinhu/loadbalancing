package com.huzb.loadbalancing;

/**
 * @author huzb
 * @version v1.0.0
 * @date 2018/4/21
 */

import java.util.*;

public class ConsistentHash<T> {
    /**
     * 节点的复制因子,实际节点个数 * numberOfReplicas =
     */
    private final int numberOfReplicas = 32;
    /**
     * 哈希环总长度
     */
    private final int consistentHashLength = 1024;
    /**
     * 存储虚拟节点的hash值到真实节点的映射
     */
    private final SortedMap<Integer, T> circle = new TreeMap<>();

    /**
     * 单例模式
     */
    private volatile static ConsistentHash consistentHash;

    private ConsistentHash() {
    }

    /**
     * 获得哈希环实例
     *
     * @return
     */
    static ConsistentHash getConsistentHash() {
        if (consistentHash == null) {
            synchronized (ConsistentHash.class) {
                if (consistentHash == null) {
                    consistentHash = new ConsistentHash();
                }
            }
        }
        return consistentHash;
    }

    /**
     * 在哈希环中添加节点
     *
     * @param node
     */
    public void add(T node) {
        for (int i = 0; i < numberOfReplicas; i++) {
            /*
             * 对于一个实际机器节点 T, 对应 numberOfReplicas 个虚拟节点
             * 不同的虚拟节点(i不同)有不同的hash值,但都对应同一个实际机器node
             * 虚拟node一般是均衡分布在环上的,数据存储在顺时针方向的虚拟node上
             */
            Integer hashCode = HashFunction.hash(Integer.toString(node.hashCode()) + i) % consistentHashLength;
            while (circle.containsKey(hashCode)) {
                hashCode = (hashCode + 137) % consistentHashLength;
            }
            circle.put(hashCode, node);
        }
    }

    /**
     * 移除哈希环中节点
     *
     * @param node
     */
    public void remove(T node) {
        for (int i = 0; i < numberOfReplicas; i++) {
            circle.remove(HashFunction.hash(Integer.toString(node.hashCode()) + i) % consistentHashLength);
        }
    }


    /**
     * 获得一个最近的顺时针节点,根据给定的key 取Hash
     * 然后再取得顺时针方向上最近的一个虚拟节点对应的实际节点
     * 再从实际节点中取得 数据
     *
     * @param key
     * @return
     */
    public T get(String key) {
        if (circle.isEmpty()) {
            return null;
        }
        /*
         * T 用String来表示,获得node在哈希环中的hashCode
         */
        Integer hash = HashFunction.hash(key) % consistentHashLength;
        /*
         * 数据映射在两台虚拟机器所在环之间,就需要按顺时针方向寻找机器
         */
        if (!circle.containsKey(hash)) {
            SortedMap<Integer, T> tailMap = circle.tailMap(hash);
            hash = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
        }
        return circle.get(hash);
    }

    /**
     * 获取哈希环中虚拟节点规模
     *
     * @return
     */
    public Integer getSize() {
        return circle.size();
    }
}