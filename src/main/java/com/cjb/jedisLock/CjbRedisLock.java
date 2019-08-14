package com.cjb.jedisLock;

import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisCluster;

/**
 * 自己重写分布式锁
 * Created by chenjiabao on 2019/8/14.
 */
public class CjbRedisLock {

    private JedisCluster jedisCluster;

    private String lockKey;

    //锁失效时间 毫秒
    private int expireTime = 60 * 1000;

    //锁等待时间 自旋时间 毫秒
    private int waitTime = 5*1000;

    public CjbRedisLock(JedisCluster jedisCluster, String lockKey) {
        this.jedisCluster = jedisCluster;
        this.lockKey = lockKey;
    }

    public CjbRedisLock(JedisCluster jedisCluster, String lockKey, int waitTime) {
        this(jedisCluster, lockKey);
        this.waitTime = waitTime;
    }

    public CjbRedisLock(JedisCluster jedisCluster, String lockKey, int waitTime, int expireTime) {
        this(jedisCluster, lockKey, waitTime);
        this.expireTime = expireTime;
    }

    public synchronized boolean lock(){
        int time = waitTime;
        while(time>0){
            String expiresStr = String.valueOf(System.currentTimeMillis() + expireTime);
            if(jedisCluster.setnx(lockKey,expiresStr)==1L){
                return true;
            }

            String currentValueStr = jedisCluster.get(lockKey);
            if(!StringUtils.isEmpty(currentValueStr) && Long.parseLong(currentValueStr)<System.currentTimeMillis()){

            }

        }
        return false;
    }
}
