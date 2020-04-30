package tech.fanpu.helper;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.concurrent.TimeUnit;

/**
 * @Title
 * @Package tech.fanpu.helper
 * @Author porridge
 * @Date 2019-07-26 17:13
 * @Version V1.0
 */
@Log4j2
@Component
public class RedisHelper {
    final String REDIS_PREFIX_KEY = "DeepTechSpaceServer.";
    @Autowired
    StringRedisTemplate redis;

    public Boolean hasKey(String key) {
        if (null == key) {
            return null;
        }
        return redis.hasKey(key);
    }

    /**
     * 获得需要抛出的秒
     * 具体使用场景：
     * 比如华夏银行一分钟只能够创建一个帐户。
     * 利用redis原子性来实现
     *
     * @param key
     * @param seconds
     * @return 返回执行的时间
     */
    public Long addSeconds(String key, Integer seconds) {
        key = REDIS_PREFIX_KEY + "addSeconds." + key;
        String timeKey = key + "_time";
        Long currentSeconds = System.currentTimeMillis();
        Long value = incr(key);
        --value;

        Long timeout = value.intValue() * new Long(seconds);
        //利用setIfAbsent 当有这个key的时候不会再次写入了
        redis.opsForValue().setIfAbsent(timeKey, currentSeconds + "", timeout + 10, TimeUnit.SECONDS);
        redis.expire(key, timeout + 10, TimeUnit.SECONDS);
        Long firstDate = Long.valueOf(redis.opsForValue().get(timeKey));
        Long result = timeout - ((currentSeconds - firstDate) / 1000);
        if (log.isDebugEnabled()) {
            log.debug("{} 下次触发的时间：{}秒", key, result);
        }
        return result;
    }

    /**
     * 原子减1
     *
     * @param key
     * @param timeout
     * @return
     */
    public Long decr(String key, Integer timeout) {
        key = REDIS_PREFIX_KEY + key;
        RedisConnection conn = null;
        Long result = null;
        try {
            conn = redis.getConnectionFactory().getConnection();
            result = conn.decr(key.getBytes());
            if (timeout != null) {
                redis.expire(key, timeout, TimeUnit.SECONDS);
            }
        } finally {
            if (conn != null) {
                conn.close();
            }
            return result;
        }
    }

    public Long decr(String key) {
        return decr(key, null);
    }

    /**
     * 原子+1
     *
     * @param key
     * @param timeout
     * @return
     */
    public Long incr(String key, Integer timeout) {
        return incr(key, timeout, TimeUnit.SECONDS);
    }

    /**
     * 原子+1
     *
     * @param key
     * @param timeout
     * @param timeUnit
     * @return
     */
    public Long incr(String key, Integer timeout, TimeUnit timeUnit) {
        key = REDIS_PREFIX_KEY + key;
        RedisConnection conn = null;
        Long result = null;
        try {
            conn = redis.getConnectionFactory().getConnection();
            result = conn.incr(key.getBytes());
            if (timeout != null) {
                redis.expire(key, timeout, timeUnit);
            }
        } finally {
            if (conn != null) {
                conn.close();
            }
            return result;
        }
    }

    public Long incr(String key) {
        return incr(key, null);
    }


    /**
     * 根据某key进行锁定
     *
     * @param key
     * @param timeout
     */
    public void lock(String key, Integer timeout, String errorMsg) {
        key = REDIS_PREFIX_KEY + key;
        Assert.isTrue(redis.opsForValue().setIfAbsent(key, "1", timeout, TimeUnit.SECONDS), errorMsg);
    }

    /**
     * 根据某key解锁
     *
     * @param key
     */
    public void unLock(String key) {
        key = REDIS_PREFIX_KEY + key;
        redis.delete(key);
    }
}

