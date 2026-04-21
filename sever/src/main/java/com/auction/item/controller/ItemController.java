package com.auction.item.controller;

import java.util.List;
import com.auction.item.dao.ItemDAOImpl;
import com.auction.item.model.Product.Item;
import com.auction.item.service.ItemService;

public class ItemController {

    private ItemService itemService = new ItemService(new ItemDAOImpl());

    // Tạo sản phẩm mới
    public String createItem(Item item) {
        return itemService.addItem(item);
    }

    // Kiểm tra sản phẩm có được duyệt không
    public boolean isApproved(Item item) {
        return item.isApproved();
    }

    // Chỉnh sửa sản phẩm khi bị từ chối
    public String editItemError(Item item) {
        return itemService.updateItem(item);
    }

    // Lấy danh sách tất cả sản phẩm
    public List<Item> listAllItems() {
        return itemService.getAllItems();
    }

    // Lấy sản phẩm theo mã
    public Item getItem(String id) {
        return itemService.getItem(id);
    }

    // Xóa sản phẩm
    public String deleteItem(String id) {
        return itemService.deleteItem(id);
    }

    // In danh sách sản phẩm ra màn hình
    public void printAllItems(List<Item> items) {
        for (Item item : items) {
            System.out.println("Name: " + item.getName());
            System.out.println("Description: " + item.getDes());
            System.out.println("Starting Price: " + item.getStartPrice());
            System.out.println("Category: " + item.getCategory());
            System.out.println("Seller ID: " + item.getSellerId());
            System.out.println("---------------------------");
        }
    }
}
