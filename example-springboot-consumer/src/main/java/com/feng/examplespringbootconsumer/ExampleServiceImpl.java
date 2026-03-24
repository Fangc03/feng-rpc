package com.feng.examplespringbootconsumer;


import com.feng.fengrpcspringbootstarter.annotation.RpcReference;
import com.feng.model.User;
import com.feng.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class ExampleServiceImpl {

    @RpcReference
    private UserService userService;

    public void test() {
        User user = new User();
        user.setName("faiz");
        User resultUser = userService.getUser(user);
        System.out.println(resultUser.getName());
    }

}
