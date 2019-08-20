package sentinel.ratelimit;

import com.alibaba.csp.sentinel.adapter.gateway.zuul.fallback.BlockResponse;
import com.alibaba.csp.sentinel.adapter.gateway.zuul.fallback.DefaultBlockFallbackProvider;
import com.alibaba.csp.sentinel.adapter.gateway.zuul.fallback.ZuulBlockFallbackProvider;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

/**
 * fallback provider
 *
 * @author yugj
 * @date 2019/8/14 上午10:21.
 */
public class MyBlockFallbackProvider implements ZuulBlockFallbackProvider {

    private Logger logger = LoggerFactory.getLogger(DefaultBlockFallbackProvider.class);

    @Override
    public String getRoute() {
        return "*";
    }

    @Override
    public BlockResponse fallbackResponse(String route, Throwable cause) {

        if (cause instanceof BlockException) {
            String rule = ((BlockException) cause).getRule().toString();
            logger.info("sentinel block fallback ,route :{}, rule :{}, code :{}, exp :{}", route, rule, HttpStatus.TOO_MANY_REQUESTS.value(), cause);
            return new BlockResponse(429, "too many request", "");
        } else {
            logger.info("sentinel block fallback ,route :{}, code :{}, exp :{}", route, HttpStatus.INTERNAL_SERVER_ERROR.value(), cause);
            return new BlockResponse(500, "System Error", route);
        }
    }
}
