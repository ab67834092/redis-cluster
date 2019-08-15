package com.cjb.jedisCluster;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.JedisCluster;

/**
 * Created by cjb on 2019/8/14.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class JedisClusterTest {

    @Autowired
    JedisCluster jedisCluster;

    @Test
    public void testString (){
        jedisCluster.set("name", "刘德华");
        System.out.println(jedisCluster.get("name"));
        String script = "if (redis.call('EXISTS', KEYS[1]) == 1) then return redis.call('del', KEYS[1]) else return 0 end";
        jedisCluster.eval(script,1,"name");
        System.out.println(jedisCluster.exists("name"));
    }
}
