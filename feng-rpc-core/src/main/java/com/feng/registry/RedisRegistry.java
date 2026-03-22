package com.feng.registry;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.feng.config.RegistryConfig;
import com.feng.model.ServiceMetaInfo;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.*;
import java.util.stream.Collectors;

public class RedisRegistry implements Registry {

    private JedisPool jedisPool;

    private final Set<String> localRegisterNodeKeySet = new HashSet<>();

    private static final String RPC_ROOT_PATH = "rpc/";

    private static final int DEFAULT_EXPIRE_SECONDS = 30;

    @Override
    public void init(RegistryConfig registryConfig) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(10);
        poolConfig.setMaxIdle(5);
        poolConfig.setMinIdle(1);
        poolConfig.setMaxWaitMillis(3000);

        String address = registryConfig.getAddress();
        String[] hostAndPort = parseAddress(address);
        String host = hostAndPort[0];
        int port = Integer.parseInt(hostAndPort[1]);

        if (registryConfig.getPassword() != null && !registryConfig.getPassword().isEmpty()) {
            jedisPool = new JedisPool(poolConfig, host, port, registryConfig.getTimeout().intValue(), registryConfig.getPassword());
        } else {
            jedisPool = new JedisPool(poolConfig, host, port, registryConfig.getTimeout().intValue());
        }
        heartBeat();
    }

    private String[] parseAddress(String address) {
        String url = address;
        if (url.startsWith("redis://")) {
            url = url.substring(8);
        } else if (url.startsWith("http://")) {
            url = url.substring(7);
        }
        String[] parts = url.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid Redis address format: " + address);
        }
        return parts;
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        String registerKey = RPC_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        String value = JSONUtil.toJsonStr(serviceMetaInfo);

        try (Jedis jedis = jedisPool.getResource()) {
            jedis.setex(registerKey, DEFAULT_EXPIRE_SECONDS, value);
            localRegisterNodeKeySet.add(registerKey);
        }
    }

    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        String registerKey = RPC_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();

        try (Jedis jedis = jedisPool.getResource()) {
            jedis.del(registerKey);
            localRegisterNodeKeySet.remove(registerKey);
        }
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {
        String searchPrefix = RPC_ROOT_PATH + serviceKey + "/";

        try (Jedis jedis = jedisPool.getResource()) {
            Set<String> keys = jedis.keys(searchPrefix + "*");
            if (CollUtil.isEmpty(keys)) {
                return Collections.emptyList();
            }

            return keys.stream()
                    .map(key -> {
                        String value = jedis.get(key);
                        return JSONUtil.toBean(value, ServiceMetaInfo.class);
                    })
                    .collect(Collectors.toList());
        }
    }

    @Override
    public void destroy() {
        System.out.println("当前节点下线");
        try (Jedis jedis = jedisPool.getResource()) {
            for (String key : localRegisterNodeKeySet) {
                try {
                    jedis.del(key);
                } catch (Exception e) {
                    throw new RuntimeException(key + "下线失败", e);
                }
            }
        }
        if (jedisPool != null) {
            jedisPool.close();
        }
    }

    @Override
    public void heartBeat() {
        CronUtil.schedule("*/10 * * * * *", new Task() {
            @Override
            public void execute() {
                System.out.println("开始执行心跳");
                for (String key : localRegisterNodeKeySet) {
                    try (Jedis jedis = jedisPool.getResource()) {
                        String value = jedis.get(key);
                        if (value == null) {
                            continue;
                        }
                        ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(value, ServiceMetaInfo.class);
                        register(serviceMetaInfo);
                    } catch (Exception e) {
                        throw new RuntimeException(key + "续签失败", e);
                    }
                }
            }
        });

        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }
}
