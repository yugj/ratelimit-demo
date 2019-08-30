package hystrix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * @author yugj
 * @date 2019/8/26 下午4:31.
 */
@SpringBootApplication
@EnableZuulProxy
@ServletComponentScan
public class ZuulHystrixStart {

    public static void main(String[] args) {
        SpringApplication.run(ZuulHystrixStart.class, args).start();
    }
}
