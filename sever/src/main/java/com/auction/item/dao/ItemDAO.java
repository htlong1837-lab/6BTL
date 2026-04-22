package com.auction.item.dao;
// Data access project

import java.util.List;

import com.auction.item.model.Product.Item;

public interface ItemDAO {
    void save(Item item);
    Item findById(String id);
    List<Item> findAll();
    List<Item> findBySellerId(String sellerId);
    void update(Item item);
    void delete(String id);
    
}
