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

        ClusterServerConfigManager.loadGlobalTransportConfig(new ServerTransportConfig()
                .setIdleSeconds(600)
                .setPort(11111));

//        initClientServerAssignProperty();
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


//    private static void initClientServerAssignProperty() {
//        String clusterMapDataId = "client-server-assign-map-data";
//        // Cluster map format:
//        // [{"clientSet":["112.12.88.66@8729","112.12.88.67@8727"],"ip":"112.12.88.68","machineId":"112.12.88.68@8728","port":11111}]
//        // machineId: <ip@commandPort>, commandPort for port exposed to Sentinel dashboard (transport module)
//        ReadableDataSource<String, ClusterClientAssignConfig> clientAssignDs = new NacosDataSource<>(remoteAddress, groupId,
//                clusterMapDataId, source -> {
//            List<ClusterGroupEntity> groupList = JSON.parseObject(source, new TypeReference<List<ClusterGroupEntity>>() {});
//            return Optional.ofNullable(groupList)
//                    .flatMap(this::extractClientAssignment)
//                    .orElse(null);
//        });
//        ClusterClientConfigManager.registerServerAssignProperty(clientAssignDs.getProperty());
//    }
//
//    private Optional<ClusterClientAssignConfig> extractClientAssignment(List<ClusterGroupEntity> groupList) {
//        if (groupList.stream().anyMatch(this::machineEqual)) {
//            return Optional.empty();
//        }
//        // Build client assign config from the client set of target server group.
//        for (ClusterGroupEntity group : groupList) {
//            if (group.getClientSet().contains(getCurrentMachineId())) {
//                String ip = group.getIp();
//                Integer port = group.getPort();
//                return Optional.of(new ClusterClientAssignConfig(ip, port));
//            }
//        }
//        return Optional.empty();
//    }
//
//    private boolean machineEqual(/*@Valid*/ ClusterGroupEntity group) {
//        return getCurrentMachineId().equals(group.getMachineId());
//    }
//
//    private String getCurrentMachineId() {
//        // Note: this may not work well for container-based env.
////        return HostNameUtil.getIp() + SEPARATOR + TransportConfig.getRuntimePort();
//        return HostNameUtil.getIp() + SEPARATOR + "8721";
//    }
//
//    private static final String SEPARATOR = "@";
}
