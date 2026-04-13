package com.auction.user.dao;

import com.auction.user.model.User;

//Interface

public interface UserDAO {
    void save(User user);
    User findByUsername(String username);
    boolean existsByEmail(String email);
}
    