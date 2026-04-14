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
    private Set<String> emails = new HashSet<>();

    @Override
    public void save(User user) {
        usersByUsername.put(user.getName(), user);
        emails.add(user.getEmail());
    }

    @Override
    public User findByUsername(String username) {
        return usersByUsername.get(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return emails.contains(email);
    }
}