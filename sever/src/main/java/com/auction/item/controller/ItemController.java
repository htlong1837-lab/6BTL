package com.auction.item.controller;
import java.util.List;
import com.auction.item.model.Product.Item;
import com.auction.item.service.ItemService;




public class ItemController {
    private ItemService itemService=new ItemService();
    //tạo item
    public Item createItem(Item item) {
        return itemService.createItem(item);
    }
    // Duyệt Item hợp lệ
    public boolean isApproved( Item item) {
        return itemService.isApproved(item);
    }
    // Chỉnh sửa Item khi duyệt lỗi
    public void editItemError(Item item) {
        itemService.editItemError(item);
    }       
    //Lập List Item để Admin Thêm/sửa/xóa
    public void listAllItems(List<Item> items) {
        itemService.listAllItems(items);
    }
    //in ra List Item
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
