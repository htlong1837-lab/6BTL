package com.auction.user.dao;

import com.auction.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDAOImpl implements UserDAO {

    private final Map<String, User> store = new HashMap<>();

    @Override
    public void save(User user) {
        store.put(user.getId(), user);
    }

    @Override
    public boolean existsById(String id) {
        return store.containsKey(id);
    }

    @Override
    public boolean existsByUsername(String username) {
        return store.values().stream()
            .anyMatch(u -> u.getName().equals(username));
    }

    @Override
    public User findByUsername(String username) {
        return store.values().stream()
            .filter(u -> u.getName().equals(username))
            .findFirst().orElse(null);
    }

    @Override
    public User findById(String id) {
        return store.get(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void update(User user) {
        store.put(user.getId(), user);
    }
}
