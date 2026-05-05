package com.auction.user.dao;
import com.auction.user.model.User;
public interface UserDAO {
    void save(User user);                   // lưu user
    
    
    boolean existsById(String id);
    boolean existsByUsername(String username); 

    User findByUsername(String username); 
    User findById(String id);  


    void update(User user);
}
    
    