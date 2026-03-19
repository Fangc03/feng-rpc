package com.feng.consumer;


import com.feng.config.RpcConfig;
import com.feng.model.User;
import com.feng.proxy.ServiceProxyFactory;
import com.feng.service.UserService;
import com.feng.utils.ConfigUtils;

/**
 * 简易服务消费者示例
 *
 */
public class SimpleConsumerExample {

    public static void main(String[] args) {

        RpcConfig rpc = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
        System.out.println(rpc);

        // 静态代理
//        UserService userService = new UserServiceProxy();
        // 动态代理
        UserService userService = ServiceProxyFactory.getProxy(UserService.class);
        User user = new User();
        user.setName("faiz");
        // 调用
        User newUser = userService.getUser(user);
        if (newUser != null) {
            System.out.println(newUser.getName());
        } else {
            System.out.println("user == null");
        }
        short number = userService.getNumber();
        System.out.println(number);
    }
}
