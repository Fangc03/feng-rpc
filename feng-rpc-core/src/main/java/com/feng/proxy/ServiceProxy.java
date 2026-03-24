package com.feng.proxy;


import cn.hutool.core.collection.CollUtil;
import com.feng.RpcApplication;
import com.feng.config.RpcConfig;
import com.feng.constant.RpcConstant;
import com.feng.fault.tolerant.TolerantStrategy;
import com.feng.fault.tolerant.TolerantStrategyFactory;
import com.feng.loadbalancer.LoadBalancer;
import com.feng.loadbalancer.LoadBalancerFactory;
import com.feng.model.RpcRequest;
import com.feng.model.RpcResponse;
import com.feng.model.ServiceMetaInfo;
import com.feng.registry.Registry;
import com.feng.registry.RegistryFactory;
import com.feng.retry.RetryStrategy;
import com.feng.retry.RetryStrategyFactory;
import com.feng.serializer.Serializer;
import com.feng.serializer.SerializerFactory;
import com.feng.server.tcp.VertxTcpClient;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 服务代理（JDK 动态代理）
 */
public class ServiceProxy implements InvocationHandler {

    /**
     * 调用代理
     *
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 指定序列化器
        final Serializer serializer = SerializerFactory.getInstance(RpcApplication.getRpcConfig().getSerializer());

        // 构造请求
        RpcRequest rpcRequest = RpcRequest.builder()
                .serviceName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .args(args)
                .build();
        try {
            // 序列化
            byte[] bodyBytes = serializer.serialize(rpcRequest);
            // 发送请求
            // 注意，这里地址被硬编码了（需要使用注册中心和服务发现机制解决）
            RpcConfig rpcConfig = RpcApplication.getRpcConfig();
            Registry registry = RegistryFactory.getInstance(rpcConfig.getRegistryConfig().getRegister());
            ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
            serviceMetaInfo.setServiceName(method.getDeclaringClass().getName());
            serviceMetaInfo.setServiceVersion(RpcConstant.DEFAULT_SERVICE_VERSION);
            List<ServiceMetaInfo> serviceMetaInfoList = registry.serviceDiscovery(serviceMetaInfo.getServiceKey());
            if (CollUtil.isEmpty(serviceMetaInfoList)) {
                throw new RuntimeException("No service found");
            }
            LoadBalancer loadBalancer = LoadBalancerFactory.getInstance(RpcApplication.getRpcConfig().getLoadBalancer());
            Map<String, Object> requestParams = new HashMap<>();
            requestParams.put("methodName", rpcRequest.getMethodName());
            ServiceMetaInfo selectServiceMetaInfo = loadBalancer.select(requestParams, serviceMetaInfoList);
            //发送TCP请求
            //重试机制
            RetryStrategy retryStrategy = RetryStrategyFactory.getInstance(RpcApplication.getRpcConfig().getRetryStrategy());
            RpcResponse rpcResponse = retryStrategy.doRetry(() ->
                    VertxTcpClient.doRequest(rpcRequest, selectServiceMetaInfo)
            );
            return rpcResponse.getData();
        } catch (IOException e) {
            TolerantStrategy tolerantStrategy = TolerantStrategyFactory.getInstance(RpcApplication.getRpcConfig().getTolerantStrategy());
            tolerantStrategy.doTolerant(null, e);
        }

        return null;
    }
}
