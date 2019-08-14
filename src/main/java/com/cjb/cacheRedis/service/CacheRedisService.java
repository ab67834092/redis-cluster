package com.cjb.cacheRedis.service;

import java.math.BigDecimal;

/**
 * Created by cjb on 2019/8/14.
 */
public interface CacheRedisService {

    /**
     * 模拟获取某个用户所有金钱收益
     * 假设明细表中某个人的收益有几百条记录，如果从数据库中sum会慢
     * 所以先从缓存中取，缓存中有直接返回，如果没有，则去数据库中取，再缓存到redis中
     * @param userCode
     * @return
     */
    BigDecimal getUserMoneyCount(String userCode);
}
