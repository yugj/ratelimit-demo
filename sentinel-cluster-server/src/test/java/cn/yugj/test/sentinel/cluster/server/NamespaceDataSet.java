package cn.yugj.test.sentinel.cluster.server;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Sets;

import java.util.Set;

/**
 * @author yugj
 * @date 2020/7/20 5:10 下午.
 */
public class NamespaceDataSet {

    public static void main(String[] args) {
        Set<String> dataSet = Sets.newHashSet();


        dataSet.add("sentinel-mvc-server");
        dataSet.add("sentinel-mvc-server2");


        System.out.println(
                JSON.toJSONString(dataSet)
        );

    }
}
