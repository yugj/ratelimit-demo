package com.github.yugj.test.guava.ratelimiter;

import com.google.common.util.concurrent.RateLimiter;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author yugj
 * @date 2020/7/31 4:11 下午.
 */
public class SmoothBurstyTest {

    /**
     * 每秒增加5个令牌，每200ms增加一个令牌
     * 默认为SmoothBursty 允许瞬时突发，并且改瞬时突发首次没有上限
     * 大流量过来可能击垮系统
     *
     */
    private static RateLimiter rateLimiter = RateLimiter.create(5.0);
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {

        testAcquire();
        System.out.println("end");


    }

    /**
     * 瞬时突发首次没有上限
     * 令牌桶允许一定的突发，首次5耗时0，第二次等待了近1秒；令牌桶允许突发请求，意味着首次是消耗了未来的令牌
     * 如果首次调整为10，第二次等待将增加到2秒
     */
    private static void testAcquire() {

//        System.out.println("acquired cost, " + rateLimiter.acquire(10));
//        System.out.println("acquired cost, " + rateLimiter.acquire(1));
//        System.out.println("acquired cost, " + rateLimiter.acquire(1));
//        System.out.println("acquired cost, " + rateLimiter.acquire(1));

        for(int i =1; i < 10;i++) {
            System.out.println("acquired cost " + rateLimiter.acquire());
        }
    }

    private static void tryAcquire() {
        while (true) {
            boolean rs = rateLimiter.tryAcquire();
            if (rs) {
                System.out.println("acquired, " + sdf.format(new Date()));
            }
        }
    }
}
