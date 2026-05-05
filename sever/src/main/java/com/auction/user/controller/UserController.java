package com.auction.user.controller;

import com.auction.user.dao.UserDAOSQLiteImpl;
import com.auction.user.model.Seller;
import com.auction.user.model.User;
import com.auction.user.service.UserService;
import com.auction.exception.UserException.UserException;

public class UserController {

    private final UserService userService = new UserService(new UserDAOSQLiteImpl());
    private final UserDAOSQLiteImpl userDAO = new UserDAOSQLiteImpl();

    public boolean createAccount(String id, String username,
                                  String password, String confirmPassword, String role) {
        try {
            userService.signUp(id, username, password, confirmPassword, role);
            return true;
        } catch (Exception e) {
            System.out.println("[UserController] Lỗi đăng ký: " + e.getMessage());
            return false;
        }
    }

    public boolean registerAsSeller(String username, String shopName) {
        User user = userDAO.findByUsername(username);
        if (user == null || user instanceof Seller) return false;
        Seller seller = new Seller(user.getId(), user.getName(), user.getPasswordHash(), "SELLER");
        seller.setShopName(shopName);
        seller.setBalance(user.getBalance());
        userDAO.update(seller);
        return true;
    }

    public User loginAccount(String username, String password) {
        try {
            return userService.login(username, password);
        } catch (UserException e) {
            System.out.println("[UserController] Lỗi đăng nhập: " + e.getMessage());
            return null;
        }
    }
}
