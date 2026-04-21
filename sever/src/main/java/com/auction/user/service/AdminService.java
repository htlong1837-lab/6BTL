package com.auction.user.service;

import com.auction.exception.UserException.UserException;
import com.auction.user.model.Admin;
import com.auction.user.model.User;

public class AdminService {
    private Admin admin;

    public void banUser(User target, boolean banned) throws UserException {

        // 1. Kiểm tra quyền
        if (!(admin instanceof Admin)) {
            System.out.println("Chỉ admin mới có quyền khóa tài khoản.");
        }

        // 2. Thực hiện khóa và mở khóa tài khoản
        target.setBanned(banned);
    }
    
}
