package com.auction.user.dao;
//Interface

import com.auction.user.model.User;
// Lưu trữ và tìm kiếm users(database)
public interface UserDAO {
    void save(User user);                   // lưu user
    User findByUsername(String username);   // tìm user theo tên
    boolean existsByEmail(String email);    // ktra xem co email tồn tại chx
}
    