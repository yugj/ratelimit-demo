package mvc.ratelimit;

import com.alibaba.csp.sentinel.annotation.aspectj.SentinelResourceAspect;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.zookeeper.ZookeeperDataSource;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author yugj
 * @date 2019/8/12 下午7:41.
 */
@Configuration
public class RateLimitConfiguration {

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

    }

    private void loadRules() {

        final String remoteAddress = "127.0.0.1:2181";
        final String path = "/Sentinel-Demo/SYSTEM-CODE-DEMO-FLOW";

        ReadableDataSource<String, List<FlowRule>> flowRuleDataSource = new ZookeeperDataSource<>(remoteAddress, path,
                buildFlowConfigParser());
        FlowRuleManager.register2Property(flowRuleDataSource.getProperty());


    }

    private Converter<String, List<FlowRule>> buildFlowConfigParser() {
        return source -> JSON.parseObject(source, new TypeReference<List<FlowRule>>() {});
    }
}
