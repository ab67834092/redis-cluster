package com.cjb.cacheRedis.service.impl;

import com.cjb.cacheRedis.service.CacheRedisService;
import com.cjb.jedisLock.BusinessLocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.params.SetParams;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by cjb on 2019/8/14.
 */
@Service
public class CacheRedisServiceImpl implements CacheRedisService {

    @Autowired
    JedisCluster jedisCluster;

    @Autowired
    BusinessLocks businessLocks;
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
     * 1.数据实时同步失效，
     * 强一致性，数据库更新数据后主动淘汰缓存，
     * 为避免缓存雪崩，更新缓存的过程需要锁控制，同一时间只允许一个请求访问数据库
     * 为了保证数据一致性，还要加上缓存失效时间(如果更新数据库成功，清除缓存失败，这时就出问题了)
     * 2.数据准实时更新
     * 准一致性，更新数据库后，异步更新缓存，使用多线程或mq
     * 3.任务调度更新
     * 最终一致性，采用任务调度，按照一定频率
     */
    @Override
    public BigDecimal getUserMoneyCount(String userCode){
        //1.从缓存中获取数据
        String money = jedisCluster.get(userCode);
        //2.如果缓存中有数据，直接返回
        if(!StringUtils.isEmpty(money)){
            return new BigDecimal(money);
        }

        BigDecimal dbMoney = null;
        //加锁，防止缓存雪崩
        if(businessLocks.acquireLock("lock:"+userCode)){
            try{
                //3.再从缓存拿到一次，避免等待锁的阻塞线程发生羊群效应都去走一遍数据库
                String cacheMoney = jedisCluster.get(userCode);
                if(!StringUtils.isEmpty(cacheMoney)){
                    return new BigDecimal(cacheMoney);
                }

                //4.如果缓存中没有数据从数据库中加载数据
                //这里防止缓存雪崩，需要做同步锁控制
                dbMoney = getUserMoneyCountFromDb(userCode);
                //5.如果缓存中没有数据，则把数据放到缓存中，方便下次查询
                if(dbMoney!=null){
                    jedisCluster.set(userCode,dbMoney.toString(), SetParams.setParams().ex(60));
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                businessLocks.releaseLock("lock:"+userCode);
            }
        }
        return dbMoney;
    }


    @Override
    public void insertMoneyCount(String userCode, String addMoney) {
        //更新数据库
        jedisCluster.incrByFloat("mysqlDB:"+userCode,Double.parseDouble(addMoney));
        //淘汰缓存
        jedisCluster.del(userCode);
    }

    @Override
    public void flushRedis() {
        //jedisCluster.
    }

    /**
     * 模拟数据库中查询数据,因为没有链接MySQL，所以拿redis假设数据库
     * @param userCode
     * @return
     */
    private BigDecimal getUserMoneyCountFromDb(String userCode){
        System.out.println("第一次从数据库中查询,耗费5秒，比较慢！");
        try {
            //模拟耗费2秒，比较慢
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String result = jedisCluster.get("mysqlDB:" + userCode);
        if(StringUtils.isEmpty(result)){
            return new BigDecimal(0);
        }
        return new BigDecimal(result);
    }
}
