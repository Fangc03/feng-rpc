package com.feng.config;

import com.feng.loadbalancer.LoadBalancerKeys;
import com.feng.retry.RetryStrategyKeys;
import com.feng.serializer.SerializerKeys;
import lombok.Data;

/**
 * RPC框架配置
 */
@Data
public class RpcConfig {
    /**
     * 服务名称
     */
    private String name="feng-rpc";
    /**
     * 服务版本
     */
    private String version="1.0.0";
    /**
     * 服务地址
     */
    private  String serverHost="127.0.0.1";
    /**
     * 服务端口
     */
    private  int serverPort=8080;
    /**
     * 模拟调用
     */
    private  boolean mock = false;
    /**
     * 序列化器
     */
    private String serializer = SerializerKeys.JDK;
    /**
     * 注册中心
     */
    private RegistryConfig registryConfig = new RegistryConfig();
    /**
     * 负载均衡器
     */
    private String loadBalancer = LoadBalancerKeys.ROUND_ROBIN;
    /**
     * 重试策略
     */
    private String retryStrategy = RetryStrategyKeys.NO;
}
