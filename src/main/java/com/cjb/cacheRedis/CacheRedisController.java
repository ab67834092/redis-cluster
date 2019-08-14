package com.cjb.cacheRedis;

import com.cjb.cacheRedis.service.CacheRedisService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 模拟获取某个用户的所有收益
 *
 * 测试缓存存取流程
 * Created by cjb on 2019/8/14.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CacheRedisController {

    @Autowired
    CacheRedisService cacheRedisService;

    @Test
    public void test (){
        while (true){
            try{
                System.out.println(Thread.currentThread().getName()+"发起了请求，获取本人的收益总金额为："+cacheRedisService.getUserMoneyCount("userCode:777"));
                Thread.currentThread().sleep(300);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
