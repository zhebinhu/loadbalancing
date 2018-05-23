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
//    private volatile static Cluster cluster;
    private ArrayList<Node> nodes = new ArrayList<>();
    private final static Integer NUM_OF_NODE = 5;
    private ArrayList<Integer> loads = new ArrayList<>();

    public Cluster(Integer port) {
        for (int i = 0; i < NUM_OF_NODE; i++) {
            nodes.add(new Node("127.0.0.1" + i, port));
            loads.add(0);
        }
    }

    public void updateloads(Integer load, Node node) {
        loads.set(getSerialNumber(node), load);
    }

    public boolean isMaxNode(Node node) {
        Integer load = node.getLoad();
        for (Integer i : loads) {
            if (i > load) {
                return false;
            }
        }
        return true;
    }

    public Node getMinNode() {
        int index = -1;
        int min = Integer.MAX_VALUE;
        for (int i = 0; i < NUM_OF_NODE; ++i) {
            if(loads.get(i) < 0 ){
                continue;
            }
            if (nodes.get(i).getLoad() < min) {
                index = i;
                min = nodes.get(i).getLoad();
            }
        }
        if (index != -1) {
            return nodes.get(index);
        }
        else {
            return null;
        }
    }

    public void removeNode(Node node) {
        int index = getSerialNumber(node);
        loads.set(index, -1);
    }

    public void addNode(Node node) {
        int index = getSerialNumber(node);
        loads.set(index, 0);
    }

//    static Cluster getCluster() {
//        if (cluster == null) {
//            synchronized (Cluster.class) {
//                if (cluster == null) {
//                    cluster = new Cluster();
//                    cluster.nodes = new ArrayList<>();
//                    for (int i = 0; i < NUM_OF_NODE; i++) {
//                        cluster.nodes.add(new Node("127.0.0.1"+i,80));
//                    }
//                }
//            }
//        }
//        return cluster;
//    }

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
