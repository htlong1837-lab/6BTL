package com.auction.item.dao;
import com.auction.item.model.Product.Item;
import com.auction.item.model.Factory.ItemFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
 
public class ItemDAOImpl implements ItemDAO {
    private Map<String, Item> items = new HashMap<>();
    //save
    @Override
    public void save(Item item) {
        items.put(item.getId(), item);
    }
    //find by ID
    @Override
    public Item findById(String id) {
        return items.get(id);
    }       
    //find all
    @Override
    public void findAll(List<Item> items)  {
        items.clear();
        items.addAll(this.items.values());
        items.printListAllItem();
        
    }

    //find by seller ID
    @Override
    public void findBySellerId(String sellerId, List<Item> items) {
        items.clear();
        for (Item item : this.items.values()) {
            if (item.getSellerId().equals(sellerId)) {
                items.add(item);
            }
        }
    }
    //update
    @Override
    public void update(Item item) {
        items.put(item.getId(), item);
    }
    //delete
    @Override
    public void delete(String id) {
        items.remove(id);
    }


}
