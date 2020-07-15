package cn.yugj.test.sentinel.cluster.server;

import com.alibaba.csp.sentinel.cluster.server.ClusterTokenServer;
import com.alibaba.csp.sentinel.cluster.server.SentinelDefaultTokenServer;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author yugj
 * @date 2020/7/15 3:33 下午.
 */
@SpringBootApplication
public class ClusterTokenServerStarter {

    public static void main(String[] args) throws Exception {

        // 创建一个 ClusterTokenServer 的实例，独立模式
        ClusterTokenServer tokenServer = new SentinelDefaultTokenServer();
        // 启动
        tokenServer.start();
    }

    /**
     * 初始化集群限流的Supplier
     * 这样如果后期集群限流的规则发生变更的话，系统可以自动感知到
     */
//    private void initClusterFlowSupplier() {
//
//        final String path = "/Sentinel-Demo/SYSTEM-CODE-DEMO-FLOW";
//        final String zkNodes = "localhost:2181";
//
//        // Supplier 会根据 namespace 生成的动态规则源，类型为 SentinelProperty<List<FlowRule>>，针对不同的 namespace 生成不同的规则源（监听不同 namespace 的 path）.
//        // 默认 namespace 为应用名（project.name）
//        // ClusterFlowRuleManager 针对集群限流规则，ClusterParamFlowRuleManager 针对集群热点规则，配置方式类似
//        ClusterFlowRuleManager.setPropertySupplier(namespace -> {
//            return new ZookeeperDataSource<>(zkNodes, path,
//                    buildFlowConfigParser());
//        });
//    }
//
//    private Converter<String, List<FlowRule>> buildFlowConfigParser() {
//        return source -> JSON.parseObject(source, new TypeReference<List<FlowRule>>() {
//        });
//    }
}
