package com.auction.user.controller;

import com.auction.user.dao.UserDAOSQLiteImpl;
import com.auction.user.model.User;
import com.auction.user.service.UserService;
import com.auction.exception.UserException.UserException;

public class UserController {

    private final UserDAOSQLiteImpl userDAO = new UserDAOSQLiteImpl();
    private final UserService userService = new UserService(userDAO);

    public String createAccount(String id, String username,
                                String password, String confirmPassword, String role) {
        try {
            userService.signUp(id, username, password, confirmPassword, role);
            return null; // null = thành công
        } catch (Exception e) {
            System.out.println("[UserController] Lỗi đăng ký: " + e.getMessage());
            return e.getMessage(); // trả về lý do lỗi
        }
    }

    //[Xoá register Seller]

    public User loginAccount(String username, String password) {
        try {
            return userService.login(username, password);
        } catch (UserException e) {
            System.out.println("[UserController] Lỗi đăng nhập: " + e.getMessage());
            return null;
        }
    }
}
