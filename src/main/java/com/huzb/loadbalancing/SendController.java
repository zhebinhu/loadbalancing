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
    private ConsistentHash<Node> consistentHash = ConsistentHash.getConsistentHash();
    private Cluster cluster = Cluster.getCluster();
    private long current = System.nanoTime();

    @RequestMapping("/getServerNum")
    public HashMap getServerNum(String data) throws InterruptedException {

        HashMap response = new HashMap();
        Node node = consistentHash.get(data);
        node = addNodes(data, node);
        System.out.println((System.nanoTime() - current) / 1000000000 + " : " + data + " : " + cluster.getNode(0).showDataQueue());
//        Integer serverNum = cluster.getSerialNumber(node);
        node.addLoad();
        if (node.isDataExist(data)) {
            Thread.sleep(1000);
        } else {
            node.addData(data);
            Thread.sleep(2000);
        }
        node.minusLoad();
//        response.put("Method", "getServerNum");
//        response.put("Data", data);
//        response.put("ServerNum", serverNum);

        return response;
    }

    @RequestMapping(value = "/getLoad", method = RequestMethod.GET)
    public Integer[] getLoad() {
        Integer[] load = new Integer[5];
        for (int i = 0; i < 5; i++) {
            load[i] = cluster.getNode(i).getLoad();
        }
        return load;
    }

    @RequestMapping(value = "/getData", method = RequestMethod.GET)
    public void getData(String data) throws InterruptedException {
//        HashMap response = new HashMap();
        Node node = consistentHash.get(data);
        node = addNodes(data, node);
        Integer serverNum = cluster.getSerialNumber(node);
        node.addLoad();
        if (node.isDataExist(data)) {
            Thread.sleep(10);
        } else {
            node.addData(data);
            Thread.sleep(5000);
        }
        node.minusLoad();
//        response.put("Method", "getData");
//        response.put("Data", data);
//        response.put("ServerNum", serverNum);

        Integer[] load = new Integer[5];
        for (int i = 0; i < 5; i++) {
            load[i] = cluster.getNode(i).getLoad();
            System.out.println(cluster.getNode(i).showDataQueue());
        }
        for (Integer i : load) {
            System.out.print(i+" ");
        }
        System.out.println();

//        return response;
    }

    private Node addNodes(String data, Node node) {
        if (node == null) {
            consistentHash.add(cluster.getNode(0));
            consistentHash.add(cluster.getNode(1));
            consistentHash.add(cluster.getNode(2));
            consistentHash.add(cluster.getNode(3));
            consistentHash.add(cluster.getNode(4));
            node = consistentHash.get(data);
        }
        return node;
    }

}
