package com.auction.item.service;
import com.auction.item.dao.ItemDAO;
import com.auction.item.model.Product.Item;

import java.util.List;

public class ItemService {

    private final ItemDAO itemDAO;

    public ItemService(ItemDAO itemDAO) {
        this.itemDAO = itemDAO;
    }

    public String addItem(Item item) {
        if (!item.isApproved())
            return "Sản phẩm không hợp lệ. Vui lòng kiểm tra lại thông tin.";
        itemDAO.save(item);
        return "Thêm sản phẩm thành công: " + item.getName();
    }

    public Item getItem(String id) {
        return itemDAO.findById(id);
    }

    public List<Item> getAllItems() {
        return itemDAO.findAll();
    }

    public List<Item> getItemsBySeller(String sellerId) {
        return itemDAO.findBySellerId(sellerId);
    }

    public String updateItem(Item item) {
        if (itemDAO.findById(item.getId()) == null)
            return "Sản phẩm không tồn tại.";
        itemDAO.update(item);
        return "Cập nhật sản phẩm thành công: " + item.getName();
    }

    public String deleteItem(String id) {
        if (itemDAO.findById(id) == null)
            return "Sản phẩm không tồn tại.";
        itemDAO.delete(id);
        return "Đã xóa sản phẩm.";
    }
}
