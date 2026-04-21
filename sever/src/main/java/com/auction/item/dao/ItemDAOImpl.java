package com.auction.item.dao;

import com.auction.item.model.Product.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemDAOImpl implements ItemDAO {

    private Map<String, Item> items = new HashMap<>();

    @Override
    public void save(Item item) {
        items.put(item.getId(), item);
    }

    @Override
    public Item findById(String id) {
        return items.get(id);
    }

    @Override
    public List<Item> findAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public List<Item> findBySellerId(String sellerId) {
        List<Item> result = new ArrayList<>();
        for (Item item : items.values()) {
            if (item.getSellerId().equals(sellerId)) {
                result.add(item);
            }
        }
        return result;
    }

    @Override
    public void update(Item item) {
        items.put(item.getId(), item);
    }

    @Override
    public void delete(String id) {
        items.remove(id);
    }
}
