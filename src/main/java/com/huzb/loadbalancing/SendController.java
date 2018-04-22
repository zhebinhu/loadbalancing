package com.huzb.loadbalancing;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.LinkedList;

import static java.lang.Thread.sleep;

/**
 * @author huzb
 * @version v1.0.0
 * @date 2018/4/21
 */

@RestController
public class SendController {

    @RequestMapping("/getServerNum")
    public HashMap getServerNum(String data) {
        HashMap response = new HashMap();
        ConsistentHash consistentHash = ConsistentHash.getConsistentHash();
        String serverNum = consistentHash.get(data).toString();

        response.put("Method", "getServerNum");
        response.put("Data", data);
        response.put("ServerNum", serverNum);

        return response;
    }

    @RequestMapping(value = "/getData", method = RequestMethod.GET)
    public HashMap getData(String data) throws InterruptedException {
        HashMap response = new HashMap();
        ConsistentHash consistentHash = ConsistentHash.getConsistentHash();
        Cluster cluster = Cluster.getCluster();
        String serverNum = consistentHash.get(data).toString();
        LinkedList cache = cluster.get(serverNum);

        if (cache.contains(data)) {
            Thread.sleep(2000);
        } else {
            if (cache.size() > 10) {
                cache.remove();
            }
            cache.add(data);
            Thread.sleep(5000);
        }

        response.put("Method", "getData");
        response.put("Data", data);
        response.put("ServerNum", serverNum);

        return response;
    }

}
