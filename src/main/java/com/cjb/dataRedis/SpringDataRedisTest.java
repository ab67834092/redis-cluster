package com.cjb.dataRedis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by cjb on 2019/8/12.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringDataRedisTest {

    @Autowired
    RedisTemplate redisTemplate;

    @Test
    public void testString (){
        redisTemplate.opsForValue().set("name", "丁洁");
        System.out.println(redisTemplate.opsForValue().get("name"));
    }
}
