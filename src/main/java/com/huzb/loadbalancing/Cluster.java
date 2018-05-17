package com.huzb.loadbalancing;

import java.util.ArrayList;

/**
 * 用于管理Node
 *
 * @author huzb
 * @version v1.0.0
 * @date 2018/5/13
 */
class Cluster {
    /**
     * 单例模式
     */
    private volatile static Cluster cluster;
    private ArrayList<Node> nodes;
    private final static Integer NUM_OF_NODE = 5;

    private Cluster() {
    }

    static Cluster getCluster() {
        if (cluster == null) {
            synchronized (Cluster.class) {
                if (cluster == null) {
                    cluster = new Cluster();
                    cluster.nodes = new ArrayList<>();
                    for (int i = 0; i < NUM_OF_NODE; i++) {
                        cluster.nodes.add(new Node("127.0.0.1"+i,80));
                    }
                }
            }
        }
        return cluster;
    }

    Node getNode(Integer serialNumber) {
        if (serialNumber < NUM_OF_NODE) {
            return nodes.get(serialNumber);
        } else {
            return null;
        }
    }

    Integer getSerialNumber(Node node) {
        return nodes.indexOf(node);
    }

}
