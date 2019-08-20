import com.alibaba.csp.sentinel.adapter.gateway.common.SentinelGatewayConstants;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayParamFlowItem;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.fastjson.JSON;

import java.util.HashSet;
import java.util.Set;

/**
 * @author yugj
 * @date 2019/8/14 下午3:54.
 */
public class FlowRuleGenerator {

    public static void main(String[] args) {

        Set<GatewayFlowRule> rules = new HashSet<>();

        GatewayParamFlowItem paramFlowItem = new GatewayParamFlowItem();
        //基于header 限流
        paramFlowItem.setParseStrategy(SentinelGatewayConstants.PARAM_PARSE_STRATEGY_HEADER);
        paramFlowItem.setFieldName("mg-us");
        //多个规则这边补充
        rules.add(new GatewayFlowRule("rest-server")
                // 限流阈值每秒允许1个
                .setCount(1)
                .setIntervalSec(1)
                .setControlBehavior(RuleConstant.CONTROL_BEHAVIOR_DEFAULT)
                .setParamItem(paramFlowItem)
        );

        System.out.println("rules");
        System.out.println(JSON.toJSONString(rules));


    }
}
