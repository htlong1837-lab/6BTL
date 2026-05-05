package com.auction.user.dao;

import com.auction.user.model.User;
import java.util.HashMap;
import java.util.Map;

public class UserDAOImpl implements UserDAO {

    private final Map<String, User> usersByUsername = new HashMap<>();
    private final Map<String, User> usersById       = new HashMap<>();

    @Override
    public void save(User user) {
        usersByUsername.put(user.getName(), user);
        usersById.put(user.getId(), user);
    }

    @Override
    public boolean existsByUsername(String username) {
        return usersByUsername.containsKey(username);
    }

    @Override
    public boolean existsById(String id) {
        return usersById.containsKey(id);
    }

    @Override
    public User findByUsername(String username) {
        return usersByUsername.get(username);
    }

    @Override
    public User findById(String id) {
        return usersById.get(id);
    }

    @Override
    public void update(User user) {
        usersByUsername.put(user.getName(), user);
        usersById.put(user.getId(), user);
    }
}
