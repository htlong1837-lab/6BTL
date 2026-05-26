package com.auction.item.controller;

import java.util.List;
// [SỬA] Dùng ItemDAOSQLiteImpl thay vì ItemDAOImpl để dữ liệu sản phẩm được lưu vào DB thực sự
import com.auction.item.dao.ItemDAOSQLiteImpl;
import com.auction.item.model.Product.Item;
import com.auction.item.service.ItemService;

public class ItemController {

    // [SỬA] Dùng SQLite DAO thay vì in-memory DAO(tức db lưu trong RAM) - sản phẩm tồn tại sau khi server restart
    private final ItemService itemService = new ItemService(new ItemDAOSQLiteImpl());

    // Tạo sản phẩm mới - trả về message kết quả
    public String createItem(Item item) {
        return itemService.addItem(item);
    }

    // Chỉnh sửa sản phẩm
    public String editItem(Item item) {
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

    // Xóa sản phẩm theo mã
    public String deleteItem(String id) {
        return itemService.deleteItem(id);
    }

}
