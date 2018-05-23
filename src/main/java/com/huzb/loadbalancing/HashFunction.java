package com.huzb.loadbalancing;

/**
 * 实现一致性哈希算法中使用的哈希函数,使用MD5算法来保证一致性哈希的平衡性
 *
 * @author huzb
 * @version v1.0.0
 * @date 2018/4/21
 */

import org.junit.jupiter.api.Test;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.SortedMap;
import java.util.TreeMap;


public class HashFunction {
    private static MessageDigest md5 = null;
    private final static Integer MAX_DATA = 65536 * 8;
    private final static Integer MAX_HASH = 1024 * 8;
    private static Integer[] hashTable = new Integer[MAX_DATA];

    static {
        SecureRandom s = new SecureRandom();
        s.setSeed(13);
        for (int i = 0; i < MAX_DATA; i++) {
            hashTable[i] = s.nextInt(MAX_HASH);
        }
    }

    static Integer hash(String key) {
//        if (md5 == null) {
//            try {
//                md5 = MessageDigest.getInstance("MD5");
//            } catch (NoSuchAlgorithmException e) {
//                throw new IllegalStateException("no md5 algrithm found");
//            }
//        }
//        md5.reset();
//        md5.update(key.getBytes());
//        byte[] bKey = md5.digest();
//
//        //具体的哈希函数实现细节--每个字节 & 0xFF 再移位
//        return ((bKey[3] & 0x7F) << 24)
//                | ((bKey[2] & 0xFF) << 16
//                | ((bKey[1] & 0xFF) << 8) | bKey[0] & 0xFF);
        return hashTable[Math.abs(key.hashCode()) % MAX_DATA];

    }

    @Test
    public void Test() {
        Cluster cluster = new Cluster(80);
        SortedMap circle = new TreeMap();
        for (int j = 0; j < 5; j++) {
            for (int i = 0; i < 5; i++) {
                /*
                 * 对于一个实际机器节点 T, 对应 numberOfReplicas 个虚拟节点
                 * 不同的虚拟节点(i不同)有不同的hash值,但都对应同一个实际机器node
                 * 虚拟node一般是均衡分布在环上的,数据存储在顺时针方向的虚拟node上
                 */
                Integer hashCode = HashFunction.hash(Integer.toString(cluster.getNode(j).hashCode()) + i) % 1024;
                while (circle.containsKey(hashCode)) {
                    hashCode = (hashCode + 137) % 1024;
                }
                circle.put(hashCode, null);
                System.out.print(hashCode + " ");
            }
            System.out.println();
        }
    }
}
