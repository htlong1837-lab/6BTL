package main.java.com.auction.user.dao;
//Interface

public interface UserDAO {
    void save(User user);
    User findByUsername(String username);
    boolean existsByEmail(String email);
}
    