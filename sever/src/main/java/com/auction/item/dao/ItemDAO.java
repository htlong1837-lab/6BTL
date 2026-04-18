package com.auction.item.dao;
// Data access project

import com.auction.item.model.Product.Item;
import java.util.List;

public interface ItemDAO {
    void save(Item item);
    Item findById(String id);
    List<Item> findAll();
    List<Item> findBySellerId(String sellerId);
    void update(Item item);
    void delete(String id);
    
}
