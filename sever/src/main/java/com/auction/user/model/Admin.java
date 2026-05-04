package com.auction.user.model;

import java.util.List;

import com.auction.auction.model.Auction;
import com.auction.item.model.Product.Item;

public class Admin extends User {
    private int adminLevel;
    public Admin(String id, String name, String email, String passwordHash) {
        super(id, name, email, passwordHash);
        this.adminLevel = 1;
    }
    public int getAdminLevel() {
        return adminLevel;
    }
    public void setAdminLevel(int level) {
        this.adminLevel = level;
    }

    
    // Xóa một phiên đấu giá khỏi danh sách */
    public void removeAuction(List<Auction> auctions, Auction target) {
        if (auctions.remove(target)) {
            System.out.println("[Admin] Auction for \"" + target.getItem().getName() + "\" has been removed.");
        } else {
            System.out.println("[Admin] Auction not found.");
        }
    }
 
    // Mở lại tài khoản người dùng
    public void isActive(User user) {
        user.setBanned(false);
        System.out.println("[Admin] User \"" + user.getName() + "\" has been activated. You can now access");
    }
    // Duyệt sản phẩm
    public void approveItem(Item item) {
        if (item.isApproved()==true) {
            System.out.println("[Admin] Item \"" + item.getName() + "\" is already approved. It's ready for auction right now");
        } else {
            System.out.println("[Admin] Item \"" + item.getName() + "\" is invalid. Please check the details and edit it for approval.");
        }
    }
    @Override
    public void printInfo() {
        super.printInfo();
        System.out.println("ADMIN này có cấp độ :" + adminLevel);
    }

}
