package cn.yugj.test.redisson;

import org.redisson.Redisson;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.IntegerCodec;
import org.redisson.config.Config;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author yugj
 * @date 2020/1/17 5:47 PM.
 */
public class RedissonRateLimitTest2 {

    private static RedissonClient redisClient;

    private static final String KEY = "MRL:" + "hell";

    static {
        Config config = getSingleConfig();
        redisClient = Redisson.create(config);
    }

    private static Config getClusterConfig() {

        String redisHosts = "10.46.235.121:6384,10.46.235.121:6385,10.46.235.122:6384,10.46.235.122:6385,10.46.235.123:6384,10.46.235.123:6385";
        String[] hostArray = redisHosts.split(",");

        Config config = new Config();
        for (String host : hostArray) {
            config.useClusterServers().addNodeAddress("redis://" + host);
        }
        config.useClusterServers().setScanInterval(5000);
        return config;
    }

    private static Config getSingleConfig() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://localhost:6379");
        return config;
    }


    public static void main(String[] args) throws InterruptedException {

        System.out.println("reload start");
        reload();
        System.out.println("reload end");

        for (int i = 0; i < 2000; i++) {
            Thread.sleep(500L);

            boolean flag = entry(KEY, 10, 2);

            if (flag) {
                System.out.println((new Date()) + "hell");
            } else {
                System.out.println(false);
            }
        }

        redisClient.shutdown();
    }

    public static void reload() {
        RMapCache<String, Integer> msgRateLimit =
                redisClient.getMapCache(KEY, IntegerCodec.INSTANCE);
        if (msgRateLimit.containsKey(KEY)) {
            msgRateLimit.delete();
        }
    }

    /**
     * 进入
     * @param entryKey key
     * @param timeToLiveInSeconds 时间窗口 存活时间
     * @param limit 限制数量
     * @return 是否成功 true 成功 false 失败
     */
    public static boolean entry(String entryKey, Integer timeToLiveInSeconds, Integer limit) {

        RMapCache<String, Integer> mRateLimit = redisClient.getMapCache(KEY, IntegerCodec.INSTANCE);

        Integer count;

        try {
            mRateLimit.putIfAbsent(entryKey, 0, timeToLiveInSeconds, TimeUnit.SECONDS);
            count = mRateLimit.addAndGet(entryKey, 1);
            return count <= limit;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
