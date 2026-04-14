package com.auction.user.model;

import java.util.ArrayList;
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
 
    // Khóa tài khoản người dùng (ví dụ khi vi phạm) */
    public void banUser(User user) {
        System.out.println("[Admin] User \"" + user.getName() + "\" has been banned.");
    }
    // Mở lại tài khoản người dùng
    public void isActive(User user) {
        System.out.println("[Admin] User \"" + user.getName() + "\" has been activated. You can now access");
    }
    // Duyệt sản phẩm
    public void approveItem(Item item) {
        if (item.isApproved()) {
            System.out.println("[Admin] Item \"" + item.getName() + "\" is already approved. It's ready for auction right now");
        } else {
            System.out.println("[Admin] Item \"" + item.getName() + "\" is invalid. Please check the details and edit it for approval.");
        }
    }
    // Ghi nhận hành vi đánh giá bất thường
    public void flagSuspiciousActivity(User user) {
        boolean loginSuccess = user.login(false);
        if (loginSuccess) {
            System.out.println("[Admin] User \"" + user.getName() + "\" has a failed login attempt.");
            boolean loginSuccess2 = user.login(false);
            if (loginSuccess2) {
                System.out.println("[Admin] User \"" + user.getName() + "\" has multiple failed login attempts. Consider banning this account.");

            }
            
        
        }
    }
    @Override
    public void printInfo() {
        super.printInfo();
        System.out.println("ADMIN này có cấp độ :" + adminLevel);
    }
    //Quản lý đấu giá trực tiếp
    // Admin đưa sản phẩm vào đấu giá
    public void setItemtoLiveAution(int i, List<Item> items){
        if (i >= 0 && i < items.size()) {
            Item item = items.get(i);
            item.printInfo();
        } else {
            System.out.println("[Admin] Invalid item index.");
        }
    }
    public void updateCurrentPrice(Auction auction){
        System.out.println("Current price has been updated. " + "New price: " + auction.getCurrentPrice());
    }

    
    public void manageLiveAuction(Auction auction) {
           
    }


    
}
