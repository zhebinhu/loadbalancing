package com.huzb.loadbalancing;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * @author huzb
 * @version v1.0.0
 * @date 2018/4/22
 */
public class Cluster {
    private HashMap<String, LinkedList> nodes = new HashMap<String, LinkedList>();
    private volatile static Cluster cluster;

    private Cluster() {
    }

    public static Cluster getCluster() {
        if (cluster == null) {
            synchronized (Cluster.class) {
                if (cluster == null) {
                    cluster = new Cluster();
                    cluster.add("1");
                    cluster.add("2");
                    cluster.add("3");
                    cluster.add("4");
                    cluster.add("5");
                }
            }
        }
        return cluster;
    }

    public void add(String nodeNum) {
        nodes.put(nodeNum, new LinkedList());
    }

    public LinkedList get(String nodeNum) {
        return nodes.get(nodeNum);
    }
}
