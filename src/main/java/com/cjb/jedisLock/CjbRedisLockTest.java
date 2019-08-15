package com.cjb.jedisLock;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.NumberUtils;
import redis.clients.jedis.JedisCluster;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by chenjiabao on 2019/8/15.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CjbRedisLockTest {

    @Autowired
    JedisCluster jedisCluster;

    @Test
    public void testString (){
        ExecutorService executorService = Executors.newCachedThreadPool();
        int num = 100;
        final CountDownLatch latch = new CountDownLatch(num);
        for(int i=1;i<=num;i++){
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    CjbRedisLock cjbRedisLock = new CjbRedisLock(jedisCluster,"994");
                    System.out.println(jedisCluster);
                    try {
                        if(cjbRedisLock.lock()){
                            //业务代码
//                            System.out.println("只有我获取到了");
                        }
                        latch.countDown();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    //如果注释掉说明没有释放锁，那么就是死锁
                    finally {
                        cjbRedisLock.unlock();
                    }
                }
            });
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
