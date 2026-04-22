package com.auction.user.dao;
//JDBC implementation

import com.auction.user.model.User;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UserDAOImpl implements UserDAO {

    // Đây là "database tạm" trong RAM
    private Map<String, User> usersByUsername = new HashMap<>();
    private Map<String, User> usersById = new HashMap<>();
    
    // ko đc trùng
    private Set<String> ids = new HashSet<>();
    private Set<String> usernames = new HashSet<>();
    private Set<String> emails = new HashSet<>();
    

    //=======================SAVE============================
    @Override
    public void save(User user) {
        
        usersByUsername.put(user.getName(), user); // key-value: username-user
        usersById.put(user.getId(), user);         // key-value: id-user

        ids.add(user.getId());
        usernames.add(user.getName());
        emails.add(user.getEmail());
    }


    //=======================CHECK EXISTENCE===========================

    //ktra xem có user chx
    @Override
    public boolean existsByUsername(String username) {
        return usernames.contains(username);
    }

    //ktra xem có email chx
    @Override
        public boolean existsByEmail(String email) {
            return emails.contains(email);
    }

    //ktra xem có id chx
    @Override
    public boolean existsById(String id) {
        return ids.contains(id);
    }

    //=======================FIND USER===========================

    //tìm bằng tên
    @Override
    public User findByUsername(String username) {
        return usersByUsername.get(username);
    }

    //tìm bằng ID
    @Override
    public User findById(String id) {
        return usersById.get(id);
    }


    // update user (chỉ dùng để cập nhật số lần đăng nhập thất bại và trạng thái bị ban)
    @Override
    public void update(User user) {
        usersByUsername.put(user.getName(), user);
        usersById.put(user.getId(), user);
    }
}
   

