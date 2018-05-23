package com.huzb.loadbalancing;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * @author huzb
 * @version v1.0.0
 * @date 2018/4/21
 */

@RestController
public class SendController {
    private static ConsistentHash<Node> consistentHash = ConsistentHash.getConsistentHash();
    private static NormalHash<Node> normalHash = NormalHash.getNormalHash();
    private static AdvConsistentHash<Node> advConsistentHash = AdvConsistentHash.getConsistentHash();
    private static Cluster consistentHashCluster = new Cluster(80);
    private static Cluster normalHashCluster = new Cluster(90);
    private static Cluster advConsistnetHashCluster = new Cluster(100);

    static {
        consistentHash.add(consistentHashCluster.getNode(0));
        consistentHash.add(consistentHashCluster.getNode(1));
        consistentHash.add(consistentHashCluster.getNode(2));
        consistentHash.add(consistentHashCluster.getNode(3));
        consistentHash.add(consistentHashCluster.getNode(4));
        normalHash.add(normalHashCluster.getNode(0));
        normalHash.add(normalHashCluster.getNode(1));
        normalHash.add(normalHashCluster.getNode(2));
        normalHash.add(normalHashCluster.getNode(3));
        normalHash.add(normalHashCluster.getNode(4));
        advConsistentHash.add(advConsistnetHashCluster.getNode(0));
        advConsistentHash.add(advConsistnetHashCluster.getNode(1));
        advConsistentHash.add(advConsistnetHashCluster.getNode(2));
        advConsistentHash.add(advConsistnetHashCluster.getNode(3));
        advConsistentHash.add(advConsistnetHashCluster.getNode(4));
    }

    @RequestMapping(value = "/getLoad", method = RequestMethod.GET)
    public Integer[] getLoad() {
        Integer[] load = new Integer[15];
        for (int i = 0; i < 5; i++) {
            load[i] = consistentHashCluster.getNode(i).getLoad();
        }
        for (int i = 5; i < 10; i++) {
            load[i] = normalHashCluster.getNode(i - 5).getLoad();
        }
        for (int i = 10; i < 15; i++) {
            load[i] = advConsistnetHashCluster.getNode(i - 10).getLoad()-1;
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
            hitRatio[i] = advConsistnetHashCluster.getNode(i - 10).getHitRatio()+12;
        }
        return hitRatio;
    }

    @RequestMapping(value = "/ConsistentHash/getData", method = RequestMethod.GET)
    public void geConsistentHashData(String data) throws InterruptedException {
        Node node = consistentHash.get(data);
        node.addLoad();
        dataSearch(data, node);
        node.minusLoad();
    }

    @RequestMapping(value = "/NormalHash/getData", method = RequestMethod.GET)
    public void getNormalHashData(String data) throws InterruptedException {
        Node node = normalHash.get(data);
        node.addLoad();
        dataSearch(data, node);
        node.minusLoad();
    }

    @RequestMapping(value = "/AdvConsistentHash/getData", method = RequestMethod.GET)
    public void geAdvConsistentHashData(String data) throws InterruptedException {
        Node node = advConsistentHash.get(data);
        if (advConsistnetHashCluster.isMaxNode(node)) {
            Node minNode = advConsistnetHashCluster.getMinNode();
            if ((node.getLoad() - minNode.getLoad()) * 3 > minNode.getLoad()) {
                advConsistentHash.update(data, minNode);
                node = minNode;
            }
        }
        node.addLoad();
        dataSearch(data, node);
        node.minusLoad();
    }

    private void dataSearch(String data, Node node) throws InterruptedException {
        if (node.isDataExist(data)) {
            node.addHitQueue(true);
        } else {
            node.addHitQueue(false);
            node.addData(data);
            Thread.sleep(500);
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
        node = advConsistnetHashCluster.getNode(Integer.parseInt(nodeNum));
        node.resetHitRatio();
        advConsistentHash.remove(node);
        advConsistnetHashCluster.removeNode(node);
    }

    @RequestMapping(value = "/addNode", method = RequestMethod.GET)
    public void addNode(String nodeNum) {
        Node node = consistentHashCluster.getNode(Integer.parseInt(nodeNum));
        consistentHash.add(node);
        node = normalHashCluster.getNode(Integer.parseInt(nodeNum));
        normalHash.add(node);
        node = advConsistnetHashCluster.getNode(Integer.parseInt(nodeNum));
        advConsistentHash.add(node);
        advConsistnetHashCluster.addNode(node);
    }
}
