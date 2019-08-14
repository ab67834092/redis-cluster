package com.cjb.jedisLock;

import redis.clients.jedis.JedisCluster;

/**
 * Created by cjb on 2019/8/14.
 */
public class JedisLock {

    private JedisCluster jedis;

    //锁路径 lock key path
    private String lockKey;

    /** Lock expiration in miliseconds. */
    int expireMsecs = 60 * 1000; //锁超时，防止线程在入锁以后，无限的执行等待

    /** Acquire timeout in miliseconds. */
    int timeoutMsecs = 10 * 1000; //锁等待，防止线程饥饿

    boolean locked = false;

    public JedisLock(JedisCluster jedis, String lockKey) {
        this.jedis = jedis;
        this.lockKey = lockKey;
    }

    public JedisLock(JedisCluster jedis, String lockKey, int timeoutMsecs) {
        this(jedis, lockKey);
        this.timeoutMsecs = timeoutMsecs;
    }

    public JedisLock(JedisCluster jedis, String lockKey, int timeoutMsecs, int expireMsecs) {
        this(jedis, lockKey, timeoutMsecs);
        this.expireMsecs = expireMsecs;
    }

    public JedisLock(String lockKey) {
        this(null, lockKey);
    }

    public JedisLock(String lockKey, int timeoutMsecs) {
        this(null, lockKey, timeoutMsecs);
    }

    public JedisLock(String lockKey, int timeoutMsecs, int expireMsecs) {
        this(null, lockKey, timeoutMsecs, expireMsecs);
    }

    public String getLockKey() {
        return lockKey;
    }

    public boolean acquire() throws InterruptedException {
        return acquire(jedis);
    }

    /**
     * 获取锁
     * @param jedis
     * @return
     */
    public boolean acquire(JedisCluster jedis) throws InterruptedException {
        //锁等待的时长
        int timeout = timeoutMsecs;
        while(timeout>0){
            long expires = System.currentTimeMillis() + expireMsecs + 1;
            String expiresStr = String.valueOf(expires);
            if(jedis.setnx(lockKey,expiresStr)==1){
                locked = true;
                return true;
            }

            String currentValueStr = jedis.get(lockKey); //redis里的时间
            if(currentValueStr!=null && Long.parseLong(currentValueStr)<System.currentTimeMillis()){
                //判断是否为空，不为空的情况下，如果被其他线程设置了值，则第二个条件判断是过不去的
                String oldValueStr = jedis.getSet(lockKey, expiresStr);
                //获取上一个锁到期时间，并设置现在的锁到期时间，
                if (oldValueStr != null && oldValueStr.equals(currentValueStr)) {
                    //如过这个时候，多个线程恰好都到了这里，但是只有一个线程的设置值和当前值相同，他才有权利获取锁
                    locked = true;
                    return true;
                }
                timeout -= 100;
                Thread.sleep(100);
            }
        }
        return false;
    }

    /**
     * 释放锁
     */
    public void release() {
        release(jedis);
    }

    /**
     * 释放锁
     * @param jedis
     */
    public void release(JedisCluster jedis) {
        if (locked) {
            jedis.del(lockKey);
            locked = false;
        }
    }
}
