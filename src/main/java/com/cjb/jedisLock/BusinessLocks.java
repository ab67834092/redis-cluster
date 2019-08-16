package com.cjb.jedisLock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.annotation.Commit;
import redis.clients.jedis.JedisCluster;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by chenjiabao on 2019/8/16.
 */
@Component
public class BusinessLocks {

    @Autowired
    JedisCluster jedisCluster;

    /**
     * 用户收益锁
     */
    public static final ConcurrentHashMap<String,CjbRedisLock> userMoneyCountLocks = new ConcurrentHashMap<>();

    public boolean acquireLock(String lockKey){

        if(userMoneyCountLocks.containsKey(lockKey)){
            return userMoneyCountLocks.get(lockKey).lock();
        }

        CjbRedisLock lock = new CjbRedisLock(jedisCluster,lockKey);
        userMoneyCountLocks.put(lockKey,lock);
        return lock.lock();

    }

    public void releaseLock(String lockKey){
        if(userMoneyCountLocks.containsKey(lockKey)){
            userMoneyCountLocks.get(lockKey).unlock();
        }
    }
}
