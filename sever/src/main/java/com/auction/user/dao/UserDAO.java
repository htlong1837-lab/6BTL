package com.auction.user.dao;
//Interface

import com.auction.user.model.User;
// Lưu trữ và tìm kiếm users(database)
public interface UserDAO {
    void save(User user);                   // lưu user
    
    
    boolean existsById(String id);               // ktra xem có id tồn tại chx
    boolean existsByUsername(String username);  // ktra xem có user tồn tại chx
    boolean existsByEmail(String email);        // ktra xem có email tồn tại chx
    

    User findByUsername(String username);   // tìm user theo tên
    User findById(String id);               // tìm user theo ID
}
    
    