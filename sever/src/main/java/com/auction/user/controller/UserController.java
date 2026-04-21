package com.auction.user.controller;
import com.auction.user.dao.UserDAOImpl;
import com.auction.user.model.User;
import com.auction.user.service.UserService;


public class UserController {
    private UserService userService=new UserService(new UserDAOImpl());
    //tạo tài khoản
    public User createAccount(User user) {
        return userService.signUp(user);
    }
    //Đăng nhập
    public void LoginAccount(User user) {
        return userService.login(user);
    }

    

}
