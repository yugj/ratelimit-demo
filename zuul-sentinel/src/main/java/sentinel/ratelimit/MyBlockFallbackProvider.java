package sentinel.ratelimit;

import com.alibaba.csp.sentinel.adapter.gateway.zuul.fallback.BlockResponse;
import com.alibaba.csp.sentinel.adapter.gateway.zuul.fallback.DefaultBlockFallbackProvider;
import com.alibaba.csp.sentinel.adapter.gateway.zuul.fallback.ZuulBlockFallbackProvider;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yugj
 * @date 2019/8/14 上午10:21.
 */
public class MyBlockFallbackProvider implements ZuulBlockFallbackProvider {


    private Logger logger = LoggerFactory.getLogger(DefaultBlockFallbackProvider.class);

    // you can define route as service level
    @Override
    public String getRoute() {
        return "*";
    }

    @Override
    public BlockResponse fallbackResponse(String route, Throwable cause) {
        logger.info(String.format("[Sentinel DefaultBlockFallbackProvider] Run fallback route: %s", route));
        if (cause instanceof BlockException) {
            return new BlockResponse(429, "Sentinel block exception", route);
        } else {
            return new BlockResponse(500, "System Error", route);
        }
    }
}
