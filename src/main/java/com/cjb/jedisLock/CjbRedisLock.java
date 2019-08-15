package com.cjb.jedisLock;

import org.apache.tomcat.jni.Thread;
import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.params.SetParams;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 自己重写分布式锁
 * Created by chenjiabao on 2019/8/14.
 */
public class CjbRedisLock {

    private JedisCluster jedisCluster;

    private String lockKey;

    //锁失效时间 毫秒
    private int expireTime = 10 * 1000;

    //锁等待时间 自旋时间 毫秒
    private int waitTime = 5*1000;

    //自旋的间隔时间 暂时没用到
    private int intervalTime=1000;


    private static final String LOCK_SUCCESS = "OK";

    public CjbRedisLock(JedisCluster jedisCluster, String lockKey) {
        this.jedisCluster = jedisCluster;
        this.lockKey = lockKey;
    }

    public CjbRedisLock(JedisCluster jedisCluster, String lockKey, int expireTime) {
        this(jedisCluster, lockKey);
        this.expireTime = expireTime;
    }


    /**
     * 尝试锁
     * 参考：https://blog.csdn.net/josn_hao/article/details/78412694
     * @return
     */
    public boolean lock(){
        String result  = jedisCluster.set(lockKey, UUID.randomUUID().toString(), SetParams.setParams().nx().px(expireTime));
        if (LOCK_SUCCESS.equals(result)) {
            System.out.println("我获取到了锁");
            return true;
        }
        return false;
    }

    /**
     * 释放锁
     */
    public void unlock(){
        String script = "if (redis.call('EXISTS', KEYS[1]) == 1) then return redis.call('del', KEYS[1]) else return 0 end";
        jedisCluster.eval(script,1,lockKey);
    }
}
