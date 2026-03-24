package com.feng.fengrpcspringbootstarter.annotation;


import com.feng.fengrpcspringbootstarter.bootstrap.RpcConsumerBootstrap;
import com.feng.fengrpcspringbootstarter.bootstrap.RpcInitBootstrap;
import com.feng.fengrpcspringbootstarter.bootstrap.RpcProviderBootstrap;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import({RpcInitBootstrap.class, RpcConsumerBootstrap.class, RpcProviderBootstrap.class})
public @interface EnableRpc {
    /**
     * 需要启动server
     * @return
     */
    boolean needServer() default true;
}
