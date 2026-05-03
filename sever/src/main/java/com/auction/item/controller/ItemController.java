package com.auction.item.controller;

import java.util.List;
// [SỬA] Dùng ItemDAOSQLiteImpl thay vì ItemDAOImpl để dữ liệu sản phẩm được lưu vào DB thực sự
import com.auction.item.dao.ItemDAOSQLiteImpl;
import com.auction.item.model.Product.Item;
import com.auction.item.service.ItemService;

public class ItemController {

    // [SỬA] Dùng SQLite DAO thay vì in-memory DAO - sản phẩm tồn tại sau khi server restart
    private final ItemService itemService = new ItemService(new ItemDAOSQLiteImpl());

    // Tạo sản phẩm mới - trả về message kết quả
    public String createItem(Item item) {
        return itemService.addItem(item);
    }

    // Kiểm tra sản phẩm có hợp lệ để đấu giá không
    public boolean isApproved(Item item) {
        return item.isApproved();
    }

    // Chỉnh sửa sản phẩm khi bị từ chối duyệt
    public String editItem(Item item) {
        return itemService.updateItem(item);
    }

    // Lấy danh sách tất cả sản phẩm
    public List<Item> listAllItems() {
        return itemService.getAllItems();
    }

    // [THÊM] Lấy sản phẩm của một Seller cụ thể - cần cho màn hình quản lý sản phẩm của Seller
    public List<Item> listItemsBySeller(String sellerId) {
        return itemService.getItemsBySeller(sellerId);
    }

    // Lấy sản phẩm theo mã
    public Item getItem(String id) {
        return itemService.getItem(id);
    }

    // Xóa sản phẩm theo mã
    public String deleteItem(String id) {
        return itemService.deleteItem(id);
    }

    // In danh sách sản phẩm ra màn hình
    public void printAllItems(List<Item> items) {
        for (Item item : items) {
            System.out.println("Name: "          + item.getName());
            System.out.println("Description: "   + item.getDes());
            System.out.println("Starting Price: " + item.getStartPrice());
            System.out.println("Category: "      + item.getCategory());
            System.out.println("Seller ID: "     + item.getSellerId());
            System.out.println("---------------------------");
        }
    }
}
