package com.huzb.loadbalancing;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 本类模拟服务器节点
 *
 * @author huzb
 * @version v1.0.0
 * @date 2018/5/12
 */
public class Node {
    private String ipAddress;
    private Integer port;
    private ConcurrentLinkedQueue<String> dataQueue;
    private static final Integer MAX_NUM_OF_DATA = 160;
    private static final Integer MAX_NUM_OF_HIT_QUEUE = 100;
    private Integer load;
    private ConcurrentLinkedQueue<Boolean> hitQueue;

    Node(String ipAddress, Integer port) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.dataQueue = new ConcurrentLinkedQueue<String>();
        this.load = 0;
        this.hitQueue = new ConcurrentLinkedQueue<Boolean>();
    }

    Boolean isDataExist(String data) {
        return dataQueue.contains(data);
    }

    void addHitQueue(Boolean hitSuccess) {
        hitQueue.add(hitSuccess);
        if (hitQueue.size() > MAX_NUM_OF_HIT_QUEUE) {
            hitQueue.poll();
        }
    }

    Integer getHitRatio() {
        if (hitQueue.size() < MAX_NUM_OF_HIT_QUEUE) {
            return 0;
        }
        int hitRatio = 0;
        for (Boolean hitSuccess : hitQueue) {
            if (hitSuccess) {
                hitRatio++;
            }
        }
        return hitRatio * 100 / hitQueue.size();
    }

    public void resetHitRatio() {
        this.hitQueue = new ConcurrentLinkedQueue<>();
    }

    void addData(String data) {
        dataQueue.add(data);
        if (dataQueue.size() > MAX_NUM_OF_DATA) {
            dataQueue.poll();
        }
    }

    String showDataQueue() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        for (String s : dataQueue) {
            stringBuilder.append("\"" + s + "\",");
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    public void addLoad() {
        synchronized (this) {
            this.load++;
        }
    }

    public void minusLoad() {
        synchronized (this) {
            this.load--;
        }
    }

    public Integer getLoad() {
        return this.load;
    }


    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

}


