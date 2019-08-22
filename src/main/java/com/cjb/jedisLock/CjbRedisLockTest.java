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
        final CountDownLatch begin = new CountDownLatch(1);
        final CountDownLatch end = new CountDownLatch(num);
        for(int i=1;i<=num;i++){
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    CjbRedisLock cjbRedisLock = null;
                    try {
                        begin.await();
                        cjbRedisLock = new CjbRedisLock(jedisCluster,"994");
                        System.out.println(jedisCluster);
                        if(cjbRedisLock.lock()){
                            //业务代码
                            System.out.println("只有我获取到了");
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }finally {
                        if(cjbRedisLock!=null){
                            cjbRedisLock.unlock();
                        }
                        end.countDown();
                    }
                }
            };
            executorService.submit(runnable);
        }
        try {
            System.out.println("开始执行...");
            begin.countDown();
            end.await();
            System.out.println("所有的都执行完了");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            executorService.shutdown();
        }
    }
}
