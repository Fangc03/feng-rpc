package com.feng.examplespringbootprovider;


import com.feng.fengrpcspringbootstarter.annotation.RpcService;
import com.feng.model.User;
import com.feng.service.UserService;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类
 *
 */
@Service
@RpcService
public class UserServiceImpl implements UserService {

    public User getUser(User user) {
        System.out.println("用户名：" + user.getName());
        return user;
    }
}
