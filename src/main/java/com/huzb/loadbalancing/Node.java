package com.huzb.loadbalancing;

import java.util.LinkedList;

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
    private LinkedList<String> dataQueue;
    private static final Integer MAX_NUM_OF_DATA = 128;
    private Integer load;

    Node(String ipAddress, Integer port) {
        this.ipAddress = ipAddress;
        this.port = port;
        this.dataQueue = new LinkedList<String>();
        this.load = 0;
    }

    @Override
    public int hashCode() {
        return HashFunction.hash(ipAddress + ":" + port);
    }

    @Override
    public boolean equals(Object obj) {
        return this.hashCode() == obj.hashCode();
    }

    Boolean isDataExist(String data) {
        return dataQueue.contains(data);
    }

    public void addData(String data) {
        dataQueue.add(data);
        if (dataQueue.size() > MAX_NUM_OF_DATA) {
            dataQueue.pop();
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


