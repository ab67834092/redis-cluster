package com.cjb.jedisLock;

import redis.clients.jedis.JedisCluster;

/**
 * Created by cjb on 2019/8/14.
 */
public class JedisLock {

    private JedisCluster jedisCluster;

    //锁路径 lock key path
    private String lockKey;


}
