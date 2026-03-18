package com.feng.provider;


import com.feng.RpcApplication;
import com.feng.registry.LocalRegistry;
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

        // 启动 web 服务
        HttpServer httpServer = new VertxHttpServer();

        httpServer.doStart(8080);
    }
}
