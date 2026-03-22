package com.feng.config;

import lombok.Data;

/**
 * RPC框架注册中心配置
 */
@Data
public class RegistryConfig {
    /**
     * 注册中心类型
     */
    private String register;
    /**
     * 注册中心地址
     */
    private String address;
    /**
     *
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 超时时间
     */
    private Long timeout = 10000L;
}
