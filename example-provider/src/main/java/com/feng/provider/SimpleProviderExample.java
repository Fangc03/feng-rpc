package com.feng.provider;


import com.feng.RpcApplication;
import com.feng.config.RegistryConfig;
import com.feng.config.RpcConfig;
import com.feng.model.ServiceMetaInfo;
import com.feng.registry.LocalRegistry;
import com.feng.registry.Registry;
import com.feng.registry.RegistryFactory;
import com.feng.server.HttpServer;
import com.feng.server.VertxHttpServer;
import com.feng.service.UserService;

/**
 * 简易服务提供者示例
 *
 */
public class SimpleProviderExample {

    public static void main(String[] args) {
        RpcApplication.init();
        // 注册服务
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        //注册服务到注册中心
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry instance = RegistryFactory.getInstance(registryConfig.getRegister());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(UserService.class.getName());
        serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
        serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
        try {
            instance.register(serviceMetaInfo);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        // 启动 web 服务
        HttpServer httpServer = new VertxHttpServer();

        httpServer.doStart(RpcApplication.getRpcConfig().getServerPort());
    }
}
