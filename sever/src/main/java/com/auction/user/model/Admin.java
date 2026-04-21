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
 // Khóa tài khoản người dùng

    public void banUser(User user) {
<<<<<<< HEAD
        user.ban();
        System.out.println("[Admin] User \"" + user.getName() + "\" has been banned. You can no longer access the platform.");
=======
        user.flagSuspiciousActivity(user);
        user.ban();
        System.out.println("[Admin] User \"" + user.getName() + "\" has been banned.");
>>>>>>> e0d5f32ea7e1c14e4101ca58c9e679746d4f3e05
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
  

    
    public void manageLiveAuction(Auction auction) {
           
    }


    
}
