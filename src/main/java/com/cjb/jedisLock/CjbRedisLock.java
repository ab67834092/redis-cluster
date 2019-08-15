package com.cjb.jedisLock;

import org.apache.tomcat.jni.Thread;
import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisCluster;

import java.util.concurrent.TimeUnit;

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

    //自旋的间隔时间
    private int intervalTime=1000;

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
        //自旋等待
        while(time>0){
            //锁的过期时间
            String expiresTimeStr = String.valueOf(System.currentTimeMillis() + expireTime+1);
            if(jedisCluster.setnx(lockKey,expiresTimeStr)==1L){
                return true;
            }
            //当前lockKey的过期时间
            String currentExpiresTimeStr = jedisCluster.get(lockKey);
            if(!StringUtils.isEmpty(currentExpiresTimeStr) && Long.parseLong(currentExpiresTimeStr)<System.currentTimeMillis()){
                System.out.println("我进来了1111111111111！！！=========================");
                String oldExpiresTimeStr = jedisCluster.getSet(lockKey, expiresTimeStr);
                if(!StringUtils.isEmpty(oldExpiresTimeStr) && currentExpiresTimeStr.equals(oldExpiresTimeStr)){
                    System.out.println("我进来了！！！=========================");
                    return true;
                }
            }
            time-=intervalTime;
            try {
                TimeUnit.MILLISECONDS.sleep(intervalTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public synchronized void unlock(){
        jedisCluster.del(lockKey);
    }
}
