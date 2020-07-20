package cn.yugj.test.sentinel.cluster.server;

import com.alibaba.csp.sentinel.cluster.flow.rule.ClusterFlowRuleManager;
import com.alibaba.csp.sentinel.cluster.flow.rule.ClusterParamFlowRuleManager;
import com.alibaba.csp.sentinel.cluster.server.ClusterTokenServer;
import com.alibaba.csp.sentinel.cluster.server.SentinelDefaultTokenServer;
import com.alibaba.csp.sentinel.cluster.server.config.ClusterServerConfigManager;
import com.alibaba.csp.sentinel.cluster.server.config.ServerTransportConfig;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.nacos.NacosDataSource;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author yugj
 * @date 2020/7/15 3:33 下午.
 */
@SpringBootApplication
public class ClusterTokenServerStarter {

    private static final String remoteAddress = "localhost";
    private static final String groupId = "SENTINEL_GROUP";
    private static final String namespaceSetDataId = "cluster-server-namespace-set";
    private static final String serverTransportDataId = "cluster-server-transport-config";

    public static void main(String[] args) throws Exception {


        init();

        ClusterServerConfigManager.loadServerNamespaceSet(Collections.singleton(DemoConstants.APP_NAME));


        // 创建一个 ClusterTokenServer 的实例，独立模式
        ClusterTokenServer tokenServer = new SentinelDefaultTokenServer();
        // 启动
        tokenServer.start();
    }

    public static void init() throws Exception {
        // Register cluster flow rule property supplier which creates data source by namespace.
        ClusterFlowRuleManager.setPropertySupplier(namespace -> {
            ReadableDataSource<String, List<FlowRule>> ds = new NacosDataSource<>(remoteAddress, groupId,
                    namespace + DemoConstants.FLOW_POSTFIX,
                    source -> JSON.parseObject(source, new TypeReference<List<FlowRule>>() {}));
            return ds.getProperty();
        });

        // Register cluster parameter flow rule property supplier.
        ClusterParamFlowRuleManager.setPropertySupplier(namespace -> {
            ReadableDataSource<String, List<ParamFlowRule>> ds = new NacosDataSource<>(remoteAddress, groupId,
                    namespace + DemoConstants.PARAM_FLOW_POSTFIX,
                    source -> JSON.parseObject(source, new TypeReference<List<ParamFlowRule>>() {}));
            return ds.getProperty();
        });

        // Server namespace set (scope) data source.
        ReadableDataSource<String, Set<String>> namespaceDs = new NacosDataSource<>(remoteAddress, groupId,
                namespaceSetDataId, source -> JSON.parseObject(source, new TypeReference<Set<String>>() {}));
        ClusterServerConfigManager.registerNamespaceSetProperty(namespaceDs.getProperty());

        // Server transport configuration data source.
        ReadableDataSource<String, ServerTransportConfig> transportConfigDs = new NacosDataSource<>(remoteAddress,
                groupId, serverTransportDataId,
                source -> JSON.parseObject(source, new TypeReference<ServerTransportConfig>() {}));
        ClusterServerConfigManager.registerServerTransportProperty(transportConfigDs.getProperty());
    }

//    private static void initNamespace() {
//        // 加载namespace
//        ClusterServerConfigManager.loadServerNamespaceSet(Collections.singleton(APP_NAME));
//    }
//
//    private static void initTransport() {
//        // 加载ServerTransportConfig
//        ClusterServerConfigManager.loadGlobalTransportConfig(new ServerTransportConfig()
//                .setIdleSeconds(600)
//                .setPort(18730));
//    }
//    /**
//     * 初始化集群限流的Supplier
//     * 这样如果后期集群限流的规则发生变更的话，系统可以自动感知到
//     */
//    private static void initClusterFlowSupplier() {
//
//        final String path = "/Sentinel-Demo/Token-Server";
//        final String zkNodes = "localhost:2181";
//
//        // Supplier 会根据 namespace 生成的动态规则源，类型为 SentinelProperty<List<FlowRule>>，针对不同的 namespace 生成不同的规则源（监听不同 namespace 的 path）.
//        // 默认 namespace 为应用名（project.name）
//        // ClusterFlowRuleManager 针对集群限流规则，ClusterParamFlowRuleManager 针对集群热点规则，配置方式类似
//        ClusterFlowRuleManager.setPropertySupplier(namespace -> {
//            return new ZookeeperDataSource<>(zkNodes, path,
//                    buildFlowConfigParser()).getProperty();
//        });
//
//    }
//
//    private static Converter<String, List<FlowRule>> buildFlowConfigParser() {
//        return source -> JSON.parseObject(source, new TypeReference<List<FlowRule>>() {
//        });
//    }
}
