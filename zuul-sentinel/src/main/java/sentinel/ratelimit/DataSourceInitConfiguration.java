package sentinel.ratelimit;

import com.alibaba.csp.sentinel.adapter.gateway.common.SentinelGatewayConstants;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayParamFlowItem;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.adapter.gateway.zuul.fallback.ZuulBlockFallbackManager;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.zookeeper.ZookeeperDataSource;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

/**
 * @author yugj
 * @date 2019/8/15 下午2:44.
 */
@Configuration
public class DataSourceInitConfiguration {

    @Value("${spring.cloud.stream.kafka.binder.zkNodes}")
    private String zkNodes;

    @PostConstruct
    public void doInit() throws Exception {

        initGatewayRules();

        //默认 fallback provider不打印日志，另外返回格式不是业务需要，重写下
        ZuulBlockFallbackManager.registerProvider(new MyBlockFallbackProvider());
    }

    /**
     * 配置限流规则
     */
    private void initGatewayRules() {
        Set<GatewayFlowRule> rules = new HashSet<>();

//        //resource 将作用于对应名字的router id中 对应配置文件 zuul.routes
//        //默认针对路由做限制
//        rules.add(new GatewayFlowRule("rest-server")
//                // 限流阈值每秒允许1个
//                .setCount(1)
//                .setIntervalSec(1)
//                //流量整形的控制效果，同限流规则的 controlBehavior 字段，
//                //目前支持快速失败和匀速排队两种模式，默认是快速失败
//                //业务更期待 warn up 方式，按目前只能等后面版本
//                .setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_RATE_LIMITER)
//                // 匀速排队模式下的最长排队时间，单位是毫秒，仅在匀速排队模式下生效
//                .setMaxQueueingTimeoutMs(1000 * 2)
//                //应对突发请求时额外允许的请求数目
//                .setBurst(1)
//        );
//
//        //多个规则这边补充
//        rules.add(new GatewayFlowRule("rest-server")
//                // 限流阈值每秒允许1个
//                .setCount(1)
//                .setIntervalSec(1)
//                .setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_DEFAULT)
//        );

        GatewayParamFlowItem paramFlowItem = new GatewayParamFlowItem();
        //基于header 限流
        paramFlowItem.setParseStrategy(SentinelGatewayConstants.PARAM_PARSE_STRATEGY_URL_PARAM);
        paramFlowItem.setFieldName("uipp");
        //多个规则这边补充
        rules.add(new GatewayFlowRule("rest-server")
                // 限流阈值每秒允许1个
                .setCount(10)
                .setIntervalSec(1)
                .setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_DEFAULT)
                .setParamItem(paramFlowItem)
        );

        System.out.println("rules");
        System.out.println(JSON.toJSONString(rules));

        GatewayRuleManager.loadRules(rules);
    }


    /**
     * with data source
     */
    private void initGatewayRules2() {

        ReadableDataSource<String, Set<GatewayFlowRule>> flowRuleDataSource =
                new ZookeeperDataSource<>(zkNodes, RateLimitConstant.RATE_LIMIT_RULE_PATH,
                buildFlowConfigParser());

        GatewayRuleManager.register2Property(flowRuleDataSource.getProperty());
    }

    private Converter<String, Set<GatewayFlowRule>> buildFlowConfigParser() {
        return source -> JSON.parseObject(source, new TypeReference<Set<GatewayFlowRule>>() {});
    }
}
