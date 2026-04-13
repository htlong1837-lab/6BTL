package com.auction.user.model;

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
    /** Xóa một phiên đấu giá khỏi danh sách */
    public void removeAuction(List<Auction> auctions, Auction target) {
        if (auctions.remove(target)) {
            System.out.println("[Admin] Auction for \"" + target.getItem().getName() + "\" has been removed.");
        } else {
            System.out.println("[Admin] Auction not found.");
        }
    }
 
    /** Khóa tài khoản người dùng (ví dụ khi vi phạm) */
    public void banUser(User user) {
        System.out.println("[Admin] User \"" + user.getName() + "\" has been banned.");
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
        if (!user.login(false)){
            int failedLogin = user.login(false)+1;
            if (failedLogin >= 3) {
                banUser(user);
                System.out.println("[Admin] User \"" + user.getName() + "\" has been flagged for suspicious activity due to multiple failed login attempts.");
            } else {
                System.out.println("[Admin] User \"" + user.getName() + "\" has a failed login attempt. Total failed attempts: " + failedLogin);
            }
        }
    @Override
    public void printInfo() {
        super.printInfo();
        System.out.println("ADMIN này có cấp độ :" + adminLevel);
    }


    
}
