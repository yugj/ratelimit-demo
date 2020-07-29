package mvc.ratelimit;

import com.alibaba.csp.sentinel.annotation.aspectj.SentinelResourceAspect;
import com.alibaba.csp.sentinel.cluster.client.config.ClusterClientAssignConfig;
import com.alibaba.csp.sentinel.cluster.client.config.ClusterClientConfigManager;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.nacos.NacosDataSource;
import com.alibaba.csp.sentinel.datasource.redis.RedisDataSource;
import com.alibaba.csp.sentinel.datasource.redis.config.RedisConnectionConfig;
import com.alibaba.csp.sentinel.datasource.zookeeper.ZookeeperDataSource;
import com.alibaba.csp.sentinel.property.DynamicSentinelProperty;
import com.alibaba.csp.sentinel.property.SentinelProperty;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.transport.config.TransportConfig;
import com.alibaba.csp.sentinel.util.HostNameUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

/**
 * @author yugj
 * @date 2019/8/12 下午7:41.
 */
@Configuration
public class RateLimitConfiguration {

    String remoteAddress = "localhost";
    String groupId = "SENTINEL_GROUP";

    /**
     * 没有引入spring-cloud-starter-alibaba-sentinel 需要手动注册下
     * @return
     */
    @Bean
    public SentinelResourceAspect sentinelResourceAspect() {
        return new SentinelResourceAspect();
    }

    @PostConstruct
    public void doInit() {

        loadRules();

        ClusterClientAssignConfig clientAssignConfig = new ClusterClientAssignConfig("192.168.250.203",18730);
        ClusterClientConfigManager.applyNewAssignConfig(clientAssignConfig);

//        clientTokenServerAssign();
//        initClientServerAssignProperty();

    }

    private void loadRules() {

//        registerWithZookeeper();
        registerWithNacos();
    }

    private void clientTokenServerAssign() {
        ClusterClientAssignConfig clientAssignConfig = new ClusterClientAssignConfig("localhost",18730);
        SentinelProperty<ClusterClientAssignConfig> property = new DynamicSentinelProperty<ClusterClientAssignConfig>(clientAssignConfig);
        ClusterClientConfigManager.registerServerAssignProperty(property);
    }

    private void initClientServerAssignProperty() {
        String clusterMapDataId = "client-server-assign-map-data";
        // Cluster map format:
        // [{"clientSet":["112.12.88.66@8729","112.12.88.67@8727"],"ip":"112.12.88.68","machineId":"112.12.88.68@8728","port":11111}]
        // machineId: <ip@commandPort>, commandPort for port exposed to Sentinel dashboard (transport module)
        ReadableDataSource<String, ClusterClientAssignConfig> clientAssignDs = new NacosDataSource<>(remoteAddress, groupId,
                clusterMapDataId, source -> {
            List<ClusterGroupEntity> groupList = JSON.parseObject(source, new TypeReference<List<ClusterGroupEntity>>() {});
            return Optional.ofNullable(groupList)
                    .flatMap(this::extractClientAssignment)
                    .orElse(null);
        });
        ClusterClientConfigManager.registerServerAssignProperty(clientAssignDs.getProperty());
    }

    private void registerWithNacos() {
        String dataId = "sentinel-mvc-server-flow-rules";
        ReadableDataSource<String, List<FlowRule>> flowRuleDataSource = new NacosDataSource<>(remoteAddress, groupId, dataId,
                source -> JSON.parseObject(source, new TypeReference<List<FlowRule>>() {
                }));
        FlowRuleManager.register2Property(flowRuleDataSource.getProperty());
    }

    private void registerWithZookeeper() {
        final String remoteAddress = "127.0.0.1:2181";
        final String path = "/sentinel/flow-rules/sentinel-mvc-server";

        ReadableDataSource<String, List<FlowRule>> flowRuleDataSource = new ZookeeperDataSource<>(remoteAddress, path,
                buildFlowConfigParser());
        FlowRuleManager.register2Property(flowRuleDataSource.getProperty());
    }

    private void registerWithRedis() {

        RedisConnectionConfig config = RedisConnectionConfig.builder()
                .withHost("localhost")
                .withPort(6379)
                .build();
        String ruleKey = "ru-sentinel-mvc-server";
        String channel = "ch-sentinel-mvc-server";

        ReadableDataSource<String, List<FlowRule>> redisDataSource = new RedisDataSource<List<FlowRule>>(config, ruleKey, channel, buildFlowConfigParser());
        FlowRuleManager.register2Property(redisDataSource.getProperty());
    }

    private Converter<String, List<FlowRule>> buildFlowConfigParser() {
        return source -> JSON.parseObject(source, new TypeReference<List<FlowRule>>() {});
    }

    private Optional<ClusterClientAssignConfig> extractClientAssignment(List<ClusterGroupEntity> groupList) {
        if (groupList.stream().anyMatch(this::machineEqual)) {
            return Optional.empty();
        }
        // Build client assign config from the client set of target server group.
        for (ClusterGroupEntity group : groupList) {
            if (group.getClientSet().contains(getCurrentMachineId())) {
                String ip = group.getIp();
                Integer port = group.getPort();
                return Optional.of(new ClusterClientAssignConfig(ip, port));
            }
        }
        return Optional.empty();
    }

    private boolean machineEqual(/*@Valid*/ ClusterGroupEntity group) {
        return getCurrentMachineId().equals(group.getMachineId());
    }

    private String getCurrentMachineId() {
        // Note: this may not work well for container-based env.
        return HostNameUtil.getIp() + SEPARATOR + TransportConfig.getRuntimePort();
//        return HostNameUtil.getIp() + SEPARATOR + "8721";
    }

    private static final String SEPARATOR = "@";
}
