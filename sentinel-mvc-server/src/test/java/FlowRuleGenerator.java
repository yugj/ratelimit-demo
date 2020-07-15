import com.alibaba.csp.sentinel.slots.block.ClusterRuleConstant;
import com.alibaba.csp.sentinel.slots.block.flow.ClusterFlowConfig;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.fastjson.JSON;

import java.util.HashSet;
import java.util.Set;

/**
 * @author yugj
 * @date 2019/8/14 下午3:54.
 */
public class FlowRuleGenerator {

    public static void main(String[] args) {

        String flowRulesJson =
                "[{\"resource\":\"test\", \"limitApp\":\"default\", \"grade\":1, \"count\":\"0.0\", \"strategy\":0, "
                        + "\"refResource\":null, "
                        +
                        "\"controlBehavior\":0, \"warmUpPeriodSec\":10, \"maxQueueingTimeMs\":500, \"controller\":null}]";

        Set<FlowRule> rules = new HashSet<>();

        FlowRule rule = new FlowRule();

        ClusterFlowConfig flowConfig = new ClusterFlowConfig();
        flowConfig.setFallbackToLocalWhenFail(false);
        flowConfig.setFlowId(1000L);
        flowConfig.setSampleCount(1);
        flowConfig.setWindowIntervalMs(1000);
        flowConfig.setThresholdType(ClusterRuleConstant.FLOW_THRESHOLD_GLOBAL);


        rule.setClusterMode(true);

        rule.setResource("sayHello");
        rule.setCount(2);

        rules.add(rule);
        rule.setClusterConfig(flowConfig);

        System.out.println(JSON.toJSONString(rules));
    }
}
