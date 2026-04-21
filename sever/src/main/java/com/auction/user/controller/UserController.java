package com.auction.user.controller;
import com.auction.user.dao.UserDAOImpl;
import com.auction.user.model.User;
import com.auction.user.service.UserService;


public class UserController {
    private UserService userService=new UserService(new UserDAOImpl());
    //tạo tài khoản
    public void createAccount(User user) throws Exception {
        userService.signUp(user.getName(), user.getEmail(), user.getPasswordHash(), user.getPasswordHash());
        
    }
    //Đăng nhập
    public void LoginAccount(User user) throws Exception {
        userService.login(user.getName(), user.getPasswordHash());
           
    } 
    

    

}
