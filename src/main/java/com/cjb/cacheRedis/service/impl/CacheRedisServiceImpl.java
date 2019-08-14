package com.cjb.cacheRedis.service.impl;

import com.cjb.cacheRedis.service.CacheRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisCluster;

import java.math.BigDecimal;

/**
 * Created by cjb on 2019/8/14.
 */
@Service
public class CacheRedisServiceImpl implements CacheRedisService {

    @Autowired
    JedisCluster jedisCluster;
    /**
     * 模拟获取某个用户所有金钱收益
     * 假设明细表中某个人的收益有几百条记录，如果从数据库中sum会慢
     * 所以先从缓存中取，缓存中有直接返回，如果没有，则去数据库中取，再缓存到redis中
     * @param userCode
     * @return
     */
    /**
     * 问题1：假设其他客户端，修改了数据库中的值，这要会导致库中与缓存中数据不一致？
     * 解决方法：
     * 1.数据实时同步失效，强一致性，数据库更新数据后淘汰缓存，
     * 为避免缓存雪崩，更新缓存的过程需要锁控制，同一时间只允许一个请求访问数据库
     * 2.
     */
    @Override
    public BigDecimal getUserMoneyCount(String userCode){
        //1.从缓存中获取数据
        String money = jedisCluster.get(userCode);
        //2.如果缓存中有数据，直接返回
        if(!StringUtils.isEmpty(money)){
            return new BigDecimal(money);
        }
        //3.如果缓存中没有数据从数据库中加载数据
        BigDecimal dbMoney = getUserMoneyCountFromDb(userCode);
        //4.如果缓存中没有数据，则把数据放到缓存中，方便下次查询
        if(dbMoney!=null){
            jedisCluster.set(userCode,dbMoney.toString());
        }
        return dbMoney;
    }

    /**
     * 模拟数据库中查询数据
     * @param userCode
     * @return
     */
    private BigDecimal getUserMoneyCountFromDb(String userCode){
        System.out.println("第一次从数据库中查询,耗费2秒，比较慢！");
        try {
            //模拟耗费2秒，比较慢
            Thread.currentThread().sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new BigDecimal("2144.50");
    }
}
