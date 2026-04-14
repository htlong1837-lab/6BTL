package com.auction.item.service;

import java.util.List;

import com.auction.item.model.Product.Item;

// CRUD items, validate
public class ItemService extends Item {

    // Duyệt Item hợp lệ
    public boolean isApproved() {
        if (this.name != null && !this.name.isEmpty() &&
            this.des != null && !this.des.isEmpty() &&
            this.startPrice > 0 && this.category != null && !this.category.isEmpty() && this.sellerId != null && !this.sellerId.isEmpty()) {
            System.out.println("[Admin] Item \"" + this.name + "\" has been approved and is ready for auction.");
            return true;
        } else if (this.name == null || this.name.isEmpty() ) {
            System.out.println("[Admin] Item \"" + this.name + "\" is invalid and cannot be added to the auction. Please provide a valid name.");
        } else if (this.des == null || this.des.isEmpty()) {
            System.out.println("[Admin] Item \"" + this.des + " of " + this.name + "\" is invalid and cannot be added to the auction. Please provide a valid description.");
        } else if (this.startPrice <= 0) {
            System.out.println("[Admin] Item \"" + this.startPrice + " of "+ this.name + "\" is invalid and cannot be added to the auction. Please provide a valid starting price.");
        } else if (this.category == null || this.category.isEmpty()) {
            System.out.println("[Admin] Item \"" + this.category + " of " + this.name + "\" is invalid and cannot be added to the auction. Please provide a valid category.");
        } else if (this.sellerId == null || this.sellerId.isEmpty()) {
            System.out.println("[Admin] Item \"" + this.sellerId + " of " + this.name + "\" is invalid and cannot be added to the auction. Please provide a valid seller ID.");
        }
        return false;
    }
    // Chỉnh sửa Item khi duyệt lỗi
    public void editItemError(Item item) {
        if (!item.isApproved()){
            this.name = name;
            this.des = des;
            this.startPrice = startPrice;
            this.category = category;
            this.sellerId = sellerId;
            System.out.println("[Admin] Item \"" + this.name + "\" has been updated. Please review it again for approval.");

        }  
    }
    //Lập List Item để Admin Thêm/sửa/xóa
    public void listAllItems(List<Item> items) {
        if (this.isApproved()){
            items.add(this);
        }   
    }
    //In ra List
    public void printListAllItems(List<Item> items) {
        System.out.println("[Admin] All items (" + items.size() + "):");
        for (Item i : items) {
            System.out.println("  - " + i.getName()
                    + "  Description: " + i.getDes()
                    + "  Start price: " + i.getStartPrice()
                    + "  Category: " + i.getCategory()
                    + "  Seller ID: " + i.getSellerId());
        }
    }
    @Override
    public void printInfo() {
        System.out.println("Sản phẩm:" + name + "mô tả:" + des + "có giá khởi điểm" + startPrice);
}
