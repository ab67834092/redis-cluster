package com.cjb.jedisCluster;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by cjb on 2019/8/14.
 */
@Configuration
public class JedisClusterConfig {

    @Value("${spring.redis.cluster.nodes}")
    private String clusterNodes;

    @Value("${spring.redis.timeout}")
    private int connectionTimeout;

    @Value("${redis.so_timeout}")
    private int soTimeout;

    @Value("${redis.max_attempts}")
    private int maxAttempts;

    @Value("${spring.redis.password}")
    private String password;

    @Value("${spring.redis.jedis.pool.max-active}")
    private int maxActive;

    @Value("${spring.redis.jedis.pool.max-wait}")
    private int maxWaitMillis;

    @Value("${spring.redis.jedis.pool.max-idle}")
    private int maxIdle;

    @Value("${spring.redis.jedis.pool.min-idle}")
    private int minIdle;



    @Bean
    public JedisCluster getJedisCluster() {

        String[] serverArray = clusterNodes.split(",");//获取服务器数组(这里要相信自己的输入，所以没有考虑空指针问题)
        Set<HostAndPort> nodes = new HashSet<>();

        for (String ipPort : serverArray) {
            String[] ipPortPair = ipPort.split(":");
            nodes.add(new HostAndPort(ipPortPair[0].trim(), Integer.valueOf(ipPortPair[1].trim())));
        }

        GenericObjectPoolConfig pool = new GenericObjectPoolConfig();
        pool.setMaxTotal(maxActive);
        pool.setMinIdle(minIdle);
        pool.setMaxIdle(maxIdle);
        pool.setMaxWaitMillis(maxWaitMillis);

        return new JedisCluster(nodes,connectionTimeout,soTimeout,maxAttempts,password,pool);
    }
}
