package com.huzb.loadbalancing;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author huzb
 * @version v1.0.0
 * @date 2018/4/21
 */

@RestController
public class SendController {
    /**
     * 集群中服务器数量
     */
    private static Integer clusterNum = 5;
    /**
     * 比较的算法数量
     */
    private static Integer algorithmNum = 3;
    /**
     * 哈希环列表，用于存放所有哈希环
     */
    private static List<Hash<Node>> hashList = new ArrayList<Hash<Node>>();
    /**
     * 一致性哈希环
     */
    private static ConsistentHash<Node> consistentHash = ConsistentHash.getConsistentHash();
    /**
     * 普通哈希区
     */
    private static NormalHash<Node> normalHash = NormalHash.getNormalHash();
    /**
     * 基于最小连接数的一致性哈希环
     */
    private static AdvConsistentHash<Node> advConsistentHash = AdvConsistentHash.getConsistentHash();
    /**
     * 一致性哈希集群
     */
    private static Cluster consistentHashCluster = new Cluster(80);
    /**
     * 普通集群
     */
    private static Cluster normalHashCluster = new Cluster(90);
    /**
     * 基于最小连接数的一致性哈希集群
     */
    private static Cluster advConsistentHashCluster = new Cluster(100);

    static {
        //集群初始化
        for (int i = 0; i < clusterNum; i++) {
            consistentHash.add(consistentHashCluster.getNode(i));
            normalHash.add(normalHashCluster.getNode(i));
            advConsistentHash.add(advConsistentHashCluster.getNode(i));
            hashList.add(consistentHash);
            hashList.add(normalHash);
            hashList.add(advConsistentHash);
        }

    }

    @RequestMapping(value = "/getLoad", method = RequestMethod.GET)
    public Integer[] getLoad() {
        Integer[] load = new Integer[clusterNum * algorithmNum];
        for (int i = 0; i < clusterNum; i++) {
            load[i] = consistentHashCluster.getNode(i).getLoad();
        }
        for (int i = 5; i < 10; i++) {
            load[i] = normalHashCluster.getNode(i - 5).getLoad();
        }
        for (int i = 10; i < 15; i++) {
            load[i] = advConsistentHashCluster.getNode(i - 10).getLoad();
        }
        return load;
    }

    @RequestMapping(value = "/getHitRatio", method = RequestMethod.GET)
    public Integer[] getHitRatio() {
        Integer[] hitRatio = new Integer[15];
        for (int i = 0; i < 5; i++) {
            hitRatio[i] = consistentHashCluster.getNode(i).getHitRatio();
        }
        for (int i = 5; i < 10; i++) {
            hitRatio[i] = normalHashCluster.getNode(i - 5).getHitRatio();
        }
        for (int i = 10; i < 15; i++) {
            hitRatio[i] = advConsistentHashCluster.getNode(i - 10).getHitRatio();
        }
        return hitRatio;
    }

    @RequestMapping(value = "/ConsistentHash/getData", method = RequestMethod.GET)
    public void geConsistentHashData(String data) throws InterruptedException {
        Node node = consistentHash.get(data);
        node.addLoad();
        dataSearch(data, node, consistentHashCluster);
        node.minusLoad();
    }

    @RequestMapping(value = "/NormalHash/getData", method = RequestMethod.GET)
    public void getNormalHashData(String data) throws InterruptedException {
        Node node = normalHash.get(data);
        node.addLoad();
        dataSearch(data, node, normalHashCluster);
        node.minusLoad();
    }

    @RequestMapping(value = "/AdvConsistentHash/getData", method = RequestMethod.GET)
    public void geAdvConsistentHashData(String data) throws InterruptedException {
        Node node = advConsistentHash.get(data);
        if (advConsistentHashCluster.isMaxNode(node)) {
            Node minNode = advConsistentHashCluster.getMinNode();
            if ((node.getLoad() - minNode.getLoad()) * 1.1 > minNode.getLoad()) {
                advConsistentHash.update(data, minNode);
                node = minNode;
            }
        }
        node.addLoad();
        dataSearch(data, node, advConsistentHashCluster);
        node.minusLoad();
    }

    private void dataSearch(String data, Node node, Cluster cluster) throws InterruptedException {
        if (node.isDataExist(data)) {
            node.addHitQueue(true);
        } else {
            node.addHitQueue(false);
            node.addData(data);
            Thread.sleep(500 + 200 * cluster.getSerialNumber(node));
        }
    }

    @RequestMapping(value = "/removeNode", method = RequestMethod.GET)
    public void removeNode(String nodeNum) {
        Node node = consistentHashCluster.getNode(Integer.parseInt(nodeNum));
        node.resetHitRatio();
        consistentHash.remove(node);
        node = normalHashCluster.getNode(Integer.parseInt(nodeNum));
        node.resetHitRatio();
        normalHash.remove(node);
        node = advConsistentHashCluster.getNode(Integer.parseInt(nodeNum));
        node.resetHitRatio();
        advConsistentHash.remove(node);
        advConsistentHashCluster.removeNode(node);
    }

    @RequestMapping(value = "/addNode", method = RequestMethod.GET)
    public void addNode(String nodeNum) {
        Node node = consistentHashCluster.getNode(Integer.parseInt(nodeNum));
        consistentHash.add(node);
        node = normalHashCluster.getNode(Integer.parseInt(nodeNum));
        normalHash.add(node);
        node = advConsistentHashCluster.getNode(Integer.parseInt(nodeNum));
        advConsistentHash.add(node);
        advConsistentHashCluster.addNode(node);
    }
}
