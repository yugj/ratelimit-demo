package com.github.yugj.test.guava.ratelimiter;

import com.google.common.util.concurrent.RateLimiter;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;

/**
 * @author yugj
 * @date 2020/7/31 4:13 下午.
 */
public class SmoothWarmingUpTest {

    static RateLimiter rateLimiter = RateLimiter.create(5,1000, TimeUnit.MILLISECONDS);
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    public static void main(String[] args) {
        testAcquire();
    }


    /**
     */
    private static void testAcquire() {

        // 首次超限获取 会造成后续等待
//        System.out.println("acquired cost, " + rateLimiter.acquire(50));
//        System.out.println("acquired cost, " + rateLimiter.acquire(1));
//        System.out.println("acquired cost, " + rateLimiter.acquire(1));
//        System.out.println("acquired cost, " + rateLimiter.acquire(1));


        /**
         * acquired cost 0.0
         * acquired cost 0.518166
         * acquired cost 0.355356
         * acquired cost 0.220128
         * acquired cost 0.19777
         * acquired cost 0.198511
         * acquired cost 0.194702
         * acquired cost 0.194465
         * acquired cost 0.197075
         * 连续获取9次，首次为0，后续时间递减，梯形上升速率的 逐渐到平滑200ms
         */
        for(int i =1; i < 10;i++) {
            System.out.println("acquired cost " + rateLimiter.acquire());
        }
    }

}
