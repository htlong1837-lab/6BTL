package com.auction.user.controller;
import com.auction.user.dao.UserDAOImpl;
import com.auction.user.model.User;
import com.auction.user.service.UserService;


public class UserController {
    private UserService userService=new UserService(new UserDAOImpl());
    //tạo tài khoản
<<<<<<< HEAD
    public boolean createAccount(User user)  {
        try {
            userService.signUp(user.getName(), user.getEmail(), user.getPasswordHash(), user.getPasswordHash());
            return true;
        } catch (Exception e) {
            System.out.println("Lỗi đăng ký tài khoản: " + e.getMessage());
            return false;
        }
        
    }
    //Đăng nhập
    public boolean LoginAccount(User user)  {
        try {
            userService.login(user.getName(), user.getPasswordHash());
            return true;
        } catch (Exception e) {
            System.out.println("Lỗi đăng nhập tài khoản: " + e.getMessage());
            return false;
        }
        
    } 
    
=======
    public User createAccount(User user) {
        return userService.signUp(user);
    }
    //Đăng nhập
    public void LoginAccount(User user) {
        return userService.login(user);
    }
>>>>>>> a92f1575ebec13298e3bc674bc90ca4aea7778ba

    

}
